package edu.anadolu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static edu.anadolu.TurkishNetwork.cities;
import static edu.anadolu.TurkishNetwork.distance;

public class mTSP implements Cloneable {
    int depots;
    int salesmen;
    int cost;
    int randomNumber1;
    int randomNumber2;
    ArrayList<Integer> depotsList;
    ArrayList<Integer> unvisitedCity;
    LinkedHashMap<Integer,ArrayList<ArrayList<Integer>>> routes;
    Integer routeCount;

    Random random = new Random();



    public mTSP(int depots, int salesmen) {
        this.depots = depots;
        this.salesmen = salesmen;
        routes = new LinkedHashMap<>();
        depotsList = new ArrayList<>();
        unvisitedCity = new ArrayList<>();
    }

    public mTSP(mTSP copy) {
        depots = copy.depots;
        salesmen = copy.salesmen;
        routeCount = copy.depots;
        routes = copyRoutes(copy.routes);
        depotsList = copyDepotsList(copy.depotsList);
        cost = copy.cost;
    }


    public void randomSolution() {
        routeCount = (cities.length-depots)/(depots*salesmen);
        unvisitedCity = assignCities(unvisitedCity);
        int randomDepots = 0;
        //depots selected randomly

        //Placement of depots
        //add depot
        for (int i=0;i<depots;i++){
            randomDepots = getRandomNumber();
            if (!routes.containsKey(randomDepots)) {
                routes.put(randomDepots, new ArrayList<>());
                unvisitedCity.remove(new Integer(randomDepots));
                depotsList.add(randomDepots);
                //takes a salesman
                for (int j = 0; j < salesmen; j++) {
                    routes.get(randomDepots).add(new ArrayList<>());
                    unvisitedCity.remove(new Integer(randomDepots));
                    //takes a route and enter cities to the route
                    for (int z=0;z<routeCount;z++) {
                        int randomCity = random.nextInt(unvisitedCity.size());
                        //get random city from unvisited cities
                        routes.get(randomDepots).get(j).add(unvisitedCity.get(randomCity));
                        unvisitedCity.remove(randomCity);
                    }
                }
                //if there exist same city in route get again select random depots
            }else i--;
        }

        //add remaining city to the last route
        int size = unvisitedCity.size();
        for (int i=0;i<size;i++){
            int randomCity = random.nextInt(unvisitedCity.size());
            routes.get(depotsList.get(depots-1)).get(salesmen-1).add(unvisitedCity.get(randomCity));
            unvisitedCity.remove(randomCity);

        }



    }

    public void neighborNearestSolution(int startedDepots){
        int[] weights = TurkishNetwork.weights;
        int[] sorted = TurkishNetwork.sorted;
        routeCount = (cities.length-depots)/(depots*salesmen);
        unvisitedCity = assignCities(unvisitedCity);
        int starts = startedDepots;
        int nextCity = startedDepots;
        //get depot and add to the map
        for (int i=0;i<depots;i++){
            routes.put(starts, new ArrayList<>());
            unvisitedCity.remove(new Integer(starts));
            depotsList.add(starts);
            //takes a salesman
            for (int j = 0; j < salesmen; j++) {
                routes.get(starts).add(new ArrayList<>());
                unvisitedCity.remove(new Integer(starts));
                //takes a route and enter cities to the route
                for (int z=0;z<routeCount;z++) {
                    int nearestcity = getNearestCity(nextCity);
                    //get random city from unvisited cities
                    routes.get(starts).get(j).add(nearestcity);
                    unvisitedCity.remove(new Integer(nearestcity));
                    nextCity = nearestcity;

                }
            }
            //if there exist same city in route get again select random depots
            //select new depots
            for (int sortedWeights :  sorted){
                if (unvisitedCity.contains(new Integer(sortedWeights))){
                    starts = sortedWeights;
                    break;
                }
            }
        }
        //add remaining city to the last route
        int size = unvisitedCity.size();
        for (int i=0;i<size;i++){
            int nearestcity = getNearestCity(nextCity);
            routes.get(depotsList.get(depots-1)).get(salesmen-1).add(nearestcity);
            unvisitedCity.remove(new Integer(nearestcity));

        }
    }

    public int getNearestCity(int city){
        int nearestDistance = Integer.MAX_VALUE;
        int nearestCity = 0;
        for (int cities : unvisitedCity) {
            if (cities == city)
                continue;
            if (distance[city-1][cities-1]<nearestDistance) {
                nearestDistance = distance[city-1][cities-1];
                nearestCity = cities;

            }
            }
        return nearestCity;
    }

    public int validate() {
        this.cost =0;
        for(int depot:routes.keySet()) {
            for(List<Integer> l:routes.get(depot)){
                int current=depot;

                for(int i:l){
                    cost+=TurkishNetwork.distance[current-1][i-1];
                    current=i;
                }
                cost+=TurkishNetwork.distance[current-1][depot-1];
            }
        }
        return cost;
    }



