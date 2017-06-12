package Pipeline

/**
  * Created by Dukecat on 6/7/17.
  */

import java.util.{Calendar, Properties}
import collection.JavaConverters._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json.JSONObject
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.apache.log4j.BasicConfigurator

class Streaming(var server: String, var receiveTopic: String, var sendTopic: String) extends LazyLogging{
  // Kafka Parameters
  val props = new Properties()
  props.put("bootstrap.servers", server)
  props.put("client.id", "Streaming")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  val producer = new KafkaProducer[String, String](props)
  BasicConfigurator.configure()
  // Kafka stream
  val conf = new SparkConf().setAppName("DataPipeline").setMaster("local[2]")
  val ssc = new StreamingContext(conf, Seconds(2))
  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> server,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "auto.offset.reset" -> "latest",
    "group.id" -> "use_a_separate_group_id_for_each_stream"
  )
  val stream = KafkaUtils createDirectStream[String, String](
    ssc,
    PreferConsistent,
    Subscribe[String, String](Array(receiveTopic), kafkaParams)
  )

  stream.foreachRDD(rdd => {
    val newRdd = rdd.map(record => {
      val tmp = new JSONObject(record.value())
      (tmp.get("StockSymbol"), (tmp.get("LastTradePrice").toString.toFloat, 1))
    }).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2)).map(x => (x._1, x._2._1 / x._2._2))
    val result = newRdd.collect()
    for (ele <- result) {
      val msg = new JSONObject(Map("StockSymbol" -> ele._1, "LastTradePrice" -> ele._2,
        "SendTime" -> System.currentTimeMillis.toDouble / 1000).asJava)
      val data = new ProducerRecord[String, String](sendTopic, msg.toString)
      producer.send(data)
      logger.info("Successfully send the averaged price " + msg)
    }
  })

}

