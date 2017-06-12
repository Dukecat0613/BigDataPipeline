package Pipeline

/**
  * Created by Dukecat on 6/8/17.
  */

import java.io.IOException
import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import GoogleFinanceJavaApi.GoogleFinance
import org.json.{JSONException, JSONObject}
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.apache.log4j.BasicConfigurator

class Producer(var server: String, var sendTopic: String) extends LazyLogging {
  val props = new Properties()
  props.put("bootstrap.servers", server)
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  val producer = new KafkaProducer[String, String](props)
  BasicConfigurator.configure()
  logger.info("producer has been built")
  def send(code: String) = {
    def getQuotes(code: String) = GoogleFinance.getQuotes(code)
    val msg = new JSONObject(getQuotes(code))
    val data = new ProducerRecord[String, String](sendTopic, msg.toString)
    producer.send(data)
    logger.info("Successfully send the msg " + msg)
  }
}
