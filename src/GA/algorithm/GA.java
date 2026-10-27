package GA.algorithm;

import GA.constructive.H1;
import GA.constructive.H2;
import GA.constructive.H3;
import GA.constructive.H4;
import GA.structure.RDPInstance;
import GA.structure.RDPSolution;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.tools.RandomManager;
import grafo.optilib.tools.Timer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class GA implements Algorithm<RDPInstance, RDPSolution> {
    int numSolutions;
    RDPSolution bestSol;
    int bestCost;
    Constructive<RDPInstance, RDPSolution> h1;
    Constructive<RDPInstance, RDPSolution> h2;
    Constructive<RDPInstance, RDPSolution> h3;
    Constructive<RDPInstance, RDPSolution> h4;
    private List<RDPSolution> population;
    private List<RDPSolution> child;
    public GA() {
        h1=new H1();
        h2=new H2();
        h3=new H3();
        h4=new H4();
    }
    @Override
    public Result execute(RDPInstance instance) {
        RandomManager.setSeed(13);
        numSolutions=(500);
        Result result=new Result(instance.getName());
        bestSol=new RDPSolution(instance);
        bestCost=0x3f3f3f3f;
        System.out.print(instance.getName());
        Timer.initTimer(1800*1000);
        //Algorithm
        initialPopulation(instance);
        population.sort(Comparator.comparingInt(RDPSolution::calculateOF));
        int generation=0;
        while(generation<10000 && !Timer.timeReached()){
            child=new ArrayList<>();
            selectionAndCrossover();
            addNew();
            generation++;
        }
        //end
        result.add("OF",bestSol.calculateOF());
        result.add("Time",Timer.getTime()/1000f);
        System.out.println(" "+bestSol.calculateOF()+" "+Timer.getTime()/1000f);
        if(!bestSol.isFeasible())
            System.out.println(bestSol.isFeasible());
        return result;
    }
    private void initialPopulation(RDPInstance instance){
        population=new ArrayList<>(numSolutions);
        for(int i=0;i<166;i++){
            newSol(h1,instance);
            //System.out.println("H1 genera");
            newSol(h2,instance);
            //System.out.println("H2 genera");
            newSol(h4,instance);
            //System.out.println("H4 genera");
        }
        newSol(h4,instance);
        newSol(h3,instance);
        //System.out.println("H3 genera");
    }
    private void newSol(Constructive<RDPInstance,RDPSolution> constructive, RDPInstance instance){
        RDPSolution solution=constructive.constructSolution(instance);
        population.add(solution);
        checkBest(solution);
    }
    private void checkBest(RDPSolution solution){
        if(solution.calculateOF()<bestCost){
            bestCost=solution.calculateOF();
            bestSol.copy(solution);
        }
    }
    private void selectionAndCrossover(){
        Set<Integer> selected=new HashSet<>();
        int i=0;
        while(selected.size()<population.size()){
            if(selected.contains(i)) {
                i++;
                continue;
            }
            RDPSolution s1=population.get(i);
            selected.add(i);
            int j=RandomManager.getRandom().nextInt(i,population.size());
            while(selected.contains(j)){
                j=RandomManager.getRandom().nextInt(i,population.size());
            }
            selected.add(j);
            RDPSolution s2=population.get(j);
            cross(s1,s2);
            i++;
        }
    }
    private void cross(RDPSolution s1,RDPSolution s2){
        RDPSolution child1=new RDPSolution(s1);
        RDPSolution child2=new RDPSolution(s2);
        int id1=RandomManager.getRandom().nextInt(s1.getInstance().getNumNodes());
        int id2=RandomManager.getRandom().nextInt(s1.getInstance().getNumNodes());
        while(id2==id1){
            id2=RandomManager.getRandom().nextInt(s1.getInstance().getNumNodes());
        }
        for (int i = Math.min(id1,id2); i < Math.max(id1,id2); i++) {
            int valueS1=child1.getSolution()[i];
            child1.setLabel(i,child2.getSolution()[i]);
            child2.setLabel(i,valueS1);
        }
        if(!child1.isFeasible()) child1.repair();
        if(!child2.isFeasible()) child2.repair();
        child.add(child1);
        child.add(child2);
    }
    private void addNew(){
        boolean paper=true;
        if(paper){
            for (RDPSolution c:child) {
                checkBest(c);
                int i=population.size()-1;
                while(c.calculateOF()<population.get(i).calculateOF() && i>0){
                    i--;
                }
                if(c.calculateOF()<population.get(0).calculateOF()){
                    population.add(0,new RDPSolution(c));
                    population.remove(population.size()-1);
                }
                else if(i!=population.size()-1) {
                    population.add(i+1,new RDPSolution(c));
                    population.remove(population.size()-1);
                }
            }
        } else{
            population.clear();
            population.addAll(child);
            population.sort(Comparator.comparingInt(RDPSolution::calculateOF));
        }
    }
    @Override
    public RDPSolution getBestSolution() {
        return bestSol;
    }
    public String toString(){
        return this.getClass().getSimpleName();
    }
}
