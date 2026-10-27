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
import java.util.concurrent.*;

public class IG_allSolutions_Parallel implements Algorithm<RDPInstance, RDPSolution> {
    private int numSolutions;
    private int iterations=30;//30
    private float beta=0.10f;
    private RDPSolution bestSol;
    private Constructive<RDPInstance, RDPSolution> constructive;
    private Improvement<RDPSolution> improvement;
    private boolean iteratedGreedy=false;
    private boolean localSearch=false;
    private boolean rg=true;
    private int NTHREADS = Runtime.getRuntime().availableProcessors();

    public IG_allSolutions_Parallel(Constructive<RDPInstance, RDPSolution> constructive, int numSolutions) {
        this.constructive=constructive;
        this.numSolutions=numSolutions;
        localSearch=false;
        iteratedGreedy=false;
    }
    public IG_allSolutions_Parallel(Constructive<RDPInstance, RDPSolution> constructive, Improvement<RDPSolution> improvement, int numSolutions) {
        this.constructive=constructive;
        this.numSolutions=numSolutions;
        this.improvement=improvement;
        localSearch=true;
        iteratedGreedy=false;
    }
    public IG_allSolutions_Parallel(Constructive<RDPInstance, RDPSolution> constructive, Improvement<RDPSolution> improvement, int numSolutions, int iterations, float beta, boolean rg) {
        this.constructive=constructive;
        this.numSolutions=numSolutions;
        this.improvement=improvement;
        this.beta=beta;
        this.iterations=iterations;
        localSearch=true;
        iteratedGreedy=true;
        this.rg=rg;
    }

    public IG_allSolutions_Parallel(Constructive<RDPInstance, RDPSolution> constructive, Improvement<RDPSolution> improvement, int numSolutions, int iterations, float beta, boolean rg, int num_threads) {
        this.constructive=constructive;
        this.numSolutions=numSolutions;
        this.improvement=improvement;
        this.beta=beta;
        this.iterations=iterations;
        localSearch=true;
        iteratedGreedy=true;
        this.rg=rg;
        this.NTHREADS =  num_threads;
    }
    @Override
    public Result execute(RDPInstance instance) {
        RandomManager.setSeed(System.currentTimeMillis());
        Result result=new Result(instance.getName());
        bestSol=new RDPSolution(instance);
        int bestValue=0x3F3F3F3F;
        Timer.initTimer(Main.limitTime);
        //Algorithm
        ArrayList<Future<RDPSolution>> futSols=new ArrayList<>(numSolutions);
//        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NTHREADS);
        for(int i=0; i<numSolutions;i++){
            futSols.add(executor.submit(() -> {
                if(Timer.timeReached()){
                    return null;
                }
                RDPSolution sol=constructive.constructSolution(instance);
                if(Timer.timeReached()){
                    return sol;
                }
                if(localSearch) improvement.improve(sol);
                if(Timer.timeReached()){
                    return sol;
                }
                if(iteratedGreedy) iteratedGreedy(sol);
                return sol;
            }));
        }
        for(Future<RDPSolution> futSol:futSols){
            try {
                RDPSolution sol=futSol.get();
                if(sol!=null && sol.calculateOF()<bestValue){
                    bestSol=sol;
                    bestValue=sol.calculateOF();
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        //end
        result.add("OF",bestSol.calculateOF());
        result.add("Time",Timer.getTime()/1000f);
        System.out.println(bestSol.calculateOF()+" "+Timer.getTime()/1000f);
        if(!bestSol.isFeasible())
            System.out.println(bestSol.isFeasible());
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    private void iteratedGreedy(RDPSolution sol) {
        int cont=0;
        while(cont<iterations && !Timer.timeReached()){
            RDPSolution copy=new RDPSolution(sol);
            destruct(copy);
            reconstruct(copy);
            if(copy.calculateOF()<sol.calculateOF()){
                cont=0;
                sol.copy(copy);
            }else{
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
        while(numNodes-sol.getLabeled().size()<Math.ceil(numNodes*beta)){
            int node=candidates.get(i);
            sol.removeLabels(node,0,1);
            i++;
        }
    }
    private void reconstruct(RDPSolution solution){
        if(rg){
            solution.reconstruct();
        } else{
            solution.reconstructRandom();
        }
    }
    @Override
    public RDPSolution getBestSolution() {
        return bestSol;
    }
    public String toString() {
        String imp="";
        if(localSearch){
            imp="LocalSearch";
        }
        String ig="";
        if(iteratedGreedy){
            ig=","+"IteratedGreedy"+"_Beta"+beta+"_Iteration"+iterations;
            if(rg){
                ig+="_"+"ReconstructGreedy";
            }else{
                ig+="_"+"ReconstructRandom";
            }
        }
        return this.getClass().getSimpleName()+"_"+numSolutions+"Solutions("+constructive.toString()+"_"+imp+ig+")_"+NTHREADS;
    }
}
