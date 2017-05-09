package foo.bar.ui;

import com.google.common.util.concurrent.RateLimiter;
import foo.bar.model.Board;
import foo.bar.model.Pixel;
import foo.bar.model.SimpleColor;

import javax.swing.*;
import java.awt.*;

public class BoardUi {

    private BoardCanvas boardCanvas;

    private final RateLimiter rateLimiter = RateLimiter.create(25);

    public void start(Board board) {

        JFrame jFrame = new JFrame();
        //jFrame.setSize(board.getColors()[0].length, board.getColors().length);
        boardCanvas = new BoardCanvas(board);
        jFrame.add(boardCanvas);
        jFrame.setSize(1000, 1000);
        jFrame.setVisible(true);
    }

    public void updateBoard(Pixel pixel) {
        boardCanvas.paintPixel(pixel);
    }
}

class BoardCanvas extends Canvas {
    private Board board;

    int scaling = 1;

    public BoardCanvas(Board board) {
        this.board = board;
    }

    @Override
    public void paint(Graphics g) {
//        SimpleColor[][] colors = board.getColors();
//        for (int x = 0; x < colors[0].length; x++) {
//            for (int y = 0; y < colors.length; y++) {
//                drawPixel(g, colors[y][x], x, y);
//            }
//        }
        super.paint(g);
    }

    private void drawPixel(Graphics g, SimpleColor simpleColor, int x, int y) {
        g.setColor(Color.decode(simpleColor.getColor()));
        g.fillRect(x * scaling, y * scaling, scaling, scaling);
    }

    public void paintPixel(Pixel pixel) {
        drawPixel(getGraphics(), pixel.getColor(), pixel.getX(), pixel.getY());
    }
}