package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.awt.geom.Path2D;
import java.lang.Math;

public class LngLatHandler implements LngLatHandling
{
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition)
    {
        double longSquared = Math.pow((endPosition.lng() - startPosition.lng()), 2);
        double latSquared = Math.pow((endPosition.lat() - startPosition.lat()),2);
        return Math.sqrt((latSquared + longSquared));
    }

    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition)
    {
        return distanceTo(startPosition, otherPosition) < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    //iterate over all the points and create a boundary
    //using path2d

    @Override
    public boolean isInRegion(LngLat position, NamedRegion region)
    {
        Path2D.Double polygon = new Path2D.Double();
        polygon.moveTo(region.vertices()[0].lat(), region.vertices()[0].lng());
        for(int i = 1; i < region.vertices().length; i++)
        {
            polygon.lineTo(region.vertices()[i].lat(), region.vertices()[i].lng());
        }
        polygon.closePath();
        return polygon.contains(position.lat(), position.lng());
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle)
    {
        double angleRad = Math.toRadians(angle);
        double diffLat = Math.toRadians(SystemConstants.DRONE_MOVE_DISTANCE) * Math.cos(angleRad);
        double diffLng = Math.toRadians(SystemConstants.DRONE_MOVE_DISTANCE) * Math.sin(angleRad);
        return new LngLat((startPosition.lng() + diffLng), (startPosition.lat()) + diffLat);
    }
}
