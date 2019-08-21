import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by qiuyang on 3/28/2017.
 * COMP9024
 */



public class MyDlist extends DList {
//    Extend the length of a array by 2 times
    private static String[] arrayExtend(String[] a){
        String[] c = new String[2*a.length];
        for(int i=0;i<a.length;i++){
            c[i] = a[i];
        }
        return c;
    }

//    Read file/SystemIO get the String Array
    private static String[] readIO(String filename) {
        String[] values = new String[4];
        String current;
        int i = 0;
        try {
            Scanner s;
            if (filename.equals("stdin")) {
                s = new Scanner(System.in);
                while (s.hasNextLine() && !(current = s.nextLine()).equals("")) {
                    if (i == values.length) {
                        values = arrayExtend(values);
                    }
                    values[i] = current;
                    i++;
                }
            }
            else{
                s = new Scanner(new BufferedReader(new FileReader(filename)));
                while (s.hasNext()&&((current=s.next())!=null)) {
                    if (i == values.length) {
                        values = arrayExtend(values);
                    }
                    values[i] = current;
                    i++;
                }
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return values;
    }

//    Build a Array from DList
    private static String[] DListToArray(MyDlist v) {
        DNode node = v.header;
        int j = 0;
        String[] v_a = new String[v.size()];
        while (node.getNext().element!=null){
            node=node.getNext();
            v_a[j] = node.element;
            j++;
        }
        return v_a;
    }

//    Build a MyDlist from Array
    private static MyDlist ArrayToDList(String[] a){
        MyDlist n = new MyDlist();
        for (int i=0;i<a.length;i++) {
            if (a[i]!=null) {
                if (i==0) {
                    n.addFirst(new DNode(a[i],null,null));
                }
                else {
                    n.addLast(new DNode(a[i],null,null));
                }
            }
        }
        return n;
    }

//    This constructor creates an empty doubly linked list.
    public MyDlist() {
        super();
    }

//    This constructor creates a doubly linked list by reading all strings from a text file named f.
//    Assume that adjacent strings in the file f are separated by one or more white space characters.
//    If f is “stdin”,  MyDlist(“stdin”)  creates a doubly linked list by reading all strings from the standard input.
//    Assume that each input line is a string and an empty line denotes end of input.
    public MyDlist(String f) {
        super();
        int i = 0;
        String[] values = readIO(f);
        while (values[i]!=null){
            if (i==0) {
                this.addFirst(new DNode(values[i],null,null));
            }
            else {
                this.addLast(new DNode(values[i],null,null));

            }
            i++;
            if (i>values.length-1){
                break;
            }
            }
        }

//    This instance method prints all elements of a list on the standard output, one element per line.
    public static void printList(MyDlist u){
        DNode node = u.header;
        node = node.getNext();
        while(u.hasNext(node)){
            System.out.println(node.element);
            node = node.getNext();
        }
    }

//    This class method creates an identical copy of a doubly linked list u and returns the resulting doubly linked list.
    public static MyDlist cloneList(MyDlist u){
        int i = 0;
        MyDlist n = new MyDlist();
        DNode node = u.header.next;
        while(u.hasNext(node)){
            if (i==0) {
                n.addFirst(new DNode(node.element,null,null));
            }
            else {
                n.addLast(new DNode(node.element,null,null));
            }
            i++;
            node = node.getNext();
            }
        return n;
    }

//    This class method computes the union of the two sets that are stored in the doubly linked lists u and v, respectively,
//    and returns a doubly linked list that stores the union.
//    Each element of a set is stored in a node of the corresponding doubly linked list.
//    Given two sets A and B, the union of A and B is a set that contains all the distinct element of A and B.

//    Time complexity analysis: Suppose Doubly list u has size n and v has size m
    public static MyDlist union(MyDlist u, MyDlist v){
        String[] u_a = DListToArray(u);                                  // n
        String[] v_a = DListToArray(v);                                  // m
        String[] combine = new String[u_a.length+v_a.length];            // Constant
        System.arraycopy(u_a,0,combine,0,u_a.length);
        System.arraycopy(v_a,0,combine,u_a.length,v_a.length);     // m+n
        String[] combine_sorted = sort(combine);                  //(m+n)log(m+n)   Merge sort for combined array
        String[] union = new String[u_a.length+v_a.length];
        int j=0;
        int i=0;
        while(i<combine_sorted.length){
            union[j] = combine_sorted[i];
            j++;
            if (i!=combine_sorted.length-1) {
                if(combine_sorted[i].equals(combine_sorted[i+1])){
                    i++;
                }
            }
            i++;
        }                                                                // m+n          Find the duplicate element and skip them
        return ArrayToDList(union);                                      // less than m+n-1 Build the doubly list from array and return
    }                                                                    // Total (m+n)log(m+n)+3(m+n)+constant = O{(m+n)log(m+n)}


//    This class method computes the intersection of the two sets that are stored in the doubly linked lists u and v, respectively,
//    and returns a doubly linked list that stores the intersection.
//    Each element of a set is stored in a node of the corresponding doubly linked list.
//    Given two sets A and B, the intersection of A and B is a set that contains all the elements of A that are also in B.

    public static MyDlist intersection(MyDlist u, MyDlist v){
        String[] u_a = DListToArray(u);                                  // n
        String[] v_a = DListToArray(v);                                  // m
        String[] combine = new String[u_a.length+v_a.length];            // Constant
        System.arraycopy(u_a,0,combine,0,u_a.length);
        System.arraycopy(v_a,0,combine,u_a.length,v_a.length);     // m+n
        String[] combine_sorted = sort(combine);                         //(m+n)log(m+n)    Merge sort for combined array
        String[] intersection = new String[u_a.length+v_a.length];
        int j=0;
        for(int i=0;i<combine_sorted.length-1;i++){
            if(combine_sorted[i].equals(combine_sorted[i+1])){
                intersection[j] = combine_sorted[i];
                j++;
            }
        }                                                                // m+n-1           Find the duplicate element which is intersection
        return ArrayToDList(intersection);                               // less than m+n-1 Build the doubly list from array and return
    }                                                                    // Total (m+n)log(m+n)+3(m+n)+constant = O{(m+n)log(m+n)}

    // Merge sort for sorting String array
    private static String[] data;
    private static String[] helper;

    private static int number;

    public static String[] sort(String[] values) {
        data = values;
        number = values.length;
        helper = new String[number];
        mergesort(0, number - 1);
        return data;
    }

    private static void mergesort(int low, int high) {
        // check if low is smaller than high, if not then the array is sorted
        if (low < high) {
            // Get the index of the element which is in the middle
            int middle = low + (high - low) / 2;
            // Sort the left side of the array
            mergesort(low, middle);
            // Sort the right side of the array
            mergesort(middle + 1, high);
            // Combine them both
            merge(low, middle, high);
        }
    }

    private static void merge(int low, int middle, int high) {

        // Copy both parts into the helper array
        for (int i = low; i <= high; i++) {
            helper[i] = data[i];
        }

        int i = low;
        int j = middle + 1;
        int k = low;
        // Copy the smallest values from either the left or the right side back
        // to the original array
        while (i <= middle && j <= high) {
            if (helper[i].compareTo(helper[j]) <= 0) {
                data[k] = helper[i];
                i++;
            } else {
                data[k] = helper[j];
                j++;
            }
            k++;
        }
        // Copy the rest of the left side of the array into the target array
        while (i <= middle) {
            data[k] = helper[i];
            k++;
            i++;
        }

    }

    public static void main(String[] args) throws Exception{

        System.out.println("please type some strings, one string each line and an empty line for the end of input:");
        /** Create the first doubly linked list
         by reading all the strings from the standard input. */
        MyDlist firstList = new MyDlist("stdin");

        /** Print all elememts in firstList */
        printList(firstList);

        /** Create the second doubly linked list
         by reading all the strings from the file myfile that contains some strings. */

        /** Replace the argument by the full path name of the text file */
        MyDlist secondList=new MyDlist("myfile.txt");
//
//        /** Print all elememts in secondList */
        printList(secondList);
//
//        /** Clone firstList */
        MyDlist thirdList = cloneList(firstList);
//
//        /** Print all elements in thirdList. */
        printList(thirdList);
//
//        /** Clone secondList */
        MyDlist fourthList = cloneList(secondList);
//
//        /** Print all elements in fourthList. */
        printList(fourthList);
//
//        /** Compute the union of firstList and secondList */
        MyDlist fifthList = union(firstList, secondList);
//
//        /** Print all elements in thirdList. */

        printList(fifthList);
//        /** Compute the intersection of thirdList and fourthList */
        MyDlist sixthList = intersection(thirdList, fourthList);
//
//        /** Print all elements in fourthList. */
        printList(sixthList);
    }
}


