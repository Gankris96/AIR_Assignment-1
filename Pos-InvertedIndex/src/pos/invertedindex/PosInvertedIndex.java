package pos.invertedindex;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 *
 * @author Ganesh 24/8/2017
 */

public class PosInvertedIndex{

    static Map<String, Integer> inputFilesList = new HashMap<>();
    static Map<String, List<postingListEntry>> invertedIndex = new HashMap<>();

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

    public static void createIndex() throws FileNotFoundException{

        for(String filepath : inputFilesList.keySet()){

            int posCounter = 1;
            int docID = inputFilesList.get(filepath);
            Scanner wordFile = new Scanner(new FileReader(filepath));

            while(wordFile.hasNext()){

                String word = wordFile.next();
                word = word.toLowerCase();
                word = word.replaceAll("[^a-zA-Z0-9\\s]", "");

                /*
                 * Check whether the word is already present in the hashmap
                 */
                if(invertedIndex.containsKey(word)){

                    /*
                     * If the word is already present, obtain a reference to its posting list
                     */

                    List<postingListEntry> postingList = invertedIndex.get(word);
                    /*
                     * This variable will be set to true if the current document ID is present in the word's posting list
                     */
                    boolean found = false;

                    /*
                     * Loop that checks whether the current document ID is present in the word's posting list
                     */
                    for(postingListEntry e : postingList){

                        if(e.getDocID() == docID){

                            found = true;
                            e.addPosition(posCounter);
                            break;
                        }
                    }

                    /*
                     * If the current document ID is present in the word's posting list, create a new posting list object
                     * and with the current document ID and add it to the posting list
                     */

                    if(!found){

                        postingListEntry newEntry = new postingListEntry(docID);
                        newEntry.addPosition(posCounter);
                        postingList.add(newEntry);
                    }
                }

                /*
                 * If the word is not present in the inverted index, add it, along with a new posting list
                 * containing the current document ID
                 */
                else{

                    List<postingListEntry> postingList = new ArrayList<>();
                    postingListEntry newEntry = new postingListEntry(docID);
                    newEntry.addPosition(posCounter);
                    postingList.add(newEntry);
                    invertedIndex.put(word, postingList);
                }

                posCounter++;
            }

            wordFile.close();
        }

        /*
         * Sort all the document IDs in every posting list, to facilitate fast query processing
         */

        for(String word : invertedIndex.keySet()){

            List<postingListEntry> postingList = invertedIndex.get(word);

            Collections.sort(postingList, new SortByDocID());
        }

        /*
         * Display the entire inverted index
         */

        /*
        for(String word : invertedIndex.keySet()){

            System.out.println("Term :" + word);
            System.out.println("Value List: " + invertedIndex.get(word).toString());
        }
        */
    }

    public static ArrayList<Integer> singleWord(String s){

        ArrayList<Integer> result = new ArrayList<>();
        /*
         * If the inverted index does not contain the word, return an empty list
         */
        if(invertedIndex.containsKey(s)){

            /*
             * Traverse the posting list of the word and add each document ID to the result list
             */
            List<postingListEntry> postingList = invertedIndex.get(s);
            for(postingListEntry e : postingList){

                result.add(e.getDocID());
            }
        }

        return result;
    }

    /*
     * Perform an intersection operation on the posting lists of two words
     */
    public static ArrayList<Integer> intersection(String s1, String s2){

        ArrayList<Integer> result = new ArrayList<>();
        /*
         * If the inverted index does not contain either word, return an empty list
         */
        if(invertedIndex.containsKey(s1) && invertedIndex.containsKey(s2)){

            /*
             * Retrieve posting lists for bot words
             */
            List<postingListEntry> postingList1 = invertedIndex.get(s1);
            List<postingListEntry> postingList2 = invertedIndex.get(s2);

            /*
             * Obtain the lengths of the posting lists
             */
            int len1 = postingList1.size();
            int len2 = postingList2.size();
            int i = 0, j = 0;

            /*
             * Traverse the posting lists
             */

            while(i < len1 && j < len2){

                int docID1 = postingList1.get(i).getDocID();
                int docID2 = postingList2.get(j).getDocID();

                /*
                 * If the document IDs match, add it to the result list and advance both pointers
                 */
                if(docID1 == docID2){

                    result.add(docID1);
                    i++;
                    j++;
                }
                /*
                 * If not, advance the pointer of the smaller document ID
                 */
                else if(docID1 < docID2){

                    i++;
                }
                else{

                    j++;
                }

            }
        }
        return result;
    }

