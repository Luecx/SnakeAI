package visual;

import game.Board;
import game.Field;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    private Board board;


    public Panel() {
    }

    public Panel(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(board == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.clearRect(0,0,this.getWidth(), this.getHeight());


        //render snake
        for(Field f:board.getSnake()){
            renderField(Color.darkGray, f, g2d);
        }
        renderField(Color.gray, board.getSnake().getFirst(), g2d);



        //render food
        renderField(Color.red, board.getNextFood(), g2d);
    }

    private void renderField(Color c, Field f, Graphics2D graphics){
        int x = f.getX() * this.getWidth() / board.getWidth();
        int y = this.getHeight() - (f.getY()+1) * this.getHeight() / board.getHeight();
        int width = (f.getX()+1) * this.getWidth() / board.getWidth() - x;
        int height = (this.getHeight() - (f.getY()) * this.getHeight() / board.getHeight()) - y;

        graphics.setColor(c);
        graphics.fillRect(x, y,width, height);
    }
}
