package uk.ac.ed.inf;

import org.javatuples.Pair;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlightPathCreator
{
    private static final LngLat APPLETON = new LngLat(-3.186874,55.944494);
    public static Pair<List<List<LngLat>>, List<FlightPath>> createFlightPath(List<Order> validOrders, Restaurant[] restaurantsArr,
                                                                              List<NamedRegion> blockedRegions, NamedRegion central)
    {
        aStar router = new aStar();
        OrderValidator validator = new OrderValidator();
        List<RouteNode> nodes;
        List<LngLat> flightPath;
        List<List<LngLat>> flightPaths = new ArrayList<>();
        List<FlightPath> fullPath = new ArrayList<>();
        List<LngLat> reverse;

        for(Order order : validOrders)
        {

            //flight to restaurant
            Restaurant orderRestaurant = validator.getRestaurant(order, restaurantsArr);
            nodes = router.aStarRouter(APPLETON, orderRestaurant.location(), blockedRegions, central);

            if(nodes != null)
            {
                order.setOrderStatus(OrderStatus.DELIVERED);
                for(int i = 1; i < nodes.size(); i++)
                {
                    RouteNode prevNode = nodes.get(i);
                    LngLat prev = new LngLat(prevNode.getCurrent().lng(), prevNode.getCurrent().lat());
                    fullPath.add(new FlightPath(order, prev, nodes.get(i).getAngle(), nodes.get(i).getCurrent()));
                }

                flightPath = nodes.stream().map(RouteNode::getCurrent).toList();
                flightPaths.add(flightPath);

                //now do the reverse flight
                reverse = new ArrayList<>(flightPath);
                Collections.reverse(reverse);
                flightPaths.add(reverse);

                Collections.reverse(nodes);

                for(int i = 1; i < nodes.size(); i++)
                {
                    RouteNode prevNode = nodes.get(i);
                    RouteNode node = nodes.get(i);
                    node.setAngle(node.getAngle() == 999 ? 999 : (node.getAngle() + 180) % 360);
                    LngLat prev = new LngLat(prevNode.getCurrent().lng(), prevNode.getCurrent().lat());
                    fullPath.add(new FlightPath(order, prev, nodes.get(i).getAngle(), nodes.get(i).getCurrent()));
                }
            }
        }
        return new Pair<>(flightPaths, fullPath);
    }
}
