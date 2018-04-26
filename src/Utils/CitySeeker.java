package Utils;

import Model.City;
import Model.Connection;
import Network.DistanceParser;
import Network.GeocodeParser;
import Network.HttpRequest;
import TempNetwork.WSGoogleMaps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class CitySeeker {

    private static final String API_KEY = "AIzaSyCMG_IEevGb9kFUfR_DVgQIT0Gfqrz3S_I";

    private WSGoogleMaps service;

    private City searchResult;

    private List<Connection> connectionResults;

    private boolean found;

    private boolean connected;

    private List<City> cities;


    public CitySeeker(){
        service = WSGoogleMaps.getInstance();
        service.setApiKey(API_KEY);
    }

    /**
     * Searches for the desired city by its name using the Google Maps API. @see Network.WSGoogleMaps
     * @param name
     */
    public void seek(String name){

        service.geolocate(name, new HttpRequest.HttpReply() {
            @Override
            public void onSuccess(String data) {
                setNewResult(GeocodeParser.getCityData(data));
            }

            @Override
            public void onError(String message) {
                System.err.println("City not found in webservice: "+ name+ "\n"+message);
            }
        });



    }

    /**
     * Searches for new connections between cities based on their distance to one another, using the Google Maps API. @see Network.WSGoogleMaps
     */
    public void connect(City source, ArrayList<City> destinations){

        double srcLat = source.getLatitude();
        double srcLng = source.getLongitude();

        int destSize = destinations.size();

        double[] destLats = new double[destSize];
        double[] destLngs = new double[destSize];

        for (int i = 0; i < destSize; i++) {
            destLats[i] = destinations.get(i).getLatitude();
            destLngs[i] = destinations.get(i).getLongitude();
        }

        service.distance(srcLat, srcLng, destLats, destLngs, new HttpRequest.HttpReply() {
            @Override
            public void onSuccess(String data) {
                setConnectionResults(data);
            }

            @Override
            public void onError(String message) {
                System.err.println("Couldn't connect city inside graph: "+ source.getName()+ "\n"+message);
            }
        });

    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public City getSearchResult(){
        found = false;
        return this.searchResult;
    }

    public List<Connection> getConnectionResults() {
        return connectionResults;
    }

    public boolean hasFound() {
        return found;
    }

    public boolean isConnected(){
        return connected;
    }

    private void setNewResult(List<City> data){
        if(!data.isEmpty()){
            found = true;
            searchResult = data.get(0);
            System.out.println(searchResult.toString());
        }
    }

    private void setConnectionResults(String data){

        Gson gson = new Gson();
        JsonObject result = gson.fromJson(data, JsonObject.class);
        JsonArray rows = result.get("rows").getAsJsonArray().get(0).getAsJsonObject()
                .get("elements").getAsJsonArray();

        int qDistances = rows.size();

        ArrayList<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < qDistances - 1; i++) {
            JsonObject row = rows.get(i).getAsJsonObject();

            //Establezco el radio maximo de conexion entre ciudades a 300km
            if(row.get("distance").getAsJsonObject().get("value").getAsInt() < 300000){
                indexes.add(i);
            }
        }
        int qIndexes = indexes.size();
        int[] indexArray = new int[qIndexes];
        for (int i = 0; i < qIndexes; i++) {
            indexArray[i] = indexes.get(i);
        }

        connectionResults = new ArrayList<>();
        DistanceParser.parseDistances(data, searchResult.getName(), cities, indexArray, connectionResults);
    }


}
