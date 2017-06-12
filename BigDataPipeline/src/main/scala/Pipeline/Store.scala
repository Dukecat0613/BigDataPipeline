package Pipeline

/**
  * Created by Dukecat on 6/10/17.
  */

import java.util.Properties
import java.util.Collections.singletonList

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import com.datastax.driver.core._
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.log4j.BasicConfigurator
import org.json.JSONObject

class Store(var cassandraServer: String, var kafkaServer: String, var kafkaTopic: String,
            var cassandraKeySpace: String, var cassandraTable: String) extends LazyLogging{
  private val cluster = Cluster.builder().addContactPoint(cassandraServer).build()
  log(cluster.getMetadata)
  val session = cluster.connect()
  BasicConfigurator.configure()
  val props = new Properties()

  props.put("bootstrap.servers", kafkaServer)
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "test")
  props.put("enable.auto.commit", "true")
  props.put("auto.commit.interval.ms", "1000")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

  private val kafkaConsumer = new KafkaConsumer[String, String](props)
  kafkaConsumer.subscribe(singletonList(kafkaTopic))

  private def log(metadata: Metadata): Unit = {
    logger.info(s"Connected to cluster: ${metadata.getClusterName}")
    for (host <- metadata.getAllHosts()) {
      logger.info(s"Datatacenter: ${host.getDatacenter()}; Host: ${host.getAddress()}; Rack: ${host.getRack()}")
    }
  }

  def create: Unit = {
    session.execute(s"create keyspace if not exists ${cassandraKeySpace} " +
      s"with replication={'class':'SimpleStrategy','replication_factor':'3'} and durable_writes= 'true'")
    session.execute(s"USE ${cassandraKeySpace}")
    session.execute(s"create table if not exists ${cassandraTable} " +
      s"(stock_symbol text,trade_time text,trade_price float, primary key ((stock_symbol),trade_time))")
  }

  private def persistData(msg: String):Unit = {
    logger.debug("Trying to save the data to cassandra " + msg)
    val jsonMsg = new JSONObject(msg)
    val StockSymbol = jsonMsg.get("StockSymbol").toString
    val LastTradePrice = jsonMsg.get("LastTradePrice").toString.toFloat
    val SendTime = jsonMsg.get("SendTime").toString
    val statement = s"""INSERT INTO ${cassandraKeySpace}.${cassandraTable} (stock_symbol,trade_time,trade_price) VALUES ('${StockSymbol}', '${SendTime}', ${LastTradePrice})"""
    session.execute(statement)
    logger.debug(s"Finished to save data for symbol: ${StockSymbol}, price: ${LastTradePrice}, tradetime: ${SendTime}")
  }

  def storeData: Unit = {
    while (true) {
      val record = kafkaConsumer.poll(1000)
      for (data <- record.asScala) {
        persistData(data.value)
      }
    }
  }
}
