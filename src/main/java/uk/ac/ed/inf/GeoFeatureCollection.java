package uk.ac.ed.inf;


import com.google.gson.JsonObject;

public class GeoFeatureCollection
{
    private JsonObject geometry;

    public GeoFeatureCollection(JsonObject geometry)
    {
        this.geometry = geometry;
    }

    public JsonObject getGeometry()
    {
        return geometry;
    }

    public void setGeometry(JsonObject geometry)
    {
        this.geometry = geometry;
    }
}