    /*
     * Perform a union operation on the posting lists of two words
     */
    public static ArrayList<Integer> union(String s1, String s2){

        ArrayList<Integer> result = new ArrayList<>();
        /*
         * If the inverted index does not contain either word, return an empty list
         */
        if(invertedIndex.containsKey(s1) && invertedIndex.containsKey(s2)){

            /*
             * Retrieve posting lists for bot words
             */
            List<postingListEntry> postingList1 = invertedIndex.get(s1);
            List<postingListEntry> postingList2 = invertedIndex.get(s2);
            /*
             * Obtain the lengths of the posting lists
             */
            int len1 = postingList1.size();
            int len2 = postingList2.size();
            int i = 0, j = 0;

            /*
             * Traverse the posting lists
             */
            while(i < len1 && j < len2){

                int docID1 = postingList1.get(i).getDocID();
                int docID2 = postingList2.get(j).getDocID();

                /*
                 * If the document IDs match, add it to the result list and advance both pointers
                 */

                if(docID1 == docID2){

                    result.add(docID1);
                    i++;
                    j++;
                }
                /*
                 * If not, add the smaller document ID and advance its pointer
                 */
                else if(docID1 < docID2){

                    result.add(docID1);
                    i++;
                }
                else{

                    result.add(docID2);
                    j++;
                }

            }
            while(i<len1){
                result.add(postingList1.get(i).getDocID());
                i++;
            }
            while(j<len2){
                result.add(postingList2.get(j).getDocID());
                i++;
            }
        }

        return result;
    }

    /*
     * Perform a phrase query search
     */
    public static ArrayList<Integer> phraseQuery(String s){
        String[] queryWords=s.split("\\s+");
        ArrayList<List<postingListEntry>> queryWordDocToPos=new ArrayList<>();
        ArrayList<Integer> result=singleWord(queryWords[0].toLowerCase());
        ArrayList<Integer> result2=singleWord(queryWords[1].toLowerCase());
        if(queryWords.length>2) {
            ArrayList<Integer> result3 = singleWord(queryWords[2].toLowerCase());
            result2.retainAll(result3);
        }
        result.retainAll(result2);
        //System.out.println(result.toString());
        for(int i=0;i<queryWords.length;i++){
            if(invertedIndex.containsKey(queryWords[i].toLowerCase())){
//              Traverse the posting list of the word and add each document ID to the result list
                List<postingListEntry> postingList = invertedIndex.get(queryWords[i].toLowerCase());
                //System.out.println(" Output for word["+i+"]:"+postingList.toString());
                queryWordDocToPos.add(postingListEntry.trimmedList(postingList,result));
            }
        }
        /*System.out.println(queryWordDocToPos.toString());
        System.out.println(queryWordDocToPos.size());
        */ArrayList<ArrayList<Integer>> intermediateList=new ArrayList<>();
        Boolean[] resultSet=new Boolean[result.size()];
        Arrays.fill(resultSet,Boolean.FALSE);
        for(int i=0;i<result.size();i++) {
            for (List<postingListEntry> e : queryWordDocToPos) {
                    intermediateList.add(e.get(i).getPositionList());
            }
            //fastest way is to check from smallest posting list so sort according to lengths
            Collections.sort(intermediateList, new Comparator<ArrayList<Integer>>() {
                @Override
                public int compare(ArrayList<Integer> o1, ArrayList<Integer> o2) {
                    return o1.size()-o2.size();
                }
            });
            int counter=1;
            for(Integer integer:intermediateList.get(0)) {
                /*System.out.println("COUNT ="+(counter++)+"-------------------------------------------------------------------");
                System.out.println(intermediateList.get(0).toString());
                System.out.println("Checking "+integer+" and "+ (integer-1)+" and "+(integer+1));
                System.out.println(intermediateList.get(1).toString());
                System.out.println(intermediateList.get(1).contains(integer + 1));
                System.out.println(intermediateList.get(1).contains(integer - 1));*/
                //sort is based on smallest posting list to largest. It is possible that 3rd word list may be smaller than 2nd word so check all cases with OR
                //abc||acb||cab||cba||bca||bac
                if (queryWords.length == 3) {
                    if (intermediateList.get(1).size() != 0 && intermediateList.get(2).size() != 0)
                        if     (intermediateList.get(1).contains(integer + 1) && intermediateList.get(2).contains(integer + 2) ||
                                intermediateList.get(1).contains(integer + 2) && intermediateList.get(2).contains(integer + 1)||
                                intermediateList.get(1).contains(integer - 1) && intermediateList.get(2).contains(integer - 2)||
                                intermediateList.get(1).contains(integer - 1) && intermediateList.get(2).contains(integer + 1)||
                                intermediateList.get(1).contains(integer + 1) && intermediateList.get(2).contains(integer - 1) ||
                                intermediateList.get(1).contains(integer - 2) && intermediateList.get(2).contains(integer - 1) )
                            resultSet[i] = Boolean.TRUE;
                } else if (queryWords.length == 2) {
                    if (intermediateList.get(1).size() != 0){
                        //ab||ba

                        if (intermediateList.get(1).contains(integer + 1) || intermediateList.get(1).contains(integer - 1))
                            resultSet[i] = Boolean.TRUE;
                        }
                       /* for(int k=0;k<resultSet.length;k++){
                            System.out.println("Index "+k+" is "+resultSet[k].booleanValue());
                        }*/
                    }
            }
            intermediateList.clear();
        }
        ArrayList<Integer> resultList = new ArrayList<>();
        for(int i=0;i<resultSet.length;i++)
            if(resultSet[i]==Boolean.TRUE)
                resultList.add(result.get(i));
        return resultList;
    }

