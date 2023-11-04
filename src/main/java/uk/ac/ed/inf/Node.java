package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class Node implements Comparable<Node>{
    private final double lng;
    private final double lat;
    private Node predecessor;
    private double costFromStart;
    private final double minCostToTarget;
    private double costSum;


    public Node(LngLat coord, Node predecessor, double costFromStart, double minCostToTarget) {
        this.lng = coord.lng();
        this.lat = coord.lat();
        this.predecessor = predecessor;
        this.costFromStart = costFromStart;
        this.minCostToTarget = minCostToTarget;
        calculateCostSum();
    }

    private void calculateCostSum()
    {
        this.costSum = this.costFromStart + this.minCostToTarget;
    }

    public double lng()
    {
        return this.lng;
    }
    public double lat()
    {
        return this.lat;
    }

    public Node getPredecessor()
    {
        return this.predecessor;
    }

    public void setPredecessor(Node prev)
    {
        this.predecessor = prev;
    }

    public double getCostFromStart()
    {
        return this.costFromStart;
    }
    public void setCostFromStart(double costFromStart)
    {
        this.costFromStart = costFromStart;
        calculateCostSum();
    }


    @Override
    public int compareTo(Node other)
    {
        return Double.compare(this.costSum, other.costSum);
    }
}
