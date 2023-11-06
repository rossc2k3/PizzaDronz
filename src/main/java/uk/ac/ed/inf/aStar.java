package uk.ac.ed.inf;


import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class aStar
{
    private final double DIRECTIONS = 16;
    private final double LATMIN = -90;
    private final double LATMAX = 90;
    private final double LNGMIN = -180;
    private final double LNGMAX = 180;
    private LngLatHandler handler = new LngLatHandler();

    private boolean isValid(LngLat location)
    {
        return (location.lat() >= LATMIN && location.lat() <= LATMAX
                && location.lng() >= LNGMIN && location.lng() <= LNGMAX);
    }
    private boolean isUnblocked(LngLat location, List<NamedRegion> blockedRegions)
    {
        for(NamedRegion region : blockedRegions)
        {
            if(handler.isInRegion(location, region))
            {
                return false;
            }
        }
        return true;
    }


    public List<LngLat> aStarSearch(LngLat start, LngLat end, List<NamedRegion> blockedRegions)
    {
        if (!isValid(start))
        {
            return null;
        }
        if (!isValid(end))
        {
            return null;
        }
        if(!isUnblocked(start, blockedRegions))
        {
            return null;
        }
        if(!isUnblocked(end, blockedRegions))
        {
             return null;
        }
        if(handler.isCloseTo(start, end))
        {
            return null;
        }

        boolean invalidRegion = false;
        double minCost = Double.MAX_VALUE;
        List<LngLat> traversed = new ArrayList<LngLat>();
        traversed.add(start);
        HashMap<LngLat, Double> frontier = new HashMap<LngLat, Double>();

        while(!handler.isCloseTo(start, end))
        {

            for (int i = 0; i < DIRECTIONS; i++)
            {
                double angle = (360 / DIRECTIONS) * i;
                LngLat potentialNeighbour = handler.nextPosition(start, angle);
                for (NamedRegion region : blockedRegions)
                {
                    if (handler.isInRegion(potentialNeighbour, region))
                    {
                        invalidRegion = true;
                        break;
                    }
                }
                if (!invalidRegion)
                {
                    Double cost = handler.distanceTo(potentialNeighbour, end);
                    frontier.put(potentialNeighbour, cost);
                }
                else
                {
                    invalidRegion = false;
                }

            }

            for (Map.Entry<LngLat, Double> entry : frontier.entrySet())
            {
                LngLat coord = entry.getKey();
                double cost = entry.getValue();
                if(cost < minCost)
                {
                    start = coord;
                    minCost = cost;
                }
            }
            traversed.add(start);
        }

        return traversed;

    }

}
