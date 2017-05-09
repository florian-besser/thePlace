package foo.bar.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import foo.bar.model.Pixel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventSocketListener extends EventSocketCounter {
    List<Pixel> setPixels = new ArrayList<>();

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        setPixels.addAll(deserialize(message));
    }

    private List<Pixel> deserialize(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            CollectionType javatype = mapper.getTypeFactory().constructCollectionType(List.class, Pixel.class);
            return mapper.readValue(json, javatype);
        } catch (IOException e) {
            throw new RuntimeException("Error wile converting String to Pixel.", e);
        }
    }

    public List<Pixel> getSetPixels() {
        return setPixels;
    }
}