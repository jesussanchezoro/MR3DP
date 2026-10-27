package proposal.algorithm;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.RandomManager;
import grafo.optilib.tools.Timer;
import proposal.structure.RDPInstance;
import proposal.structure.RDPSolution;
import proposal.structure.Utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Basic implements Algorithm<RDPInstance, RDPSolution> {
    private int numSolutions;
    private RDPSolution bestSol;
    private boolean draw=false;
    private boolean diversity=false;
    private Constructive<RDPInstance, RDPSolution> constructive;
    private Improvement<RDPSolution> improvement;
    public Basic(Constructive<RDPInstance, RDPSolution> constructive, int numSolutions) {
        this.constructive=constructive;
        this.numSolutions=numSolutions;
    }
    public Basic(Constructive<RDPInstance, RDPSolution> constructive, Improvement<RDPSolution> improvement, int numSolutions) {
        this.constructive=constructive;
        this.numSolutions=numSolutions;
        this.improvement=improvement;
    }
    @Override
    public Result execute(RDPInstance instance) {
        RandomManager.setSeed(2304);
        Result result=new Result(instance.getName());
        bestSol=new RDPSolution(instance);
        int bestOf=0x3F3F3F3F;
        List<RDPSolution> solutions= new ArrayList<>();
        Timer.initTimer();
        //Algorithm
        for(int i=0; i<numSolutions;i++){
            RDPSolution sol=constructive.constructSolution(instance);
            if(improvement!=null) improvement.improve(sol);
            solutions.add(sol);
            if(sol.calculateOF()<bestOf){
                bestSol=sol;
                bestOf=sol.calculateOF();
            }
        }
        //end
        result.add("OF",bestSol.calculateOF());
        result.add("Time",Timer.getTime()/1000f);
        System.out.println(bestSol.calculateOF()+" "+Timer.getTime()/1000f);
        if(!bestSol.isFeasible())
            System.out.println(bestSol.isFeasible());
        return result;
    }

    @Override
    public RDPSolution getBestSolution() {
        return bestSol;
    }
    public String toString(){
        String imp="";
        if(improvement!=null){
            imp=improvement.toString()+"_";
        }
        return this.getClass().getSimpleName()+"("+constructive.toString()+"_"+imp+"NumSolutions"+numSolutions+")";
    }
}
