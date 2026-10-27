package GA;

import GA.algorithm.GA;
import GA.structure.RDPFactory;
import GA.structure.RDPInstance;
import GA.structure.RDPSolution;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.results.Experiment;

public class Main {
    public static void main(String[] args){
        Algorithm<RDPInstance, RDPSolution>[] algorithms=new Algorithm[]{
                new GA()
        };
        String instanceFolder = ((args.length == 0) ? "instances/preliminary" : args[0]);
        RDPFactory factory=new RDPFactory();
        Experiment<RDPInstance, RDPFactory, RDPSolution> experiment=new Experiment<>(algorithms,factory);
        experiment.launch(instanceFolder, new String[]{".mtx"});
    }
}
