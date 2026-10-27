package proposal.algorithm;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.tools.RandomManager;
import grafo.optilib.tools.Timer;
import proposal.Main;
import proposal.structure.RDPInstance;
import proposal.structure.RDPSolution;
import proposal.structure.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IG_eliteSolutions implements Algorithm<RDPInstance, RDPSolution> {
    private int numSolutions;
    private int iterations=5;
    private RDPSolution bestSol;
    private boolean draw=false;
    private float beta=0.30f;
    private boolean diversity=false;
    private float eliteSetSize=0.1f;
    private Constructive<RDPInstance, RDPSolution> constructive;
    private Improvement<RDPSolution> improvement;
    public IG_eliteSolutions(Constructive<RDPInstance, RDPSolution> constructive, int numSolutions) {
        this.constructive=constructive;
        this.numSolutions=numSolutions;
    }
    public IG_eliteSolutions(Constructive<RDPInstance, RDPSolution> constructive, Improvement<RDPSolution> improvement, int numSolutions) {
        this.constructive=constructive;
        this.numSolutions=numSolutions;
        this.improvement=improvement;
    }
    @Override
    public Result execute(RDPInstance instance) {
        RandomManager.setSeed(2304);
        Result result=new Result(instance.getName());
        bestSol=new RDPSolution(instance);
        int bestValue=0x3F3F3F3F;
        List<RDPSolution> solutions= new ArrayList<>();
        Timer.initTimer();
        //Algorithm
        for(int i=0; i<numSolutions;i++){
            RDPSolution sol=constructive.constructSolution(instance);
            if(improvement!=null) improvement.improve(sol);
            solutions.add(sol);
            if(sol.calculateOF()<bestValue){
                bestValue=sol.calculateOF();
                bestSol=sol;
            }
        }
        solutions.sort(RDPSolution::better);
        for(int i=0;i<solutions.size()*eliteSetSize;i++){
            RDPSolution sol=new RDPSolution(solutions.get(i));
            iteratedGreedy(sol);
            if(sol.calculateOF()<bestSol.calculateOF()){
                bestSol=sol;
            }
        }
        //end
        result.add("OF",bestSol.calculateOF());
        result.add("Time",Timer.getTime()/1000f);
        System.out.println(bestSol.calculateOF()+" "+Timer.getTime()/1000f);
        if(!bestSol.isFeasible())
            System.out.println(bestSol.isFeasible());
        if (draw) Utils.saveInfoToDraw(bestSol,constructive.toString());
        if (diversity) Utils.diversity(solutions);
        return result;
    }
    private void iteratedGreedy(RDPSolution sol) {
        int cont=0;
        while(cont<iterations && Timer.getTime()/1000< Main.limitTime){
            RDPSolution copy=new RDPSolution(sol);
            destruct(sol);
            sol.reconstruct();
            sol.clean();
            if(copy.calculateOF()>sol.calculateOF()){
                cont=0;
            }else{
                sol.copy(copy);
                cont++;
            }
        }
    }
    private void destruct(RDPSolution sol) {
        int numNodes=sol.getInstance().getNumNodes();
        List<Integer> candidates=new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            candidates.add(i);
        }
        Collections.shuffle(candidates,RandomManager.getRandom());
        int i=0;
        while(sol.getUnlabeled().size()<Math.ceil(numNodes*beta)){
            int node=candidates.get(i);
            sol.removeLabels(node,0,2);
            i++;
        }
    }
    @Override
    public RDPSolution getBestSolution() {
        return bestSol;
    }
    public String toString() {
        String imp="";
        if(improvement!=null){
            imp=improvement.toString()+"_";
        }
        return this.getClass().getSimpleName()+"("+constructive.toString()+"_"+imp+"NumSolutions"+numSolutions+")";
    }
}
