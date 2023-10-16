package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.awt.geom.Path2D;
import java.lang.Math;

public class LngLatHandler implements LngLatHandling
{

    /**
     *
     * @param startPosition     the longitude and latitude of the original coordinates
     * @param endPosition       the longitude and latitude of the new coordinates
     * @return                  the distance between the two coordinates
     */
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition)
    {
        double longSquared = Math.pow((endPosition.lng() - startPosition.lng()), 2);
        double latSquared = Math.pow((endPosition.lat() - startPosition.lat()),2);
        return Math.sqrt((latSquared + longSquared));
    }

    /**
     *
     * @param startPosition     the longitude and latitude of the start coordinates
     * @param otherPosition     the longitude and latitude of some other coordinates
     * @return                  true if both co-ords within "close distance"
     *                          given from system constants, false otherwise
     */
    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition)
    {
        return distanceTo(startPosition, otherPosition) < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }


    /**
     * iterate over all the points and create a boundary
     * using path2d.
     * @param position  given coordinates
     * @param region    the polygon region to check the position against
     * @return          true if coords in region, false if not
     */


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


    /**
     * finds the next position from given start coords and an angle
     * using basic trigonometry.
     * distance travelled is fixed per system constants
     * @param startPosition the starting coords
     * @param angle         the angle the distance is travelling along
     * @return              the new coords
     */
    @Override
    public LngLat nextPosition(LngLat startPosition, double angle)
    {
        double angleRad = Math.toRadians(angle);
        double diffLat = SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angleRad);
        double diffLng = SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angleRad);
        return new LngLat((startPosition.lng() + diffLng), (startPosition.lat()) + diffLat);
    }
}
