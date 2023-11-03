package uk.ac.ed.inf;


import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class aStar
{
    double DIRECTIONS = 16;
    LngLatHandler handler = new LngLatHandler();

    /*boolean isValid(LngLat location)
    {
        return (location.lat() >= LATMIN && location.lat() <= LATMAX
                && location.lng() >= LNGMIN && location.lng() <= LNGMAX);
    }*/
    boolean isUnblocked(LngLat location, List<NamedRegion> blockedRegions)
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

    boolean isDestination(LngLat location, LngLat destination)
    {
        return handler.isCloseTo(location, destination);
    }

    double calculateHValue(LngLat location, LngLat destination)
    {
        return handler.distanceTo(location, destination);
    }

    public List<LngLat> aStarSearch(LngLat start, LngLat end, List<NamedRegion> blockedRegions)
    {
        /*if (!isValid(start))
        {
            return;
        }
        if (!isValid(end))
        {
            return;
        }*/
        if(!isUnblocked(start, blockedRegions))
        {
            return null;
        }
        if(!isUnblocked(end, blockedRegions))
        {
             return null;
        }
        if(isDestination(start, end))
        {
            return null;
        }

        boolean invalidRegion = false;
        double minCost = Double.MAX_VALUE;
        List<LngLat> traversed = new ArrayList<LngLat>();

        while(!isDestination(start, end))
        {
            HashMap<LngLat, Double> frontier = new HashMap<LngLat, Double>();
            for (int i = 0; i < 360 / DIRECTIONS; i++)
            {
                double angle = (360 / DIRECTIONS) * i;
                LngLat potentialNeighbour = handler.nextPosition(start, angle);
                //System.out.println("potential neighbour selected: " + potentialNeighbour.lng() + ", "
                //        + potentialNeighbour.lat());
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
                    //System.out.println("neighbour valid! added.");
                    Double cost = Double.valueOf(handler.distanceTo(potentialNeighbour, end));
                    //System.out.println("costs: " + cost);
                    frontier.put(potentialNeighbour, cost);
                }
                else
                {
                    invalidRegion = false;
                }

            }

            for (Map.Entry<LngLat, Double> entry : frontier.entrySet())
            {
                System.out.println("selecting neighbour: " + entry.getKey().lat() + ", " +
                        entry.getKey().lng());
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
