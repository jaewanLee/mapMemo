package product.dp.io.mapmo.LockScreen;

/**
 * Created by jaewanlee on 2017. 8. 11..
 */

public class CalculateDistance {

    //이게 현재 위치
    double currentLat = 37.502779;
    double currentLon = 127.004145;

    public CalculateDistance(){
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public void setCurrentLon(double currentLon) {
        this.currentLon = currentLon;
    }

    public Double calculate(double markerLat, double markerLon) {
        double value = 6371 * Math.acos(Math.cos(Math.toRadians(currentLat)) * Math.cos(Math.toRadians(markerLat)) * Math.cos(Math.toRadians(markerLon) - Math.toRadians(currentLon)) + Math.sin(Math.toRadians(currentLat)) * Math.sin(Math.toRadians(markerLat)));
        return  value;
    }


}
