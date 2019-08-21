/**
 * Created by qiuya on 4/20/2017.
 */

package net.datastructures;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


import static java.lang.Math.pow;

public class ExtendedAVLTree<K,V> extends AVLTree<K,V>{
    /* the inner method to build the clone AVL tree recursively by DFS*/
    public static <K,V> void _clone(AVLNode<K,V> node, AVLNode<K,V> clone){
        AVLNode leftChild = (AVLNode)node.getLeft();
        AVLNode rightChild = (AVLNode)node.getRight();
        AVLNode lcClone =new AVLNode();
        AVLNode rcClone =new AVLNode();
        lcClone.height = leftChild.height;
        rcClone.height = rightChild.height;
        if (leftChild!=null) {
            clone.setLeft(lcClone);
            lcClone.setParent(clone);
            if (leftChild.element() != null) {
                lcClone.setElement(leftChild.element());
                _clone(leftChild, lcClone);
            }
        }
        if (rightChild!=null) {
            clone.setRight(rcClone);
            rcClone.setParent(clone);
            if (rightChild.element() != null) {
                rcClone.setElement(rightChild.element());
                _clone(rightChild, rcClone);
            }
        }
    }

    /* the outer method to build the clone AVL tree recursively by DFS*/
    /* the time complexity is O(n), because it only traverses the whole tree once, and build the new tree along traversal  */
    public static <K,V> AVLTree<K,V> clone(AVLTree<K,V> tree){
        AVLTree<K, V> cloneTree = new AVLTree<K, V>();
        cloneTree.root = new AVLNode<>();
        _clone((AVLNode) tree.root, (AVLNode) cloneTree.root);
        cloneTree.root.setElement(new BSTEntry(tree.root.element().getKey(),tree.root.element().getValue(),cloneTree.root));
        ((AVLNode) cloneTree.root).height = ((AVLNode) tree.root).height;
        cloneTree.numEntries = tree.size();
        cloneTree.size = tree.size;
        return cloneTree;
    }



    /* AVL tree to Sorted Array List */
    public static <K,V> ArrayList<Entry<K,V>> AVLtoSortedList(AVLTree<K,V> tree){
        return inorder(tree.root);
    }

    /* in-order traversal of AVL tree and generate a sorted Array List*/
    /* the time complexity is O(n), because it only traverses the whole tree once, and build the array list along traversal */
    private static <K,V> ArrayList<Entry<K,V>> inorder(BTPosition<Entry<K,V>> node){
        ArrayList<Entry<K,V>> left_trace = new ArrayList<Entry<K,V>>();
        ArrayList<Entry<K,V>> right_trace = new ArrayList<Entry<K,V>>();
        /* recursively go to the left part if left child exist*/
        if (node.getLeft().element()!=null)
            left_trace = inorder(node.getLeft());
        /* recursively go to the right part if right child exist*/
        if (node.getRight().element()!=null)
            right_trace = inorder(node.getRight());
        /* concat all trace in the in-order way to generate a sorted Array List*/
        left_trace.add(node.element());
        left_trace.addAll(right_trace);
        return left_trace;
    }

    /* inner recursive method to generate a AVL tree from a sorted array list and return the parent */
    private static <K,V> AVLNode<K,V> _SortedListToAVL(AVLTree<K, V> tree, ArrayList<Entry<K, V>> treeList, int start, int end) {
        /* base case */
        if (start > end) {
            return new AVLNode<K,V>(null, null,null,null);
        }
        /* find and create the parent node */
        int mid = (start + end) / 2;
        AVLNode<K,V> node = new AVLNode<K,V>(treeList.get(mid),null,null,null);
        /* recursive find the left child and link left child with its parent */
        AVLNode<K,V> left = _SortedListToAVL(tree, treeList, start, mid - 1);
        node.setLeft(left);
        left.setParent(node);
         /* recursive find the right child and link right child with its parent */
        AVLNode<K,V> right = _SortedListToAVL(tree, treeList, mid + 1, end);
        node.setRight(right);
        right.setParent(node);
        /* set the height of current node */
        tree.setHeight(node);
        return node;
    }

    /* outer recursive method to generate a AVL tree from a sorted array list */
    /* the time complexity is O(n), because it uses elements only once in the array list */
    public static <K,V> AVLTree<K,V> SortedListToAVL(ArrayList<Entry<K,V>> treeList){
        int size = treeList.size();
        AVLTree tree = new AVLTree();
        if (size>0) {
            AVLNode<K,V> root = _SortedListToAVL(tree, treeList, 0, size - 1);
            tree.root = root;
        }
        return tree;
    }

    /* merge two sorted array list together */
    /* the time complexity is O(n+m), because it goes through both array list element only once */
    private static <K,V> ArrayList<Entry<K,V>> mergeSortedList(ArrayList<Entry<K,V>> list1,
                                                               ArrayList<Entry<K,V>> list2){
        ArrayList<Entry<K,V>> mergedList = new ArrayList<>();
        while(!list1.isEmpty()&&!list2.isEmpty()){
            if (Integer.parseInt(list1.get(0).getKey().toString())>=Integer.parseInt(list2.get(0).getKey().toString())){
                mergedList.add(list1.remove(0));
            }
            else{
                mergedList.add(list2.remove(0));
            }
        }
        while(list1.isEmpty()&&!list2.isEmpty()){
            mergedList.add(list2.remove(0));
        }
        while(!list1.isEmpty()&&list2.isEmpty()){
            mergedList.add(list1.remove(0));
        }
        return mergedList;
    }

