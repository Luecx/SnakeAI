package game;

import core.vector.Vector2d;
import core.vector.Vector3d;
import genetic_algorithm.neat.neat.Client;

import java.util.LinkedList;
import java.util.Queue;

public class Board {


    private final int width;
    private final int height;


    private Field nextFood;
    private LinkedList<Field> snake;

    private int direction;  //0 = north, 1=west, 2=south, 3=east
    private boolean removeTailNextTime = false;
    private boolean gameOver = false;


    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.reset();
    }

    public int snakeSize(){
        return snake.size();
    }

    public Field traverse(Field root){
        switch (direction){
            case 0: return new Field(root.getX(), root.getY()+1);
            case 1: return new Field(root.getX()-1, root.getY());
            case 2: return new Field(root.getX(), root.getY()-1);
            case 3: return new Field(root.getX()+1, root.getY());
        }
        return null;
    }

    public Field traverse(Field root, int control){
        switch (transformDirection(this.direction, control)){
            case 0: return new Field(root.getX(), root.getY()+1);
            case 1: return new Field(root.getX()-1, root.getY());
            case 2: return new Field(root.getX(), root.getY()-1);
            case 3: return new Field(root.getX()+1, root.getY());
        }
        return null;
    }

    public int transformDirection(int direction, int control){

        return (direction + control + 4) % 4;
    }

    public boolean isObstacle(Field field){
        return snake.lastIndexOf(field) > 0 || field.getX() < 0 || field.getX() >= width || field.getY() < 0 || field.getY() >= height;
    }

    /**
     *  index       |       meaning
     *  ---------------------------
     *      0       |       obstacle to the left
     *      1       |       obstacle in front
     *      2       |       obstacle to the right
     *      3       |       angle to the food
     *
     *
     * @return
     */
    public double[] extractNetworkInput() {

        double[] out = new double[4];

        out[0] = isObstacle(traverse(snake.getFirst(), 1)) ? 1:0;
        out[1] = isObstacle(traverse(snake.getFirst(), 0)) ? 1:0;
        out[2] = isObstacle(traverse(snake.getFirst(), -1)) ? 1:0;

        Vector2d location = new Vector2d(snake.getFirst().getX(), snake.getFirst().getY());

        Vector2d dir = new Vector2d(traverse(snake.getFirst()).getX(),traverse(snake.getFirst()).getY()).sub(location);
        Vector2d target = new Vector2d(nextFood.getX(), nextFood.getY()).sub(location);
        dir.self_normalise();
        target.self_normalise();

        double angle = Math.atan2(target.getY(), target.getX()) - Math.atan2(dir.getY(), dir.getX());

        if (angle > Math.PI)        { angle -= 2 * Math.PI; }
        else if (angle <= -Math.PI) { angle += 2 * Math.PI; }

        out[3] = angle;



//        switch (direction){
//            case 0:
//                if(nextFood.getX() == snake.getFirst().getX())
//                    out[3] = 0;
//                if(nextFood.getX() > snake.getFirst().getX())
//                    out[3] = -1;
//                if(nextFood.getX() < snake.getFirst().getX())
//                    out[3] = 1;
//                break;
//            case 1:
//                if(nextFood.getY() == snake.getFirst().getY())
//                    out[3] = 0;
//                if(nextFood.getY() > snake.getFirst().getY())
//                    out[3] = -1;
//                if(nextFood.getY() < snake.getFirst().getY())
//                    out[3] = 1;
//                break;
//            case 2:
//                if(nextFood.getX() == snake.getFirst().getX())
//                    out[3] = 0;
//                if(nextFood.getX() < snake.getFirst().getX())
//                    out[3] = -1;
//                if(nextFood.getX() > snake.getFirst().getX())
//                    out[3] = 1;
//
//                break;
//            case 3:
//                if(nextFood.getY() == snake.getFirst().getY())
//                    out[3] = 0;
//                if(nextFood.getY() < snake.getFirst().getY())
//                    out[3] = -1;
//                if(nextFood.getY() > snake.getFirst().getY())
//                    out[3] = 1;
//
//                break;
//        }

        return out;
    }

    public void reset() {
        this.snake = new LinkedList<>();
        this.snake.add(new Field(width / 2,height/2));
        this.direction = 0;

        this.removeTailNextTime = false;

        this.gameOver = false;

        newFoodTarget();
    }

    public void newFoodTarget() {
        Field next = new Field((int)(this.width * Math.random()),(int)(this.width * Math.random()));
        while(snake.contains(next)){
            next = new Field((int)(this.width * Math.random()),(int)(this.width * Math.random()));
        }
        nextFood = next;
    }

    public static int directionFromOutput(double[] out) {
        int index = 0;
        for(int i = 1; i < out.length; i++){
            if(out[i] > out[index]){
                index = i;
            }
        }
        return index -1;
    }

    public void move(Client client, boolean debug){
        if (debug)
            System.out.println("Calculating");
        double[] out = client.calculate(this.extractNetworkInput());
        if (debug)
            System.out.println("Calculating stop");
        int dir = directionFromOutput(out);

        if (debug)
            System.out.println("Moving");
        this.move(dir);
        if (debug)
            System.out.println("Moving stop");
    }

    /**
     *  1 to move left
     *  0 to move straight
     * -1 to move right
     *
     * @param direction
     */
    public void move(int direction){
        if(gameOver) return;

        synchronized (snake){
            this.direction = (this.direction + direction + 4) % 4;
            Field newHead = traverse(snake.getFirst());

            snake.addFirst(newHead);
            if(removeTailNextTime){
                snake.removeLast();
            }else{
                removeTailNextTime = true;
            }

            if(newHead.equals(nextFood)){
                newFoodTarget();
                removeTailNextTime = false;
            }

            if(isObstacle(newHead)){
                gameOver = true;
            }
        }



    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Field getNextFood() {
        return nextFood;
    }

    public LinkedList<Field> getSnake() {
        return snake;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
