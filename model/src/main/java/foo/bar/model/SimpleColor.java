package foo.bar.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class SimpleColor {

    private String color;

    public SimpleColor() {
        // Only to be used from Jackson
    }

    public SimpleColor(String color) {
        this.color = color;
    }

    @JsonCreator
    public static SimpleColor fromJson(String json) {
        return new SimpleColor(json);
    }

    @JsonValue
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "SimpleColor{" +
                "color='" + color + '\'' +
                '}';
    }
}
