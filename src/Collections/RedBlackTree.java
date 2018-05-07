package Collections;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementa un arbol rojo negro de Strings (TODO: implementarlo para numeros enteros)
 */
public class RedBlackTree {

    private static final char BLACK = 'b';

    private static final char RED = 'r';

    public static final char STRING_NODE = 'S';

    public static final char INT_NODE = 'I';


    /**
     * Auxiliar class for the nodes and elements of the structure
     */
    private class RBTnode implements Comparable
    {

        char color;

        String element;

        RBTnode rightChild;

        RBTnode leftChild;

        RBTnode parent;

        Object content;

        @Override
        public int compareTo(Object o) {
            if(o.getClass().toString().equals(RBTnode.class.toString()))
                if(((RBTnode) o).element.getClass() == String.class){
                    return this.element.compareTo(((RBTnode) o).element);
                }

            return -1;
        }

        public boolean equals(Object o){
            if(!o.getClass().toString().equals(RBTnode.class.toString())){
                return false;
            }

            return ((RBTnode) o).element.equals(this.element);
        }

        /**
         * Toggles the color of the node
         */
        public void changeColor(){
            this.color = this.color == RED? BLACK : RED;
        }
    }

    /**
     * Thrown when trying to construct a new RBTree from a type different from Integer or String
     */
    public class RBTException extends Exception{

        private final String MSG = "Root element can only be of type String or Integer";

        public String getMessage(){
            return MSG;
        }

    }

    /**
     * Root element of the RBTree
     */
    private RBTnode root;

    /**
     * Number of elements stored in the structure
     */
    private int nodeCount;

    /**
     * Builds a new RedBlackTree
     * @param rootIndex element's to be stored in the root of the tree
     * @param nodeType type of element that the tree will store
     * @throws RBTException
     */
    public RedBlackTree(String rootIndex, Object rootContent, char nodeType) throws RBTException {

        switch(nodeType){
            case STRING_NODE:
                this.root = new RBTnode();
                break;
            case INT_NODE:
                this.root = new RBTnode();
                break;
            default:
                throw new RBTException();
        }

        root.element = rootIndex;
        root.content = rootContent;
        root.color = BLACK;

        root.rightChild = null;
        root.leftChild = null;
        root.parent = null;

        this.nodeCount = 1;

    }

    /**
     * Inserta un nuevo elemento del tipo que se ha definido en la construccion del arbol en la estructura
     * @param index Nuevo elemento a isnertar
     */
    public void insert(String index, Object content){

        RBTnode newNode = new RBTnode();
        newNode.element = index;
        newNode.content = content;
        newNode.color = RED;

        insert(newNode, root);

        repairTree(newNode);

        while(this.root.parent != null)
            this.root = this.root.parent;

        this.nodeCount++;
    }

    /**
     * Looks for the node with the specified index in the structure
     * @param nodeIndex index to be found
     * @return null fi not found. The content of the node if found
     */
    public Object get(String nodeIndex){

        RBTnode index = new RBTnode();
        index.element = nodeIndex;

        RBTnode result = getNode(index, this.root);

        if(result == null) return null;

        return result.content;

    }

    /**
     * @return node count of the tree
     */
    public int size(){
        return this.nodeCount;
    }

    /**
     * @return Content of the tree ordered in Pre-Order
     */
    public Object[] preOrder(){
        ArrayList pre = new ArrayList();

        preOrder(this.root, pre);

        return pre.toArray();
    }

    /**
     * @return Content of the tree oredered in In-Order
     */
    public Object[] inOrder(){

        ArrayList in = new ArrayList();

        inOrder(this.root, in);

        return in.toArray();
    }

    /**
     * @return Content of the tree ordered in Post-Order
     */
    public Object[] postOrder(){

        ArrayList post = new ArrayList();

        postOrder(this.root, post);

        return post.toArray();
    }

    //----------------------------------------------------------------------------------------------------------------//

    /**
     * @param node
     * @return the other child of the node's parent, if both exist. Null otherwise
     */
    private RBTnode sibling(RBTnode node){
        if(node == null || node.parent == null){
            return null;
        }

        return node == node.parent.leftChild? node.parent.rightChild: node.parent.leftChild;
    }

