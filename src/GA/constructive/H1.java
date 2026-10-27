package GA.constructive;

import GA.structure.RDPInstance;
import GA.structure.RDPSolution;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;

import java.util.*;

public class H1 implements Constructive<RDPInstance, RDPSolution> {
    @Override
    public RDPSolution constructSolution(RDPInstance instance) {
        List<Integer> candidates=new ArrayList<>();
        Set<Integer> labeled=new HashSet<>();
        RDPSolution sol=new RDPSolution(instance);
        for(int i=0; i<instance.getNumNodes();i++){
            sol.setLabel(i,0);
        }
        for(int i=0;i<instance.getNumNodes();i++){
            candidates.add(i);
        }
        int i=0;
        Collections.shuffle(candidates,RandomManager.getRandom());
        while(labeled.size()<instance.getNumNodes()){
            int node=candidates.get(i);
            if(!labeled.contains(node)){
                if(instance.getAdjacencyList()[node].size()==0){
                    sol.setLabel(node,2);
                } else {
                    int cont=0;
                    for (int neighbor: instance.getAdjacencyList()[node]) {
                        if (labeled.contains(neighbor)) {
                            cont++;
                        }
                    }
                    if(cont==instance.getAdjacencyList()[node].size()){
                        sol.setLabel(node,1);
                    } else {
                        sol.setLabel(node,0);
                        int rnd=RandomManager.getRandom().nextInt(instance.getAdjacencyList()[node].size());
                        int neighbor=instance.getAdjacencyList()[node].get(rnd);
                        boolean neighborLabeled=labeled.contains(neighbor);
                        while(neighborLabeled){
                            rnd=RandomManager.getRandom().nextInt(instance.getAdjacencyList()[node].size());
                            neighbor=instance.getAdjacencyList()[node].get(rnd);
                            neighborLabeled=labeled.contains(neighbor);
                        }
                        sol.setLabel(neighbor,3);
                        labeled.add(neighbor);
                        for (int n: instance.getAdjacencyList()[node]) {
                            if (!labeled.contains(n)) {
                                sol.setLabel(n,2);
                                labeled.add(n);
                            }
                        }
                    }
                }
            }
            labeled.add(node);
            i++;
        }
        sol.calculateOF();
        return sol;
    }
}
