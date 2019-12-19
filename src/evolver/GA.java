package evolver;

import game.Board;
import genetic_algorithm.neat.flappy_bird.Game;
import genetic_algorithm.neat.neat.Client;
import genetic_algorithm.neat.neat.Neat;
import genetic_algorithm.neat.visual.Frame;

public class GA {

    public static void rateClient(Board board, Client client){
        board.reset();

        int iteration = 0;
        int iterationsWithoutFood = 0;
        int food = 0;
        while(iteration < 1000 && iterationsWithoutFood < 20 && board.isGameOver() == false){
            iteration ++;
            board.move(client,false);
            if(board.snakeSize() != food){
                iterationsWithoutFood = 0;
                food = board.snakeSize();
            }else{
                iterationsWithoutFood ++;
            }
        }
        client.setScore(board.snakeSize());

    }

    public static void evolve(Board board, Neat neat){
        for(int i = 0; i < neat.getMax_clients(); i++){
            rateClient(board, neat.getClient(i));
        }
        neat.evolve();
    }

    public static void main(String[] args) {

        Board game = new Board(10,10);

        Neat neat = new Neat(4,3,1000);
        neat.setCP(3);

        neat.setPROBABILITY_MUTATE_WEIGHT_RANDOM(0.01);
        neat.setPROBABILITY_MUTATE_WEIGHT_SHIFT(0.01);
        neat.setPROBABILITY_MUTATE_LINK(0.3);
        neat.setPROBABILITY_MUTATE_NODE(0.1);
        neat.setSURVIVORS(0.3);

        for(int i = 0; i < neat.getMax_clients(); i++){
            rateClient(game, neat.getClient(i));
        }


        for(int i = 0; i < 200; i++){
            System.out.println("#################### " + i + " ######################");
            evolve(game, neat);
            neat.printSpecies();
            System.out.println(neat.getBestClient().getScore());
            neat.printScoreInformation();
        }

        for(int i = 0; i < neat.getMax_clients(); i++){
            rateClient(game, neat.getClient(i));
        }


        new Frame(neat.getBestClient().getGenome());
        new visual.Frame(new Board(20,20), neat.getBestClient());

    }

}
