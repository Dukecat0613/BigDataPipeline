package Pipeline

/**
  * Created by Dukecat on 6/8/17.
  */

import akka.actor.ActorSystem

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

object DataProducer extends App {
  override def main(args: Array[String]): Unit = {
  val stockCode = args(0)
  val server = args(1)
  val sendTopic = args(2)
  // Kafka producer
  val producer = new Producer(server, sendTopic)
  val system = ActorSystem("system")
  system.scheduler.schedule(0 seconds, 1 seconds)(producer.send(stockCode))
  }
}
