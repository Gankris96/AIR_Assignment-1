package pos.invertedindex;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ganesh 24/8/2017
 */
public class PosInvertedIndex {

    static Map<String, Integer> inputFilesList = new HashMap<>();

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
                System.out.println(file.getPath());
//                put the relative path of the file as the key in the map
                inputFilesList.put(file.getPath(), docId++);
            }

        }
//        verify if list is created correctly
        for (String file : inputFilesList.keySet()) {

            System.out.println("FileName: " + file + "DocID : " + inputFilesList.get(file));
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //read the inputfiles folder and generate Hashmap
        init();
        //create the hashMap 
    }

}
