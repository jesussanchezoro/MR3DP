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

public class IG_bestSolution_Parallel implements Algorithm<RDPInstance, RDPSolution> {
    private int numSolutions;
    private int iterations=30;
    private RDPSolution bestSol;
    private float beta=0.30f;
    ThreadPoolExecutor executor;
    private Constructive<RDPInstance, RDPSolution> constructive;
    private Improvement<RDPSolution> improvement;
    public IG_bestSolution_Parallel(Constructive<RDPInstance, RDPSolution> constructive, int numSolutions) {
        this.constructive=constructive;
        this.numSolutions=numSolutions;
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }
    public IG_bestSolution_Parallel(Constructive<RDPInstance, RDPSolution> constructive, Improvement<RDPSolution> improvement, int numSolutions) {
        this.constructive=constructive;
        this.numSolutions=numSolutions;
        this.improvement=improvement;
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }
    @Override
    public Result execute(RDPInstance instance) {
        RandomManager.setSeed(2304);
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        Result result=new Result(instance.getName());
        bestSol=new RDPSolution(instance);
        int bestValue=0x3F3F3F3F;
        Timer.initTimer();
        //Algorithm
        ArrayList<Future<RDPSolution>> futSols=new ArrayList<>(numSolutions);
        for(int i=0; i<numSolutions;i++){
            futSols.add(executor.submit(() -> {
                RDPSolution sol = constructive.constructSolution(instance);
                if (improvement != null) improvement.improve(sol);
                return sol;
            }));
        }
        for(Future<RDPSolution> sol:futSols){
            try {
                if(sol.get().calculateOF()<bestValue){
                    bestSol=sol.get();
                    bestValue=bestSol.calculateOF();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.print(bestSol.calculateOF()+"->");
        iteratedGreedy(bestSol);
        System.out.println(bestSol.calculateOF());
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
    private void iteratedGreedy(RDPSolution sol){
        int cont=0;
        while(cont<iterations && Timer.getTime()/1000<Main.limitTime){
            ArrayList<Future<RDPSolution>> futSols=new ArrayList<>(numSolutions);
            int bestOF=0x3f3f3f;
            RDPSolution bestLocalSol=new RDPSolution(sol.getInstance());
            for(int i=0; i<Runtime.getRuntime().availableProcessors();i++){
                futSols.add(executor.submit(() -> {
                    RDPSolution copy = new RDPSolution(sol);
                    destruct(copy);
                    copy.reconstruct();
                    copy.clean();
                    return copy;
                }));
            }
            for(Future<RDPSolution> futSol:futSols){
                try {
                    RDPSolution prov=futSol.get();
                    if(prov.calculateOF()<bestOF){
                        bestLocalSol=prov;
                        bestOF=bestLocalSol.calculateOF();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            if(bestOF<sol.calculateOF()){
                cont=0;
                sol.copy(bestLocalSol);
            }else{
                cont++;
            }
        }
    }
    private void destruct(RDPSolution sol){
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
    @Override
    public RDPSolution getBestSolution() {
        return bestSol;
    }
    public String toString(){
        String imp="";
        if(improvement!=null){
            imp=improvement.toString()+"_";
        }
        return this.getClass().getSimpleName()+"("+constructive.toString()+"_"+imp+"NumSolutions"+numSolutions+"_Beta"+beta+")";
    }
}
