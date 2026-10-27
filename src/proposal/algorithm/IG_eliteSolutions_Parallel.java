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

import java.util.*;
import java.util.concurrent.*;

public class IG_eliteSolutions_Parallel implements Algorithm<RDPInstance, RDPSolution> {
    private int numSolutions;
    private int iterations=30;
    private RDPSolution bestSol;
    private float beta=0.30f;
    private Constructive<RDPInstance, RDPSolution> constructive;
    private Improvement<RDPSolution> improvement;
    public IG_eliteSolutions_Parallel(Constructive<RDPInstance, RDPSolution> constructive, int numSolutions) {
        this.constructive=constructive;
        this.numSolutions=numSolutions;
    }
    public IG_eliteSolutions_Parallel(Constructive<RDPInstance, RDPSolution> constructive, Improvement<RDPSolution> improvement, int numSolutions) {
        this.constructive=constructive;
        this.numSolutions=numSolutions;
        this.improvement=improvement;
    }
    @Override
    public Result execute(RDPInstance instance) {
        RandomManager.setSeed(2304);
        Result result=new Result(instance.getName());
        bestSol=new RDPSolution(instance);
        int bestValue=0x3f3f3f;
        Timer.initTimer();
        //Algorithm
        ArrayList<Future<RDPSolution>> futSols=new ArrayList<>(numSolutions);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        for(int i=0; i<numSolutions;i++){
            futSols.add(executor.submit(() -> {
                RDPSolution sol = constructive.constructSolution(instance);
                if (improvement != null) improvement.improve(sol);
                return sol;
            }));
        }
        PriorityQueue<RDPSolution> q=new PriorityQueue<>(Comparator.comparingInt(RDPSolution::calculateOF));
        for(Future<RDPSolution> futSol:futSols){
            try {
                q.add(new RDPSolution(futSol.get()));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        futSols.clear();
        for(int i=0;i< Runtime.getRuntime().availableProcessors();i++){
            RDPSolution localSol = q.poll();
            futSols.add(executor.submit(() -> {
                RDPSolution sol = new RDPSolution(localSol);
                iteratedGreedy(sol);
                return sol;
            }));
        }
        for(Future<RDPSolution> futSol:futSols){
            try {
                RDPSolution sol=futSol.get();
                if(sol.calculateOF()<bestValue){
                    bestSol=sol;
                    bestValue=sol.calculateOF();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
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
        return this.getClass().getSimpleName()+"("+constructive.toString()+"_"+imp+"NumSolutions"+numSolutions+"_Beta"+beta+")";
    }
}
