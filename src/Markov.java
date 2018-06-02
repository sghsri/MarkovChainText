/**
 * Created by SriramHariharan on 5/24/18.
 */


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.System.out;

public class Markov {
    private static String filename = "mobydick";
    private static String filetype = ".txt";
    String[] input;
    Set<String> wordlist;
    Map<String, Map<String, Double>> markovmap;
    public static void main(String[] args) throws IOException{
        Markov m = new Markov();
        m.loadfromFile("input/"+filename+filetype, true);
        m.calculateWeights();
        m.generateOutput("",10);
    }
    public Markov(){
        markovmap = new TreeMap<>();
        wordlist = new HashSet<>();
    }


    private void generateOutput(String start, int numsentences){
        String word = start;
        StringBuilder sb = new StringBuilder();
        if(start.equals("")){
            word = input[(int)(Math.random()*input.length)];
        }
        for(int i = 0; i<numsentences;){
            sb.append(word);
            sb.append(" ");
            if(word.contains(".") && !word.contains("Mrs.") && !word.contains("Mr.")){
                sb.append("\n");
                i++;
            }
            word=predictNextWord(word);
        }
        String output = sb.toString();
        output = (output.substring(0,1).toUpperCase()+output.substring(1));
        try {
            FileWriter fout = new FileWriter("output/"+filename + "_generated" + filetype);
            fout.write(output);
            fout.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String predictNextWord(String s){
        Map<String, Double> adjacent = markovmap.get(s);
        double rand = Math.random();
        double sum = 0;
        for(String output : adjacent.keySet()){
            sum+=adjacent.get(output);
            if(sum >rand){
                return output;
            }
        }
        return "";
    }

    private void loadfromFile(String filename, boolean punctuation) throws IOException{
        Scanner sc = new Scanner(new File(filename));
        if(!punctuation){
            sc.useDelimiter("[ ,!?.\"-;\n]");
        }
        StringBuilder text = new StringBuilder();
        while(sc.hasNext()){
            String next = sc.next();
            if(!isNumber(next)) {
                wordlist.add(next);
                text.append(" ");
                text.append(next);
            }
        }
        input = text.toString().split(" ");
    }
    private void calculateWeights(){
        countUpAdjacency();
        for(String word : markovmap.keySet()){
            for(String adj : markovmap.get(word).keySet()){
                double rowsum = markovmap.get(word).get("\u200B");
                double weight = markovmap.get(word).get(adj)/rowsum;
                markovmap.get(word).put(adj,weight);
            }
            markovmap.get(word).remove("\u200B");
        }
    }
    private boolean isNumber(String s){
        try{
            double x = Double.parseDouble(s);
        }catch (Exception e){
            return false;
        }
        return true;
    }
    private void countUpAdjacency(){
        for(String current : wordlist){
            //for each unique word in the file, make a map of adjacent words
            Map<String, Double> adjacent = new TreeMap<>();
            double sum = 0;
            for(int i = 0; i<input.length-1;i++){
                //go through the whole file again and count which words are adjacent to current and the number of times they are adjacent
                if(input[i].equals(current) && !input[i].contains("\n")){
                    String next = input[i+1];
                    Double count = adjacent.get(next);
                    if(count != null){
                        adjacent.put(next,count+1);
                    }
                    else{
                        adjacent.put(next,1.0);
                    }
                    sum++;
                }
            }
            //put the sum of each "row" in the map with the zero length space as its key
            adjacent.put("\u200B",sum);
            markovmap.put(current,adjacent);
        }
    }

}
