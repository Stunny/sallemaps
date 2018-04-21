package Collections;

import Model.City;
import Model.Connection;

public class RBTCityGraph extends CityGraph {

    private RedBlackTree cityIndexes;

    /**
     * Builds a new City DiGraph with space for up to initialSize cities.
     * No routes between cities will be stored when the DiGraph is constructed.
     *
     * @param cities initial set of cities to be stored in the graph.
     */
    public RBTCityGraph(City[] cities) {
        super(cities);

        try {
            cityIndexes = new RedBlackTree(cities[0].getName(), 0, RedBlackTree.STRING_NODE);
        } catch (RedBlackTree.RBTException e) {
            e.printStackTrace();
            return;
        }

        for (int i = 1; i < cities.length; i++) {
            cityIndexes.insert(cities[i].getName(), i);
        }
    }

    @Override
    /**
     * Adds a new connection between two cities stored in the DiGraph.
     * If any of the cities isn't yet stored in the structure, it will be added
     * @param origin Origin city for the connection
     * @param destination Destination city for the connection
     * @param route data of the new connection
     */
    public void addRoute(City origin, City destination, Connection route) {

        AdjListNode newEdge = new AdjListNode();
        newEdge.source = origin;
        newEdge.destination = destination;
        newEdge.label = route;
        newEdge.next = null;

        Object index = cityIndexes.get(origin.getName());

        if (index != null) {
            int i = (int) index;

            AdjListNode aux = adjList[i];
            adjList[i].childCount++;
            while (aux.next != null) {
                aux = aux.next;
            }
            aux.next = newEdge;
        } else {
            adjList[nextFreeSpot] = new AdjListNode();
            adjList[nextFreeSpot].source = origin;
            adjList[nextFreeSpot].label = null;
            adjList[nextFreeSpot].destination = null;

            adjList[nextFreeSpot].next = newEdge;
            cityIndexes.insert(origin.getName(), nextFreeSpot);

            nextFreeSpot++;

            checkCapacity();
        }

    }

    @Override
    /**
     * Searches the stored route betweeen the two specified cities
     * @param origin
     * @param destination
     * @return null if there is no connection between the two cities. If found, returns a connection object with the route data
     */
    public Connection getLabel(City origin, City destination){

        Object index = cityIndexes.get(origin.getName());

        if(index != null){
            int i = (int) index;

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

    @Override
    /**
     * Searches all cities connected directly with the specified origin city
     * @param origin Source city for which the children are wanted
     * @return null if the city isn't stored in the structure. An array of cities if found and filled with those
     * cities with a direct connection.
     */
    public City[] getChildren(City origin) {

        City[] children = null;
        Object index = cityIndexes.get(origin.getName());

        if(index != null){

            int i = (int) index;

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
}
