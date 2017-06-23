import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Mayanka on 19-06-2017.
  */
object NGRAM {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("TermFrequency - IDF").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    val a = sc.textFile("ProjectData/wiki(obama).txt",2)
    a.foreach(f=>println(f.mkString(" ")))
  }

  def getNGrams(sentence: String, n:Int): Array[Array[String]] = {
    val words = sentence
    val ngrams = words.split(' ').sliding(n)
    ngrams.toArray
  }

}


