import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
/**
 * Created by pavan on 6/20/2017.
 */
public class Sample {
    public static void main(String[] args)
    {
        System.setProperty("hadoop.home.dir", "C:\\Users\\pavan\\Desktop\\hadoop-winutils-2.6.0");
        SparkConf conf = new SparkConf().setAppName("Sample").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        pipeline = new StanfordCoreNLP(props, false);
        String text = "The dog saw John in the park The little bear saw the fine fat trout in the rocky brook";
        Annotation document = pipeline.process(text);
        List<String>inputs= new ArrayList<String>();
        for(CoreMap sentence: document.get(CoreAnnotations.SentencesAnnotation.class))
        {
            String lemma="";
            for(CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class))
            {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                inputs.add(lemma);
            }
        }
        String[] newArray = new String[inputs.size()];
        newArray = inputs.toArray(newArray);
    JavaRDD<String> rddX = sc.parallelize(
            Arrays.asList(
                    newArray
            ), 5);
    JavaPairRDD<Character, Iterable<String>> rddY = rddX.groupBy(word -> word.charAt(0));
    System.out.println(rddY.collect());
    }
}

