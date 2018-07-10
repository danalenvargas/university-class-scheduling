/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.model;

/**
 *
 * @author Dan
 */
/**
 * ****************************************************************************
 * Compilation: javac IntervalST.java Execution: java IntervalST Dependencies:
 * Interval1D.java
 *
 * Interval search tree implemented using a randomized BST.
 *
 * Duplicate policy: if an interval is inserted that already exists, the new
 * value overwrite the old one
 *
 *****************************************************************************
 */
import java.util.LinkedList;

public class IntervalST<Value> {

    private Node root;   // root of the BST

    // BST helper node data type
    private class Node {

        Interval interval;      // key
        Value value;              // associated data
        Node left, right;         // left and right subtrees
        int N;                    // size of subtree rooted at this node
        int max;                  // max endpoint in subtree rooted at this node

        Node(Interval interval, Value value) {
            this.interval = interval;
            this.value = value;
            this.N = 1;
            this.max = interval.high;
        }

        Node(Interval interval) {
            this.interval = interval;
            this.N = 1;
            this.max = interval.high;
        }
    }

    public Node searchNode(Interval interval) {
        return searchNode(root, interval);
    }

    // look in subtree rooted at x
    public Node searchNode(Node x, Interval interval) {
        while (x != null) {
            if (interval.intersects(x.interval)) {
                return x;
            } else if (x.left == null) {
                x = x.right;
            } else if (x.left.max < interval.low) {
                x = x.right;
            } else {
                x = x.left;
            }
        }
        return null;
    }
    //  SPLIT

    /**
     * *************************************************************************
     * BST search
     * *************************************************************************
     */
    public boolean contains(Interval interval) {
        return (get(interval) != null);
    }

    public int contains(Interval interval, Value value) {
        return get(root, root, root, interval, null, value);
    }

    // return value associated with the given key
    // if no such value, return null
    public Value get(Interval interval) {
        return get(root, root, root, interval);

    }

    private Value get(Node x, Node successor, Node predecessor, Interval interval) {
        if (x == null) {
//            if(root!=null)
//            merge(successor, predecessor, interval, last, value);//return null if not able to merge, otherwise return the node being searched
//            else System.out.println("root is null");

            return null;
            //intersecting if equal value or both null
        }
        int cmp = interval.compareTo(x.interval);
//        System.out.println("comparing... " + interval + " : " + x.interval + " = " + cmp);
        if (cmp < 0) {
            //if (x.interval.compareTo(successor.interval) > 0) {
            successor = x;
//            System.out.println("successor = " + successor.interval);
            //}
//            System.out.println("going left...");
            return get(x.left, successor, predecessor, interval);
        } else if (cmp > 0) {
            // if (x.interval.compareTo(predecessor.interval) > 0) {
            predecessor = x;
//            System.out.println("predecessor = " + predecessor.interval);
            // }
//            System.out.println("going right...");
            return get(x.right, successor, predecessor, interval);
        } else {
            return x.value;
        }
    }

    private int get(Node x, Node successor, Node predecessor, Interval interval, Boolean last, Value value) {
        if (x == null) {
            if (root != null) {
                return merge(successor, predecessor, interval, last, value);//return null if not able to merge, otherwise return the node being searched
                //intersecting if equal value or both null
            } else {
//                System.out.println("root is null");
            }

            return 0;
        }
        int cmp = interval.compareTo(x.interval);
//        System.out.println("comparing... " + interval + " : " + x.interval + " = " + cmp);
        if (cmp < 0) {
            //if (x.interval.compareTo(successor.interval) > 0) {
            successor = x;
//            System.out.println("successor = " + successor.interval);
            //}
//            System.out.println("going left...");
            return get(x.left, successor, predecessor, interval, true, value);
        } else if (cmp > 0) {
            // if (x.interval.compareTo(predecessor.interval) > 0) {
            predecessor = x;
//            System.out.println("predecessor = " + predecessor.interval);
            // }
//            System.out.println("going right...");
            return get(x.right, successor, predecessor, interval, false, value);
        } else {
            return 1;
        }
    }

