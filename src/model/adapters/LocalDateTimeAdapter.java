package model.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(final JsonWriter out, final LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        String time = value.toString();
        out.value(time);
    }

    @Override
    public LocalDateTime read(final JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String time = in.nextString();
        return LocalDateTime.parse(time);
    }
}
