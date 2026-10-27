package proposal.constructive;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import proposal.structure.RDPInstance;
import proposal.structure.RDPSolution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GreedyConstructive implements Constructive<RDPInstance, RDPSolution> {

    @Override
    public RDPSolution constructSolution(RDPInstance instance) {
        RDPSolution sol=new RDPSolution(instance);
        while(!sol.getUnlabeled().isEmpty()){
            int bestValue=-1;
            int bestNode=-1;
            for(int node: sol.getUnlabeled()){
                int value=sol.calculateNodeValue(node);
                if(value>bestValue){
                    bestNode=node;
                    bestValue=value;
                }
            }
            sol.setLabel(bestNode,3,true);
        }
        sol.clean();
        return sol;
    }

    public String toString(){
        return this.getClass().getSimpleName();
    }
}
