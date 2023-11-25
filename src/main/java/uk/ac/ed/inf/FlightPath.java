package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;

public class FlightPath
{
    private String orderNo;
    private double fromLongitude;
    private double fromLatitude;
    private double angle;
    private double toLongitude;
    private double toLatitude;

    FlightPath(Order order, LngLat from, double angle, LngLat to)
    {
        this.orderNo = order.getOrderNo();
        this.fromLongitude = from.lng();
        this.fromLatitude = from.lat();
        this.angle = angle;
        this.toLongitude = to.lng();
        this.toLatitude = to.lat();
    }

    String getOrderNo()
    {
        return this.orderNo;
    }
    double getFromLongitude()
    {
        return this.fromLongitude;
    }
    double getFromLatitude()
    {
        return this.fromLatitude;
    }
    double getAngle()
    {
        return this.angle;
    }
    double getToLongitude()
    {
        return this.toLongitude;
    }
    double getToLatitude()
    {
        return this.toLatitude;
    }

    void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }
    void setFromLongitude(double longitude)
    {
        this.fromLongitude = longitude;
    }
    void setFromLatitude(double latitude)
    {
        this.fromLatitude = latitude;
    }
    void setAngle(double angle)
    {
        this.angle = angle;
    }
    void setToLongitude(double longitude)
    {
        this.toLongitude = longitude;
    }
    void setToLatitude(double latitude)
    {
        this.toLatitude = latitude;
    }
}
