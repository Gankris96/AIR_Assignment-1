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
        }

        return result;
    }

    /*
     * Perform a phrase query search
     */
    public static ArrayList<Integer> phraseQuery(String s){

        ArrayList<Integer> result = new ArrayList<>();



        return result;
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
        System.out.println(singleWord("old"));
        System.out.println(intersection("snake", "frog"));
        System.out.println(union("snake", "frog"));
    }
}


class postingListEntry{

    private int docID;
    private ArrayList<Integer> positions;

    public postingListEntry(int docID){

        this.docID = docID;
        positions = new ArrayList<Integer>();
    }

    public postingListEntry(int docID, ArrayList<Integer> positions){

        this.docID = docID;
        this.positions = positions;
    }

    public void addPosition(int pos){

        if(!positions.contains(pos)){

            positions.add(pos);
        }

    }

    public void sortPositions(){

        Collections.sort(positions);
    }

    public int getDocID(){

        return docID;
    }

    public ArrayList<Integer> getPositionList(){

        return positions;
    }

    @Override
    public String toString(){

        return docID + " => " + positions.toString();
    }
}

class SortByDocID implements Comparator<postingListEntry>{

    public int compare(postingListEntry e1, postingListEntry e2){

        return e1.getDocID() - e2.getDocID();
    }

}