    /* merge two AVL tree together */
    /* the steps:
    *       1. AVL tree to sorted array list. O(m)+O(n)
    *       2. merge two sorted array list together to new sorted array list. O(m+n)
    *       3. merged sorted array list to AVL tree, and destroy the old trees. O(m+n)
    *       Total complexity: O(m)+O(n)+2*O(m+n) = O(m+n)
    * */
    public static <K,V> AVLTree<K, V> merge(AVLTree<K,V> tree1, AVLTree<K,V> tree2){
        ArrayList<Entry<K,V>> tree1List = AVLtoSortedList(tree1);
        ArrayList<Entry<K,V>> tree2List = AVLtoSortedList(tree2);
        int numEntries = tree1List.size()+tree2List.size();
        ArrayList<Entry<K,V>> mergedTreeList = mergeSortedList(tree1List, tree2List);
        AVLTree<K, V> mergedTree = SortedListToAVL(mergedTreeList);
        mergedTree.numEntries =numEntries;
        mergedTree.size=2*mergedTree.numEntries+1;
        tree1 = null;
        tree2 = null;
        return mergedTree;
    }

    /* external Node class for swing graphic*/
    public static class eNode extends JComponent{
        private static final long serialVersionUID = 3L;
        private int x;
        private int y;

        public eNode(int x, int y){
            super();
            this.x = x;
            this.y = y;
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawRect(x,y,40,40);
        }
    }

    /* internal Node class for swing graphic*/
    public static class iNode extends JComponent{
        private static final long serialVersionUID = 2L;
        private int x;
        private int y;
        private Entry entry;

        public iNode(int x, int y, Entry entry){
            super();
            this.x = x;
            this.y = y;
            this.entry = entry;
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawOval(x,y,40,40);
            g.drawString(entry.getKey().toString(),x+15,y+25);
        }
    }

    /* link between Node class for swing graphic*/
    public static class Link extends JComponent{
        private static final long serialVersionUID = 1L;
        private int x1;
        private int y1;
        private int x2;
        private int y2;
        public Link(int x1, int y1, int x2, int y2){
            super();
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2d = ( Graphics2D ) g;
            g2d.drawLine(x1,y1,x2,y2);
        }
    }

    /* the inner method to print the AVL tree on swing
    * using DFS
    * */
    public static void printOnFrame(AVLNode node, JFrame frame, int x, int y, int height){
        int seqx = (int) pow(2,height)*25;
        int seqy = 80;
        AVLNode leftChild = (AVLNode)node.getLeft();
        AVLNode rightChild = (AVLNode)node.getRight();
        if (leftChild!=null){
            if (leftChild.element()!=null){
                frame.add(new iNode(x-seqx/2,y+seqy,(Entry)leftChild.element()));
                frame.setVisible(true);
                printOnFrame(leftChild,frame,x-seqx/2,y+seqy,height-1);
            }
            else{
                frame.add(new eNode(x-seqx/2,y+seqy));
                frame.setVisible(true);
            }
            frame.add(new Link(x+20,y+seqy/2,x-seqx/2+20,y+seqy));
            frame.setVisible(true);
        }

        if (rightChild!=null){
            if (rightChild.element()!=null){
                frame.add(new iNode(x+seqx/2,y+seqy,(Entry)rightChild.element()));
                frame.setVisible(true);
                printOnFrame(rightChild,frame,x+seqx/2,y+seqy,height-1);
            }
            else{
                frame.add(new eNode(x+seqx/2,y+seqy));
                frame.setVisible(true);
            }
            frame.add(new Link(x+20,y+seqy/2,x+seqx/2+20,y+seqy));
            frame.setVisible(true);
        }
    }

    /* print the AVL tree on swing */
    public static <K,V> void print(AVLTree<K, V> tree){
        JFrame frame = new JFrame( "Drawing Tree shapes" );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920,1080);
        AVLNode root = (AVLNode) tree.root;
        printOnFrame(root, frame, (int) (pow(2,root.height)*25),50,root.height);
        frame.add(new iNode((int) (pow(2,root.height)*25),50,(Entry)root.element()));
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        String values1[] = {"Sydney", "Beijing", "Shanghai", "New York", "Tokyo", "Berlin",
                "Athens", "Paris", "London", "Cairo"};
        int keys1[] = {20, 8, 5, 30, 22, 40, 12, 10, 3, 5};
        String values2[] = {"Fox", "Lion", "Dog", "Sheep", "Rabbit", "Fish"};
        int keys2[] = {40, 7, 5, 32, 20, 30};

          /* Create the first AVL tree with an external node as the root and the
         default comparator */

        AVLTree<Integer, String> tree1 = new AVLTree<Integer, String>();

        // Insert 10 nodes into the first tree

        for (int i = 0; i < 10; i++)
            tree1.insert(keys1[i], values1[i]);

          /* Create the second AVL tree with an external node as the root and the
         default comparator */

        AVLTree<Integer, String> tree2 = new AVLTree<Integer, String>();

        // Insert 6 nodes into the tree
        for (int i = 0; i < 6; i++)
            tree2.insert(keys2[i], values2[i]);

        ExtendedAVLTree.print(tree1);
        ExtendedAVLTree.print(tree2);
        ExtendedAVLTree.print(ExtendedAVLTree.clone(tree1));
        ExtendedAVLTree.print(ExtendedAVLTree.clone(tree2));
        ExtendedAVLTree.print(ExtendedAVLTree.merge(ExtendedAVLTree.clone(tree1),
                ExtendedAVLTree.clone(tree2)));
    }
}

