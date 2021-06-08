package edu.anadolu;

public class Counter {
    int swapNodesInRoute;
    int swapHubWithNodeInRoute;
    int swapNodesBetweenRoutes;
    int insertNodeBetweenRoutes;
    int insertNodeInRoute;
    public void counter(int x){
        if (x==0){
            swapNodesInRoute++;
        }
        else if(x == 1){
            swapHubWithNodeInRoute++;
        }
        else if(x ==2){
            swapNodesBetweenRoutes++;
        }
        else if(x==3){
            insertNodeInRoute++;
        }
        else{
            insertNodeBetweenRoutes++;
        }

    }
}
