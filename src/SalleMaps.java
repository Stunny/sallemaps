import Collections.CityGraph;
import Model.City;
import Model.Connection;
import Utils.JsonReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Scanner;

public class SalleMaps {

    private CityGraph graph;

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

    private void calculateRoute() {

        System.out.println();
        System.out.print("Introduce origin city name: ");
        String origin = readInput();
        System.out.println();
        System.out.print("Introduce destination city name: ");
        String destination = readInput();

        System.out.println(
                graph.shortestPath(origin, destination, CityGraph.PATH_BY_DURATION)
                        .toString()
        );

    }

    private void searchCity() {

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


        }
    }

}
