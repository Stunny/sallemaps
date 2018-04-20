package Collections;

import Model.City;
import Model.Connection;

import java.util.Arrays;

/**
 * Implementa la representacion de un grafo de ciudades dirigido y etiquetado
 * en el que las aristas son las rutas entre ciudad y ciudad y las etiquetas
 * son la distancia de la ruta y la duracion
 */
public class CityGraph {

    private class AdjListNode{

        Connection label;
        AdjListNode next;

        City source;
        City destination;

        int childCount;

        public AdjListNode(){
            childCount = 0;
        }

    }

    private int cityCount;

    private int conncetionCount;

    private AdjListNode[] adjList;

    /**
     * Builds a new City DiGraph with space for up to initialSize cities.
     * No routes between cities will be stored when the DiGraph is constructed.
     * @param cities initial set of cities to be stored in the graph.
     */
    public CityGraph(City[] cities){
        adjList = new AdjListNode[cities.length + 331];
        this.conncetionCount = 0;
        this.cityCount = 0;

        for (int i = 0; i < cities.length; i++) {
            adjList[i] = new AdjListNode();
            adjList[i].source = cities[i];

            adjList[i].label = null;
            adjList[i].next = null;
            adjList[i].destination = null;
        }
    }

    /**
     * @return actual city count of the structure
     */
    public int cities(){
        return this.cityCount;
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
            adjList[i] = new AdjListNode();
            adjList[i].source = origin;
            adjList[i].label = null;
            adjList[i].destination = null;

            adjList[i].next = newEdge;
        }

        checkCapacity();
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
    //----------------------------------------------------------------------------------------------------------------//

    /**
     * Checks and resizes the structure in order to keep enough space for later city additions
     * without having to resize it every time a new city is added
     */
    private void checkCapacity(){

        if(cityCount/adjList.length > 0.6){
            int newCap = cityCount += 617;
            adjList = Arrays.copyOf(adjList, newCap);
        }

    }
}
