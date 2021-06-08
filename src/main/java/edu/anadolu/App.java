package edu.anadolu;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;

import java.util.ArrayList;
import java.util.Random;

import static edu.anadolu.TurkishNetwork.sorted;
import static edu.anadolu.TurkishNetwork.weights;
import static edu.anadolu.TurkishNetwork.cities;
import static edu.anadolu.TurkishNetwork.distance;
/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {

        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }


        mTSP best = null;
        int minCost = Integer.MAX_VALUE;

        for (int i = 0; i < 100_000; i++) {

            mTSP mTSP = new mTSP(params.getNumDepots(), params.getNumSalesmen());

            mTSP.randomSolution();
            mTSP.validate();
            //mTSP.print(false);

            final int cost = mTSP.cost();

            //System.out.println("Total cost is " + cost);

            if (cost < minCost) {
                best = mTSP;
                minCost = cost;
            }
        }

        if (best != null) {
            System.out.println("**************************************Initial random solution*************************************");
            best.print(params.getVerbose());
            System.out.println();
            System.out.println("**Total cost is " + best.cost());

        }
        if (!params.getSolutionType().equals("random")) {
            best = new mTSP(params.getNumDepots(), params.getNumSalesmen());
            //select a city to start
            best.neighborNearestSolution(params.getStartCity());
            if (best != null) {
                System.out.println("**************************************Neighbour Nearest Solution*************************************");
                best.print(params.getVerbose());
                best.validate();
                System.out.println();
                System.out.println("**Total cost is " + best.cost());
            }
        }
        minCost = best.cost;
        Counter counter = new Counter();
        int cost =0;
        System.out.println();
        System.out.println("Waiting for best solution with hill climbing...");
        for (int iter = 0; iter < 5_000_000; iter++) {
            mTSP temp = new mTSP(best);

            int random = new Random().nextInt(5);
            if (random == 0){
                temp.swapNodesInRoute();
            }
            else if (random == 1) {
                temp.swapHubWithNodeInRoute();
            }
            else if (random == 2 && params.getNumDepots() >1){
                temp.swapNodesBetweenRoutes();
            }
            else if (random == 3) {
                temp.insertNodeInRoute();
            }
            else if (random == 4) {
                temp.insertNodeBetweenRoutes();
            }
            cost = temp.cost();

            if (cost < minCost) {
                best = temp;
                minCost = cost;
                counter.counter(random);
            }

        }
        System.out.println("***********************************" +
                "Best Solution With Hil Climbing Iteration"+"*************************************");
        best.print(params.getVerbose());
        System.out.println();
        System.out.println("**Total cost is " + best.cost());
        System.out.println();
        System.out.println("Swap nodes in routes : "+ counter.swapNodesInRoute);
        System.out.println("Swap hub node in routes : "+ counter.swapHubWithNodeInRoute);
        System.out.println("Swap nodes between routes : "+ counter.swapNodesBetweenRoutes);
        System.out.println("Insert node in routes : "+ counter.insertNodeInRoute);
        System.out.println("Insert node between routes : "+ counter.insertNodeBetweenRoutes);

        best.writeJSONFILE();
    }
}
