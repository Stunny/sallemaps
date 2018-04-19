package Collections;

import java.util.ArrayList;

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

        RBTnode sibling;

        Object content;

        @Override
        public int compareTo(Object o) {
            if(o.getClass() == RBTnode.class)
                if(((RBTnode) o).element.getClass() == String.class){
                    return (((String) this.element)).compareTo(((String)((RBTnode) o).element));
                }

            return -1;
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
        root.sibling = null;
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

        //Tras la insercion pasamos a comprobar si hacen falta cambios en el arbol
        if(newNode.parent != this.root){
            if(newNode.parent.sibling != null && newNode.parent.sibling.color == RED){
                insert1(newNode);
            }else{
                if(newNode == newNode.parent.rightChild && newNode.parent == newNode.parent.parent.leftChild){
                    insert2a(newNode);
                }else
                if(newNode == newNode.parent.leftChild && newNode.parent == newNode.parent.parent.rightChild){
                    insert2b(newNode);
                }else
                if(newNode == newNode.parent.leftChild && newNode.parent == newNode.parent.parent.leftChild){
                    insert3a(newNode);
                }else
                if(newNode == newNode.parent.rightChild && newNode.parent == newNode.parent.parent.rightChild){
                    insert3b(newNode);
                }
            }
        }

        //Actualizamos la raiz del arbol
        if(this.root.parent != null){

            this.root = this.root.parent;
            this.root.parent = null;
        }

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
        Object[] contents = new Object[nodeCount];
        ArrayList pre = new ArrayList();

        preOrder(this.root, pre);

        return pre.toArray();
    }

    /**
     * @return Content of the tree oredered in In-Order
     */
    public Object[] inOrder(){
        Object[] contents = new Object[nodeCount];

        ArrayList in = new ArrayList();

        inOrder(this.root, in);

        return in.toArray();
    }

    /**
     * @return Content of the tree ordered in Post-Order
     */
    public Object[] postOrder(){
        Object[] contents = new Object[nodeCount];

        ArrayList post = new ArrayList();

        postOrder(this.root, post);

        return post.toArray();
    }

    //----------------------------------------------------------------------------------------------------------------//

    /**
     * Insercion de nodos en el arbol al estilo BST.
     * @param newNode Nuevo nodo a inserir
     * @param root subarbol que se esta explorando en este momento
     */
    private void insert(RBTnode newNode, RBTnode root){

        if(newNode.compareTo(root) < 0){

            if(root.leftChild == null){
                newNode.parent = root;
                newNode.sibling = root.rightChild;
                root.leftChild = newNode;
                return;
            }

            insert(newNode, root.leftChild);
        }

        if (newNode.compareTo(root) > 0){

            if(root.rightChild == null){
                newNode.parent = root;
                newNode.sibling = root.leftChild;
                root.rightChild = newNode;
                return;
            }

            insert(newNode, root.rightChild);
        }

        //Si resulta que encontramos que el elemento ya habia sido insertado, no se producira ningun cambio
        //en el arbol, por lo que subimos
    }

    /**
     * Se intercambia el color del nodo abuelo al nuevo con el del padre y el del tio
     * @param newNode El nuevo nodo insertado
     */
    private void insert1(RBTnode newNode){

        newNode.parent.color = newNode.parent.parent.color;
        newNode.parent.sibling.color = newNode.parent.parent.color;
        newNode.parent.parent.changeColor();

        if(newNode.parent.parent == this.root)
            this.root.color = BLACK;
    }

    /**
     * Se hace una rotacion para poder acceder al caso de insercion 3a
     * @param newNode
     */
    private void insert2a(RBTnode newNode){
        RBTnode aux = new RBTnode();

        aux.parent = newNode.parent.parent;
        newNode.parent.parent = newNode;

        newNode.leftChild = newNode.parent;
        newNode.parent = aux.parent;

        aux.parent.leftChild = newNode;
        newNode.leftChild.rightChild = null;

        insert3a(newNode);
    }

    /**
     * Se hace una rotacion para poder acceder al caso de insercion 3b
     * @param newNode
     */
    private void insert2b(RBTnode newNode){
        RBTnode aux = new RBTnode();

        aux.parent = newNode.parent.parent;
        newNode.parent.parent = newNode;

        newNode.rightChild = newNode.parent;
        newNode.parent = aux.parent;

        aux.parent.rightChild = newNode;
        newNode.rightChild.leftChild = null;

        insert3b(newNode);
    }

    /**
     * Hacemos una rotacion LL respecto al abuelo del nuevo nodo y
     * e intercambiamos el color del antiguo abuelo (ahora hermano) con el del padre
     * @param newNode
     */
    private void insert3a(RBTnode newNode){
        RBTnode aux = new RBTnode();

        aux.parent = newNode.parent.parent.parent;
        aux.rightChild = newNode.parent.parent;

        newNode.parent.parent = aux.parent;
        aux.rightChild.leftChild = null;
        newNode.parent.rightChild = aux.rightChild;
        aux.rightChild.parent = newNode.parent;

        if(aux.parent != null){
            if(aux.parent.rightChild == newNode.parent.rightChild)
                aux.parent.rightChild = newNode.parent;
            else aux.parent.leftChild = newNode.parent;
        }

        //Actualizo las referencias a los hermanos
        newNode.parent.sibling = newNode.parent.rightChild.sibling;
        newNode.parent.rightChild.sibling = newNode;
        newNode.sibling = newNode.parent.rightChild;

        //Actualizo los colores
        newNode.sibling.changeColor();
        newNode.parent.changeColor();
    }

    /**
     * Hacemos una rotacion RR respecto al abuelo del nuevo nodo y
     * e intercambiamos el color del antiguo abuelo (ahora hermano) con el del padre
     * @param newNode
     */
    private void insert3b(RBTnode newNode){
        RBTnode aux = new RBTnode();

        aux.parent = newNode.parent.parent.parent;
        aux.leftChild = newNode.parent.parent;

        newNode.parent.parent = aux.parent;
        aux.leftChild.rightChild = null;
        newNode.parent.leftChild = aux.leftChild;
        aux.leftChild.parent = newNode.parent;

        if(aux.parent != null){
            if(aux.parent.leftChild == newNode.parent.leftChild)
                aux.parent.leftChild = newNode.parent;
            else aux.parent.rightChild = newNode.parent;
        }

        //Actualizo las referencias a los hermanos
        newNode.parent.sibling = newNode.parent.leftChild.sibling;
        newNode.parent.leftChild.sibling = newNode;
        newNode.sibling = newNode.parent.leftChild;

        //Actualizo los colores
        newNode.sibling.changeColor();
        newNode.parent.changeColor();
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

        if(currentNode.element.equals(index)){
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

            rbt.insert("50", 50);
            rbt.insert("64", 64);
            rbt.insert("04", 4);
            rbt.insert("08", 8);
            rbt.insert("85", 85);
            rbt.insert("14", 14);

            System.out.println(rbt.preOrder().toString());
        } catch (RBTException e) {
            System.out.println(e.getMessage());
        }

    }

}
