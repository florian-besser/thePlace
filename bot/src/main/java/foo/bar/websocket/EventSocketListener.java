package foo.bar.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import foo.bar.model.Pixel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventSocketListener extends EventSocketCounter {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final CollectionType javatype = MAPPER.getTypeFactory().constructCollectionType(List.class, Pixel.class);
    private final Consumer<List<Pixel>> listener;
    List<Pixel> setPixels = new ArrayList<>();

    public EventSocketListener(Consumer<List<Pixel>> listener) {
        this.listener = listener;
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        List<Pixel> deserialize = deserialize(message);
        setPixels.addAll(deserialize);
        if (listener != null) {
            listener.accept(deserialize);
        }
    }

    private List<Pixel> deserialize(String json) {
        try {
            return MAPPER.readValue(json, javatype);
        } catch (IOException e) {
            throw new RuntimeException("Error wile converting String to Pixel.", e);
        }
    }

    public List<Pixel> getSetPixels() {
        return setPixels;
    }
}