    /**
     * Insercion de nodos en el arbol al estilo BST.
     * @param newNode Nuevo nodo a inserir
     * @param root subarbol que se esta explorando en este momento
     */
    private void insert(RBTnode newNode, RBTnode root){

        if(newNode.element.compareTo(root.element) < 0){

            if(root.leftChild == null){
                newNode.parent = root;
                root.leftChild = newNode;
                return;
            }

            insert(newNode, root.leftChild);
        }

        if (newNode.element.compareTo(root.element) > 0){

            if(root.rightChild == null){
                newNode.parent = root;
                root.rightChild = newNode;
                return;
            }

            insert(newNode, root.rightChild);
        }

        //Si resulta que encontramos que el elemento ya habia sido insertado, no se producira ningun cambio
        //en el arbol, por lo que simplemente actualizamos el contenido y subimos
    }

    /**
     * Se hace una rotacion para poder acceder al caso de insercion 3a
     * @param node
     */
    private void insert2a(RBTnode node){
        RBTnode aux = new RBTnode();

        aux.parent = node.parent.parent;
        aux.leftChild = node.leftChild;
        node.parent.parent = node;

        node.leftChild = node.parent;
        node.parent = aux.parent;

        aux.parent.leftChild = node;
        node.leftChild.rightChild = aux.leftChild;

        insert3a(node.leftChild);
    }

    /**
     * Se hace una rotacion para poder acceder al caso de insercion 3b
     * @param node
     */
    private void insert2b(RBTnode node){
        RBTnode aux = new RBTnode();

        aux.parent = node.parent.parent;
        aux.rightChild = node.rightChild;
        node.parent.parent = node;

        node.rightChild = node.parent;
        node.parent = aux.parent;

        aux.parent.rightChild = node;
        node.rightChild.leftChild = aux.rightChild;

        insert3b(node.rightChild);
    }

    /**
     * Hacemos una rotacion LL respecto al abuelo del nuevo nodo y
     * e intercambiamos el color del antiguo abuelo (ahora hermano) con el del padre
     * @param node
     */
    private void insert3a(RBTnode node){
        RBTnode aux = new RBTnode();

        aux.parent = node.parent.parent;
        aux.rightChild = node.parent.rightChild;

        node.parent.parent = aux.parent.parent;
        node.parent.rightChild = aux.parent;

        if(aux.parent.parent != null){
            if(aux.parent.parent.leftChild == aux.parent)
                aux.parent.parent.leftChild = node.parent;
            else
                aux.parent.parent.rightChild = node.parent;
        }

        aux.parent.parent = node.parent;
        sibling(node).leftChild = aux.rightChild;

        if(sibling(node).leftChild != null)
            sibling(node).leftChild.parent = sibling(node);


        //Actualizo los colores
        sibling(node).color = RED;
        node.parent.color = BLACK;

    }

    /**
     * Hacemos una rotacion RR respecto al abuelo del nuevo nodo y
     * e intercambiamos el color del antiguo abuelo (ahora hermano) con el del padre
     * @param node
     */
    private void insert3b(RBTnode node){
        RBTnode aux = new RBTnode();

        aux.parent = node.parent.parent;
        aux.leftChild = node.parent.leftChild;

        node.parent.parent = aux.parent.parent;
        node.parent.leftChild = aux.parent;

        if(aux.parent.parent != null){
            if(aux.parent.parent.leftChild == aux.parent)
                aux.parent.parent.leftChild = node.parent;
            else
                aux.parent.parent.rightChild = node.parent;
        }

        aux.parent.parent = node.parent;
        sibling(node).rightChild  = aux.leftChild;

        if(sibling(node).rightChild != null)
            sibling(node).rightChild.parent = sibling(node);


        //Actualizo los colores
        sibling(node).color = RED;
        node.parent.color = BLACK;

    }

