import java.util.Calendar
import collection.JavaConverters._
import org.json.JSONObject

import scala.util.parsing.json.JSON
val person_json = """{
  "name": "Joe Doe",
  "age": 45,
  "kids": ["Frank", "Marta", "Joan"]
}"""

val person = JSON.parseFull(person_json)
person match {
  case Some(e:Map[String, Object]) => {
    e.foreach( pair=>
      println("the key is " + pair._1 + ";  and the value is" + pair._2)
    )
  }
  case None => println("Failed.")

}

val tuple = ("45", 4)
println(tuple._2)
val now = Calendar.getInstance()
println(now.getTime)
val msg = Map("StockSymbol" -> "AAPL", "LastTradePrice" -> 13.45, "SendTime" -> Calendar.getInstance.getTime)
println(new JSONObject(msg.asJava).toString)
val timestamp = System.currentTimeMillis.toDouble / 1000
