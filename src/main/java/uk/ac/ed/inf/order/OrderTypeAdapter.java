package uk.ac.ed.inf.order;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.IOException;

public class OrderTypeAdapter extends TypeAdapter<Order>
{
    @Override
    public void write(JsonWriter out, Order order) throws IOException {
        out.beginObject();
        out.name("orderNo").value(order.getOrderNo());
        out.name("orderStatus").value(order.getOrderStatus().toString());
        out.name("orderValidationCode").value(order.getOrderValidationCode().toString());
        out.name("costInPence").value(order.getPriceTotalInPence());
        out.endObject();
    }

    @Override
    public Order read(JsonReader jsonReader)
    {
        // don't need this
        return null;
    }
}