    /**
     * Repairs the structure of the tree after an insertion so the 5 properties of RedBlack trees remain true
     * @param node New inserted node
     */
    private void repairTree(RBTnode node){

        if(node == null)
            return;

        if(node.parent == null){
            //Node is the root. So we paint it black, because root must always be black
            node.color = BLACK;

        }else if(node.parent.color == BLACK){
            //Nothing to be done. Properties of the tree remain satisfied
        }else if(sibling(node.parent) != null && sibling(node.parent).color == RED){
            //Insert case 1

            node.parent.color = BLACK;
            sibling(node.parent).color = BLACK;

            //After pushing blackness from grandparent, properties must be checked so
            //grandparent remains satisfying the trees properties. So it must be repaired
            node.parent.parent.color = RED;
            repairTree(node.parent.parent);
        } else{

            //Rotations

            if(node == node.parent.rightChild && node.parent == node.parent.parent.leftChild){
                insert2a(node); //Pre-rotateRight

            }else if(node == node.parent.leftChild && node.parent == node.parent.parent.rightChild){
                insert2b(node); //Pre-rotateLeft

            }else if(node == node.parent.leftChild && node.parent == node.parent.parent.leftChild){
                insert3a(node); //Rotate right

            }else if(node == node.parent.rightChild && node.parent == node.parent.parent.rightChild) {
                insert3b(node); //Rotate Left
            }
        }

    }

    /**
     * Searches for the node that has the specified index as if it was a Binary Search Tree
     * @param index Aux node with the index to be found
     * @param currentNode
     * @return null if the index is not stored in the structure. If found, returns the RBTNode
     */
    private RBTnode getNode(RBTnode index, RBTnode currentNode){

        if(currentNode == null){
            return null;
        }

        if(currentNode.equals(index)){
            return currentNode;
        }else{

            if(index.compareTo(currentNode) < 0){
                return getNode(index, currentNode.leftChild);
            }else{
                return getNode(index, currentNode.rightChild);
            }

        }

    }

    /**
     * Collects all the content of the tree in a Pre=Ordered way
     * @param root
     * @param pre
     */
    private void preOrder(RBTnode root, ArrayList<Object> pre){
        if(root == null)
            return;

        pre.add(root.content);
        preOrder(root.leftChild, pre);
        preOrder(root.rightChild, pre);
    }

    /**
     * Collects all the content of the tree in a In-Ordered way
     * @param root
     * @param in
     */
    private void inOrder(RBTnode root, ArrayList<Object> in){
        if(root == null)
            return;

        inOrder(root.leftChild, in);
        in.add(root.content);
        inOrder(root.rightChild, in);

    }

    /**
     * Collects all the content of the tree in a Post-Ordered way
     * @param root
     * @param post
     */
    private void postOrder(RBTnode root, ArrayList<Object> post){
        if(root == null)
            return;

        postOrder(root.leftChild, post);
        postOrder(root.rightChild, post);
        post.add(root.content);
    }

    //----------------------------------------------------------------------------------------------------------------//

    public static void main(String[] args) {

        RedBlackTree rbt;

        try {
            rbt = new RedBlackTree("30", 30, RedBlackTree.INT_NODE);

            rbt.insert("Barcelona", 1);
            rbt.insert("Tarragona", 2);
            rbt.insert("Lleida", 3);
            rbt.insert("Girona", 4);
            rbt.insert("Vielha", 5);
            rbt.insert("Valencia", 6);
            rbt.insert("Zaragoza", 7);
            rbt.insert("Madrid", 8);
            rbt.insert("Toledo", 9);
            rbt.insert("Malaga", 10);
            rbt.insert("Sevilla", 11);
            rbt.insert("Leon", 12);
            rbt.insert("Oviedo", 13);
            rbt.insert("Cartagena", 14);
            rbt.insert("A Coruña", 15);
            rbt.insert("Bilbao", 16);
            rbt.insert("San Sebastian", 17);

            System.out.println(Arrays.toString(rbt.postOrder()));
            System.out.println(Arrays.toString(rbt.preOrder()));
            System.out.println(Arrays.toString(rbt.inOrder()));
            System.out.println();
        } catch (RBTException e) {
            System.out.println(e.getMessage());
        }

    }

}
