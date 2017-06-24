import org.apache.spark.{SparkConf,SparkContext}

/**
  * Created by pavan on 6/11/2017.
  */

object Hello{
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir","C:\\Users\\pavan\\Desktop\\hadoop-winutils-2.6.0")
    val sparkConf = new SparkConf().setAppName("Hello").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)
    val counts=sc.textFile("input.txt")
    val countWords=counts.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey(_ + _).collect()
    countWords.foreach(println)
  }
}
