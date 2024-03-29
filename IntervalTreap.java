import java.util.Random;
import java.util.*;
import java.lang.*;

/**
 * Treap: http://ja.wikipedia.org/wiki/Treap
 * 
 * @authors: Austin Nebel, Rithvik Menon
 * 
*/
public class IntervalTreap {

    public boolean DEBUG = true;
    public static final Random rand = new Random();
    public Node root;
    public int size, height;
    

    public static void main(String[] args){

        IntervalTreap treap = new IntervalTreap();
        treap.add(new Interval(5,10));
        treap.add(new Interval(11,15));
        treap.add(new Interval(20,25));
        treap.add(new Interval(24,25));
        treap.add(new Interval(23,25));
        treap.add(new Interval(22,25));
        treap.add(new Interval(21,25));
        treap.add(new Interval(0,1));
        treap.add(new Interval(3,4));

        //System.out.println(overlappingIntervals(new Interval(19, 26)));
        //System.out.println(getSize(treap.root));
        //System.out.println(treap.root.toString());
        int d = depthOfTree(treap.root, 1);   
        printLevelOrder(treap.root, d); 
    }


    public Node getRoot() {
        return this.root;   
    }

    public int getSize() {
        this.size = getSize(this.root);
        return this.size;   
    }

    public int getHeight() {
        this.height = depthOfTree(this.root, 1);
        return this.height;   
    }
    
    public static int getSize(Node n){
        if(n == null){
            return 0;
        }

        int left = getSize(n.getLeft());
        int right = getSize(n.getRight());
        
        return left + right + 1;
    }

    public void print(String message){
        if(DEBUG){
            System.out.println(message);
        }
    }

    public void add(Interval data) {
        root = intervalInsert1(root, data);
    }

    /* Adds a node to the tree
     * 
     * Args:
     *      Node node: The root node to rotate
     *      Interval interv: The interval of the new node to be added
     * Returns:
     *      The node that was added to the tree.
     */
    public Node intervalInsert1(Node root, Interval interv) {

        if (root == null){
            return new Node(interv);
        }

        //if data less than root data
        if (interv.getLow() < root.getInterv().getLow()) {

            //add data to the left 
            root.left = intervalInsert1(root.getLeft(), interv);

            //sets root imax to whatever is larger
            if(root.getLeft().getImax() > root.getImax()){
                root.imax = root.getLeft().getImax();
            }

            //if new node has higher priority than root, rotate new node 
            //right to take root node's positition
            if (root.getLeft().getPriority() < root.getPriority()){
                return rotateRight(root);
            }
        //if data greater than root data
        } else if (interv.getLow() > root.getInterv().getLow()) {

            //add data to right
            root.right = intervalInsert1(root.getRight(), interv);

            //sets root imax to whatever is larger
            if(root.getRight().getImax() > root.getImax()){
                root.imax = root.getRight().getImax();
            }

            //if new node has higher priority than root, rotate new node 
            //left to take root node's positition
            if (root.getRight().getPriority() < root.getPriority()){
                return rotateLeft(root);
            }
        }
        return root;
    }
    
    
    public void intervalInsert(Node node) {
    	
    	Node z = null;		
    	z = intervalInsert1(root,node.interv);

    }

        /* Removes a node from the treap. 
     * 
     * Args:
     *      Node root: The root node to start searching from
     *      T data: The data of the node to be removed  
     */
    public Node intervalDelete1(Node root, Interval interv) {

        if (root != null) {

            //Compares root node data to data we're looking for
            int compare = interv.compareTo(root.getInterv());

            //if data is less than root, recursive call to the left
            if (compare < 0) {
                root.left = intervalDelete1(root.getLeft(), interv);

            //if data is greater than root, recursive call to the right
            } else if (compare > 0) {
                root.right = intervalDelete1(root.getRight(), interv);
            
            //if data is the same as root data
            } else {

                //if only right child exist, return it
                if (root.getLeft() == null) {
                    return root.getRight();

                //if only left child exist, return it
                } else if (root.getRight() == null) {
                    return root.getLeft();

                //if root is a leaf node
                } else {
                    root.interv = first(root.getRight());
                    root.right = intervalDelete1(root.getRight(), root.getInterv());
                }
            }
        }
        return root;
    }

    public void intervalDelete(Node node) {
    	
    	Node z = null;
    	z = intervalDelete1(root, node.interv);
    	
    }
    
    
    /* 
     * Returns:
     *      The node if the tree contains the data, null otherwise.
     */
    public Node intervalSearch(Interval interv) {

        Node node = root;
        
        Node returned = null;

        while (node != null) {
            
            //iterates either left or right down the tree
            if (interv.getLow() < node.getInterv().getLow()){
                node = node.getLeft();

            }else if(interv.getLow() > node.getInterv().getLow()){
                node = node.getRight();

            }else{
                returned = node;
                return returned;
            }
        }
        return returned;
    }

    /* Rotates the treap with root 'node' to the right
     * 
     * Args:
     *      Node node: The root node to rotate
     */
    public Node rotateRight(Node root) {

        Node lnode = root.getLeft();
        root.left = lnode.getRight();
        lnode.right = root;
        updateImax(root);
        updateImax(lnode);
        return lnode;
    }

    /* Rotates the treap with root 'node' to the left
     * 
     * Args:
     *      Node node: The root node to rotate
     */
    public Node rotateLeft(Node root) {
        Node rnode = root.getRight();
        root.right = rnode.getLeft();
        rnode.left = root;
        updateImax(root);
        updateImax(rnode);
        return rnode;
    }

