/**
  * Created by pavan on 6/20/2017.
  */

package scala.collection

import org.apache.spark.{SparkConf, SparkContext}
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.Annotation
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.util.CoreMap
import java.util.Properties
import org.apache.spark.api.java.JavaPairRDD
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.SparkConf
import org.apache.spark.api.java._
import org.apache.spark.api.java.function._

import edu.stanford.nlp.trees.TreeCoreAnnotations
import java.nio.file.Files
import java.util
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.lang.Package
import java.util.Scanner
import java.util._
import java.lang.StringBuilder

import scala.collection.JavaConversions._
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation
import org.apache.spark.api.java.JavaSparkContext

import scala.collection.mutable.ArrayBuffer

object Examp {
  val conf = new SparkConf().setAppName("name1")
  conf.setMaster("local")
  conf.setAppName("my app")
  val sc = new JavaSparkContext(conf)

  val stopWords = sc.broadcast(
    scala.io.Source.fromFile("input.txt").getLines().toSet).value

  def plainTextToLemmas(text: String, stopWords: Set[String]): Unit = {
    val props = new Properties()
    props.put("annotators", "tokenize, ssplit, pos, lemma")
    val pipeline = new StanfordCoreNLP(props)
    val doc = new Annotation(text)
    pipeline.annotate(doc)
    val lemmas = new ArrayBuffer[String]()
    val sentences = doc.get(classOf[SentencesAnnotation])
    for (sentence <- sentences; token <- sentence.get(classOf[TokensAnnotation])) {
      val lemma = token.get(classOf[LemmaAnnotation])
      if (lemma.length > 2 && !stopWords.contains(lemma)) {
        lemmas += lemma.toLowerCase
      }
    }
    println(lemmas)

  }
}

