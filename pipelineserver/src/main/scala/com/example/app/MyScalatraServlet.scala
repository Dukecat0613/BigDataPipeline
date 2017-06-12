package com.example.app

import org.scalatra.NotFound
import Pipeline.Producer
import akka.actor.{ActorSystem, Cancellable}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import scala.collection.mutable

class MyScalatraServlet extends PipelineserverStack {
  val kafkaServer = "localhost:9092"
  val topic = "stocksender"
  val system = ActorSystem("system")
  val producer = new Producer(kafkaServer, topic)
  val taskMap = mutable.HashMap[String, Cancellable]()

  post("/:symbol/add") {
    val symbol = params("symbol")
    symbol match {
      case "" => NotFound("Sorry, the symbol you ask is wrong")
      case _ => {
        val cancellable = system.scheduler.schedule(0 seconds, 3 seconds)(producer.send(symbol))
        taskMap.put(symbol, cancellable)
        println(s"Add ${symbol} to send to the ${topic}")
      }
    }
  }

  post("/:symbol/delete") {
    val symbol = params("symbol")
    symbol match {
      case "" => NotFound("Sorry, the symbol you ask is wrong")
      case _ => {
        if (taskMap.contains(symbol)) {
          val job = taskMap.get(symbol)
          job.get.cancel()
          taskMap.remove(symbol)
          println(s"Successfully remove the ${symbol}")
        } else {
          println(s"The ${symbol} is not in the pool")
        }
      }
    }
  }
}





