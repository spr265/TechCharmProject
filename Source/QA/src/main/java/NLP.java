import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Quadruple;
import jdk.nashorn.internal.ir.WhileNode;

import javax.xml.bind.Element;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.*;

public class NLP implements Serializable {

Multimap<String, String> h = HashMultimap.create();
List<String> tuple=new ArrayList<>();
    public String lemm(String data) {

        Properties prop = new Properties();

        StringBuilder res = new StringBuilder();
        prop.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(prop);
        Annotation doc = new Annotation(data);

        pipeline.annotate(doc);

        List<CoreMap> sents = doc.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sents) {
            for (CoreLabel token1 : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String lemma = token1.get(CoreAnnotations.LemmaAnnotation.class);
                res.append(lemma + " ");
            }
        }
        return res.toString();
    }
    public void nerealtions(String d)
    {
        Properties p=new Properties();
        p.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(p);
        Annotation a = new Annotation(d);
        pipeline.annotate(a);
        List<CoreMap> lines=a.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap line:lines){
            for(CoreLabel t:line.get(CoreAnnotations.TokensAnnotation.class))
            {
                String ne=t.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                String w=t.get(CoreAnnotations.TextAnnotation.class);
                    h.put(ne, w);

                }
            }
        }
public String ret(String data,String ans)
{
    nerealtions(data);
    Collection<String> c=h.get(ans);

    StringBuilder b=new StringBuilder();
    for(String ele:c)
    {
        b.append(ele+" ");
    }

    return b.toString();
}

    public List<String> returnTriplets(String sentence) {

        Document doc = new Document(sentence);

        for (Sentence sent : doc.sentences()) {


            Collection<Quadruple<String, String, String, Double>> l=sent.openie();

             Iterator it=l.iterator();
          while(it.hasNext()) {
              //System.out.println(it.next().toString());
              String x=it.next().toString();

              if (!tuple.contains(x)) {
                   tuple.add(x);
              }
          }

        }
  //  System.out.println(tuple);
        return tuple;
    }

public String ma(String d, List<String> li) {
   //  System.out.println(li);
    StringBuffer res = new StringBuffer();

     int count=0;
        Iterator i = li.iterator();
        while (i.hasNext()) {
            String get = i.next().toString();
            StringTokenizer st = new StringTokenizer(d," ");
            while (st.hasMoreTokens()) {
                String x=st.nextToken();
            if (get.contains(x)) {
                count++;
              //  System.out.println(count);
                if(count>=2) {
                    int n = get.length();
                    get = get.replaceAll(",", " ");

                    res.append(get.substring(1, n - 4) + "\n");
                    break;
            }

            }
        }
        count=0;

    }
    return res.toString();
}
    }