    /**
     * Updates imax values to be correct. Should be done on rotations
     * and insertions.
     */
    public void updateImax(Node root){
        if(root == null){
            return;
        }
        if(root.getRight() == null && root.getLeft() == null){
            root.imax = root.getInterv().getHigh();
        }else if(root.getRight() == null){
            root.imax = Math.max(root.getInterv().getHigh(), root.getLeft().getImax());
        }else if(root.getLeft() == null){
            root.imax = Math.max(root.getInterv().getHigh(), root.getRight().getImax());
        }else{
            root.imax = Math.max(root.getInterv().getHigh(), Math.max(root.getRight().getImax(), root.getLeft().getImax()));
        }        
    }

    /* Removes a node from the tree that has the specified data,
     * starting at the root.
     * 
     * Args:
     *      Interval data: The interval to be found and removed
     */
    public void remove(Interval interv) {
        root = intervalDelete1(root, interv);
    }

    /* Finds the node furthest to the left in the tree, starting at
     * the root of the tree.
     * 
     * Returns:
     *      The leftmost node in the tree.
     */
    public Interval first() {
        return first(root);
    }

    /* Finds the node furthest to the left in the tree, starting at
     * the specified root.
     *
     * Args:
     *      Node root: The node to start searching from
     * 
     * Returns:
     *      The interval at the end of the tree
     */
    public Interval first(Node root) {

        Node node = root;
        while (node.getLeft() != null){
            node = node.getLeft();
        }

        return node.getInterv();
    }

    @Override
    public String toString() {

        return "Treap{" +
                "root=" + root +
                '}';
    }

    public static int depthOfTree(Node root, int d) {
        if(root == null) {
            return d;
        }
        int left = d;
        int right = d;
        if(root.getLeft() != null) {
            left = depthOfTree(root.getLeft(), d+1);
        }
        if(root.getRight() != null) {
            right = depthOfTree(root.getRight(), d+1);
        }
        return Math.max(left, right);
    }
      
    public static void printLevelOrder(Node root, int depth)
    {
        if(root == null)
            return;
    
        Queue<Node> q =new LinkedList<Node>();
    
        q.add(root);            
        while(true)
        {               
            int nodeCount = q.size();
            if(nodeCount == 0)
                break;
            for(int i=0; i<depth; i++) {
            System.out.print("       ");
            }
            while(nodeCount > 0)
            {    
                Node node = q.peek();
                System.out.print(Integer.toString(node.getImax()) + node.getInterv());
    
                q.remove();
    
                if(node.getLeft() != null)
                    q.add(node.getLeft());
                if(node.getRight() != null)
                    q.add(node.getRight());
    
                if(nodeCount>1){
                    System.out.print("         ");
                }
                nodeCount--;    
            }
            depth--;
            System.out.println();
        }
    }       

    /**
     * Does level order traversal of tree and returns node
     * if it matches exactly with the param interv.
     * 
     * Args:
     *      Interval interv: Interval to search
     * 
     * Returns:
     *      The node if found, null otherwise
     */
    public Node intervalSearchExactly(Interval interv){
        return intervalSearchExactly(interv, this.root, 1);
    }

    /**
     * Does level order traversal of tree and returns node
     * if it matches exactly with the param interv.
     * 
     * Args:
     *      Interval interv: Interval to search
     *      Node root: root node to search from
     *      int depth: The depth of the current node
     * 
     * Returns:
     *      The node if found, null otherwise
     */
    public static Node intervalSearchExactly(Interval interv, Node root, int depth)
    {
        if(root == null)
            return null;
    
        Queue<Node> q =new LinkedList<Node>();
    
        q.add(root);            
        while(true)
        {               
            int nodeCount = q.size();
            if(nodeCount == 0)
                break;

            while(nodeCount > 0)
            {    
                Node node = q.peek();
    
                if(node.getInterv().getLow() == interv.getLow() && node.getInterv().getHigh() == interv.getHigh()){
                    return node;
                }
                q.remove();
    
                if(node.getLeft() != null)
                    q.add(node.getLeft());
                if(node.getRight() != null)
                    q.add(node.getRight());
    
                nodeCount--;    
            }
            depth--;
        }
        return null;
    }       



    /**
     * Does level order traversal of tree and returns a list of overlapping intervals.
     * 
     * Args:
     *      Interval interv: Interval to search
     * 
     * Returns:
     *      ArrayList<Node> list of all overlapping intervals
     */
    public ArrayList<Node> overlappingIntervals(Interval interv){
        return overlappingIntervals(interv, this.root, 1);
    }

    /**
     * Does level order traversal of tree and returns a list of overlapping intervals.
     * 
     * Args:
     *      Interval interv: Interval to search
     *      Node root: root node to search from
     *      int depth: The depth of the current node
     * 
     * Returns:
     *      ArrayList<Node> list of all overlapping intervals
     */
    public static ArrayList<Node> overlappingIntervals(Interval interv, Node root, int depth)
    {
        if(root == null)
            return null;
    
        ArrayList<Node> list = new ArrayList<Node>();

        Queue<Node> q =new LinkedList<Node>();
    
        q.add(root);            
        while(true)
        {               
            int nodeCount = q.size();
            if(nodeCount == 0)
                break;

            while(nodeCount > 0)
            {    
                Node node = q.peek();
    
                if(node.getInterv().getLow() < interv.getLow() && node.getInterv().getHigh() > interv.getLow()) {
                    list.add(node);
                }

                else if(node.getInterv().getLow() > interv.getLow() && node.getInterv().getLow() < interv.getHigh()) {
                    list.add(node);
                }


                q.remove();
    
                if(node.getLeft() != null)
                    q.add(node.getLeft());
                if(node.getRight() != null)
                    q.add(node.getRight());
    
                nodeCount--;    
            }
            depth--;
        }
        return list;
    }     

    
}


