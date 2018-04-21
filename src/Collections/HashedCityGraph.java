package Collections;

import Model.City;
import Model.Connection;
import Model.Path;

import java.util.ArrayList;

public class HashedCityGraph extends CityGraph {

    private HashTable cityIndexes;

    /**
     * Builds a new City DiGraph with space for up to initialSize cities.
     * No routes between cities will be stored when the DiGraph is constructed.
     *
     * @param cities initial set of cities to be stored in the graph.
     */
    public HashedCityGraph(City[] cities) {
        super(cities);

        cityIndexes = new HashTable(cities.length + 331);

        for (int i = 0; i < cities.length; i++) {
            cityIndexes.put(cities[i].getName(), i);
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
    public void addRoute(Connection route) {

        AdjListNode newEdge = new AdjListNode();
        newEdge.label = route;
        newEdge.next = null;

        Object originIndex = cityIndexes.get(route.getFrom()),
                destIndex = cityIndexes.get(route.getTo());

        if(originIndex != null && destIndex != null){
            int i = (int) originIndex;
            int j = (int) destIndex;

            newEdge.source = adjList[i].source;
            newEdge.destination = adjList[j].source;

            AdjListNode aux = adjList[i];
            adjList[i].childCount++;
            while(aux.next != null){
                aux = aux.next;
            }
            aux.next = newEdge;
        }

    }

    @Override
    /**
     * Searches the stored route betweeen the two specified cities
     * @param origin
     * @param destination
     * @return null if there is no connection between the two cities. If found, returns a connection object with the route data
     */
    public Connection getLabel(String origin, String destination){

        Object index = cityIndexes.get(origin);

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
    public City[] getChildren(String origin) {

        City[] children = null;
        Object index = cityIndexes.get(origin);

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

    /**
     * Calculates the shortest path in terms of distance betweeen the two specified cities using Dijkstra's algorithm
     * @param from Origin city. Path's start
     * @param to Destination city. Path's finnish
     * @return Result path.
     */
    protected Path dijkstra(String from, String to, int mode){
        ArrayList<AdjListNode> conjuntVertex = new ArrayList<>();
        Connection[] d = new Connection[nextFreeSpot];
        Integer[] c = new Integer[nextFreeSpot];

        int fromIndex = (int) cityIndexes.get(from);
        int toIndex = (int) cityIndexes.get(to);
        AdjListNode fromNode = adjList[fromIndex];
        AdjListNode toNode = adjList[toIndex];


        //INICIALIZACION
        for(int i = 0; i < nextFreeSpot; i++){

            AdjListNode w = adjList[i];

            if(w.source.getName().equals(from)) {
                d[i] = new Connection(from, null, 0, 0);
            } else{
                conjuntVertex.add(w);
                d[i] = getLabel(fromNode.source.getName(), w.source.getName());
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
                    j = (int) cityIndexes.get(w.source.getName());

                    if (d[j].getDistance() <= val) {
                        u = w;
                        val = d[j].getDistance();
                    }
                }

                conjuntVertex.remove(u);

                //Actualizacion de las distancias minimas de todos los vertices del conjunto pasando por u
                for (AdjListNode w : conjuntVertex) {
                    int k = (int) cityIndexes.get(w.source.getName());

                    Connection uw = getLabel(u.source.getName(), w.source.getName());

                    if (d[k] == null || d[j].getDistance() + uw.getDistance() < d[k].getDistance()) {
                        d[k] = new Connection(fromNode.source.getName(), uw.getTo(), 0, 0);
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
                    j = (int) cityIndexes.get(w.source.getName());

                    if (d[j].getDuration() <= val) {
                        u = w;
                        val = d[j].getDuration();
                    }
                }

                conjuntVertex.remove(u);

                //Actualizacion de las distancias minimas de todos los vertices del conjunto pasando por u
                for (AdjListNode w : conjuntVertex) {
                    int k = (int) cityIndexes.get(w.source.getName());

                    Connection uw = getLabel(u.source.getName(), w.source.getName());

                    if (d[k] == null || d[j].getDuration() + uw.getDuration() < d[k].getDuration()) {
                        d[k] = new Connection(fromNode.source.getName(), uw.getTo(), 0, 0);
                        d[k].setDuration(d[j].getDuration() + uw.getDuration());
                        c[k] = j;
                    }
                }
            }
            return getPath(c, fromNode, toNode, fromIndex, toIndex);

        }
    }
}
