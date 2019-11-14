package ai.marketplace.server.structures;

import ai.marketplace.server.common.Functions;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

//position class. Important remarks
//valid() method checks if lat and long are inside the boundaries

public class Position implements Comparable<Position>
{

    public static final Double maxLatitude=90.00;
    public static final Double minLatitude=-90.00;
    public static final Double maxLongitude=180.00;
    public static final Double minLongitude=-180.00;
    public static final Double radius= 6371000.00;
    public static final Double maxSpeed= 100.00;


    @GeoSpatialIndexed(type=GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint coordinates;
    private GeoJsonPoint roundedCoordinates;
    private Double latitude;
    private Double longitude;
    private Double roundedLatitude;
    private Double roundedLongitude;
    private Long mark;
    private Long roundedMark;
    private String positionOwner;

    public Position(){}


    public Position(Double latitude, Double longitude, Long mark, String  owner) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.mark = mark;
        this.coordinates=new GeoJsonPoint(longitude,latitude);
        this.positionOwner=owner;
        this.roundedCoordinates=new GeoJsonPoint(Functions.floatRounder(this.longitude, 2), Functions.floatRounder(this.latitude, 2));
        this.roundedMark=Math.floorDiv(mark, 60000L)*60000;
        this.roundedLatitude=Functions.floatRounder(this.latitude, 2);
        this.roundedLongitude=Functions.floatRounder(this.longitude, 2);
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Long getMark() {
        return mark;
    }

    public void setMark(Long mark) {

        this.mark = mark;
        this.roundedMark=Math.floorDiv(mark, 60000L)*60000;
    }

    public GeoJsonPoint getCoordinates() {
        return coordinates;
    }

    public GeoJsonPoint getRoundedCoordinates() {
        return roundedCoordinates;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
        this.updateCoordinates();
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
        this.updateCoordinates();
    }

    public String getPositionOwner() {
        return positionOwner;
    }

    public void setPositionOwner(String positionOwner) {
        this.positionOwner = positionOwner;
    }

    public void updateCoordinates()
    {
        if(this.longitude!=null&&this.latitude!=null)
        {
            this.coordinates=new GeoJsonPoint(longitude,latitude);
            this.roundedCoordinates=new GeoJsonPoint(Functions.floatRounder(this.longitude, 2), Functions.floatRounder(this.latitude, 2));
            this.roundedLatitude=Functions.floatRounder(this.latitude, 2);
            this.roundedLongitude=Functions.floatRounder(this.longitude, 2);
        }

    }

    @Override
    public int compareTo(Position that)
    {
        long result=this.mark-that.mark;
        if(result==0)
        {
            return 0;
        }
        else if (result>0)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }

    //returns true if this Position can follow the one passed as argument
    public boolean isCompatibleFollow (Position that)
    {
        double dist=distance(that);
        long time=this.mark-that.mark;
        if(time>0&&(dist/time)<maxSpeed)//dist and time are both positive
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    //return true if this position can precede the one passed as argument
    public boolean isCompatiblePrecede (Position that)
    {
        double dist=distance(that);
        long time=that.mark-this.mark;
        if(time>0&&(dist/time)<maxSpeed)//dist and time are both positive
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //as the two above, but without order
    public boolean isCompatible (Position that)
    {
        double dist=distance(that);
        long time=Math.abs(that.mark-this.mark);
        if(time>0&&(dist/(time))<maxSpeed*100)//dist and time are both positive
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //express a mesure of how much two positions are compatible
    public long compatibility (Position that)
    {
        double dist=distance(that);
        long time=Math.abs(that.mark-this.mark);
        double speed=dist/(time+1);
        if (speed <1)
        {
            speed =1;
        }
        return (long) (100*maxSpeed/speed);
    }


    // https://www.movable-type.co.uk/scripts/latlong.html
    public double distance(Position that)
    {
        double thisLatRad;
        double thisLonRad;
        double thatLatRad;
        double thatLonRad;
        double deltaLat;
        double deltaLon;
        double a;
        double distance;
        try
        {
            thisLatRad=Math.toRadians(this.latitude);
            thisLonRad=Math.toRadians(this.longitude);
            thatLatRad=Math.toRadians(that.latitude);
            thatLonRad=Math.toRadians(that.longitude);
            deltaLat=Math.toRadians(that.latitude-this.latitude);
            deltaLon=Math.toRadians(that.longitude-this.longitude);
            a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(thisLatRad) * Math.cos(thatLatRad) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
            distance = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * radius;
            return distance;
        }
        catch(ArithmeticException ae)
        {
            throw new RuntimeException(ae);
        }
        catch (NumberFormatException nfe)
        {
            throw new RuntimeException(nfe);
        }
    }
    //returns true is the latitude and longitude are between the lower and upper bounds
    public boolean valid() {
        return longitude < maxLongitude && longitude > minLongitude && latitude < maxLatitude && latitude > minLatitude&&mark>0;
    }

    public String formatTimestamp() {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        return isoFormat.format(this.mark);
    }
    @Override
    public String toString() {
        StringBuffer s=new StringBuffer("");
        s.append("lat").append(latitude).append(",lon:").append(longitude).append(",mark:").append(formatTimestamp());
        return s.toString();
    }
}
