package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class aStar2
{
    final int DIRECTIONS = 16;
    boolean invalidRegion = false;

    private final LngLatHandler handler = new LngLatHandler();

    private boolean isValid(LngLat location)
    {
        //constants of map boundaries w.r.t latitude and longitude

        final double LATMAX = 90;
        final double LATMIN = -90;
        final double LNGMIN = -180;
        final double LNGMAX = 180;
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

    public List<LngLat> aStarRouter(LngLat start, LngLat end, List<NamedRegion> blockedRegions)
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


        Queue<RouteNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(RouteNode::heuristic));
        Map<LngLat, RouteNode> allNodes = new HashMap<>();

        RouteNode startNode = new RouteNode(start, null, 0d, 1.5 * handler.distanceTo(start, end), 999);
        openSet.add(startNode);
        allNodes.put(start, startNode);


        while(!openSet.isEmpty())
        {
            RouteNode next = openSet.poll();

            //if we've found our target:

            if(handler.isCloseTo(next.getCurrent(), end))
            {
                List<LngLat> route = new ArrayList<>();
                RouteNode current = next;
                do
                {
                    route.add(0, current.getCurrent());
                    current = current.getPrevious();
                }
                while(current != null);
                return route;
            }

            for(double angle = 0; angle < 360; angle += (360d / DIRECTIONS))
            {
                LngLat potentialNeighbour = handler.nextPosition(next.getCurrent(), angle);

                for(NamedRegion region : blockedRegions)
                {
                    if(handler.isInRegion(potentialNeighbour, region))
                    {
                        invalidRegion = true;
                        break;
                    }
                }

                if(!invalidRegion)
                {
                    double newScore = next.getRouteScore() + handler.distanceTo(next.getCurrent(), potentialNeighbour);
                    double estScore = 1.5*handler.distanceTo(potentialNeighbour, end);

                    RouteNode nextNode = allNodes.getOrDefault(potentialNeighbour, new RouteNode(potentialNeighbour,
                                                                                    next, newScore, estScore, angle));
                    allNodes.put(potentialNeighbour, nextNode);

                    if (!openSet.contains(nextNode))
                    {
                        openSet.add(nextNode);
                    }
                }
                else
                {
                    //if neighbour is invalid, reset invalidRegion value and check next
                    invalidRegion = false;
                }
            }
        }
        //if we didn't find a valid route to the target: (shouldn't ever be reached)
        return null;
    }
}
