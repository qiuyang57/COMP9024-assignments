import java.io.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;


public class CompactCompressedSuffixTrie {
	private String phrase;
	private Node root;
	private int leafEnd;
	private Node iNode;
	private Node aNode;
	private int aEdge;
	private int aLength;
	private int remainSuffix;
    /**
     * Read DNA string from file
     * The time complexity is O(s) for s is the length of string in file
     */
    public static String read_file(String file){
    StringBuilder Temp= new StringBuilder();
    StringBuilder data= new StringBuilder();
    Scanner s;
        try {
            s = new Scanner(new File(file));
            while(s.hasNext()){
                Temp.append(s.next());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not exists");
        }
        for(int i=0;i<Temp.length();i++){
            char temp=Temp.charAt(i);
            if(temp=='A' || temp=='C' || temp=='G' || temp=='T'){
                data.append(temp);
            }
            else{
                System.out.println("Invalid character");
            }
        }
        return data.toString();
    }
    /**
     * Ukkonen's algorithm to construct suffix tree
	 * Ukkonen, Esko. "On-line construction of suffix trees." Algorithmica 14.3 (1995): 249-260.
	 * https://www.cs.helsinki.fi/u/ukkonen/SuffixT1withFigs.pdf
     * Using constant time to find the correct position for character for each character
     * The total time complexity is O(n) where n is the length of String S.
     */
    public CompactCompressedSuffixTrie(String f){
    	String data = read_file(f)+'$';
    	root = new Node(-1);
    	leafEnd = -1;
    	aNode = root;
    	aEdge = -1;
    	aLength =0;
    	remainSuffix =0;
    	if(Objects.equals(data, "")){
    		System.out.println("Empty file");
    		System.exit(0);
    	}
    	else construct_suffix_tree(data);
    }

    public void construct_suffix_tree(String str) {
    	phrase=str;
		for(int i=0;i<phrase.length();i++){
			index_build(i);
		}
	}

    private void index_build(int i){
    	leafEnd=i;
    	remainSuffix +=1;
    	iNode =null;
    	while(remainSuffix > 0){
    		if (aLength ==0){
    			aEdge =i;
    		}
    		if(!aNode.edge_map.containsKey(phrase.charAt(aEdge))){
    			aNode.edge_map.put(phrase.charAt(aEdge), new Node(i));
    			if (iNode !=null){
    				iNode.suf_link = aNode;
    				iNode =null;
    			}
    		}
    		else{
    			Node next = aNode.edge_map.get(phrase.charAt(aEdge));
    			if (goDown(next)){
    				continue;
    			}
    			if(phrase.charAt(next.start+ aLength)==phrase.charAt(i)){
    				if (iNode !=null && aNode !=root){
    					iNode.suf_link = aNode;
    					iNode =null;
    				}
    				aLength +=1;
    				break;
    			}
    			int splitEnd=next.start+ aLength -1;
    			Node split=new Node(next.start,splitEnd);
    			aNode.edge_map.put(phrase.charAt(aEdge), split);
    			split.edge_map.put(phrase.charAt(i), new Node(i));
    			next.start+= aLength;
    			split.edge_map.put(phrase.charAt(next.start), next);
    			if(iNode !=null){
    				iNode.suf_link =split;
    			}
    			iNode =split;
    		}
    		remainSuffix -=1;
    		if (aNode ==root && aLength >0){
    			aLength -=1;
    			aEdge =i- remainSuffix +1;
    		}
    		else if(aNode !=root){
    			aNode = aNode.suf_link;
    		}
    	}
    }

    private boolean goDown(Node n){
    	if (aLength >= edge_len(n)){
    		aNode =n;
    		aEdge += edge_len(n);
    		aLength -= edge_len(n);
    		return true;
    	}
    	return false;
    }

    private int edge_len(Node n) {
		return n.end != -1 ? n.end-n.start+1 : leafEnd-n.start+1;
	}

	// Node class stored suffix and link
   class Node{
	   Map<Character,Node> edge_map;
	   Node suf_link;
	   int suf_index;
	   int start;
	   int end;
	   Node(int start){
		   this.start = start;
		   this.end = -1;
		   edge_map = new HashMap<>();
		   suf_link = root;
		   suf_index = -1;
	   }
	   Node(int start,int end){
		   this.start = start;
		   this.end = end;
		   edge_map = new HashMap<>();
		   suf_link = root;
		   suf_index = -1;
	   }
   }
   /**
    * find String description
    *
    * In suffix tree, using hash map to find the node starting with the first character of String S
    * hashmap.get() is O(1)
    * The time complexity is O(s)
	* Where s is the length of String
    */
   public int findString(String s){
		char[] char_of_string=s.toCharArray();
		char current_char;
		int end_index=0;
		int start_of_node=0;
		int end_of_node=-1;
		Node current_node=root;
		for(int i =0;i<char_of_string.length;i++){
			current_char=char_of_string[i];
			if(start_of_node>end_of_node){
				current_node=current_node.edge_map.get(current_char);
				if(current_node==null){
					return -1;
				}
				else{
					start_of_node=current_node.start;
					if(current_node.end==-1){
						end_of_node=phrase.length()-1;
					}
					else{
						end_of_node=current_node.end;
					}
				}
			}
			if(current_char==phrase.charAt(start_of_node)){
				end_index=start_of_node;
				start_of_node+=1;				
			}
			else{
				return -1;
			}
		}
		return end_index-char_of_string.length+1;
	}
    /**
     * kLongestSubstrings
     *
     * Finding the longest Substring is O(mn) by dynamic programming.
	 * Where m is the length of S1 and n is the length of S2
     * Using String.substring to remove the longest substring from both S1 and S2 is O(m+n).
     * Since it will be repeated k times, the total time complexity is O(k*(mn+m+n)) = O(kmn)
     */
   public static void kLongestSubstrings(String f1, String f2, String f3, int k){
	   String s1= read_file(f1);
	   String s2= read_file(f2);
	   if(Objects.equals(s1, "") || Objects.equals(s2, "")){
		   System.out.println("Empty file.");
		   System.exit(0);
	   }
	   FileWriter output;
	   File file= new File(f3);
	   try{
		  if(file.exists()){
			  output=new FileWriter(f3,false);
		  }
		  else{
			  output=new FileWriter(f3);
		  }
		  }
	   catch (IOException e) {
		   System.out.println("Failed create or find output file.");
		   return;
	   }
	   for(int i=0;i<k;i++){
		   int[] lcs_index= get_longest_common_string(s1, s2);
		   String longest_common_string=i+1+": ";
		   if(lcs_index[2]!=0){
			   	longest_common_string+=s1.substring(lcs_index[0],lcs_index[0]+lcs_index[2]);
				try {
					output.write(longest_common_string);
					if(i!=k-1)output.write(System.getProperty("line.separator"));
				} catch (IOException e) {
					System.out.println("Failed write to the file");
				}
			   s1=s1.substring(0,lcs_index[0])+s1.substring(lcs_index[0]+lcs_index[2],s1.length());
			   s2=s2.substring(0,lcs_index[1])+s2.substring(lcs_index[1]+lcs_index[2],s2.length());
		   }
		   else{
			   try {
					output.write(longest_common_string);
					if(i!=k-1)output.write(System.getProperty("line.separator"));
				} catch (IOException e) {
					System.out.println("Failed write to the file.");
				}
		   }
	   }
	  try {
			output.close();
		} catch (IOException e) {
			System.out.println("Failed close the file.");
	  }
   }
   /**
    * get_longest_common_string
    *
    * Dynamic programming:
	* Two loop to construct a matrix
	* storing the information of common substrings.
    * The time complexity is O(mn).
	* Where m is the length of s1 and n is the length of s2
    */
   public static int[] get_longest_common_string(String s1, String s2) {
	   int[][] m= new int[s1.length()+1][s2.length()+1];
	   int[] lcs_index={0,0,0};
	   for(int i=0;i<s1.length()+1;i++){
		   for(int j=0;j<s2.length()+1;j++){
			   if(i==0 || j==0){
				   m[i][j]=0;
			   }
			   else if (s1.charAt(i-1)==s2.charAt(j-1)){
				   m[i][j]=m[i-1][j-1]+1;
				   if(m[i][j]>lcs_index[2]){
					   lcs_index[2]=m[i][j];
					   lcs_index[0]=i-m[i][j];
					   lcs_index[1]=j-m[i][j];
				   }
			   }
			   else{
				   m[i][j]=0;
			   }
		   }
	   }
	   return lcs_index;
   }

	public static void main(String args[]) throws Exception{

		CompactCompressedSuffixTrie trie1 = new CompactCompressedSuffixTrie("file1.txt");

		System.out.println("ACTTCGTAAG is at: " + trie1.findString("ACTTCGTAAG"));

		System.out.println("AAAACAACTTCG is at: " + trie1.findString("AAAACAACTTCG"));

		System.out.println("ACTTCGTAAGGTT : " + trie1.findString("ACTTCGTAAGGTT"));

		CompactCompressedSuffixTrie.kLongestSubstrings("file2.txt", "file3.txt", "file4.txt", 6);
	}
}
