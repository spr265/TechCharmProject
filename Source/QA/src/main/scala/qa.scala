import java.io.{BufferedWriter, File, FileWriter}

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object qa {
  def main(args: Array[String]): Unit = {

    //System.setProperty("hadoop.home.dir", "E:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)
    val call: NLP = new NLP();
    val i = 0

    val text = sc.textFile("data/sentenceSample");
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val in = sc.textFile("data/sentenceSample").map(line => {
      //Getting OpenIE Form of the word using lda.CoreNLP

      val t=call.returnTriplets(line)
      t
    })
  //in.foreach(println)
for (i <- 0 to 2) {
  val input = scala.io.StdIn.readLine()
  if (input.contains("who")) {
    val r1 = text.map(line => {
      call.ret(line, "PERSON")
    })
    fun(r1, input)
  }
  if (input.contains("where")) {
    val r1 = text.map(line => {
      call.ret(line, "LOCATION")
    })
    fun(r1, input)
  }
  if (input.contains("when")) {
    val r1 = text.map(line => {
      call.ret(line, "DATE")
    })
    fun(r1, input)
  }
  else {
    val x = in.map(f => call.ma(input, f))
    x.foreach(println)

  }
}
  }
  def fun(value: RDD[String], str: String)
  {

    val r2=value.flatMap(s=>{s.split(" ")}).map(w=>(w)).filter(f=>{f.length>2})
    val r3=r2.distinct()
    r3.take(10).foreach(println)
  }
}
