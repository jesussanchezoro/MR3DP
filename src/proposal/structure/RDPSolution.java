package proposal.structure;

import grafo.optilib.structure.Solution;
import grafo.optilib.tools.RandomManager;

import java.util.*;

public class RDPSolution implements Solution {
    private RDPInstance instance;
    private  int []solution;
    private int of;
    private boolean updated;
    Set<Integer> labeled;
    Set<Integer> unlabeled;
    public RDPSolution(RDPInstance instance){
        this.instance=instance;
        solution=new int[instance.getNumNodes()];
        labeled=new HashSet<>();
        unlabeled=new HashSet<>();
        for (int i = 0; i < instance.getNumNodes(); i++) {
            unlabeled.add(i);
        }
    }
    public RDPSolution(RDPSolution solution){
        copy(solution);
    }
    public void copy(RDPSolution sol){
        updated=sol.updated;
        instance=sol.instance;
        solution=sol.solution.clone();
        of=sol.of;
        if(labeled!=null) labeled.clear();
        else labeled=new HashSet<>();
        if(unlabeled!=null) unlabeled.clear();
        else unlabeled=new HashSet<>();
        labeled.addAll(sol.labeled);
        unlabeled.addAll(sol.unlabeled);
    }
    public void initialize(int value){
        for(int i=0; i<instance.getNumNodes();i++){
            setLabel(i,value,false);
        }
    }
    public void setLabel(int node, int label, boolean neighbors){
        updated=true;
        solution[node]=label;
        unlabeled.remove(node);
        labeled.add(node);
        if(neighbors && label==3) {
            for (int neighbor : instance.getAdjacencyList()[node]) {
                if (solution[neighbor] == 2 || !labeled.contains(neighbor)) {
                    solution[neighbor] = 0;
                    unlabeled.remove(neighbor);
                    labeled.add(neighbor);
                }
            }
        }
    }
    public void removeLabel(int node){
        labeled.remove(node);
        unlabeled.add(node);
    }
    public void removeLabels(int node,int k,int kMax){
        removeLabel(node);
        k++;
        if(k<kMax){
            for(int neighbor:instance.getAdjacencyList()[node]){
                removeLabels(neighbor,k,kMax);
            }
        }
    }
    public void clean(){
        for(int i=0; i< instance.getNumNodes();i++) {
            if(solution[i]!=0){
                int oldLabel=solution[i];
                int label=findBestLabel(i);
                solution[i]=label;
                if(label!=oldLabel) updated=true;
            }
        }
    }
    public void reconstruct(){ //greedy
        while (!unlabeled.isEmpty()){
            int bestNode=-1;
            int maxNeighborsLabeled=-1;
            for (int node:unlabeled) {
                int neighborsLabeled=0;
                for(int neighbor:instance.getAdjacencyList()[node]){
                    if(labeled.contains(neighbor)){
                        neighborsLabeled++;
                    }
                }
                if(neighborsLabeled>maxNeighborsLabeled){
                    bestNode=node;
                    maxNeighborsLabeled=neighborsLabeled;
                }
            }
            int label=findBestLabel(bestNode);
            setLabel(bestNode, label,false);
        }
        clean();
    }
    public void reconstructRandom(){ //random
        List<Integer> unlabeledList = new ArrayList<>(unlabeled);
        while (!unlabeled.isEmpty()){
            //int rndNode=unlabeled.iterator().next();
            int rndNode = unlabeledList.remove(RandomManager.getRandom().nextInt(unlabeledList.size()));
            int label=findBestLabel(rndNode);
            setLabel(rndNode, label,false);
        }
        clean();
    }
    public int findBestLabel(int node){
        int sum=0;
        int minSumNeighbors=0x3F3F3F3F;
        for(int neighbor:instance.getAdjacencyList()[node]){
            int sum2=0;
            if(labeled.contains(neighbor)){
                sum2+=solution[neighbor];
                sum+=solution[neighbor];
                for(int neighbor2:instance.getAdjacencyList()[neighbor]){
                    if(neighbor2!=node && labeled.contains(neighbor2)){
                        sum2+=solution[neighbor2];
                    }
                }
                if(sum2<minSumNeighbors){
                    minSumNeighbors=sum2;
                }
            }
        }
        return Math.max(Math.min(2,Math.max(0,3-sum)), 3-Math.min(3,minSumNeighbors));
    }
    public int calculateNodeValue(int node){
        int value=0;
        for(int neighbor:instance.getAdjacencyList()[node]){
            if(unlabeled.contains(neighbor)){
                value++;
            }
        }
        return value;
    }
    public int calculateOF(){
        if(updated){
            of=0;
            for (int node : solution) {
                of+=node;
            }
            updated = false;
        }
        return of;
    }
    public boolean isFeasible(){
        for(int i=0; i<instance.getNumNodes();i++) {
            boolean feasible = false;
            int sumWeight = solution[i];
            if(solution[i]==2){
                continue;
            }
            for (int neighbour : instance.getAdjacencyList()[i]) {
                sumWeight += solution[neighbour];
                if (sumWeight >= 3) {
                    feasible = true;
                    break;
                }
            }
            if (!feasible) {
                return false;
            }
        }
        return true;
    }
    public boolean isFeasibleOld(){ //nuevo
        for(int i=0; i<instance.getNumNodes();i++){
            if(solution[i]==0){
                boolean feasible=false;
                int sumWeight=0;
                for(int neighbour:instance.getAdjacencyList()[i]){
                    sumWeight+=solution[neighbour];
                    if(sumWeight>=3){
                        feasible=true;
                        break;
                    }
                }
                if(!feasible){
                    return false;
                }
            }
            if(solution[i]==1){
                boolean feasible=false;
                int sumWeight=0;
                for(int neighbour:instance.getAdjacencyList()[i]){
                    sumWeight+=solution[neighbour];
                    if(sumWeight>=2){
                        feasible=true;
                        break;
                    }
                }
                if(!feasible){
                    return false;
                }
            }
        }
        return true;
    }
    public boolean isFeasiblePartial(int node,int label){
        int sum=label;
        for(int neighbor:instance.getAdjacencyList()[node]){
            int sum2=solution[neighbor];
            sum+=solution[neighbor];
            for(int neighbor2:instance.getAdjacencyList()[neighbor]){
                sum2+=solution[neighbor2];
            }
            if(sum2<3){
                return false;
            }
        }
        return sum>=3;
    }
    public void repair(){
        updated=true;
        for(int i=0; i<instance.getNumNodes();i++){
            if(solution[i]==0) {
                boolean feasible = false;
                int sumWeight=0;
                for (int neighbour : instance.getAdjacencyList()[i]) {
                    sumWeight+=solution[neighbour];
                    if (sumWeight >= 3) {
                        feasible=true;
                        break;
                    }
                }
                if (!feasible) {
                    solution[i]=2;
                }
            }
            if(solution[i]==1) {
                boolean feasible = false;
                int sumWeight=0;
                for (int neighbour : instance.getAdjacencyList()[i]) {
                    sumWeight+=solution[neighbour];
                    if (sumWeight >= 2) {
                        feasible=true;
                        break;
                    }
                }
                if (!feasible) {
                    solution[i]=2;
                }
            }
        }
    }
    public int calculateDifference(String type, RDPSolution s){
        if(type.equals("of")){
            return Math.abs(calculateOF()-s.calculateOF());
        }else{
            return 1;
        }
    }
    public int better(RDPSolution s){
        return calculateOF()-s.calculateOF();
    }
    public RDPInstance getInstance() {
        return instance;
    }
    public int[] getSolution() {
        return solution;
    }
    public int getOf() {
        return of;
    }
    public boolean isUpdated() {
        return updated;
    }
    public Set<Integer> getLabeled() {
        return labeled;
    }
    public Set<Integer> getUnlabeled() {
        return unlabeled;
    }
    public boolean isLabeled(int node){
        return labeled.contains(node);
    }
    public int getLabel(int node){
        return solution[node];
    }
}