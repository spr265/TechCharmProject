import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.Quadruple;
import scala.Serializable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class CoreNLP implements Serializable{

    public String returnTriplets(String sentence) {

        Document doc = new Document(sentence);
        String lemma="";
        for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences


            Collection<Quadruple<String, String, String, Double>> l=sent.openie();

                lemma+= l.toString();
            Iterator it=l.iterator();
                while(it.hasNext())
                    System.out.println(it.next());
           // System.out.println(lemma);
        }

        return lemma;
    }

}
