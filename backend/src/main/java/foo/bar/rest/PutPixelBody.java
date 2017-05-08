package foo.bar.rest;

import javax.validation.constraints.NotNull;

public class PutPixelBody {
    @NotNull
    private String color;

    @NotNull
    private String user;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
