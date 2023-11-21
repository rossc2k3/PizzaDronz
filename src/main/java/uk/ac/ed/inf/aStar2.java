package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class aStar2
{
    int DIRECTIONS = 16;
    boolean invalidRegion = false;

    public List<LngLat> aStar2(LngLat start, LngLat end, List<NamedRegion> blockedRegions)
    {
        LngLatHandler handler = new LngLatHandler();

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
                    current = allNodes.get(current.getPrevious());
                }
                while(current != null);
                return route;
            }

            for(double angle = 0; angle < 360; angle += (360 / DIRECTIONS))
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
                    invalidRegion = false;
                }
            }
        }
        //if we didn't find a valid route to the target:

        return null;
    }
}
