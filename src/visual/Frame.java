package visual;

import game.Board;
import genetic_algorithm.neat.neat.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class Frame extends JFrame implements KeyListener {


    private Panel panel;
    private Client client;

    public Frame(Board board, Client client) {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Snake");
        this.setSize(1200,1200);
        this.setLocation(2560 / 2 - 600, 1440 / 2 - 600);

        this.setLayout(new BorderLayout());
        this.panel = new Panel(board);
        this.add(panel, BorderLayout.CENTER);

        this.client = client;

        new Thread(() -> {
            board.reset();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < 20; i++){
                System.out.println("");
            }
            while(board.isGameOver() == false){
                board.move(client,false);
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print("\rscore: " + board.snakeSize());

                repaint();
            }
        }).start();

        this.panel.addKeyListener(this);
        this.addKeyListener(this);

        this.setVisible(true);
    }

    public Frame(Board board) {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Snake");
        this.setSize(700,700);

        this.setLayout(new BorderLayout());
        this.panel = new Panel(board);
        this.add(panel, BorderLayout.CENTER);

        this.panel.addKeyListener(this);
        this.addKeyListener(this);

        this.setVisible(true);
    }

    public static void main(String[] args) {
        new Frame(new Board(10,10));
    }



    public Panel getPanel() {
        return panel;
    }

    public void setPanel(Panel panel) {
        this.panel = panel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(client != null) return;
        this.panel.getBoard().move(-(e.getExtendedKeyCode() - 38));
        System.out.println(Arrays.toString(this.panel.getBoard().extractNetworkInput()));
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
