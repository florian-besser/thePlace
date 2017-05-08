package foo.bar.board;

public class SimpleColor {

    private String color;

    public SimpleColor() {
        // Only to be used from Jackson
    }

    public SimpleColor(String color) {
        this.color = color;
    }

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