    public int cost() {
        return cost;
    }

    public void print(boolean verbose) {
        if(!verbose){
            int i=1;
            for(int depot:routes.keySet()){
                System.out.println("Depot"+i+": "+depot);
                int route=1;

                for(List<Integer> a:routes.get(depot)){
                    System.out.print("  Route"+route+": ");
                    route++;

                    for(int j:a){

                        if(j==a.get(a.size()-1)){
                            System.out.print(j);
                        }else
                            System.out.print(j+",");
                    }
                    System.out.println();
                }
                i++;
            }
        }else{
            int i=1;

            for(int depot:routes.keySet()){
                System.out.println("Depot"+i+": "+ cities[depot-1]);
                int route=1;

                for(List<Integer> a:routes.get(depot)){
                    System.out.print("  Route"+route+": ");
                    route++;

                    for(int j:a){

                        if(j==a.get(a.size()-1)){
                            System.out.print(cities[j-1]);
                        }else
                            System.out.print(cities[j-1]+",");
                    }
                    System.out.println();
                }
                i++;
            }
        }
    }

    public void swapNodesInRoute(){
        int randomDepot = depotsList.get(random.nextInt(depots));
        ArrayList<ArrayList<Integer>> allRoutes = routes.get(randomDepot);

        int randomRouteIndex = random.nextInt(allRoutes.size());
        ArrayList<Integer> randomRoute = allRoutes.get(randomRouteIndex);

        if(randomRoute.size()==1)
            return;

        randomInInterval(randomRoute.size());
        Collections.swap(routes.get(randomDepot).get(randomRouteIndex),randomNumber1,randomNumber2);
        validate();
    }
    public void swapHubWithNodeInRoute(){
        int randomDepot = depotsList.get(random.nextInt(depots));
        ArrayList<ArrayList<Integer>> allRoutes = routes.get(randomDepot);

        int randomRouteIndex = random.nextInt(allRoutes.size());
        List<Integer> randomRoute = allRoutes.get(randomRouteIndex);

        randomNumber1 = random.nextInt(randomRoute.size());

        int newHub = randomRoute.get(randomNumber1);
        allRoutes.get(randomRouteIndex).add(randomNumber1,randomDepot);
        allRoutes.get(randomRouteIndex).remove(new Integer(newHub));

        routes.put(newHub,allRoutes);
        routes.remove(randomDepot);
        depotsList.add(newHub);
        depotsList.remove(new Integer(randomDepot));
        validate();
    }

    public void swapNodesBetweenRoutes(){
        int randomDepot1 = 1;
        int randomDepot2 = 1;

        while (randomDepot1==randomDepot2){
            randomDepot1 = depotsList.get(random.nextInt(depots));
            randomDepot2 = depotsList.get(random.nextInt(depots));
        }
        //change
        while (routes.get(randomDepot1) ==null && routes.get(randomDepot2) == null){
            randomDepot1 = depotsList.get(random.nextInt(depots));
            randomDepot2 = depotsList.get(random.nextInt(depots));

        }
        ArrayList<ArrayList<Integer>> allRoutes1 = routes.get(randomDepot1);

        ArrayList<ArrayList<Integer>> allRoutes2 = routes.get(randomDepot2);

        int randomRouteIndex1 = random.nextInt(allRoutes1.size());
        List<Integer> randomRoute1 = allRoutes1.get(randomRouteIndex1);

        int randomRouteIndex2 = random.nextInt(allRoutes2.size());
        List<Integer> randomRoute2 = allRoutes2.get(randomRouteIndex2);

        int randomNode1 = randomRoute1.get(random.nextInt(randomRoute1.size()));
        int randomNode2 = randomRoute2.get(random.nextInt(randomRoute2.size()));
        routes.get(randomDepot1).get(randomRouteIndex1).add(randomRoute1.indexOf(randomNode1),randomNode2);
        routes.get(randomDepot1).get(randomRouteIndex1).remove(new Integer(randomNode1));

        routes.get(randomDepot2).get(randomRouteIndex2).add(randomRoute2.indexOf(randomNode2),randomNode1);
        routes.get(randomDepot2).get(randomRouteIndex2).remove(new Integer(randomNode2));
        validate();
    }

