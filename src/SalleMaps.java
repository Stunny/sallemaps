import Collections.CityGraph;
import Collections.HashedCityGraph;
import Collections.RBTCityGraph;
import Model.City;
import Model.Connection;
import Model.Path;
import Utils.CitySeeker;
import Utils.JsonReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SalleMaps {

    private CityGraph graph;

    private HashedCityGraph hashedGraph;

    private RBTCityGraph rbtGraph;

    public SalleMaps(){
        graph = null;
    }

    public void menu(){

        String option, suboption = "0";
        CityGraph selectedGraph = graph;

        do{
            printMenu();
            System.out.print("Option: ");
            option = readInput();
            System.out.println();

            switch(option){
                case "1":
                    System.out.println();
                    System.out.print("Introduce the Json file graph's path: ");
                    importMap(readInput());
                    break;
                case "2":
                    if (graph == null){
                        System.out.println();
                        System.err.println("Before doing any operation you must initialize the city graph. Please execute option #1.");
                        System.out.println();
                    }else {
                        do{
                            System.out.println();
                            System.out.println("Please select the level of optimization of the task:");
                            printSubMenu();

                            suboption = readInput();
                            switch(suboption){
                                case "1":
                                    selectedGraph = graph;
                                    break;
                                case "2":
                                    selectedGraph = rbtGraph;
                                    break;
                                case "3":
                                    selectedGraph = hashedGraph;
                                    break;
                            }
                        }while(!suboption.equals("1") && !suboption.equals("2") && !suboption.equals("3"));
                        searchCity(selectedGraph);
                    }
                    break;
                case "3":
                    if (graph == null){
                        System.out.println();
                        System.err.println("Before doing any operation you must initialize the city graph. Please execute option #1.");
                        System.out.println();
                    }else {
                        do{
                            System.out.println();
                            System.out.println("Please select the level of optimization of the task:");
                            printSubMenu();

                            suboption = readInput();
                            switch(suboption){
                                case "1":
                                    selectedGraph = graph;
                                    break;
                                case "2":
                                    selectedGraph = rbtGraph;
                                    break;
                                case "3":
                                    selectedGraph = hashedGraph;
                                    break;
                            }
                        }while(!suboption.equals("1") && !suboption.equals("2") && !suboption.equals("3"));
                        calculateRoute(selectedGraph);
                    }
                    break;

                case "4":
                    System.out.println("Bye");
                    System.exit(1);
                    break;

                default:
                    System.out.println();
                    System.err.println("Option "+option+" does not exist.");
                    System.out.println();
            }

        }while(true);

    }

    private void importMap(String path) {
        JsonReader jr = new JsonReader();
        JsonObject mapJson = jr.lecturaObject(path);
        if(mapJson == null)
            return;

        JsonArray citiesJson = mapJson.get("cities").getAsJsonArray();
        City[] cities = new City[citiesJson.size()];

        for (int i = 0; i < cities.length; i++) {

            JsonObject cityJson = citiesJson.get(i).getAsJsonObject();

            cities[i] = new City(
                    cityJson.get("name").getAsString(),
                    cityJson.get("address").getAsString(),
                    cityJson.get("country").getAsString(),
                    cityJson.get("latitude").getAsDouble(),
                    cityJson.get("longitude").getAsDouble()
            );

        }

        graph = new CityGraph(cities);
        hashedGraph = new HashedCityGraph(cities);
        rbtGraph = new RBTCityGraph(cities);

        JsonArray connectionsJson = mapJson.get("connections").getAsJsonArray();
        int connsize = connectionsJson.size();

        for (int i = 0; i < connsize; i++) {
            JsonObject connJson = connectionsJson.get(i).getAsJsonObject();

            graph.addRoute(
                    new Connection(
                            connJson.get("from").getAsString(),
                            connJson.get("to").getAsString(),
                            connJson.get("distance").getAsInt(),
                            connJson.get("duration").getAsInt()
                    )
            );

            hashedGraph.addRoute(new Connection(
                    connJson.get("from").getAsString(),
                    connJson.get("to").getAsString(),
                    connJson.get("distance").getAsInt(),
                    connJson.get("duration").getAsInt()
            ));

            rbtGraph.addRoute(new Connection(
                    connJson.get("from").getAsString(),
                    connJson.get("to").getAsString(),
                    connJson.get("distance").getAsInt(),
                    connJson.get("duration").getAsInt()
            ));


        }
    }

    private void searchCity(CityGraph selectedGraph) {

        System.out.println();
        System.out.print("Type city name: ");
        String origin = readInput();

        if(selectedGraph.checkCityInStructure(origin)){
            printSearchResult(origin, selectedGraph);

            System.out.println();

        }else{
            CitySeeker seeker = new CitySeeker();
            CitySeeker inverseSeeker = new CitySeeker();

            seeker.seek(origin);
            if(seeker.hasFound()){
                City result = seeker.getSearchResult();

                graph.addCity(result);
                rbtGraph.addCity(result);
                hashedGraph.addCity(result);

                ArrayList<City> currentStoredCities = graph.getAllCities();

                seeker.setCities(currentStoredCities);
                seeker.connect(result, currentStoredCities);

                if(seeker.isConnected()){
                    List<Connection> connResult = seeker.getConnectionResults();

                    for (Connection conn : connResult){
                        if(conn.getDistance() < 300000) {
                            graph.addRoute(conn);
                            rbtGraph.addRoute(conn);
                            hashedGraph.addRoute(conn);

                            ArrayList<City> inverse = new ArrayList<>();
                            inverse.add(result);
                            inverseSeeker.setCities(inverse);
                            inverseSeeker.seek(conn.getTo());
                            inverseSeeker.connect(graph.getCity(conn.getTo()), inverse);
                            List<Connection> inverseResult = inverseSeeker.getConnectionResults();

                            Connection inverseConn = inverseResult.get(0);
                            graph.addRoute(inverseConn);
                            rbtGraph.addRoute(inverseConn);
                            hashedGraph.addRoute(inverseConn);
                        }
                    }

                }

                System.out.println();
                System.out.println("NEW CITY ADDED");
                System.out.println();

                printSearchResult(result.getName(), selectedGraph);
                System.out.println();
            } else {
                System.out.println("Couldn't find the city neither in the system nor in the GMaps API.");
            }
        }
    }

    private void calculateRoute(CityGraph selectedGraph) {

        System.out.println();
        System.out.print("Introduce origin city name: ");
        String origin = readInput();

        System.out.println();
        System.out.print("Introduce destination city name: ");
        String destination = readInput();

        System.out.println();
        System.out.println("1. Shortest route");
        System.out.println("2. Fastest route");
        System.out.println();

        System.out.print("Option: ");
        String option = readInput();
        int mode = 0;
        do {
            switch (option){
                case "1":
                    mode = CityGraph.PATH_BY_DISTANCE;
                    break;
                case "2":
                    mode = CityGraph.PATH_BY_DURATION;
                    break;
                default:
                    System.out.println();
                    System.err.println("Option "+option+" does not exist.");
                    System.out.println();

                    System.out.print("Option: ");
                    option = readInput();
            }
        }while(!option.equals("1") && !option.equals("2"));
        System.out.println();


        try {
            long now = System.nanoTime();
            Path p = graph.shortestPath(origin, destination, mode);
            System.out.println(p.toString());

            System.out.println();
            System.out.println("Using "+selectedGraph.getClass().toString());
            System.out.println("Task finished in: "+Long.toString((System.nanoTime()-now)/1000)+"us");

        } catch (CityGraph.CityNotFoundException e) {
            System.err.println(e.getMessage());
        }

        System.out.println();
    }

    private void printSearchResult(String origin, CityGraph selectedGraph){
        long now = System.nanoTime();
        City c = selectedGraph.getCity(origin);

        System.out.println();
        System.out.println("-->Name: "+c.getName());
        System.out.println("-->Country: "+c.getCountry());
        System.out.println("-->Coordinates: "+c.getLatitude()+", "+c.getLongitude());
        System.out.println("-->Connections: ");

        City[] conns = selectedGraph.getChildren(origin);

        if(conns == null){
            System.out.println();
            System.out.println("\t NONE");
            return;
        }

        for (City city : conns){

            Connection conn = selectedGraph.getLabel(origin, city.getName());

            System.out.println();
            System.out.println("\t>-->Name: "+city.getName());
            System.out.println("\t>-->Country: "+city.getCountry());
            System.out.println("\t>-->Coordinates: "+city.getLatitude()+", "+city.getLongitude());
            System.out.println("\t>-->Distance from "+origin+": "+Float.toString(conn.getDistance()/1000)+"km");
            System.out.println("\t>-->Duration of trip from "+origin+": "+LocalTime.ofSecondOfDay(conn.getDuration()).toString());
        }

        System.out.println();
        System.out.println("Using "+selectedGraph.getClass().toString());
        System.out.println("Task finished in: "+Long.toString((System.nanoTime()-now)/1000)+"us");
    }

    private String readInput(){
        Scanner kb = new Scanner(System.in);
        return kb.nextLine();
    }

    private void printMenu(){
        System.out.println("SALLE MAPS");
        System.out.println();
        System.out.println("1. Import map");
        System.out.println("2. Search city");
        System.out.println("3. Calculate route");
        System.out.println("4. Shut down");
        System.out.println();
    }

    private void printSubMenu(){
        System.out.println();
        System.out.println("1. No optimization");
        System.out.println("2. Indexes stored in a Red/Black Tree");
        System.out.println("3. Indexes stored in a Hash Table");
        System.out.println();
    }


}
