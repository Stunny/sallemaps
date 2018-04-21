package Model;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

public class Path {

    private City from;

    private City to;

    private ArrayList<City> path;

    private int distance;

    private int duration;

    private boolean straight;

    public Path(City from, City to) {
        this.from = from;
        this.to = to;
        path = new ArrayList<>();

        distance = 0;
        duration = 0;
        straight = false;
    }

    public void addToPath(){

    }

    public City getFrom() {
        return from;
    }

    public City getTo() {
        return to;
    }

    public ArrayList<City> getPath() {
        if(!straight) {
            Collections.reverse(path);
            straight = true;
        }
        return path;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }

    public void addToPath(City c, int distance, int duration){
        path.add(c);
        this.distance += distance;
        this.duration += duration;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder("-->Origin: "+from
                +"\n-->Destination: "+to
                +"\n-->Distance: "+ Float.toString(distance/1000)+"km"
                +"\n-->Total duration: "+ LocalTime.ofSecondOfDay(duration).toString()
                +"\n-->Path: "+from+"->");

        for(City c : path){
            sb.append(c.getName()+"->");
        }

        sb.append(to);

        return sb.toString();
    }
}
