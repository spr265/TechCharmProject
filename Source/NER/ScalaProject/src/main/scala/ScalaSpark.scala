import org.apache.spark.{SparkConf, SparkContext}
import java.io.{File, PrintWriter}
import javax.servlet.http.HttpServletRequest
import jdk.nashorn.internal.runtime.Context
import org.apache.hadoop.conf.Configuration
import org.json4s.jackson.JsonMethods.{parse, pretty, render}
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods
import scala.util.parsing.input.NoPosition
import scala.util.parsing.json.JSON
/**
  * Created by chaitu on 4/1/2017.
  */
  object ScalaSpark
{
  def main(args: Array[String]): Unit =
  {
    System.setProperty("hadoop.home.dir","C:\\Users\\chaitu\\Desktop\\hadoop-winutils-2.6.0")
    val sparkConf = new SparkConf().setAppName("NewScala").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)
    // Ad events File Location : C:\Users\chaitu\Documents\GUM GUM\ad-events_2014-01-20_00_domU-12-31-39-01-A1-34
    val lines = sc.textFile(args(0))
    val jsonData=lines.flatMap(line=>line.split("\n"))
    val jsonExtract_view=jsonData.filter(line=> line.contains("\"e\":\"view\""))
    val jsonObj_view=jsonExtract_view.map(line => line.substring(line.indexOf("{"),line.lastIndexOf("}")+1).trim)
    val jsonobjArray_view=jsonObj_view.map(line=>"["+line+"]")
    val extracted_view=jsonobjArray_view.map(line => (pretty(render(parse(line) \ "pv")).toString.replace("\"",""),1)).reduceByKey(_+_)

    val jsonData_click=lines.flatMap(line=>line.split("\n"))
    val jsonExtract_click=jsonData_click.filter(line=> line.contains("\"e\":\"click\""))
    val jsonObj_click=jsonExtract_click.map(line=>line.substring(line.indexOf("{"),line.lastIndexOf("}")+1).trim)
    val jsonobjArray_click=jsonObj_click.map(line=>"["+line+"]")
    val extracted_click=jsonobjArray_click.map(line => (pretty(render(parse(line) \ "pv")).toString.replace("\"","").trim,1)).reduceByKey(_+_)

    val extracted_adEvents=extracted_view.fullOuterJoin(extracted_click)
    // file location : C:\Users\chaitu\Documents\GUM GUM\assets_2014-01-20_00_domU-12-31-39-01-A1-34
    val file_assets=sc.textFile(args(1))
    val Assets=file_assets.flatMap(line=>line.split("\n"))
    val jsonDataAssets=Assets.map(line=>line.substring(line.indexOf("{"),line.lastIndexOf("}")+1))
    val jsonAssetArray=jsonDataAssets.map(line=>"["+line+"]")
    val asset_Extracted=jsonAssetArray.map(line=> (pretty(parse(line) \ "pv").toString.replace("\"","").trim,1)).reduceByKey(_+_)

    val output_join=asset_Extracted.leftOuterJoin(extracted_adEvents)
    val out_edit=output_join.map(line => (line._1,line._2._1,line._2._2.getOrElse(0,0)))
    val out_final=out_edit.map(line => (line._1,line._2,line._3._1,line._3._2))

    // printToFile(new File("extracted_view")) { p => extracted_view.collect().foreach(p.println)}
    // printToFile(new File("extracted_click")) { p => extracted_click.collect().foreach(p.println)}
    // printToFile(new File("extracted_view_click")) { p => extracted_adEvents.collect().foreach(p.println)}
    printToFile(new File("output_final")) { p =>  out_final.collect().foreach(line =>p.println(line._1+" "+line._2+" "+ line._3.toString.replace("None","0").replaceAll("[A-Za-z()]","")+" "+ line._4.toString.replace("None","0").replaceAll("[A-Za-z()]","")))}

 }
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit)
  {
    val p = new java.io.PrintWriter(f)
    try
    {
      op(p)
    }
    finally
    {
      p.close()
    }
  }
}

