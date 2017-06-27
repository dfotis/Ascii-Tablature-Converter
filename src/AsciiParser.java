package asciitomusicxml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dilaris Fotis 2017
 */
public class AsciiParser {
    
    public void AsciiParser() {
    }
        
    //Parsing the text file line by line and coverting it to a manageable form
    public String ParseTextFile(String file) throws FileNotFoundException, IOException {
        try(BufferedReader br  = new BufferedReader(new FileReader(file))) {
            String temp = null;
            ArrayList<String> lines = new ArrayList<String>();
            ArrayList<ArrayList<String>> tokens = new ArrayList<ArrayList<String>>();
      
            int i=0;
            while(true) {
                String str = br.readLine();
                
                if(str != null) {
                    str = str.replace("−", "-");
                    str = str.replace("|", "");
                }
                //System.out.println(str);
                if(str == null || (!str.contains("-"))) break;
                
                lines.add(str);
                //System.out.println(str);
                tokens.add(Tokenizer(str, false)); 
                i = str.length();
            }      
            
            String xordi = mergeStrings(tokens, i);
            System.out.println("MRG>"+ xordi);
            
            ArrayList<String> tokenized = Tokenizer(xordi, true);
            //System.out.println(tokenized.toString());
            
            temp = PatternConstructor(tokenized);
            //System.out.println(temp);

            temp = addMeasures(temp , 4);
            
            return temp;
        }   
    }
    
    private String addMeasures(String pattern, float timeSign) {
        String temp = pattern;
        String[] durations = pattern.split(" ");
        
        String[][] element = new String[durations.length][2];
        
        int i = 0; 
        int position = 0;
        int len = 0;
        for(String e : durations) {
            len = e.length();
            position += len+1;            
            
            element[i][0] = "" + e.charAt(len - 1);
            element[i][1] = "" + position;
            i++;
        }
        
        int extraPosition = 0;
        float sumDuration = timeSign; //deafult 4/4
        for(int j=0; j <durations.length; j++) {
            System.out.println(element[j][0].trim());
            switch(element[j][0].trim()) {
                case "w":
                    sumDuration -= 4;
                    break;
                case "h":
                    sumDuration -= 2;
                    break;
                case "q":
                    sumDuration -= 1;
                    break;
                case "i":
                    sumDuration -= 0.5;
                    break;
                case "s":
                    sumDuration -= 0.25;
                    break;
            }
            if(sumDuration == 0) {
                temp = temp.substring( 0, Integer.parseInt(element[j][1]) + extraPosition-1 ) 
                   + " | " 
                   + temp.substring( Integer.parseInt(element[j][1])+ extraPosition, temp.length() );
                extraPosition += 2;
                sumDuration = timeSign;
            }
        }
        
        //System.out.println(temp.substring(0, temp.length()-3));
    
        return temp;
    }
    
    private String mergeStrings(ArrayList<ArrayList<String>> tokens, int s) { 
        char[] chars = new char[2*s];
        Arrays.fill(chars, '-');
        String m = new String(chars);
        StringBuilder mrg = new StringBuilder(m);
        ArrayList<String> merge = new ArrayList<String>();
        
        //initialize merge
        for(int i=0; i<s+2; i++) {
            merge.add("-");
        }
        
        for(ArrayList<String> string : tokens){
            int i = 1;
            int position = 0;
            int temp;
            while (i < string.size()) {
                if (!string.get(i).contains("-")) {
                    temp = string.get(i).length();
                    if(i>1 && string.get(i-1).contains("-")) {
                        string.set(i, string.get(i)+"("+position+")");
                    } else if(i == 1) {
                        string.set(i, string.get(i)+"("+0+")");
                    }
                    position += temp;
                    string.set(i, string.get(0) + string.get(i));
                } else {
                    position += string.get(i).length();
                }
                i++;
            }
            //System.out.println("tokenized string: " + string);
        }
        
        Pattern pattern = Pattern.compile("\\(");
        Matcher matcher;
        String foundNote;
        int position;
        String currentString;
        int counter=0;
        
        for(ArrayList<String> string : tokens){
            int i = 1;
            while (i < string.size()) {
                currentString = string.get(i);
                if (!currentString.contains("-")) {
                    matcher = pattern.matcher(currentString);
                    if(matcher.find()) {
                        foundNote = currentString.substring(0, matcher.start());
                        position = Integer.parseInt(currentString.substring(matcher.start()+1, currentString.length()-1));
                        
                        if(merge.get(position) == "-") {                             
                            merge.add((position), foundNote); 
                        } else {
                            merge.add(position, foundNote+"+"+merge.get(position));
                        }                        
                        merge.remove(position+1);
                    }
                }
                i++;
            }            
        }
        
        String str;
        for(int i=0; i<merge.size(); i++) {
            str = merge.get(i);
            if(!(str.contains("-")) && ( str.length()>2 || (str.contains("#") && str.length()>3 ))) {
                //merge.remove(i+1);
            }
        }
        
        for(int i=0; i<merge.size(); i++) {
            mrg.append(merge.get(i));
        }
        
        //System.out.println("merged string: " + mrg.toString());

        return mrg.toString();
    }
    
