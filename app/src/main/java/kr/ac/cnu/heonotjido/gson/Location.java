package kr.ac.cnu.heonotjido.gson;

public class Location {
    public String address;
    public double longtitude;
    public double latitude;

    public Location(String address, double longtitude, double latitude) {
        this.address = address;
        this.longtitude = longtitude;
        this.latitude = latitude;
    }
}
