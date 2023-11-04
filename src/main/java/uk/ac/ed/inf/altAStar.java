package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class altAStar
{
    double DIRECTIONS = 16;
    double LATMIN = -90;
    double LATMAX = 90;
    double LNGMIN = -180;
    double LNGMAX = 180;
    LngLatHandler handler = new LngLatHandler();

    boolean isValid(LngLat location)
    {
        return (location.lat() >= LATMIN && location.lat() <= LATMAX
                && location.lng() >= LNGMIN && location.lng() <= LNGMAX);
    }
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

    private static List<LngLat> buildPath(Node nodeWrapper)
    {
        List<LngLat> path = new ArrayList<>();
        while(nodeWrapper != null)
        {
            path.add(new LngLat(nodeWrapper.lng(), nodeWrapper.lat()));
            nodeWrapper = nodeWrapper.getPredecessor();
        }
        Collections.reverse(path);
        return path;
    }

    public List<LngLat> altAStarSearch(LngLat start, LngLat end, List<NamedRegion> blockedRegions)
    {
        boolean invalidRegion = false;
        TreeSet<Node> queue = new TreeSet<>();
        Map<LngLat, Node> nodeWrappers = new HashMap<>();
        Set<LngLat> shortestPathFound = new HashSet<>();
        Node startNode = new Node(start, null, 0.0, handler.distanceTo(start, end));
        nodeWrappers.put(start, startNode);
        queue.add(startNode);

        while(!queue.isEmpty())
        {
            Node nodeWrapper = queue.pollFirst();
            assert nodeWrapper != null;
            LngLat node = new LngLat(nodeWrapper.lng(), nodeWrapper.lat());
            shortestPathFound.add(node);
            System.out.println(node);
            if(handler.isCloseTo(node, end))
            {
                return buildPath(nodeWrapper);
            }

            List<LngLat> neighbors = new ArrayList<>();

            for(int i = 0; i < (360 / DIRECTIONS); i++)
            {
                double angle = (360 / DIRECTIONS) * i;
                LngLat potentialNeighbour = handler.nextPosition(start, angle);
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
                    neighbors.add(potentialNeighbour);
                }
            }

            for (LngLat neighbor : neighbors)
            {
                if(shortestPathFound.contains(neighbor))
                {
                    continue;
                }
                double cost = handler.distanceTo(node, neighbor);
                double costFromStart = nodeWrapper.getCostFromStart() + cost;

                Node neighborWrapper = nodeWrappers.get(neighbor);
                if(neighborWrapper == null)
                {
                    neighborWrapper = new Node(neighbor, nodeWrapper, costFromStart, handler.distanceTo(neighbor, end));
                    nodeWrappers.put(neighbor, neighborWrapper);
                    queue.add(neighborWrapper);
                }
                else if (costFromStart < neighborWrapper.getCostFromStart())
                {
                    queue.remove(neighborWrapper);
                    neighborWrapper.setCostFromStart(costFromStart);
                    neighborWrapper.setPredecessor(nodeWrapper);
                    queue.add(neighborWrapper);
                }
            }
        }

        return null;
    }

}
