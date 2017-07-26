

package openie;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Quadruple;
import rita.RiWordNet;

import java.io.*;
import java.util.*;

/**
 * Created by Megha Nagabhushan on 7/17/2017.
 */
public class MainNLPClass {

    public static String returnTriplets(String sentence) throws IOException {

        Document doc = new Document(sentence);
        String triplet = "";
        FileWriter fileWriter = new FileWriter("fileWriter", true);
        FileWriter fileSubject = new FileWriter("myClass", true);
        FileWriter fileTriplets = new FileWriter("TripletsNew", true);
        FileWriter fileData = new FileWriter("DataProperties", true);
        FileWriter fileIndividual = new FileWriter("Individuals", true);
        FileWriter temp = new FileWriter("temp", true);
        FileWriter myClass = new FileWriter("Class", true);
        FileWriter objectFile = new FileWriter("ObjectProperties", true);

        ArrayList<String> subjectList = new ArrayList<>();
        ArrayList<String> objectList = new ArrayList<>();
        ArrayList<String> predicateList = new ArrayList<>();
        HashSet<String> predicateValues = new HashSet();
        ArrayList<String> subjects = new ArrayList<>();
        ArrayList<String> objects = new ArrayList<>();

        //creating a hash set for Class file
        HashSet<String> classSet = new HashSet<>();
        //adding our predefined classes to define the schema
        if (classSet.size() == 0) {
            classSet.add("TAX");
            classSet.add("CONSTRUCTION");
        }


        for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
            Collection<Quadruple<String, String, String, Double>> l = sent.openie();//.iterator();

            for (Quadruple x : l) {

                //retrieving subject from the quadruple and saving it in a list
                String subject = (String) x.first();
                subjectList.add(subject);

                //retrieving object from the quadruple and saving it in a list
                String object = (String) x.third();
                objectList.add(object);

                //retrieving predicates from the quadruple
                String predicate = (String) x.second();

                //checking if a subject has NER and adding the NER values into the class set and adding them as intance in Indiviaduals File
                String subjectNER = returnNER(subject);
                if (!subjectNER.equals("O")) {
                    fileIndividual.write(subjectNER + "," + subject.replaceAll(" .*$", "").replaceAll(" .*$", "") + "\n");
                    classSet.add(subjectNER);
                } else {
                    if (subject.contains("construction") || subject.contains("infrastucture") || subject.contains("building")) {
                        fileIndividual.write("CONSTRUCTION," + subject.replaceAll(" .*$", "") + "\n");
                    } else if (subject.contains("TAX")) {
                        fileIndividual.write("TAX," + subject.replaceAll(" .*$", "") + "\n");
                    }
                }

                //checking if a object has NER and adding the NER values into the class set and adding them as intance in Indiviaduals File
                String objectNER = returnNER(object);
                if (!objectNER.equals("O")) {
                    fileIndividual.write(objectNER + "," + object.replaceAll(" .*$", "") + "\n");
                    classSet.add(objectNER);
                } else {
                    if (object.contains("construction") || object.contains("infrastructure") || object.contains("building")) {
                        fileIndividual.write("CONSTRUCTION," + object.replaceAll(" .*$", "") + "\n");
                    } else if (object.contains("tax")) {
                        fileIndividual.write("TAX," + object.replaceAll(" .*$", "") + "\n");
                    }
                }


                //writing out the Class file
                for (String s : classSet) {
                    myClass.write(s + "\n");
                }


                //populating objectProperties file
                if (!subjectNER.equals("O") && !objectNER.equals("O")) {
                    objectFile.write(predicate + "," + subjectNER + "," + objectNER + ",Func\n");

                }

                if ((subject.contains("construction") || subject.contains("building") || subject.contains("infrastructure")) &&
                        (object.contains("construction") || object.contains("building") || object.contains("infrastructure"))) {

                    objectFile.write(predicate + ",CONSTRUCTION,CONSTRUCTION,Func\n");

                }
                if ((subject.contains("cosntruction") || subject.contains("building") || subject.contains("infrastructure")) &&
                        (object.contains("tax"))) {

                    objectFile.write(predicate + ",CONSTRUCTION,TAX,Func\n");

                }
                if ((subject.contains("tax")) &&
                        (object.contains("construction") || object.contains("building") || object.contains("infrastructure"))) {

                    objectFile.write(predicate + ",TAX,CONSTRUCTION,Func\n");

                }
                if ((subject.contains("tax")) &&
                        (object.contains("tax"))) {

                    objectFile.write(predicate + ",TAX,TAX,Func\n");

                }
                //end of the objectProperty File

                //adding realtedTo triplets


                //creating dataproperties
                for (String str : classSet) {
                    fileData.write("realtedTo," + str + ",string\n");
                }

                if (!subjectNER.equals("O")) {
                    subjectList.add(subject);
                }

                if (!subjectNER.equals("O")) {
                    subjects.add(subject);
                }

                if (!objectNER.equals("O")) {
                    objects.add(object);
                }

                tripletGeneration(subject, predicate, object);


                triplet = subject + predicate + object;

            }


            //removing stopwords and duplicates for the subject
            HashSet<String> subjectSet = stopWordRemoving(subjectList);


            HashSet<String> synonymSet = new HashSet<>();
            for (String str : subjectSet) {
                synonymSet = getSynonyms(str);
                for (String syn : synonymSet) {
                    fileWriter.write("relatedTo" + "," + str + "," + syn + ",Func\n");
                }
            }

            HashSet<String> subjectsSet = stopWordRemoving(subjects);


            HashSet<String> synonymSubjectSet = new HashSet<>();
            for (String str : subjectsSet) {
                synonymSubjectSet = getSynonyms(str);
                for (String syn : synonymSubjectSet) {
                    fileTriplets.write(str + ",relatedTo" + syn + ",Data\n");
                }
            }

            HashSet<String> objectsSet = stopWordRemoving(objects);


            HashSet<String> synonymObjectSet = new HashSet<>();
            for (String str : objectsSet) {
                synonymObjectSet = getSynonyms(str);
                for (String syn : synonymObjectSet) {
                    fileWriter.write(str + ",relatedTo" + syn + ",Data\n");
                }
            }


            //creating data properties
            /*for(String str : subjectSet){
                fileData.write("hasNer,"+str+",string\n");
            }*/


        }
        temp.close();
        fileData.close();
        fileIndividual.close();
        fileSubject.close();
        fileTriplets.close();
        fileWriter.close();
        myClass.close();
        objectFile.close();
        stripDuplicatesFromFile("ObjectProperties");
        stripDuplicatesFromFile("myClass");
        stripDuplicatesFromFile("Individuals");
        stripDuplicatesFromFile("DataProperties");
        stripDuplicatesFromFile("Class");
        return triplet;

    }

    public static String returnNER(String word) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(word);
        pipeline.annotate(document);
        String stringNER = "";
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                stringNER = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            }
        }
        return stringNER;
    }

    public static HashSet<String> stopWordRemoving(ArrayList arrayList) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader("data/stopwords.txt"));

        for (String line = br.readLine(); line != null; line = br.readLine()) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).equals(line)) {
                    arrayList.remove(i);
                }
            }
        }
        HashSet<String> subjectSet = new HashSet<String>(arrayList);
        return subjectSet;
    }

    public static HashSet<String> getSynonyms(String word) {
        RiWordNet wordnet = new RiWordNet("/home/sowmya/Downloads/WordNet-3.0");
        String[] poss = wordnet.getPos(word);
        HashSet<String> synonym = new HashSet<>();
        for (int j = 0; j < poss.length; j++) {
            System.out.println("\n\nSynonyms for " + word + " (pos: " + poss[j] + ")");
            String[] synonyms = wordnet.getAllSynonyms(word, poss[j], 10);
            for (int i = 0; i < synonyms.length; i++) {
                synonym.add(synonyms[i]);
            }
        }
        return synonym;
    }

    public static void stripDuplicatesFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        Set<String> lines = new HashSet<String>(10000); // maybe should be bigger
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (String unique : lines) {
            writer.write(unique);
            writer.newLine();
        }
        writer.close();
    }

    public static void tripletGeneration(String subject, String predicate, String object) throws IOException {

        FileWriter fileWriter = new FileWriter("TripletGenerated", true);
        String subjectNER = returnNER(subject);
        String objectNER = returnNER(object);
        HashSet<String> subjectSynonym = new HashSet<>();
        HashSet<String> objectSynonym = new HashSet<>();
        if (subject.contains("construct")) {
            String x = subject.replaceAll(".+", "constructing");
            subject = x;
        }
        if (subject.contains("elect")) {
            String x = subject.replaceAll(".+", "elected");
            subject = x;
        }
        if (subject.contains("building")) {
            subject = subject.replaceAll(".+", "buildings");
        }
        if (object.contains("material")) {
            object = object.replaceAll(".+", "materials");
        }
        if (object.contains("finance")) {
            object = object.replaceAll(".+", "financial");
        }
        if (object.contains("country")) {
            object = object.replaceAll(".+", "countries");
        }
        if (object.contains("project")) {
            object = object.replaceAll(".+", "projects");
        }


        if (!subjectNER.equals("O") && !objectNER.equals("O")) {
            if (predicate.contains("was") || predicate.contains("at") || predicate.contains("would")) {
                fileWriter.write(subject + "," + predicate + "," + object + ",Obj\n");
            }
        }


        if (!subjectNER.equals("O") && !objectNER.equals("O")) {
            if (predicate.contains("was") || predicate.contains("at") || predicate.contains("would")) {
                fileWriter.write(subject + "," + predicate + "," + object + ",Obj\n");
            }
        }


        if ((subject.contains("construction") || subject.contains("building") || subject.contains("infrastructure")) &&
                (object.contains("construction") || object.contains("building") || object.contains("infrastructure"))) {
            if (predicate.contains("was") || predicate.contains("at") || predicate.contains("would")) {
                fileWriter.write(subject + "," + predicate + "," + object + ",Obj\n");
            }
        }
        if ((subject.contains("construction") || subject.contains("building") || subject.contains("infrastructure")) &&
                (object.contains("tax"))) {
            if (predicate.contains("was") || predicate.contains("at") || predicate.contains("would")) {
                fileWriter.write(subject + "," + predicate + "," + object + ",Obj\n");
            }
        }
        if ((subject.contains("tax")) &&
                (object.contains("construction") || object.contains("building") || object.contains("infrastructure"))) {
            if (predicate.contains("was") || predicate.contains("at") || predicate.contains("would")) {
                fileWriter.write(subject + "," + predicate + "," + object + ",Obj\n");
            }
        }
        if ((subject.contains("tax")) &&
                (object.contains("tax"))) {
            if (predicate.contains("was") || predicate.contains("at") || predicate.contains("would")) {
                fileWriter.write(subject + "," + predicate + "," + object + ",Obj\n");
            }
        }

        if (predicate.contains("ratify")) {
            fileWriter.write(subject.replaceAll(" .*$", "") + ",ratified," + object.replaceAll(" .*$", "") + ",Data\n");
        } else if (predicate.contains("be elect")) {
            fileWriter.write(subject.replaceAll(" .*$", "") + ",been elected," + object.replaceAll(" .*$", "") + ",Data\n");
        } else if (predicate.contains("great")) {
            fileWriter.write(subject.replaceAll(" .*$", "") + ",greater," + object.replaceAll(" .*$", "") + ",Data\n");
        } else if (predicate.contains("service")) {
            fileWriter.write(subject.replaceAll(" .*$", "") + ",services," + object.replaceAll(" .*$", "") + ",Data\n");
        } else if (predicate.contains("gain")) {
            fileWriter.write(subject.replaceAll(" .*$", "") + ",gains," + object.replaceAll(" .*$", "") + ",Data\n");
        } else if (predicate.contains("low")) {
            fileWriter.write(subject.replaceAll(" .*$", "") + ",lower," + object.replaceAll(" .*$", "") + ",Data\n");
        } else if (predicate.contains("high")) {
            fileWriter.write(subject.replaceAll(" .*$", "") + ",higher," + object.replaceAll(" .*$", "") + ",Data\n");
        } else if (predicate.contains("be")) {
            fileWriter.write(subject.replaceAll(" .*$", "") + ",are," + object.replaceAll(" .*$", "") + ",Data\n");
        } else if (predicate.contains("use")) {
            fileWriter.write(subject.replaceAll(" .*$", "") + ",used," + object.replaceAll(" .*$", "") + ",Data\n");
        } else if (predicate.contains("such a")) {
            fileWriter.write(subject.replaceAll(" .*$", "") + ",such as," + object.replaceAll(" .*$", "") + ",Data\n");
        }


        subjectSynonym = getSynonyms(subject);
        System.out.println("printing sub synonyms0");
        for (String str : subjectSynonym) {
            fileWriter.write(subject + ",relatedTo," + str.replaceAll(" .*$", "") + ",Data\n");
        }


        System.out.println("inside obj ner");
        objectSynonym = getSynonyms(object);
        for (String str : objectSynonym) {
            fileWriter.write(subject + ",relatedTo," + str.replaceAll(" .*$", "") + ",Data\n");
        }

        stripDuplicatesFromFile("TripletGenerated");
        fileWriter.close();
    }

}