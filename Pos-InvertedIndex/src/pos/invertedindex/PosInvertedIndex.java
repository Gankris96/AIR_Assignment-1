package pos.invertedindex;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 *
 * @author Ganesh 24/8/2017
 */
public class PosInvertedIndex {

    static Map<String, Integer> inputFilesList = new HashMap<>();
    static HashMap<String,List<Map<Integer,Set<Integer>>>> invertedIndex= new HashMap<>();

    /**
     * This function performs the initial setup of reading the inputfiles
     * directory and placing all the .txt files into a Map. This can be used to
     * read the files
     */
    public static void init() {
//    check what is the current directory to make sure it is the right directory

//     System.out.println("Working Directory = " + System.getProperty("user.dir"));
//      traverse the directory for all files and put them into a Hashmap<String,DocID>
        File folder = new File("inputfiles");
        File[] listOfFiles = folder.listFiles();

        int docId = 1;
        for (File file : listOfFiles) {
            if (file.isFile()) {
//                put the relative path of the file as the key in the map
                 inputFilesList.put(file.getPath().replace("\\","/"), docId++);
            }

        }
//        verify if list is created correctly
        for (String file : inputFilesList.keySet()) {

            System.out.println("FileName: " + file + "DocID : " + inputFilesList.get(file));
        }
    }

    /*
    * Method to read all the files in inputFiles and generate an inverted index
    *
    * */
    public static void createIndex(){
        Scanner wordFile=null;
        String res="";
        String word;     // A word read from the file
        Integer count;   // The number of occurrences of the word
        int counter = 0;
        int docCounter = 0;
        int docC=0;
        int flag=0;
        //**FOR LOOP TO READ THE DOCUMENTS*
        Map<Integer,Set<Integer>> documentMap=null;
        Set<Integer> posSet=null;
        List<Map<Integer,Set<Integer>>> valuePostingList=null;
        List<Map<Integer,Set<Integer>>> valuePostingListIterator=null;

        for(String filepath:inputFilesList.keySet())
        { //start of for loop [*
            int posCounter=1;
            docC++;
            docCounter=inputFilesList.get(filepath);
            try
            {
                wordFile = new Scanner(new FileReader(filepath));
                res+="rwfsuccess\n";
                while (wordFile.hasNext( ))
                {

                    // Read the next word and get rid of the end-of-line marker if needed:
                    word = wordFile.next( );

                    // Convert the Word to lower case.
                    word = word.toLowerCase();

                    word = word.replaceAll("[^a-zA-Z0-9\\s]", "");
                    //System.out.println(word);
                    if(invertedIndex.containsKey(word)){
                        //if the word is a key of the HashMap then
                        valuePostingList=invertedIndex.get(word);
                        valuePostingListIterator=invertedIndex.get(word);
                        for(Map<Integer,Set<Integer>> innerMap:valuePostingListIterator){
                            //if the value list for this Key has the docID then just add the position to Inner Map PositionList
                            if(innerMap.containsKey(docCounter)){
                                innerMap.get(docCounter).add(posCounter);
                            }else{
                                //if the docID for that word does not exist , then add it to the value List along with its required value
                                documentMap=new HashMap<>();
                                posSet=new TreeSet<>();
                                posSet.add(posCounter);
                                documentMap.put(docCounter,posSet);
                                //do this to prevent concurrent adding
                                //we cannot add to the same list that we are iterating
                                flag=1;
                            }
                        }
                    }else{
                        //if word is not yet seen then add it to the map
                        documentMap=new HashMap<>();
                        posSet=new TreeSet<>();
                        posSet.add(posCounter);
                        documentMap.put(docCounter,posSet);
                        List<Map<Integer,Set<Integer>>> postingList=new ArrayList<>();
                        postingList.add(documentMap);
                        invertedIndex.put(word,postingList);
                    }

                    posCounter++;
                    //prevents the Concurrent addition Exception
                    if(flag==1){
                        valuePostingList.add(documentMap);
                        flag=0;
                    }
                }
            }
            catch (FileNotFoundException e)
            {
                System.err.println(e);
                res+="rwfException\n";
                System.out.println("-1");
            }
        }
        for(String keyWord:invertedIndex.keySet()){
                System.out.println("Term :"+keyWord+ "\nValue List:"+invertedIndex.get(keyWord).toString());
            }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  {
        // TODO code application logic here
        //read the inputfiles folder and generate Hashmap
        init();
        //create the hashMap
        createIndex();

    }

}
