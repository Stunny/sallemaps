package Model;

import java.util.ArrayList;
import java.util.Collections;

public class Path {

    private City from;

    private City to;

    private ArrayList<City> path;

    private int distance;

    private int duration;

    public Path(City from, City to) {
        this.from = from;
        this.to = to;
        path = new ArrayList<>();

        distance = 0;
        duration = 0;
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
        Collections.reverse(path);
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
}
