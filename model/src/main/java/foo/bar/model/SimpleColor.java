package foo.bar.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class SimpleColor {

    private String color;

    public SimpleColor() {
        // Only to be used from Jackson
    }

    public SimpleColor(String color) {
        checkColor(color);
        this.color = color;
    }

    private void checkColor(String color) {
        if (!color.startsWith("#")) {
            throw new IllegalArgumentException("Color code must start with hash");
        }
        if (color.length() != 7) {
            throw new IllegalArgumentException("Color code must be 7 characters");
        }
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
        checkColor(color);
        this.color = color;
    }

    @Override
    public String toString() {
        return "SimpleColor{" +
                "color='" + color + '\'' +
                '}';
    }

    public int[] getAsIntArray() {
        int[] ints = new int[3];
        for (int i = 0; i < 3; i++) {
            int i1 = i * 2 + 1;
            ints[i] = Integer.parseInt(color.substring(i1, i1 + 2), 16);
        }
        return ints;
    }

    public int getAsRgbInt() {
        int[] asIntArray = getAsIntArray();
        return asIntArray[0] << 16 | asIntArray[1] << 8 | asIntArray[2];
    }
}