    private int merge(Node successor, Node predecessor, Interval interval, Boolean last, Value value) {
//        System.out.println("inside merge... Interval: " + interval);
//        System.out.println("predecessor: " + predecessor.interval);
//        System.out.println("successor: " + successor.interval);

        if (predecessor.interval.high == interval.low && successor.interval.low == interval.high && areEqualValues(value, successor.value, predecessor.value)) {
            if(value!=null)
            System.out.println("inside 1st if");
            if (last == true) {  //last accessed node is SUCCESSOR
//                System.out.println("last node is successor");
                int temp = successor.interval.high;
//                if (successor.right != null) {
//                    successor = successor.right;
//                } else {
                    remove(successor.interval);
                    //successor = null;
//                }
                predecessor.interval.high = temp;
            } else {  //last accessed node is PREDECESSOR
//                System.out.println("last node is predecessor");
                int temp = predecessor.interval.low;
//                if (predecessor.left != null) {
//                    System.out.println("predecessor left is NOT null");
//                    predecessor = predecessor.left;
//                } else {
//                    System.out.println("predecessor left is null, REMOVING...");
                    remove(predecessor.interval);
                    //predecessor = null;
                //    predecessor = joinLR(predecessor.left, predecessor.right);
//                }
                successor.interval.low = temp;
            }
            return -1;
        } else if (predecessor.interval.high == interval.low && areEqualValues(value, predecessor.value)) {
            predecessor.interval.high = interval.high;
            return -1;
        } else if (successor.interval.low == interval.high && areEqualValues(value, successor.value)) {
            successor.interval.low = interval.low;
            return -1;
        }
        return 0;
    }

    public boolean areEqualValues(Object a, Object b, Object c){
        if(a == null && b == null && c == null){
            return true;
        } else if(a == null){
            return false;
        } else if(b == null) {
            return false;
        } else if(c == null){
            return false;
        } else{
            SchedEntry x = (SchedEntry) a;
            SchedEntry y = (SchedEntry) b;
            SchedEntry z = (SchedEntry) c;
            if(x.isSimilar(y) && x.isSimilar(z)){
                return true;
            }
        }
        return false;
    }
    
    public boolean areEqualValues(Object a, Object b){
        if(a == null && b == null){
            return true;
        } else if(a == null){
            return false;
        } else if(b == null) {
            return false;
        }else{
            SchedEntry x = (SchedEntry) a;
            SchedEntry y = (SchedEntry) b;
            if(x.isSimilar(y)){
                return true;
            }
        }
        return false;
    }
    /**
     * *************************************************************************
     * randomized insertion
     * *************************************************************************
     */
    public void put(Interval interval, Value value) {
//        if (contains(interval)) {
//            System.out.println("duplicate");
//            remove(interval);
//        }
//        root = randomizedInsert(root, interval, value);
        int x = contains(interval, value);
        if (x == 1) {   //found duplicate
            System.out.println("duplicate");
            remove(interval);
            root = randomizedInsert(root, interval, value);
        } else if (x == 0) {    //did not find duplicate NOR mergeable nodes
            root = randomizedInsert(root, interval, value);
        } else if(x ==-1){      //merged nodes
            System.out.println("merged... ");
        }
    }

    // make new node the root with uniform probability
    private Node randomizedInsert(Node x, Interval interval, Value value) {
        if (x == null) {
            return new Node(interval, value);
        }
        if (Math.random() * size(x) < 1.0) {
            return rootInsert(x, interval, value);
        }
        int cmp = interval.compareTo(x.interval);
        if (cmp < 0) {
            x.left = randomizedInsert(x.left, interval, value);
        } else {
            x.right = randomizedInsert(x.right, interval, value);
        }
        fix(x);
        return x;
    }

    private Node rootInsert(Node x, Interval interval, Value value) {
        if (x == null) {
            return new Node(interval, value);
        }
        int cmp = interval.compareTo(x.interval);
        if (cmp < 0) {
            x.left = rootInsert(x.left, interval, value);
            x = rotR(x);
        } else {
            x.right = rootInsert(x.right, interval, value);
            x = rotL(x);
        }
        return x;
    }

    /**
     * *************************************************************************
     * deletion
     * *************************************************************************
     */
    private Node joinLR(Node a, Node b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }

