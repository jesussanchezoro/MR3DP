package proposal.improvement;

import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.RandomManager;
import grafo.optilib.tools.Timer;
import proposal.Main;
import proposal.structure.RDPSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocalSearch_kNeighborhoods implements Improvement<RDPSolution> {
    private int kMax;//TODO:probar otros valores
    public LocalSearch_kNeighborhoods(int kMax){
        this.kMax= kMax;
    }
    @Override
    public void improve(RDPSolution solution) {
        boolean improve=true;
        while(improve){
            improve=checkImprove(solution);
            if (Timer.timeReached()) break;
        }
    }
    private boolean checkImprove(RDPSolution solution){
        List<Integer> candidates= new ArrayList<Integer>();
        for (int i = 0; i < solution.getInstance().getNumNodes(); i++) {
            candidates.add(i);
        }
        Collections.shuffle(candidates, RandomManager.getRandom());
        for(int i=0; i< candidates.size();i++) {
            int node=candidates.get(i);
            RDPSolution copy=new RDPSolution(solution);
            copy.removeLabels(node,0,kMax);
            copy.reconstruct();
            if(copy.calculateOF()<solution.calculateOF()){
                solution.copy(copy);
                return true;
            }
            if (Timer.timeReached()) break;
        }
        return false;
    }
    public String toString(){
        return this.getClass().getSimpleName()+"_kMax"+kMax;
    }
}