    public void insertNodeInRoute(){
        int randomDepot = depotsList.get(random.nextInt(depots));

        ArrayList<ArrayList<Integer>> allRoutes = routes.get(randomDepot);
        int randomRouteIndex = random.nextInt(allRoutes.size());
        while (allRoutes.get(randomRouteIndex).size() <3){
            randomDepot = depotsList.get(random.nextInt(depots));
            allRoutes = routes.get(randomDepot);
            randomRouteIndex = random.nextInt(allRoutes.size());
        }
        List<Integer> randomRoute = allRoutes.get(randomRouteIndex);

        int randomNode = randomRoute.get(random.nextInt(randomRoute.size()));
        routes.get(randomDepot).get(randomRouteIndex).remove(new Integer(randomNode));
        routes.get(randomDepot).get(randomRouteIndex).add(randomNode);
        validate();

    }

    public void insertNodeBetweenRoutes(){
        int randomDepot1 = depotsList.get(random.nextInt(depots));
        int randomDepot2 = depotsList.get(random.nextInt(depots));

        if (routes.get(randomDepot1) ==null && routes.get(randomDepot2)==null){
            randomDepot1 = depotsList.get(random.nextInt(depots));
            randomDepot2 = depotsList.get(random.nextInt(depots));
        }

        ArrayList<ArrayList<Integer>> allRoutes1 = routes.get(randomDepot1);

        ArrayList<ArrayList<Integer>> allRoutes2 = routes.get(randomDepot2);

        int randomRouteIndex1 = random.nextInt(salesmen);
        if (allRoutes1.isEmpty())
            return;
        List<Integer> randomRoute1 = allRoutes1.get(randomRouteIndex1);

        int randomRouteIndex2 = random.nextInt(salesmen);
        List<Integer> randomRoute2 = allRoutes2.get(randomRouteIndex2);
        
        if(randomRoute1==randomRoute2||randomRoute1.size()==1){
            return;
        }
        int randomNode1 = randomRoute1.get(random.nextInt(randomRoute1.size()));
        int randomNode2 = randomRoute2.get(random.nextInt(randomRoute2.size()));
        routes.get(randomDepot1).get(randomRouteIndex1).remove(new Integer(randomNode1));

        if (randomRoute2.indexOf(randomNode2) == randomRoute2.size()-1)
            routes.get(randomDepot2).get(randomRouteIndex2).add(randomNode1);
        else
            routes.get(randomDepot2).get(randomRouteIndex2).add(randomRoute2.indexOf(randomNode2)+1,randomNode1);
        validate();
    }

    public void randomInInterval(int interval){
        randomNumber1 = random.nextInt(interval);
        randomNumber2 = random.nextInt(interval);

        if(randomNumber1==randomNumber2)
            randomInInterval(interval);
    }

    public Integer getRandomNumber(){
        int randomNumber = random.nextInt(81)+1;
        return randomNumber;
    }

    public ArrayList<Integer> assignCities(ArrayList<Integer> cities){
        for (int i=1;i<82;i++)
            cities.add(i);

        return cities;
    }


    public void writeJSONFILE() {

        JSONObject obj = new JSONObject();
        JSONArray list = new JSONArray();
          for (Map.Entry<Integer,ArrayList<ArrayList<Integer>>> entry : routes.entrySet()){
            JSONObject Obj = new JSONObject();
            obj.put("solution", list);
            Obj.put("depot", entry.getKey().toString());
            JSONArray array = new JSONArray();
            for (int i = 0; i < entry.getValue().size(); i++) {
                String temp = "";
                for (int j = 0; j < entry.getValue().get(i).size(); j++) {
                    if (entry.getValue().get(i).size() - 1 == j) {
                        temp += entry.getValue().get(i).get(j);
                    } else {
                        temp += entry.getValue().get(i).get(j) + " ";
                    }
                }
                array.put(temp);
            }
            Obj.put("route", array);

            list.put(Obj);
        }

        Path path = Paths.get("solution_" + "d" + depots + "s" + salesmen + ".json");
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(obj.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private LinkedHashMap<Integer,ArrayList<ArrayList<Integer>>> copyRoutes(LinkedHashMap<Integer,ArrayList<ArrayList<Integer>>> map) {
        LinkedHashMap<Integer, ArrayList<ArrayList<Integer>>> new_map = new LinkedHashMap<>();
        for (Map.Entry<Integer, ArrayList<ArrayList<Integer>>> entry : map.entrySet()) {
            ArrayList<ArrayList<Integer>> arrayLists = new ArrayList<>();
            for (int i = 0; i < entry.getValue().size(); i++) {
                ArrayList<Integer> copyRoutes = new ArrayList<>();
                for (int j = 0; j < entry.getValue().get(i).size(); j++) {
                    copyRoutes.add(entry.getValue().get(i).get(j));
                }
                arrayLists.add(copyRoutes);
            }
            new_map.put(entry.getKey(), arrayLists);
        }
        return new_map;
    }
    private ArrayList<Integer> copyDepotsList(ArrayList<Integer> map){
        ArrayList<Integer> depotList = new ArrayList<>(map);
        return depotList;

    }


}