        if (Math.random() * (size(a) + size(b)) < size(a)) {
            a.right = joinLR(a.right, b);
            fix(a);
            return a;
        } else {
            b.left = joinLR(a, b.left);
            fix(b);
            return b;
        }
    }

    // remove and return value associated with given interval;
    // if no such interval exists return null
    public Value remove(Interval interval) {
        Value value = get(interval);
        root = remove(root, interval);
        return value;
    }

    private Node remove(Node h, Interval interval) {
        if (h == null) {
            return null;
        }
        int cmp = interval.compareTo(h.interval);
        if (cmp != 0 && h.interval.contains(interval)) {
            split(h, interval);
            cmp = interval.compareTo(h.interval);
        }
        if (cmp < 0) {
            h.left = remove(h.left, interval);
        } else if (cmp > 0) {
            h.right = remove(h.right, interval);
        } else {
            h = joinLR(h.left, h.right);
        }
        fix(h);
        return h;

    }

    //   SPLIT  
    private void split(Node h, Interval interval) {

        if (h != null) {
            Interval intervalLeft = h.interval.low < interval.low ? new Interval(h.interval.low, interval.low) : null;
            Interval intervalRight = h.interval.high > interval.high ? new Interval(interval.high, h.interval.high) : null;

            h.interval.low = interval.low;
            h.interval.high = interval.high;

            if (intervalLeft != null) {
                Node hLeft = new Node(intervalLeft, h.value);
                hLeft.left = h.left;
                h.left = hLeft;
            }

            if (intervalRight != null) {
                Node hRight = new Node(intervalRight, h.value);
                hRight.right = h.right;
                h.right = hRight;
            }
        }
    }

    /**
     * *************************************************************************
     * Interval searching
     * *************************************************************************
     */
    // return an interval in data structure that intersects the given inteval;
    // return null if no such interval exists
    // running time is proportional to log N
    public Interval search(Interval interval) {
        return search(root, interval);
    }

    // look in subtree rooted at x
    public Interval search(Node x, Interval interval) {
        while (x != null) {
            if (interval.intersects(x.interval)) {
                return x.interval;
            } else if (x.left == null) {
                x = x.right;
            } else if (x.left.max < interval.low) {
                x = x.right;
            } else {
                x = x.left;
            }
        }
        return null;
    }

    // search wether there exists an interval that contains the given interval
    public boolean searchInclusive(Interval interval) {
        return (searchInclusive(root, interval) != null);
    }

    public Interval searchInclusive(Node x, Interval interval) {
        while (x != null) {
            if (x.interval.contains(interval)) {
                return x.interval;
            } else if (x.left == null) {
                x = x.right;
            } else if (x.left.max < interval.low) {
                x = x.right;
            } else {
                x = x.left;
            }
        }
        return null;
    }

    // return *all* intervals in data structure that intersect the given interval
    // running time is proportional to R log N, where R is the number of intersections
    public Iterable<Interval> searchAll(Interval interval) {
        LinkedList<Interval> list = new LinkedList<Interval>();
        searchAll(root, interval, list);
        return list;
    }

    // look in subtree rooted at x
    public boolean searchAll(Node x, Interval interval, LinkedList<Interval> list) {
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        if (x == null) {
            return false;
        }
        if (interval.intersects(x.interval)) {
            list.add(x.interval);
            found1 = true;
        }
        if (x.left != null && x.left.max >= interval.low) {
            found2 = searchAll(x.left, interval, list);
        }
        if (found2 || x.left == null || x.left.max < interval.low) {
            found3 = searchAll(x.right, interval, list);
        }
        return found1 || found2 || found3;
    }

    /**
     * *************************************************************************
     * useful binary tree functions
     * *************************************************************************
     */
    // return number of nodes in subtree rooted at x
    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) {
            return 0;
        } else {
            return x.N;
        }
    }

    // height of tree (empty tree height = 0)
    public int height() {
        return height(root);
    }

    private int height(Node x) {
        if (x == null) {
            return 0;
        }
        return 1 + Math.max(height(x.left), height(x.right));
    }

    /**
     * *************************************************************************
     * helper BST functions
     * *************************************************************************
     */
    // fix auxilliar information (subtree count and max fields)
    private void fix(Node x) {
        if (x == null) {
            return;
        }
        x.N = 1 + size(x.left) + size(x.right);
        x.max = max3(x.interval.high, max(x.left), max(x.right));
    }

    private int max(Node x) {
        if (x == null) {
            return Integer.MIN_VALUE;
        }
        return x.max;
    }

    // precondition: a is not null
    private int max3(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }

    // right rotate
    private Node rotR(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        fix(h);
        fix(x);
        return x;
    }

    // left rotate
    private Node rotL(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        fix(h);
        fix(x);
        return x;
    }

    /**
     * *************************************************************************
     * Debugging functions that test the integrity of the tree
     * *************************************************************************
     */
    // check integrity of subtree count fields
    public boolean check() {
        return checkCount() && checkMax();
    }

    // check integrity of count fields
    private boolean checkCount() {
        return checkCount(root);
    }

    private boolean checkCount(Node x) {
        if (x == null) {
            return true;
        }
        return checkCount(x.left) && checkCount(x.right) && (x.N == 1 + size(x.left) + size(x.right));
    }

    private boolean checkMax() {
        return checkMax(root);
    }

    private boolean checkMax(Node x) {
        if (x == null) {
            return true;
        }
        return x.max == max3(x.interval.high, max(x.left), max(x.right));
    }

    //
    public Iterable<Value> getAllValues() {
        LinkedList<Value> list = new LinkedList<>();
        getAllValues(root, list);
        return list;
    }

    // look in subtree rooted at x
    public boolean getAllValues(Node x, LinkedList<Value> list) {
        boolean found1 = true;
        boolean found2 = false;
        boolean found3 = false;
        if (x == null) {
            return false;
        }
        //if (interval.intersects(x.interval)) {
        //found1 = true;
        //}
        if (x.left != null) {
            found2 = getAllValues(x.left, list);
        }

        list.add(x.value);

        if (found2 || x.left == null) {
            found3 = getAllValues(x.right, list);
        }
        return found1 || found2 || found3;
    }

    public String toString() {
        return traverse(root);

    }

    private String traverse(Node n) {
        if (n == null) {
            return "";
        }
        return traverse(n.left) + " " + n.interval + traverse(n.right);
    }

    public static void main(String args[]) {
        IntervalST st = new IntervalST();
        IntervalST<Integer> st2 = new IntervalST<>();
        
        st.put(new Interval(1,2), null);
        System.out.println(st);
        st.put(new Interval(7,10),null);
        System.out.println(st);
        System.out.println(st.contains(new Interval(2,7)));
        System.out.println(st);
        
        st.put(new Interval(2,7),null);
        System.out.println(st);
        
//        st.put(new Interval(0, 119), null);
//        System.out.println("--------------------------------------------------");
//        System.out.println(st);
//        st.remove(new Interval(1, 4));
//        System.out.println("--------------------------------------------------");
//        System.out.println(st);
//        st.put(new Interval(1, 4), null);
//        System.out.println("--------------------------------------------------");
//        System.out.println(st);
//        st.remove(new Interval(2, 7));
//        System.out.println("--------------------------------------------------");
//        System.out.println(st);

//        st.remove(new Interval(38, 41));
//        System.out.println("--------------------------------------------------");
//        System.out.println(st);
//        st.remove(new Interval(84, 89));
//        System.out.println("--------------------------------------------------");
//        System.out.println(st);
//        st.remove(new Interval(96, 101));
//        System.out.println("--------------------------------------------------");
//        System.out.println(st);
//        st.put(new Interval(2, 7), null);
//        System.out.println("--------------------------------------------------");
//        System.out.println(st);

//        System.out.println("\n==========\n");
//        st2.put(new Interval(0, 119), 1);
//        System.out.println(st2);
//        st2.remove(new Interval(0,3));
//        System.out.println(st2);
//        st2.remove(new Interval(38,41));
//        System.out.println(st2);
//        st2.remove(new Interval(84,89));
//        System.out.println(st2);
//        st2.remove(new Interval(96,101));
//        System.out.println(st2);
    }
}
