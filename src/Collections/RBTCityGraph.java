package Collections;

import Model.City;
import Model.Connection;
import Model.Path;

import java.util.ArrayList;

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
    public void addCity(City c) {
        cityIndexes.insert(c.getName(), nextFreeSpot);
        super.addCity(c);
    }

    @Override
    /**
     * Adds a new connection between two cities stored in the DiGraph.
     * If any of the cities isn't yet stored in the structure, it will be added
     * @param origin Origin city for the connection
     * @param destination Destination city for the connection
     * @param route data of the new connection
     */
    public void addRoute(Connection route) {

        Object originIndex = cityIndexes.get(route.getFrom());
        Object destIndex = cityIndexes.get(route.getTo());

        AdjListNode newEdge = new AdjListNode();
        newEdge.label = route;
        newEdge.next = null;

        if (originIndex != null && destIndex != null) {
            int i = (int) originIndex,
                    j = (int) destIndex;

            newEdge.source = adjList[i].source;
            newEdge.destination = adjList[j].source;

            AdjListNode aux = adjList[i];
            adjList[i].childCount++;
            while (aux.next != null) {
                aux = aux.next;
            }
            aux.next = newEdge;
        }

    }

    @Override
    protected Integer getIndex(String name) {

        Integer index = (Integer) cityIndexes.get(name);

        return index == null? -1: index;
    }
}
