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
     * Adds a new connection between two cities stored in the DiGraph.
     * If any of the cities isn't yet stored in the structure, it will be added
     * @param origin Origin city for the connection
     * @param destination Destination city for the connection
     * @param route data of the new connection
     */
    public void addRoute(City origin, City destination, Connection route){
        AdjListNode newEdge = new AdjListNode();
        newEdge.source = origin;
        newEdge.destination = destination;
        newEdge.label = route;
        newEdge.next = null;

        boolean found = false;

        int i;

        for (i = 0; i < adjList.length; i++) {
            if(adjList[i] == null){
                break;
            }

            if (adjList[i].source.equals(origin)){
                found = true;
                break;
            }
        }

        if(found){
            AdjListNode aux = adjList[i];
            adjList[i].childCount++;
            while(aux.next != null){
                aux = aux.next;
            }
            aux.next = newEdge;
        }else{
            adjList[nextFreeSpot] = new AdjListNode();
            adjList[nextFreeSpot].source = origin;
            adjList[nextFreeSpot].label = null;
            adjList[nextFreeSpot].destination = null;

            adjList[nextFreeSpot].next = newEdge;
            nextFreeSpot++;

            checkCapacity();
        }

    }

    /**
     * Searches the stored route betweeen the two specified cities
     * @param origin
     * @param destination
     * @return null if there is no connection between the two cities. If found, returns a connection object with the route data
     */
    public Connection getLabel(City origin, City destination){
        boolean found = false;

        int i;

        for (i = 0; i < adjList.length; i++) {
            if(adjList[i] == null){
                break;
            }

            if (adjList[i].source.equals(origin)){
                found = true;
                break;
            }
        }

        if(found){
            AdjListNode aux = adjList[i];
            while(aux.next != null){
                aux = aux.next;
                if(aux.destination.equals(destination)){
                    return aux.label;
                }
            }
        }

        return null;
    }

    /**
     * Searches all cities connected directly with the specified origin city
     * @param origin Source city for which the children are wanted
     * @return null if the city isn't stored in the structure. An array of cities if found and filled with those
     * cities with a direct connection.
     */
    public City[] getChildren(City origin) {
        boolean found = false;
        City[] children = null;
        int i;

        for (i = 0; i < adjList.length; i++) {
            if (adjList[i] == null) {
                break;
            }

            if (adjList[i].source.equals(origin)) {
                found = true;
                break;
            }
        }

        if(found){
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
    public Path shortestPath(City from, City to, int mode){
        boolean fromOK = checkCityInStructure(from),
                toOK = checkCityInStructure(to);

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
    protected boolean checkCityInStructure(City c) {
        boolean found = false;

        for (AdjListNode anAdjList : adjList) {
            if (anAdjList == null) {
                break;
            }

            if (anAdjList.source.equals(c)) {
                found = true;
                break;
            }
        }

        return found;
    }

    /**
     * Calculates the shortest path in terms of distance betweeen the two specified cities using Dijkstra's algorithm
     * @param from Origin city. Path's start
     * @param to Destination city. Path's finnish
     * @return Result path.
     */
    protected Path dijkstra(City from, City to, int mode){
        ArrayList<AdjListNode> conjuntVertex = new ArrayList<>();
        Connection[] d = new Connection[nextFreeSpot];
        Integer[] c = new Integer[nextFreeSpot];

        AdjListNode fromNode = null;
        AdjListNode toNode = null;
        int fromIndex = -1;
        int toIndex = -1;

        for (int i = 0; i < nextFreeSpot; i++) {
            if(adjList[i].source.getName().equals(to.getName())){
                toIndex = i;
                toNode = adjList[i];
            }
            if(adjList[i].source.getName().equals(from.getName())) {
                fromNode = adjList[i];
                fromIndex = i;
            }

        }

        //INICIALIZACION
        for(int i = 0; i < nextFreeSpot; i++){

            AdjListNode w = adjList[i];

            if(w.source.getName().equals(from.getName())) {
                d[i] = new Connection(from.getName(), null, 0, 0);
            } else{
                conjuntVertex.add(w);
                d[i] = getLabel(from, w.source);
                c[i] = d[i] == null? null: fromIndex;
            }

        }

        if(mode == PATH_BY_DISTANCE) {

            //CALCULO DE DISTANCIAS ENTRE VERTICES MEDIANTE DISTANCIA ENTRE CIUDADES
            for (int i = 0; i < nextFreeSpot - 1; i++) {

                //Escogemos un vertice u tal que su distancia al origen sea minima
                int val = Integer.MAX_VALUE;
                AdjListNode u = null;
                int j = 0;
                for (AdjListNode w : conjuntVertex) {
                    for (j = 0; j < d.length; j++)
                        if (d[j].getFrom().equals(w.source.getName()))
                            break;

                    if (d[j].getDistance() <= val) {
                        u = w;
                        val = d[j].getDistance();
                    }
                }

                conjuntVertex.remove(u);

                //Actualizacion de las distancias minimas de todos los vertices del conjunto pasando por u
                for (AdjListNode w : conjuntVertex) {
                    int k = 0;
                    for (k = 0; k < d.length; k++)
                        if (d[k].getFrom().equals(w.source.getName()))
                            break;

                    Connection uw = getLabel(u.source, w.source);

                    if (d[j].getDistance() + uw.getDistance() < d[k].getDistance()) {
                        d[k].setDistance(d[j].getDistance() + uw.getDistance());
                        c[k] = j;
                    }
                }
            }

            return getPath(c, fromNode, toNode, fromIndex, toIndex);
        }else{

            //CALCULO DE DISTANCIAS ENTRE VERTICES MEDIANTE TIEMPO ENTRE CIUDADES
            for (int i = 0; i < nextFreeSpot - 1; i++) {

                //Escogemos un vertice u tal que su distancia al origen sea minima
                int val = Integer.MAX_VALUE;
                AdjListNode u = null;
                int j = 0;
                for (AdjListNode w : conjuntVertex) {
                    for (j = 0; j < d.length; j++)
                        if (d[j].getFrom().equals(w.source.getName()))
                            break;

                    if (d[j].getDuration() <= val) {
                        u = w;
                        val = d[j].getDuration();
                    }
                }

                conjuntVertex.remove(u);

                //Actualizacion de las distancias minimas de todos los vertices del conjunto pasando por u
                for (AdjListNode w : conjuntVertex) {
                    int k = 0;
                    for (k = 0; k < d.length; k++)
                        if (d[k].getFrom().equals(w.source.getName()))
                            break;

                    Connection uw = getLabel(u.source, w.source);

                    if (d[j].getDuration() + uw.getDuration() < d[k].getDuration()) {
                        d[k].setDuration(d[j].getDuration() + uw.getDuration());
                        c[k] = j;
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

            semiPath = getLabel(adjList[k].source, adjList[j].source);

            p.addToPath(adjList[k].source, semiPath.getDistance(), semiPath.getDuration());
        }

        return p;
    }

}
