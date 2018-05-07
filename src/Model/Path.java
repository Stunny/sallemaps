package Model;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Representa el camino a seguir entre dos ciudades, las ciudades por las que pasa, su duracion y distancia total
 */
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

        Collections.reverse(path);

        StringBuilder sb = new StringBuilder("-->Origin: "+from.getName()
                +"\n-->Destination: "+to.getName()
                +"\n-->Distance: "+ Float.toString(distance/1000)+"km"
                +"\n-->Total duration: "+ LocalTime.ofSecondOfDay(duration).toString()
                +"\n-->Path: "
        );

        for(City c : path){
            sb.append(c.getName()+"->");
        }

        sb.append(to.getName());

        return sb.toString();
    }
}
