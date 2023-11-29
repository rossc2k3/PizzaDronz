package uk.ac.ed.inf.pathing;

import uk.ac.ed.inf.ilp.data.LngLat;

public class RouteNode
{
    private final LngLat current;
    private RouteNode previous;
    private double routeScore;
    private double estimatedScore;
    private double angle;


    RouteNode(LngLat current, RouteNode previous, double routeScore, double estimatedScore, double angle)
    {
        this.current = current;
        this.previous = previous;
        this.routeScore = routeScore;
        this.estimatedScore = estimatedScore;
        this.angle = angle;
    }

    LngLat getCurrent()
    {
        return current;
    }

    RouteNode getPrevious()
    {
        return previous;
    }

    double getRouteScore()
    {
        return routeScore;
    }

    double getAngle()
    {
        return angle;
    }

    void setAngle(double angle)
    {
        this.angle = angle;
    }


    double heuristic()
    {
        return routeScore + (1.5 * estimatedScore);
    }
}
