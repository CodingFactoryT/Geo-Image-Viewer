package org.madcomputerscientists;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import org.jxmapviewer.viewer.GeoPosition;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

public class ImageRecordLocation {
    private final String fileName;
    private File file;
    private BufferedImage image;
    private Coordinate coordinate;
    private ClickableWaypoint waypoint;

    public ImageRecordLocation(File file) throws ImageProcessingException, IOException {
        coordinate = this.getCoordinateByFile(file);
        fileName = file.getName();
        this.file = file;
        image = ImageIO.read(file);
        GeoPosition geoPosition = new GeoPosition(coordinate.getLatitude_dec(), coordinate.getLongitude_dec());
        waypoint = new ClickableWaypoint(geoPosition, 20,20);
    }

    /**
     *
     * @param file The file from which the geo data should be extracted
     * @return boolean tells if set was successful
     * @throws ImageProcessingException
     * @throws IOException
     */
    private Coordinate getCoordinateByFile(File file) throws ImageProcessingException, IOException {
        Metadata metadata = null;
        try{
            metadata = ImageMetadataReader.readMetadata(file);
        } catch(Exception e){
            ErrorMessageHandler.addErrorMessage("File format could not be determined");
            return new Coordinate(404, 404);    //coordinate will not be displayed on final map
        }
        String latitude_str = null;
        String longitude_str = null;

        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                if (tag.getTagName().equals("GPS Latitude")) {
                    latitude_str = tag.getDescription().replaceAll(" ", "").replaceAll(",", ".");
                }
                else if (tag.getTagName().equals("GPS Longitude")) {
                    longitude_str = tag.getDescription().replaceAll(" ", "").replaceAll(",", ".");
                }
            }
        }
        if (latitude_str == null || longitude_str == null) {
            return new Coordinate(404, 404);    //coordinate will not be displayed on final map
        }
        double latitude_dec = Coordinate.toDecimal(latitude_str);
        double longitude_dec = Coordinate.toDecimal(longitude_str);
        return new Coordinate(latitude_dec, longitude_dec);
    }

    public boolean equals(ImageRecordLocation irl) {
        Coordinate thisCoordinate = this.coordinate;
        Coordinate parameterCoordinate = irl.coordinate;
        return thisCoordinate.getLatitude_dec() == parameterCoordinate.getLatitude_dec()
               && thisCoordinate.getLongitude_dec() == parameterCoordinate.getLongitude_dec();
    }

    public BufferedImage getBufferedImage() {
        return image;
    }

    public String getFileName() {
        return fileName;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public ClickableWaypoint getWaypoint() {
        return waypoint;
    }

    public File getFile(){
        return file;
    }
}
