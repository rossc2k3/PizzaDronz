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

        List<LngLat> returnAnyway = new ArrayList<>();
        returnAnyway.add(start);


        LngLatHandler handler = new LngLatHandler();

        Queue<RouteNode> openSet = new PriorityQueue<>();
        Map<LngLat, RouteNode> allNodes = new HashMap<>();

        RouteNode startNode = new RouteNode(start, null, 0d, handler.distanceTo(start, end));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while(!openSet.isEmpty())
        {
            RouteNode next = openSet.poll();
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

            for(double angle = 0; angle < 360; angle += 22.5)
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
                                                                                    next.getCurrent(), newScore, estScore));
                    allNodes.put(potentialNeighbour, nextNode);
                    if(newScore < nextNode.getRouteScore())
                    {

                        nextNode.setPrevious(next.getCurrent());
                        nextNode.setRouteScore(newScore);
                        nextNode.setEstimatedScore(newScore + handler.distanceTo(potentialNeighbour, end));
                        openSet.add(nextNode);



                        returnAnyway.add(nextNode.getCurrent());
                    }
                }
                else
                {
                    invalidRegion = false;
                }
            }

        }
        return returnAnyway;
    }
}
