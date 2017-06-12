package Pipeline

/**
  * Created by Dukecat on 6/8/17.
  */
object DataStreaming extends App{
  override def main(args: Array[String]) {
    val server = args(0)
    val receiveTopic = args(1)
    val sendTopic = args(2)
    val stockStreaming = new Streaming(server, receiveTopic, sendTopic)
    val ssc = stockStreaming.ssc
    ssc.start()
    ssc.awaitTermination()
  }
}
