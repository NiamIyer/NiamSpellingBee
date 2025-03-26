import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Spelling Bee
 *
 * This program accepts an input of letters. It prints to an output file
 * all English words that can be generated from those letters.
 *
 * For example: if the user inputs the letters "doggo" the program will generate:
 * do
 * dog
 * doggo
 * go
 * god
 * gog
 * gogo
 * goo
 * good
 *
 * It utilizes recursion to generate the strings, mergesort to sort them, and
 * binary search to find them in a dictionary.
 *
 * @author Zach Blick, Niam Iyer
 *
 * Written on March 5, 2023 for CS2 @ Menlo School
 *
 * DO NOT MODIFY MAIN OR ANY OF THE METHOD HEADERS.
 */
public class SpellingBee {

    private String letters;
    private ArrayList<String> words;
    public static final int DICTIONARY_SIZE = 143091;
    public static final String[] DICTIONARY = new String[DICTIONARY_SIZE];

    public SpellingBee(String letters) {
        this.letters = letters;
        words = new ArrayList<String>();
    }


    public void generate() {
        // Calls the recursive overloaded function with the blank word and the remaining letters
        generate("", letters);
    }

    private void generate(String word, String remaining) {
        // If there are no letters remaining, add the word to the list and return
        if (remaining.length() < 1) {
            words.add(word);
            return;
        }
        // Makes sure that there are letters in the word before returning
        if (!word.equals("")) {
            words.add(word);
        }
        // For each letter in the remaining letters, add the remaining letter to the word
        // Recursively call the method but with the new sequence of letters for word and the other letters in remaining
        for (int i = 0; i < remaining.length(); i++) {
            String newWord = word + remaining.charAt(i);
            generate(newWord, remaining.substring(0,i) + remaining.substring(i+1));
        }



    }


    public void sort() {
        // Calls the helper function split which contains the lowest and highest indices, as well as the original word
        words = split(words, 0, words.size() - 1);

    }

    private ArrayList<String> split(ArrayList<String> arr1, int low, int high) {
        // If there is one index in the ArayList, return a copy of the original Arraylist
        if (low == high) {
            ArrayList<String> array = new ArrayList<String>();
            array.add(arr1.get(low));
            return array;
        }
        // Calls merge on two recursive calls of split
        // These recursive calls split the ArrayList into two ArrayLists
         return merge(split(arr1, low, (high+low)/2), split(arr1, (high+low)/2 + 1, high));


    }

    private ArrayList<String> merge(ArrayList<String> arr1, ArrayList<String> arr2) {
        // New ArrayList which will contain the sorted Strings
        ArrayList<String> arr3 = new ArrayList<String>();
        // Variables to hold the indices of each ArrayList
        int indexOne = 0;
        int indexTwo = 0;
        // While the indices are less than the length of the ArrayLists
        while (indexOne < arr1.size() && indexTwo < arr2.size()) {
            // Check if the first ArrayList at indexOne comes later in the alphabet than
            // The second ArrayList at indexTwo
            if (arr1.get(indexOne).compareTo(arr2.get(indexTwo)) > 0) {
                // Adds the String from the second ArrayList at indexTwo to the new ArrayList
                arr3.add(arr2.get(indexTwo));
                // Increments index two so the next index of the second ArrayList is checked
                indexTwo ++;
            }
            // If the two Strings are the same or if the first String comes before the second String
            else {
                // Add the value from the first ArrayList to the new ArrayList and increments the first index
                arr3.add(arr1.get(indexOne));
                indexOne ++;
            }
        }
        while (indexOne < arr1.size()) {
            arr3.add(arr1.get(indexOne));
            indexOne ++;
        }
        while (indexTwo < arr2.size()) {
            arr3.add(arr2.get(indexTwo));
            indexTwo ++;
        }
        return arr3;
    }

    // Removes duplicates from the sorted list.
    public void removeDuplicates() {
        int i = 0;
        while (i < words.size() - 1) {
            String word = words.get(i);
            if (word.equals(words.get(i + 1)))
                words.remove(i + 1);
            else
                i++;
        }
    }


    public void checkWords() {
        // For each word, if the search method can't find it in the dictionary, remove it
        for (int i = 0; i < words.size(); i++) {
            if (!search(words.get(i), 0, DICTIONARY_SIZE - 1)) {
                words.remove(i);
                // Decrements i if a word is removed because all the numbers after will shift down an index
                i --;
            }
        }

    }

    public boolean search(String word, int low, int high) {
        // Base case which checks if the word is found, return true
        if (word.compareTo(DICTIONARY[(low+high)/2]) == 0) {
            return true;
        }
        // Base case which checks if the lowest index in the dictionary is higher than
        // The highest index in the dictionary
        if (low > high) {
            return false;
        }
        // Recursive step which would check the later half of the dictionary if the word is
        // Farther down the alphabet than the midpoint
        if (word.compareTo(DICTIONARY[(low + high)/2]) > 0) {
            return search(word, (low+high)/2 + 1, high);
        }
        // If not, it checks the earlier half of the alphabet
        else {
            return search(word, low, (low+high)/2 - 1);
        }
    }

    // Prints all valid words to wordList.txt
    public void printWords() throws IOException {
        File wordFile = new File("Resources/wordList.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(wordFile, false));
        for (String word : words) {
            writer.append(word);
            writer.newLine();
        }
        writer.close();
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    public SpellingBee getBee() {
        return this;
    }

    public static void loadDictionary() {
        Scanner s;
        File dictionaryFile = new File("Resources/dictionary.txt");
        try {
            s = new Scanner(dictionaryFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open dictionary file.");
            return;
        }
        int i = 0;
        while(s.hasNextLine()) {
            DICTIONARY[i++] = s.nextLine();
        }
    }

    public static void main(String[] args) {

        // Prompt for letters until given only letters.
        Scanner s = new Scanner(System.in);
        String letters;
        do {
            System.out.print("Enter your letters: ");
            letters = s.nextLine();
        }
        while (!letters.matches("[a-zA-Z]+"));

        // Load the dictionary
        SpellingBee.loadDictionary();

        // Generate and print all valid words from those letters.
        SpellingBee sb = new SpellingBee(letters);
        sb.generate();
        sb.sort();
        sb.removeDuplicates();
        sb.checkWords();
        try {
            sb.printWords();
        } catch (IOException e) {
            System.out.println("Could not write to output file.");
        }
        s.close();
    }
}
