import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by qiuya on 6/4/2017.
 */
public class CompactCompressedSuffixTrie {
    /** You need to define your data structures for the compressed trie */
    private String data;
    private Node root;
    private static char sentinel = '$';
    /** Constructor */
    public CompactCompressedSuffixTrie(String f) // Create a compact compressed suffix trie from file f
    {try {
        this.data = "";
        String line;
        BufferedReader br = new BufferedReader(new FileReader(f));
        // read line by line
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            // add lines read into string input
            this.data += line;
        }
        this.data += sentinel;
        br.close();

    } catch (Exception e) {
        System.out.println(f + " does not exist.");
        e.printStackTrace();
    }
        String suffix;
        this.root = new Node();
        // suffixIndex will be set to -1 by default and actual suffix index will
        // be set later for leaves at the end of all phases
        this.root.setStart(-1);
        // after every suffix inserted into suffix tree, decrease length of
        // input by length of suffix without the added sentinel
        for (int i = this.data.length() - 1; i >= 0; i--) {
            suffix = this.data.substring(i);

            // continue with other suffixes when each suffix is inserted into
            // suffix tree
            if (suffix.length() == 0)
                continue;
            this.construct(this.root, suffix, i, 0, false);
        }
    }
    /**
     * Time Complexity:
     *
     * Traversing through the suffix tree will take O(n * log n) n = |node| and
     * reading each character in edges m = |edge|, overall time complexity will
     * be O(mn * log n).
     */

    private void construct(Node currentNode, String suffix, int index, int diffIndex, boolean fork) {
        if (currentNode.getChildren().size() != 0) { // Check children if node has children
            boolean match = false;
            for (Node child : currentNode.getChildren().values()) {
                // Check if it is the correct branch
                if (!(child.getEdge().length() == 0 || suffix.length() == 0)
                        && child.getEdge().charAt(0) == suffix.charAt(0)) {

                    int breakpoint = -1;
                    for (int i = 1; i <= child.getEdge().length(); i++) {
                        try {
                            if (child.getEdge().charAt(i) != suffix.charAt(i)) {
                                breakpoint = i;
                                break;
                            }
                        } catch (Exception e) {
                            breakpoint = i;
                        }
                    }
                    // Cut string for new edge
                    suffix = breakpoint == -1 ? suffix : suffix.substring(breakpoint);
                    if (suffix.length() == 0)
                        continue;

                    // Create new branches for this current child if there is a
                    // match of suffix to belong to
                    match = true;
                    fork = child.getChildren().size() != 0 && breakpoint < child.getEdge().length();
                    this.construct(child, suffix, index, breakpoint, fork);
                    currentNode.setStart(index);
                    break;
                }
            }

            // Create a new branch when nothing matches and suffix length is at least 1
            if (!match && suffix.length() >= 1) {
                Node child = new Node();
                child.setEdge(suffix);
                child.setStart(index);
                currentNode.addChild(suffix.charAt(0), child);
                currentNode.setStart(index);
            }
        } else {

            if (fork) {
                String edge = currentNode.getEdge();

                String updatedEdge = edge.substring(0, diffIndex);
                String newEdge = edge.substring(diffIndex);

                Node child1 = new Node();
                child1.setEdge(newEdge);
                child1.setStart(currentNode.getStart());
                child1.cloneChild(currentNode);
                currentNode.forkChild(newEdge.charAt(0), child1);

                Node child2 = new Node();
                child2.setEdge(suffix);
                child2.setStart(index);
                currentNode.addChild(suffix.charAt(0), child2);

                currentNode.setEdge(updatedEdge);
                currentNode.setStart(index);

            } else {
                // empty node case
                if (currentNode == this.root) { // Append a new child to node if
                    // current node is root

                    Node child = new Node(); // this only happens once
                    child.setEdge(suffix);
                    child.setStart(index);
                    currentNode.addChild(suffix.charAt(0), child);
                } else { // fork if the current node is a child
                    String edge = currentNode.getEdge();

                    String updatedEdge = edge.substring(0, diffIndex);
                    String newEdge = edge.substring(diffIndex);

                    Node child1 = new Node();
                    child1.setEdge(newEdge);
                    child1.setStart(currentNode.getStart());
                    currentNode.addChild(newEdge.charAt(0), child1);

                    Node child2 = new Node();
                    child2.setEdge(suffix);
                    child2.setStart(index);
                    currentNode.addChild(suffix.charAt(0), child2);

                    currentNode.setEdge(updatedEdge);
                    currentNode.setStart(index);
                }
            }
        }
    }

    /** Method for finding the first occurrence of a pattern s in the DNA sequence */
    public int findString( String s ) {
        Node currentNode = this.root;
        int index = -1;
        boolean inEdge = false;
        int edgeIndex = 0;

        // terminate quickly if length of substring = input length
        if (s.length() == this.data.length())
            return 0;
        // Traverse down the tree with each character as guidance
        for (int i = 0; i < s.length(); i++) {
            // If length of edge > 1 character,
            // follow this edge character by character
            if (inEdge) {
                try {
                    // Stop when there is a mismatch
                    if (s.charAt(i) == currentNode.getEdge().charAt(edgeIndex)) {
                        edgeIndex++;
                        continue;
                    } else {
                        return -1;
                    }
                } catch (Exception e) {
                    // when out of range, go back 1 step and
                    // try next child node's edge
                    inEdge = false;
                    i--;
                }
            } else {
                try {
                    currentNode = currentNode.getChildren().get(s.charAt(i));
                    if (currentNode.getEdge().length() > 1) {
                        inEdge = true;
                        edgeIndex = 1;
                    }

                    index = currentNode.getStart();
                } catch (Exception e) {
                    // If there is no match found
                    return -1;
                }
            }
        }
        return index;
    }

    /**
     * PART THREE:
     * Method for computing the degree of similarity of two DNA sequences stored
     * in the text files f1 and f2.
     */

    /**
     * The longest common subsequence (LCS) problem consists in finding the
     * longest subsequence common to two (or more) sequences. It differs from
     * problems of finding common substrings: unlike substrings, subsequences
     * are not required to occupy consecutive positions within the original
     * sequences.
     *
     * It is used by the different utility, by Git for reconciling multiple
     * changes, etc.
     *
     * This method currently implements the dynamic programming approach,
     * which has a space requirement O(mn).
     */

    private static String readFromFile(String file) {
        String string = "";
        try {
            // File1
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(file + ".txt"));
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                string += line;
            }
            reader.close();

        } catch (Exception e) {
            System.out.println(file + " does not exist.");
            e.printStackTrace();
        }

        return string;
    }

    private static void writeToFile(String input, String file) throws IOException {
        // check whether file exists to be written to
        File writeFile = new File(file + ".txt");
        File printFile = new File(file); // for printing without the txt file extension
        try {
            if (!writeFile.exists()) { // if file does not exist
                try {
                    // create file to be written to if it does not exist
                    writeFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // if file already exists
                System.out.println(printFile + " already exists.");
                return;
            }

            FileWriter fw = null;
            BufferedWriter bw = null;
            // create FileWriter getting name of File from writeFile object
            fw = new FileWriter(writeFile.getName(), true);
            // create BufferedWriter to write through
            bw = new BufferedWriter(fw);

            /**
             * Write the input string containing appended LCS into file all
             * at once, this is done only when file does not exist.
             */
            bw.write(input);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Time Complexity:
     *
     * Creating the matrix will take O(mn) n = |f1| and m = |f2|, and recovering
     * the LCS string from the matrix will take O(max(m,n)).
     *
     * Therefore, overall time complexity will be O(mn).
     */

    public static float similarityAnalyser(String f1, String f2, String f3) throws IOException {

        /**
         * Computing the longest common subsequence of the two DNA sequences
         * stored in the text files f1 and f2, respectively, and writing it to
         * the file f3.
         */
        // reading the inputs from the 2 files
        f1 = readFromFile(f1);
        f2 = readFromFile(f2);

        int M = f1.length();
        int N = f2.length();

        // opt[i][j] = length of LCS of x[i..M] and y[j..N]
        int[][] opt = new int[M + 1][N + 1];

        // compute length of LCS and all sub-problems via dynamic programming
        for (int i = M - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                if (f1.charAt(i) == f2.charAt(j))
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                else
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
            }
        }

        // recover LCS and print it to standard output
        String LCS = "";
        int i = 0, j = 0;
        while (i < M && j < N) {
            if (f1.charAt(i) == f2.charAt(j)) {
                LCS += f1.charAt(i);
                i++;
                j++;
            } else if (opt[i + 1][j] >= opt[i][j + 1]) {
                i++;
            } else {
                j++;
            }
        }

        // write string to file
        writeToFile(LCS, f3);

        /**
         * Returning the degree of similarity of two DNA sequences stored in the
         * text files f1 and f2.
         */
        // Minimum value for comparison ratio
        final double MIN_RATIO = 0.0;
        // Maximum value for comparison ratio
        final double MAX_RATIO = 1.0;

        if (f1 == null || f2 == null) {
            float min_ratio = (float) MIN_RATIO; // convert double to float
            return min_ratio;
        }

        if (f1.isEmpty() && f2.isEmpty()) {
            float max_ratio = (float) MAX_RATIO; // convert double to float
            return max_ratio;

        } else if (f1.isEmpty() || f2.isEmpty()) {
            float min_ratio2 = (float) MIN_RATIO; // convert double to float
            return min_ratio2;
        }

        // get the percentage match against the longer of the 2 strings, where |LCS(f1,f2)|/max{|f1|,|f2|}
        float similarity = (float) LCS.length() / ((f1.length() > f2.length()) ? f1.length() : f2.length());
        return similarity;
    }

}

class Node {

    // fields of Node class
    private Map<Character, Node> children;
    private String edge;
    private int start;

    // empty constructor with some set parameters, getters and setters used to
    // update parameters
    public Node() {
        children = new HashMap<>();
        edge = "";
    }

    // getters and setters to set fields for Node objects
    public String getEdge() {
        return this.edge;
    }

    public void setEdge(String edge) {
        this.edge = edge;
    }

    public int getStartIndex() {
        return this.start;
    }

    public void setStartIndex(int start) {
        this.start = start;
    }

    public Map<Character, Node> getChildren() {
        return this.children;
    }

    public void addChild(Character key, Node child) {
        this.children.put(key, child);
    }

    public void forkChild(Character key, Node child) {
        this.children.clear();
        this.children.put(key, child);
    }

    public void cloneChild(Node clone) {
        for (Node child : clone.getChildren().values()) {
            this.children.put(child.getEdge().charAt(0), child);
        }
    }


    /** Method for finding k longest common substrings of two DNA sequences stored
     in the text files f1 and f2 */
    public static void kLongestSubstrings(String f1, String f2, String f3, int k)
    { }

    public static void main(String args[]) throws Exception{
    /* Construct a compact compressed suffix trie named trie1
    */
        CompactCompressedSuffixTrie trie1 = new CompactCompressedSuffixTrie("file1.txt");

        System.out.println("ACTTCGTAAG is at: " + trie1.findString("ACTTCGTAAG"));

        System.out.println("AAAACAACTTCG is at: " + trie1.findString("AAAACAACTTCG"));

        System.out.println("ACTTCGTAAGGTT : " + trie1.findString("ACTTCGTAAGGTT"));

//        CompactCompressedSuffixTrie.kLongestSubstrings("file2.txt", "file3.txt", "file4.txt", 6);
    }
}
