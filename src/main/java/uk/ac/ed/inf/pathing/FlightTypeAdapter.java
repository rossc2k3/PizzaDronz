package uk.ac.ed.inf.pathing;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class FlightTypeAdapter extends TypeAdapter<FlightMove>
{
    public void write(JsonWriter out, FlightMove flight) throws IOException
    {
        out.beginObject();
        out.name("orderNo").value(flight.getOrderNo());
        out.name("fromLongitude").value(flight.getFromLongitude());
        out.name("fromLatitude").value(flight.getFromLatitude());
        out.name("angle").value(flight.getAngle());
        out.name("toLongitude").value(flight.getToLongitude());
        out.name("toLatitude").value(flight.getToLatitude());
        out.endObject();
    }

    @Override
    public FlightMove read(JsonReader jsonReader)
    {
        // don't need this
        return null;
    }
}
