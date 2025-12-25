package api;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;

public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        out.value(value != null ? value.toString() : null);
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String dateString = in.nextString();
        return (dateString != null && !dateString.isEmpty()) 
                ? LocalDate.parse(dateString) 
                : null;
    }
}