    public static void enterQueries(){

        Scanner stdIn = new Scanner(System.in);
        System.out.println("Enter Queries");
        char choice;
        do{

            String query = stdIn.nextLine();

            System.out.println("Continue?(y/n)");
            choice = stdIn.nextLine().charAt(0);

        }while(choice == 'y' || choice == 'Y');

        stdIn.close();
    }

    public static void main(String []args){

        init();
        try {

            createIndex();
        }
        catch (FileNotFoundException e) {

            System.out.println("FILE NOT FOUND EXCEPTION");
        }

        //enterQueries();
        System.out.println("(a) 'Old' "+singleWord("old"));
        System.out.println("(b) 'Snake AND Frog' "+intersection("snake", "frog"));
        System.out.println("(c) 'Snake OR Frog' "+union("snake", "frog"));
        ArrayList<Integer> phraseQ1=phraseQuery("There was a");
        ArrayList<Integer> phraseQ2=phraseQuery("One day");
        System.out.println("(d) 'There was a' "+phraseQ1);
        ArrayList<Integer> resultPhraseQueryWithAND=new ArrayList<>(phraseQ2);
        resultPhraseQueryWithAND.retainAll(phraseQ1);
        System.out.println("(e) 'There was a AND One day' "+resultPhraseQueryWithAND);
    }
}


class postingListEntry {

    private int docID;
    private ArrayList<Integer> positions;

    public postingListEntry(int docID) {

        this.docID = docID;
        positions = new ArrayList<Integer>();
    }

    public postingListEntry(int docID, ArrayList<Integer> positions) {

        this.docID = docID;
        this.positions = positions;
    }

    public void addPosition(int pos) {

        if (!positions.contains(pos)) {

            positions.add(pos);
        }

    }

    public void sortPositions() {

        Collections.sort(positions);
    }

    public int getDocID() {

        return docID;
    }

    public ArrayList<Integer> getPositionList() {

        return positions;
    }

    @Override
    public String toString() {

        return docID + " => " + positions.toString();
    }

    public static List<postingListEntry> trimmedList(List<postingListEntry> entry,ArrayList<Integer> requiredParams) {
        List<postingListEntry> docIDSet = new ArrayList<>();
        //System.out.println(requiredParams.toString());
        for (int i = 0; i < entry.size(); i++) {
            if(requiredParams.contains(entry.get(i).getDocID())) {
                //System.out.println(entry.get(i).toString());
                docIDSet.add(entry.get(i));
            }
        }
        return docIDSet;
    }
}

class SortByDocID implements Comparator<postingListEntry>{

    public int compare(postingListEntry e1, postingListEntry e2){

        return e1.getDocID() - e2.getDocID();
    }

}