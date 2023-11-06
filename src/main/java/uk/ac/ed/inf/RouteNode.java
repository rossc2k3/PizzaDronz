package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class RouteNode implements Comparable<RouteNode>
{
    private final LngLat current;
    private LngLat previous;
    private double routeScore;
    private double estimatedScore;

    RouteNode(LngLat current)
    {
        this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    RouteNode(LngLat current, LngLat previous, double routeScore, double estimatedScore)
    {
        this.current = current;
        this.previous = previous;
        this.routeScore = routeScore;
        this.estimatedScore = estimatedScore;
    }

    LngLat getCurrent()
    {
        return current;
    }

    LngLat getPrevious()
    {
        return previous;
    }

    double getRouteScore()
    {
        return routeScore;
    }

    double getEstimatedScore()
    {
        return estimatedScore;
    }

    void setPrevious(LngLat previous)
    {
        this.previous = previous;
    }

    void setRouteScore(double routeScore)
    {
        this.routeScore = routeScore;
    }

    void setEstimatedScore(double estimatedScore)
    {
        this.estimatedScore = estimatedScore;
    }

    public int compareTo(RouteNode other)
    {
        if (this.estimatedScore > other.estimatedScore)
        {
            return 1;
        }
        else if (this.estimatedScore < other.estimatedScore)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
