package proposal;

import GA.algorithm.GA;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.results.Experiment;
import proposal.algorithm.*;
import proposal.constructive.GRASPConstructive;
import proposal.constructive.GreedyConstructive;
import proposal.constructive.RandomConstructive;
import proposal.constructive.RandomConstructiveRepair;
import proposal.improvement.LocalSearch_kNeighborhoods;
import proposal.structure.RDPFactory;
import proposal.structure.RDPInstance;
import proposal.structure.RDPSolution;
public class Main {
    public static int kMax=2;
    public static int limitTime=1800*1000;
    public static boolean reconstructGreedy=true;
    private static float[] betas = new float[]{0.1f/*,0.2f,0.3f,0.4f*/};//0.2
    private static int[] alphas = new int[]{/*25,*/50/*,75,-1*/};//0.25
    private static int[] iterationsWithoutImprove = new int[]{900/*100,200,300,400,500,600,700,800,900,1000*/};//700
    private static int[] numberOfSolutions = new int[]{/*100,*/200/*,300,400,500,600,700,800,900,1000*/};
    public static void main(String[] args) {
        Algorithm<RDPInstance, RDPSolution>[] algorithms = new Algorithm[]{
                //new IG_allSolutions_Parallel(new GRASPConstructive(50), new LocalSearch_kNeighborhoods(kMax), 30000, 3000, 0.1f, true),
//                new IG_allSolutions_Parallel(new GRASPConstructive(50), new LocalSearch_kNeighborhoods(kMax), 200, 900, 0.1f, true),
                //new IG_allSolutions_Parallel(new GRASPConstructive(50), new LocalSearch_kNeighborhoods(kMax), 900000),
                //new IG_allSolutions_Parallel(new GRASPConstructive(50), new LocalSearch_kNeighborhoods(kMax), 200),
                //new IG_allSolutions_Parallel(new GRASPConstructive(50),200),

                // PAPER
                new IG_allSolutions_Parallel(new GRASPConstructive(50), new LocalSearch_kNeighborhoods(kMax), 200, 900, 0.1f, true),

                // THREADS
//                new IG_allSolutions_Parallel(new GRASPConstructive(50), new LocalSearch_kNeighborhoods(kMax), 200, 900, 0.1f, true, 1),
//                new IG_allSolutions_Parallel(new GRASPConstructive(50), new LocalSearch_kNeighborhoods(kMax), 200, 900, 0.1f, true, 2),
//                new IG_allSolutions_Parallel(new GRASPConstructive(50), new LocalSearch_kNeighborhoods(kMax), 200, 900, 0.1f, true, 4),
//                new IG_allSolutions_Parallel(new GRASPConstructive(50), new LocalSearch_kNeighborhoods(kMax), 200, 900, 0.1f, true, 8),
//                new IG_allSolutions_Parallel(new GRASPConstructive(50), new LocalSearch_kNeighborhoods(kMax), 200, 900, 0.1f, true, 16),

//                new IG_allSolutions_Parallel_Robustness(new GRASPConstructive(50), new LocalSearch_kNeighborhoods(kMax), 200, 900, 0.1f, true, Runtime.getRuntime().availableProcessors(), 10),

        };
        String instanceFolder = ((args.length == 0) ? "instances/preliminary" : args[0]);
        RDPFactory factory = new RDPFactory();
        Experiment<RDPInstance, RDPFactory, RDPSolution> experiment = new Experiment<>(algorithms, factory);
        experiment.launch(instanceFolder, new String[]{".mtx"});
    }
}