    //Tokenize the Ascii tablature
    private ArrayList<String> Tokenizer(String line, boolean finalString) {
        
        line = line.replace(" ", "");
        
        ArrayList<String> list = new ArrayList<String>();
        if(!finalString) {
            System.out.println(line);
            String key = String.valueOf(line.charAt(0));
            line = line.substring(1,line.length());
            list.add(key);
        }
  
        ArrayList<String> tokens = new ArrayList<String>();            

        String[] tkns = line.split("(?=(?!^)-)(?<!-)|(?!-)(?<=-)");
        int index = 0;
        boolean flagFirstTime = true;
        for(String tmp : tkns) {            
                
                if(!tmp.contains("-")) {
                    index += line.substring(index, line.length()).indexOf(tmp);
                    //System.out.println("1--->"+ index);
                    tokens.add(tmp);
                    index += tmp.length();
                    //System.out.println("2--->"+ index);
                } else {
                   tokens.add(tmp); 
                }
                
                //System.out.println("tokens: "+tmp);
        }
        
        for(String str:tokens) {
            list.add(str);
        }
  
        return list;
    }
    
    private String valueAssin(String str) {
        StringBuilder sb;
        int keyvalue=0;
        
        String t = str.replaceAll("[^a-zA-Z]", ""); 
        int v = Integer.parseInt(str.replaceAll("[^0-9]", ""));
        //System.out.println(t);
        
        switch (t) {
            case "A": keyvalue += 57;
                      break;
            case "A#": keyvalue += 58;
                      break;
            case "B": keyvalue += 59;
                      break;
            case "C": keyvalue += 60;
                      break;
            case "C#": keyvalue += 61;
                      break;
            case "D": keyvalue += 62;
                      break;
            case "D#": keyvalue += 63;
                      break;
            case "E": keyvalue += 64;
                      break;
            case "F": keyvalue += 65;
                      break;
            case "F#": keyvalue += 66;
                      break;
            case "G": keyvalue += 67;
                      break;
            case "G#": keyvalue += 68;
                      break;
            case "a": keyvalue += 69;
                      break;
            case "a#": keyvalue += 70;
                      break;
            case "b": keyvalue += 71;
                      break;
            case "c": keyvalue += 72;
                      break;
            case "c#": keyvalue += 73;
                      break;
            case "d": keyvalue += 74;
                      break;
            case "d#": keyvalue += 75;
                      break;
            case "e": keyvalue += 76;
                      break;
            case "f": keyvalue += 77;
                      break;
            case "f#": keyvalue += 78;
                      break;
            case "g": keyvalue += 79;
                      break;
            case "g#": keyvalue += 80;
                      break;            
        }
        
        sb = new StringBuilder();
        //System.out.println("int-> "+ str);
        sb.append("[");
        sb.append((v + keyvalue));
        sb.append("]");
        
        return sb.toString();
    }
    
    private String timeAssin(String str) {
        String time = null;
        int duration;
        
        if(str.contains("-")) {
            duration = str.length();
            switch (duration) {
                case 0:
                    time = "s";
                    break;
                case 1:
                    time = "i";
                    break;
                case 2:
                    time = "q";
                    break;
                case 3:
                    time = "h";
                    break;
                case 4:
                    time = "w";
                    break;
                default:
                    time = "h";
                    break;
            }
            
        } else {
            time = "s";
        }
        
        return time;
    }
    
    
    //Transform the text to Integer values which represent each note
    private String PatternConstructor( ArrayList<String> tokens) {
        ArrayList<String> valueList = new ArrayList<String>();
        StringBuilder pattern = new StringBuilder();
        
        boolean flag = false;
        
        int i=0;
        String time;
        while(i < tokens.size()) {
            int temp = 1;
            if(tokens.get(i).contains("+")) {
                //System.out.println("Chord");
                
                if(i+1 < tokens.size()){ 
                    time = timeAssin(tokens.get(i+1));
                    temp++;
                } else {
                    time = " ";
                }
                
                for(String note : tokens.get(i).split("\\+")) {
                    pattern.append(valueAssin(note));
                    pattern.append(time);
                    pattern.append("+");
                }
                pattern.deleteCharAt(pattern.length()-1);
                pattern.append(" ");
            } else if (!tokens.get(i).contains("-")) {
                if (i + 1 < tokens.size()) {
                    time = timeAssin(tokens.get(i + 1));
                    temp++;
                } else {
                    time = " ";
                }
                pattern.append(valueAssin(tokens.get(i)));
                pattern.append(time);
                pattern.append(" ");
            }
            i += temp;
        }
        return pattern.toString();
        
    }
}

