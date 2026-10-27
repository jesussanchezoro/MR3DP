package GA.structure;

import grafo.optilib.structure.Solution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RDPSolution implements Solution {
    private RDPInstance instance;
    private  int []solution;
    private int of;
    private boolean updated;
    public RDPSolution(RDPInstance instance){
        this.instance=instance;
        solution=new int[instance.getNumNodes()];
        for(int i=0;i<instance.getNumNodes();i++){
            solution[i]=0;
        }
    }
    public RDPSolution(RDPSolution solution){
        copy(solution);
    }
    public void copy(RDPSolution sol){
        updated=sol.isUpdated();
        instance=sol.getInstance();
        solution=sol.getSolution().clone();
        of=sol.getOf();
    }
    public void setLabel(int node, int label){
        updated=true;
        solution[node]=label;
    }
    public int calculateNodeValue(int node){
        int value=0;
        for(int neighbor:instance.getAdjacencyList()[node]){
            if(solution[neighbor]!=-1){
                value++;
            }
        }
        return value;
    }
    public int calculateOF(){
        //updated=true;
        if(updated){
            of=0;
            for (int node : solution) {
                of+=node;
            }
            updated = false;
        }
        return of;
    }
    public boolean isFeasible(){ //nuevo
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
    public void repair(){
        updated=true;
        for(int i=0; i<instance.getNumNodes();i++){
            if(solution[i]==0) {
                int sum = 0;
                for (int neighbour : instance.getAdjacencyList()[i]) {
                    sum+=solution[neighbour];
                }
                if (sum < 3) {
                    if(sum==2) setLabel(i, 1);
                    if(sum<2) setLabel(i, 2);
                }
            }else if(solution[i]==1) {
                int sum = 0;
                for (int neighbour : instance.getAdjacencyList()[i]) {
                    sum+=solution[neighbour];
                }
                if (sum < 2) {
                    setLabel(i, 2);
                }
            }
        }
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

    @Override
    public String toString() {
        return this.getOf()+"";
    }
}
