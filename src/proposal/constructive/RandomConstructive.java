package proposal.constructive;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import proposal.structure.RDPInstance;
import proposal.structure.RDPSolution;

import java.util.*;

public class RandomConstructive implements Constructive<RDPInstance, RDPSolution> {
    @Override
    public RDPSolution constructSolution(RDPInstance instance) {
        RDPSolution sol=new RDPSolution(instance);
        List<Integer> nodes=new ArrayList<>(instance.getNumNodes());
        for(int i=0; i<instance.getNumNodes();i++){
            nodes.add(i);
        }
        Collections.shuffle(nodes,RandomManager.getRandom());
        for (int rnd:nodes){
            if(!sol.isLabeled(rnd)){
                sol.setLabel(rnd,3, true);
            }
        }
        sol.clean();
        return sol;
    }

    public String toString(){
        return getClass().getSimpleName();
    }


}
