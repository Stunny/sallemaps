package Collections;

import Model.City;
import Model.Connection;
import Model.Path;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementa la representacion de un grafo de ciudades dirigido y etiquetado
 * en el que las aristas son las rutas entre ciudad y ciudad y las etiquetas
 * son la distancia de la ruta y la duracion
 */
public class CityGraph {

    public static final int PATH_BY_DISTANCE = 0;
    public static final int PATH_BY_DURATION = 1;

    public class CityNotFoundException extends Exception{

        private String cityNotFound;

        CityNotFoundException(String city){
            this.cityNotFound = city;
        }

        private static final String MSG = "City not found int the structure: ";

        @Override
        public String getMessage() {
            return MSG+cityNotFound;
        }
    }


    protected class AdjListNode{

        Connection label;
        AdjListNode next;

        City source;
        City destination;

        int childCount;

        public AdjListNode(){
            childCount = 0;
        }

    }

    protected int nextFreeSpot;

    protected int conncetionCount;

    protected AdjListNode[] adjList;

    /**
     * Builds a new City DiGraph with space for up to initialSize cities.
     * No routes between cities will be stored when the DiGraph is constructed.
     * @param cities initial set of cities to be stored in the graph.
     */
    public CityGraph(City[] cities){
        adjList = new AdjListNode[cities.length + 331];
        this.conncetionCount = 0;
        this.nextFreeSpot = 0;

        for (int i = 0; i < cities.length; i++) {
            adjList[i] = new AdjListNode();
            adjList[i].source = cities[i];

            adjList[i].label = null;
            adjList[i].next = null;
            adjList[i].destination = null;
            this.nextFreeSpot++;
        }
    }

    /**
     * @return actual city count of the structure
     */
    public int cities(){
        return this.nextFreeSpot;
    }

    /**
     * @return actual route between cities count of the structure
     */
    public int connections(){
        return this.conncetionCount;
    }

    /**
     * Returns the inner indexes of the stored cities on the list
     * @param c
     * @return
     */
    public int getCityIndex(City c){

        return getIndex(c.getName());

    }

    /**
     * @return a list of all the cities stored in the structure
     */
    public ArrayList<City> getAllCities(){

        ArrayList<City> list = new ArrayList<>();

        for (int i = 0; i < nextFreeSpot; i++) {
            list.add(adjList[i].source);
        }

        return list;

    }

    /**
     * Adds a new city to the structure. If the city is already stored in it, does nothing.
     * @param c new city to be added to the structure
     */
    public void addCity(City c){
        if (getIndex(c.getName()) == -1){
            adjList[nextFreeSpot] = new AdjListNode();
            adjList[nextFreeSpot].source = c;

            adjList[nextFreeSpot].label = null;
            adjList[nextFreeSpot].next = null;
            adjList[nextFreeSpot].destination = null;

            this.nextFreeSpot++;

            checkCapacity();
        }
    }

    /**
     * Adds a new connection between two cities stored in the DiGraph.
     * If any of the cities isn't yet stored in the structure, it will be added
     * @param route data of the new connection
     */
    public void addRoute(Connection route){

        int originIndex = getIndex(route.getFrom()),
                destIndex = getIndex(route.getTo());


        if(originIndex != -1){
            AdjListNode newEdge = new AdjListNode();
            newEdge.source = adjList[originIndex].source;
            newEdge.destination = adjList[destIndex].source;
            newEdge.label = route;
            newEdge.next = null;

            AdjListNode aux = adjList[originIndex];
            aux.childCount++;
            while(aux.next != null){
                aux = aux.next;
            }
            aux.next = newEdge;
        }

    }

    /**
     * Searches the stored route betweeen the two specified cities
     * @param origin
     * @param destination
     * @return null if there is no connection between the two cities. If found, returns a connection object with the route data
     */
    public Connection getLabel(String origin, String destination){

        int i = getIndex(origin);

        if(i != -1){
            AdjListNode aux = adjList[i];
            while(aux.next != null){
                aux = aux.next;
                if(aux.destination.getName().equals(destination)){
                    return aux.label;
                }
            }
        }

        return null;
    }

    /**
     * Before using this method, please check if the city is stored in the structure using the checkCityInStructure method
     * @param name city name
     * @return city data
     */
    public City getCity(String name){
        return adjList[getIndex(name)].source;
    }

    /**
     * Searches all cities connected directly with the specified origin city
     * @param origin Source city for which the children are wanted
     * @return null if the city isn't stored in the structure. An array of cities if found and filled with those
     * cities with a direct connection.
     */
    public City[] getChildren(String origin) {
        City[] children = null;
        int i = getIndex(origin);

        if(i != -1){
            children = new City[adjList[i].childCount];
            AdjListNode aux = adjList[i];
            int j = 0;
            while(aux.next != null){
                aux = aux.next;
                children[j] = aux.destination;
                j++;
            }
        }

        return children;
    }

    /**
     * Claculates the shortest path betweeen the two specified cities
     * @param from Origin city. Path's start
     * @param to Destination city. Path's finnish
     * @param mode Shortest path by distance of trip or by duration of trip
     * @return Result path. Null if mode isn't one of the specified or if one of the cities isn't yet in the structure
     */
    public Path shortestPath(String from, String to, int mode) throws CityNotFoundException {
        boolean fromOK = checkCityInStructure(from),
                toOK = checkCityInStructure(to);

        if(!fromOK){
            throw new CityNotFoundException(from);
        }

        if(!toOK){
            throw new CityNotFoundException(to);
        }

        if(fromOK && toOK){

            switch(mode){
                case PATH_BY_DISTANCE:
                    return dijkstra(from, to, mode);

                case PATH_BY_DURATION:
                    return dijkstra(from, to, mode);

                default:
                    return null;
            }
        }

        return null;
    }

