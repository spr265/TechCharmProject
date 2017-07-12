package wordnet;

import rita.RiWordNet;
import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import java.nio.file.Paths;import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;


import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WordNetMain {
    public static void main(String args[]) {

        RiWordNet wordnet = new RiWordNet("C:\\Users\\sreel\\IdeaProjects\\WordNet-3.0");
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        String line = null;
        String fileName = "data/file.txt";



        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);

// create an empty Annotation just with the given text
        Annotation document = new Annotation(line);

// run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {

                System.out.println("\n" + token);

                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                System.out.println("Text Annotation");
                System.out.println(token + ":" + word);
                // this is the POS tag of the token

                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                System.out.println("Lemma Annotation");
                System.out.println(token + ":" + lemma);

                // Demo finding parts of speech
                String word1 = lemma;
                System.out.println("\nFinding parts of speech for " + word1 + ".");


                if (wordnet.getPos(word1) == null) {

                    System.out.println("exception");

                }
                else {
                    String[] partsofspeech = wordnet.getPos(word1);


                    for (int i = 0; i < partsofspeech.length; i++) {
                        System.out.println(partsofspeech[i]);
                    }
                }

                /*word1 = lemma;
                String pos = wordnet.getBestPos(word1);
                System.out.println("\n\nDefinitions for " + word1 + ":");
                if (wordnet.getAllGlosses(word1, pos) == null) {

                    System.out.println("exception");

                }
                // Get an array of glosses for a word
                else {
                    String[] glosses = wordnet.getAllGlosses(word1, pos);

                    // Display all definitions
                    for (int i = 0; i < glosses.length; i++) {
                        System.out.println(glosses[i]);
                    }

                }
                */
                // Demo finding a list of related words (synonyms)
                word1 = lemma;
                if (wordnet.getPos(word1) == null) {

                    System.out.println("exception");

                } else {


                String[] poss = wordnet.getPos(word1);
                for (int j = 0; j < poss.length; j++) {
                    System.out.println("\n\nSynonyms for " + word + " (pos: " + poss[j] + ")");
                    String[] synonyms = wordnet.getAllSynonyms(word1, poss[j], 10);
                    for (int i = 0; i < synonyms.length; i++) {
                        System.out.println(synonyms[i]);
                    }
                }
            }
                // Demo finding a list of related words
                // X is Hypernym of Y if every Y is of type X
                // Hyponym is the inverse
             /*   word1 = lemma;

                if ( wordnet.getPos(word1) == null)
                {

                    System.out.println("exception");

                }

                else
                {
                    String pos = wordnet.getBestPos(word1);
                    System.out.println("\n\nHyponyms for " + word1 + ":");


                    String[] hyponyms = wordnet.getAllHyponyms(word1, pos);
                    for (int i = 0; i < hyponyms.length; i++) {
                        System.out.println(hyponyms[i]);
                    }

                    System.out.println("\n\nHypernyms for " + word1 + ":");
                    String[] hypernyms = wordnet.getAllHypernyms(word1, pos);
                    for (int i = 0; i < hypernyms.length; i++) {
                        System.out.println(hypernyms[i]);
                    }
                }
*/
       /*String start = "dog";
        String end = "giraffe";
        pos = wordnet.getBestPos(start);

        // Wordnet can find relationships between words
        System.out.println("\n\nRelationship between: " + start + " and " + end);
        float dist = wordnet.getDistance(start,end,pos);
        String[] parents = wordnet.getCommonParents(start, end, pos);
        System.out.println(start + " and " + end + " are related by a distance of: " + dist);

        // These words have common parents (hyponyms in this case)
        System.out.println("Common parents: ");
        if (parents != null) {
            for (int i = 0; i < parents.length; i++) {
                System.out.println(parents[i]);
            }

*/
                //System.out.println("\n\nHypernym Tree for " + start);
                //int[] ids = wordnet.getSenseIds(start,wordnet.NOUN);
                //wordnet.printHypernymTree(ids[0]);

            }
        }
    }
        bufferedReader.close();
    }

        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }


    }
}