import Collections.CityGraph;
import Collections.HashedCityGraph;
import Collections.RBTCityGraph;
import Model.City;
import Model.Connection;
import Network.HttpRequest;
import Network.WSGoogleMaps;
import Utils.JsonReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalTime;
import java.util.Scanner;

public class SalleMaps {

    private CityGraph graph;

    private HashedCityGraph hashedGraph;

    private RBTCityGraph rbtGraph;

    public SalleMaps(){
        graph = null;
    }

    public void menu(){

        String option;

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
                    }else
                        searchCity();
                    break;
                case "3":
                    if (graph == null){
                        System.out.println();
                        System.err.println("Before doing any operation you must initialize the city graph. Please execute option #1.");
                        System.out.println();
                    }else
                        calculateRoute();
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

    private void searchCity() {

        System.out.println();
        System.out.print("Type city name: ");
        String origin = readInput();

        if(hashedGraph.checkCityInStructure(origin)){
            City c = hashedGraph.getCity(origin);

            System.out.println();
            System.out.println("-->Name: "+c.getName());
            System.out.println("-->Country: "+c.getCountry());
            System.out.println("-->Coordinates: "+c.getLatitude()+", "+c.getLongitude());
            System.out.println("-->Connections: ");

            City[] conns = hashedGraph.getChildren(origin);

            if(conns == null){
                System.out.println();
                System.out.println("\t NONE");
                return;
            }

            for (City city : conns){

                Connection conn = hashedGraph.getLabel(origin, city.getName());

                System.out.println();
                System.out.println("\t>-->Name: "+city.getName());
                System.out.println("\t>-->Country: "+city.getCountry());
                System.out.println("\t>-->Coordinates: "+city.getLatitude()+", "+city.getLongitude());
                System.out.println("\t>-->Distance from "+origin+": "+Float.toString(conn.getDistance()/1000)+"km");
                System.out.println("\t>-->Duration of trip from "+origin+": "+LocalTime.ofSecondOfDay(conn.getDuration()).toString());
            }

            System.out.println();

        }else{
            WSGoogleMaps ws = WSGoogleMaps.getInstance();
            ws.geolocate(origin, new HttpRequest.HttpReply() {
                @Override
                public void onSuccess(String s) {
                    //todo
                }

                @Override
                public void onError(String s) {
                    //todo
                }
            });
        }
    }

    private void calculateRoute() {

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


        long now = System.nanoTime();
        System.out.println(
                graph.shortestPath(origin, destination, mode)
                        .toString()
        );
        long noNow = System.nanoTime()-now;
        System.out.println();
        System.out.println("Normal Graph-->Calculated in: "+Float.toString(noNow/1000)+"us");
        System.out.println();


        now = System.nanoTime();
        System.out.println(
                hashedGraph.shortestPath(origin, destination, mode)
                        .toString()
        );
        noNow = System.nanoTime()-now;
        System.out.println("Hashed indexes Graph-->Calculated in: "+Float.toString(noNow/1000)+"us");
        System.out.println();

        /*

        long now = System.currentTimeMillis();
        System.out.println(
                rbtGraph.shortestPath(origin, destination, mode)
                        .toString()
        );
        long noNow = System.currentTimeMillis()-now;
        System.out.println("RBT indexes Graph-->Calculated in: "+Long.toString(noNow)+"ms");

        System.out.println();*/
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

    private String readInput(){
        Scanner kb = new Scanner(System.in);
        return kb.nextLine();
    }

}
