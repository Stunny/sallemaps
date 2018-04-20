package Model;

public class City {

    private double latitude;
    private double longitude;

    private String name;
    private String address;
    private String country;


    public City(double latitude, double longitude, String name, String address, String country) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean equals(Object o){
        if(o instanceof City){
            City c = (City) o;
            return c.getName().equals(this.name)
                    && c.getLatitude() == this.latitude
                    && c.getLongitude() == this.longitude;
        }

        return false;
    }
}
