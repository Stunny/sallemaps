package Collections;

import java.util.Arrays;

public class HashTable {

    private class TablePair{
        String key;
        Object value;
    }

    private TablePair[] values;

    private int size;

    private int pairCount;

    /**
     * Builds a new Hash Table that stores pairs of String(key)-Object(Value)
     * @param initialSize initial dimension for the data set
     */
    public HashTable(int initialSize){

        values = new TablePair[initialSize];
        this.size = initialSize;
        this.pairCount = 0;

    }

    /**
     * Inserts a new pair in the structure. If the key alreadu exists the content will be overridden
     * @param key Identifier for the new pair
     * @param value Content of the new pair
     */
    public void put(String key, Object value){

        int i = 0;
        int hash = hashI(key, i);

        while(hash < size && values[hash] != null && !values[hash].key.equals(key)){
            hash = hashI(key, ++i);
        }

        if(values[hash] == null){
            values[hash] = new TablePair();
            values[hash].key = key;
            values[hash].value = value;

            pairCount++;
            return;
        }

        if(values[hash].key.equals(key))
            values[hash].value = value;

        checkCapacity();
    }

    /**
     * Deletes the first encounter for the data pair for which the key matches with the one spefified in the parameters
     * @param key Identifier to be found in the structure
     */
    public void delete(String key){
        int i = 0;
        int hash = hashI(key, i);

        while(hash < size && values[hash] != null && !values[hash].key.equals(key)){
            hash = hashI(key, ++i);
        }

        if(values[hash] != null && values[hash].key.equals(key)){
            values[hash] = null;
            pairCount--;
        }
    }

    /**
     * A data pair will be searched for which the key specified as a aparameter will be searched in the structure
     * @param key Key to be found in the structure
     * @return Value of the data pair. Null if the key is not found in the structure
     */
    public Object get(String key){
        int i = 0;
        int hash = hashI(key, i);

        while(hash < size && values[hash] != null && !values[hash].key.equals(key)){
            hash = hashI(key, ++i);
        }

        if(values[hash] == null){
            return null;
        }

        if(values[hash].key.equals(key))
            return values[hash].value;

        return null;
    }

    /**
     * @return Number of pairs stored in the structure
     */
    public int size(){
        return pairCount;
    }

    //----------------------------------------------------------------------------------------------------------------//

    /**
     * @param key Key to be hashed
     * @param i iteration of rehash
     * @return rehash for the iteration
     */
    private int hashI(String key, int i){
        int hash = 0;
        int length = key.length();

        for(int j = length-3; j < length; j++)
            hash += (int)key.charAt(j < 0? 0: j) * Math.pow(27, j < 0? 0 - j: j);

        return (hash +(i*(8681-(hash/8681))))%this.size;

    }

    /**
     * Comprueba la cantidad de registros almacenados en la estructura
     * y redimensiona el vector en caso de ser necesario
     */
    private void checkCapacity(){

        if(pairCount/size > 0.6){
            int newCapacity = size + 617;

            values = Arrays.copyOf(values, newCapacity);
            size = newCapacity;
        }
    }

    //----------------------------------------------------------------------------------------------------------------//

    public static void main(String[] args) {
        int initialSize = 331;

        HashTable ht = new HashTable(initialSize);

        ht.put("hola", 56);
        ht.put("hello", 45);
        ht.put("lmao", 73);
        ht.put("lolaso", 12);
        ht.put("kappa", 43);

        System.out.println(ht.get("hola"));
        System.out.println(ht.get("hello"));
        System.out.println(ht.get("lolaso"));
        System.out.println(ht.get("kappa"));


    }

}
