package Collections;

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

        while(hash < size && !values[hash].key.equals(key)){
            hash = hashI(key, ++i);
        }

        if(values[hash] == null){
            values[hash] = new TablePair();
            values[hash].key = key;
            values[hash].value = value;
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

        while(hash < size && !values[hash].key.equals(key)){
            hash = hashI(key, ++i);
        }

        if(values[hash] != null && values[hash].key.equals(key)){
            values[hash] = null;
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

        while(hash < size && !values[hash].key.equals(key)){
            hash = hashI(key, ++i);
        }

        if(values[hash] == null){
            return null;
        }

        if(values[hash].key.equals(key))
            return values[hash].value;

        return null;
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
            hash += (int)key.charAt(j) * Math.pow(27, j);

        return (hash +(i*(8681-(hash/8681))))%this.size;

    }

    /**
     * Comprueba la cantidad de registros almacenados en la estructura
     * y redimensiona el vector en caso de ser necesario
     */
    private void checkCapacity(){
        int qPairs = 0;

        for(int i = 0; i < values.length; i++)
            qPairs += values[i] != null ? 1 : 0;

        if(qPairs/size > 0.6){
            int newCapacity = size + 617;
            TablePair[] auxValues = new TablePair[newCapacity];

            for(int i = 0; i < size; i++){
                auxValues[i] = values[i];
            }

            values = auxValues;
            size = newCapacity;
        }
    }

}
