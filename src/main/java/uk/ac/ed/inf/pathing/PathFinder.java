package uk.ac.ed.inf.pathing;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class PathFinder
{
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

    public List<RouteNode> routeFind(LngLat start, LngLat end, List<NamedRegion> blockedRegions, NamedRegion centralArea)
    {

        /*
        making sure our start and endpoints are valid locations. not strictly necessary in our testing but to be robust
         */

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

        /*
        setup structures for path, nodes and first node
         */

        Queue<RouteNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(RouteNode::heuristic));
        Map<LngLat, RouteNode> allNodes = new HashMap<>();

        RouteNode startNode = new RouteNode(start, null, 0d, 1.5 * handler.distanceTo(start, end), 999);
        openSet.add(startNode);
        allNodes.put(start, startNode);


        while(!openSet.isEmpty())
        {
            //select the next node from the queue based on the heuristic
            RouteNode next = openSet.poll();

            //if we've found our target:

            if(handler.isCloseTo(next.getCurrent(), end))
            {
                List<RouteNode> route = new ArrayList<>();

                //set reference angle for last node visited
                next.setAngle(999);

                RouteNode current = next;
                do
                {
                    route.add(0, current);
                    current = current.getPrevious();
                }
                while(current != null);
                return route;
            }


            int DIRECTIONS = 16;
            for(double angle = 0; angle < 360; angle += (360d / DIRECTIONS))
            {
                LngLat potentialNeighbour = handler.nextPosition(next.getCurrent(), angle);

                /*since we are only routing the drone from appleton to the restaurant (and then reversing this path),
                the only illegal move related to the central area rule is when the first move is out of the area and the
                second one is in it. so we check for this and flag it as invalid
                 */

                if(!handler.isInCentralArea(next.getCurrent(), centralArea) && handler.isInCentralArea(potentialNeighbour, centralArea))
                {
                    invalidRegion = true;
                }

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
                    //add valid node to the open set and all nodes

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
        System.err.println("Could not find a valid route.");
        return null;
    }
}