    //----------------------------------------------------------------------------------------------------------------//

    /**
     * Checks and resizes the structure in order to keep enough space for later city additions
     * without having to resize it every time a new city is added
     */
    protected void checkCapacity(){

        if(nextFreeSpot/adjList.length > 0.6){
            int newCap = nextFreeSpot += 617;
            adjList = Arrays.copyOf(adjList, newCap);
        }

    }

    /**
     * Checks if the specified city is currently stored in the structure
     */
    public boolean checkCityInStructure(String c) {

        Integer index = getIndex(c);

        return !new Integer(-1).equals(index);
    }

    /**
     * Calculates the shortest path in terms of distance betweeen the two specified cities using Dijkstra's algorithm
     * @param from Origin city. Path's start
     * @param to Destination city. Path's finnish
     * @return Result path.
     */
    protected Path dijkstra(String from, String to, int mode){

        ArrayList<AdjListNode> q = new ArrayList<>();
        Connection[] d = new Connection[nextFreeSpot];
        Integer[] c = new Integer[nextFreeSpot];

        int fromIndex = getIndex(from);
        int toIndex = getIndex(to);
        AdjListNode fromNode = adjList[fromIndex];
        AdjListNode toNode = adjList[toIndex];


        //INICIALIZACION
        for(int i = 0; i < nextFreeSpot; i++){

            AdjListNode w = adjList[i];

            if(w.source.getName().equals(from)) {
                d[i] = new Connection(from, from, 0, 0);
                c[i] = fromIndex;
            } else{
                q.add(w);
                d[i] = getLabel(fromNode.source.getName(), w.source.getName());
                c[i] = d[i] == null? null: fromIndex;
            }

        }

        if(mode == PATH_BY_DISTANCE) {

            //CALCULO DE DISTANCIAS ENTRE VERTICES MEDIANTE DISTANCIA ENTRE CIUDADES
            while (!q.isEmpty()) {

                //Escogemos un vertice u tal que su distancia al origen sea minima
                int val = Integer.MAX_VALUE;
                AdjListNode u = null;
                int j = 0;
                int uIndex = 0;
                for (AdjListNode w : q) {
                   j = getIndex(w.source.getName());

                    if (d[j] != null && !d[j].getTo().equals(from) && d[j].getDistance() <= val) {
                        u = w;
                        val = d[j].getDistance();
                        uIndex = j;
                    }
                }

                q.remove(u);

                //Actualizacion de las distancias minimas de todos los vertices del conjunto pasando por u
                for (AdjListNode w : q) {
                    int k;
                    k = getIndex(w.source.getName());

                    Connection uw = getLabel(u.source.getName(), w.source.getName());

                    if (uw != null && (d[k] == null || d[uIndex].getDistance() + uw.getDistance() < d[k].getDistance())) {
                        d[k] = new Connection(fromNode.source.getName(), uw.getTo(), 0, 0);
                        d[k].setDistance(d[uIndex].getDistance() + uw.getDistance());
                        c[k] = uIndex;
                    }
                }
            }

            return getPath(c, fromNode, toNode, fromIndex, toIndex);
        }else{

            //CALCULO DE DISTANCIAS ENTRE VERTICES MEDIANTE DISTANCIA ENTRE CIUDADES
            while (!q.isEmpty()) {

                //Escogemos un vertice u tal que su distancia al origen sea minima
                int val = Integer.MAX_VALUE;
                AdjListNode u = null;
                int j = 0;
                int uIndex = 0;
                for (AdjListNode w : q) {
                    j = getIndex(w.source.getName());

                    if (d[j] != null && !d[j].getTo().equals(from) && d[j].getDuration() <= val) {
                        u = w;
                        val = d[j].getDuration();
                        uIndex = j;
                    }
                }

                q.remove(u);

                //Actualizacion de las distancias minimas de todos los vertices del conjunto pasando por u
                for (AdjListNode w : q) {
                    int k;
                    k = getIndex(w.source.getName());

                    Connection uw = getLabel(u.source.getName(), w.source.getName());

                    if (uw != null && (d[k] == null || d[uIndex].getDuration() + uw.getDuration() < d[k].getDuration())) {
                        d[k] = new Connection(fromNode.source.getName(), uw.getTo(), 0, 0);
                        d[k].setDuration(d[uIndex].getDuration() + uw.getDuration());
                        c[k] = uIndex;
                    }
                }
            }
            return getPath(c, fromNode, toNode, fromIndex, toIndex);

        }
    }


    /**
     * Devuelve el camino inimo generado por el algoritmo de dijkstra
     * @param c Vector de ciudades mas cercanas por indice
     * @param from nodo origen del viaje
     * @param to nodo destino del viage
     * @param fromIndex indice del nodo origen en el grafo
     * @param toIndex indice del nodo destino en el grafo
     * @return Camino de una
     */
    protected Path getPath(Integer[] c, AdjListNode from, AdjListNode to, int fromIndex, int toIndex){

        Path p = new Path(from.source, to.source);
        int j,
            k = toIndex;

        Connection semiPath;

        while(k != fromIndex){
            j = k;
            k = c[j];

            semiPath = getLabel(adjList[k].source.getName(), adjList[j].source.getName());

            p.addToPath(adjList[k].source, semiPath.getDistance(), semiPath.getDuration());
        }

        return p;
    }

    /**
     * Returns the index corresponding to the city in the adjacencies list
     * @param name city name
     * @return Index in graph. -1 if the city isn't stored in the structure
     */
    protected Integer getIndex(String name){
        Integer index = -1;
        for (int i = 0; i < nextFreeSpot; i++) {
            if(adjList[i].source.getName().equals(name)){
                index = i;
            }
        }

        return index;
    }

}
