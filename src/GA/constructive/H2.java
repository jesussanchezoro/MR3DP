package GA.constructive;

import GA.structure.RDPInstance;
import GA.structure.RDPSolution;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;

import java.util.*;

public class H2 implements Constructive<RDPInstance, RDPSolution> {
    @Override
    public RDPSolution constructSolution(RDPInstance instance) {
        Set<Integer> unvisited=new HashSet<>();
        RDPSolution sol=new RDPSolution(instance);
        for(int i=0; i<instance.getNumNodes();i++){
            unvisited.add(i);
        }
        while(!unvisited.isEmpty()){
            int rnd=RandomManager.getRandom().nextInt(unvisited.size());
            int cont1=0;
            int node=-1;
            for (int unv: unvisited){
                if(cont1==rnd){
                    node=unv;
                    break;
                }
                cont1++;
            }
            unvisited.remove(node);
            int cont=0;
            for (int neighbor: instance.getAdjacencyList()[node]) {
                if (!unvisited.contains(neighbor)) {
                    cont++;
                }
            }
            if(cont == instance.getAdjacencyList()[node].size()){
                sol.setLabel(node,2);
            } else {
                sol.setLabel(node,3);
                for (int n: instance.getAdjacencyList()[node]) {
                    if (unvisited.contains(n)) {
                        sol.setLabel(n,0);
                        unvisited.remove(n);
                    }
                }
            }
        }
        sol.calculateOF();
        return sol;
    }
}
