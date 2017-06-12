package Pipeline

/**
  * Created by Dukecat on 6/10/17.
  */
object DataStore extends App{
  override def main(args: Array[String]): Unit = {
    val cassandraServer = args(0)
    val kafkaServer = args(1)
    val kafkaTopic = args(2)
    val cassandraKeySpace = args(3)
    val cassandraTable = args(4)
    val dataStore = new Store(cassandraServer, kafkaServer,
      kafkaTopic, cassandraKeySpace, cassandraTable)
    dataStore.create
    dataStore.storeData
  }
}
