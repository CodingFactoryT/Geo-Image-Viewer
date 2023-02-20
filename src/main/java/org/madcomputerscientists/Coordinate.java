package org.madcomputerscientists;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Coordinate {
    private double latitude_dec;
    private double longitude_dec;

    private String latitude_str;
    private String longitude_str;


    public Coordinate(double latitude, double longitude) {
        this.latitude_dec = latitude;
        this.longitude_dec = longitude;

        this.latitude_str = decToStr(latitude);
        this.longitude_str = decToStr(longitude);
    }

    /**
     * uses the <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine</a>
     * formula to calculate the distance between two coordinates on earth
     * @param coordinate coordinate to which the distance should be calculated
     * @return distance from the coordinate that the method was called on to the given coordinate
     */
    public double distanceTo(Coordinate coordinate) {
        final int EARTH_RADIUS_KM = 6371;

        double lat1 = this.latitude_dec;
        double long1 = this.longitude_dec;
        double lat2 = coordinate.latitude_dec;
        double long2 = coordinate.longitude_dec;

        double latDistance = Math.toRadians(lat2-lat1);
        double longDistance = Math.toRadians(long2-long1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(longDistance / 2) * Math.sin(longDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c * 1000; //convert distance from kilometers in meters
    }

    /**
     *
     * @param coordinate the coordinate the range should be compared to
     * @param range range in meter
     * @return true/false if coordinate is in range
     */
    public boolean isInRange(Coordinate coordinate, double range) {
        return distanceTo(coordinate) <= range;
    }

    public static double toDecimal(String value) {
        value = value.replaceAll(" ", "").replaceAll(",", ".");

        int degreeIndex = value.indexOf('°');
        int minuteIndex = value.indexOf('\'');
        int secondsIndex = value.indexOf("\"");

        int degrees = Integer.parseInt(value.substring(0, degreeIndex));

        if (degrees < 0) {
            degrees *= -1;
        }

        int minutes = Integer.parseInt(value.substring(degreeIndex + 1, minuteIndex));
        double seconds = Double.parseDouble(value.substring(minuteIndex + 1, secondsIndex));

        double result = degrees + (minutes * 60.0 + seconds) / 3600.0;

        if (value.contains("-")) {
            result *= -1;
        }

        return result;
    }

    public static String decToStr(double value) {
        DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));

        String pre = "";
        if (value < 0) {
            pre = "-";
            value *= -1;
        }

        int degrees = (int) value;
        double a = (value - degrees) * 60;
        int minutes = (int) a;
        double seconds = (a - minutes) * 60;

        return pre + degrees + "° " + minutes + "' " + df.format(seconds) + "\"";
    }

    public String toString() {
        return "Lat: " + latitude_str + " Long: " + longitude_str;
    }

    public double getLatitude_dec() {
        return this.latitude_dec;
    }
    public double getLongitude_dec() {
        return longitude_dec;
    }

    public String getLatitude_str() {
        return this.latitude_str;
    }

    public String getLongitude_str() {
        return this.longitude_str;
    }

    public void setCoordinate(double latitude, double longitude){
        this.latitude_dec = latitude;
        this.longitude_dec = longitude;

        this.latitude_str = decToStr(latitude);
        this.longitude_str = decToStr(longitude);
    }

    public String getLatitude_dec_Formatted(){
        DecimalFormat df = new DecimalFormat("0.000000", DecimalFormatSymbols.getInstance(Locale.US));
        return df.format(latitude_dec);
    }

    public String getLongitude_dec_Formatted(){
        DecimalFormat df = new DecimalFormat("0.000000", DecimalFormatSymbols.getInstance(Locale.US));
        return df.format(longitude_dec);
    }
}
