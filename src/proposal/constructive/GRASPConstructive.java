package proposal.constructive;

import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;
import proposal.Main;
import proposal.structure.RDPInstance;
import proposal.structure.RDPSolution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GRASPConstructive implements Constructive<RDPInstance, RDPSolution> {
    private class Candidate{
        int node;
        int value;
        public Candidate(int node,int value){
            this.node=node;
            this.value=value;
        }
        public void setValue(int value){
            this.value=value;
        }
        public int getNode(){
            return node;
        }
        public int getValue(){
            return value;
        }
    }
    private float alpha=0.25f;
    private boolean rnd=false;
    public GRASPConstructive(int alpha){
        if(alpha==-1){
            rnd=true;
        }else{
            this.alpha=alpha/100f;
        }
    }
    @Override
    public RDPSolution constructSolution(RDPInstance instance) {
        RDPSolution sol=new RDPSolution(instance);
        int gMin=0x3F3F3F3F;
        int gMax=0;
        if(rnd){
            alpha=RandomManager.getRandom().nextFloat();
        }
        int [] rcl=new int[instance.getNumNodes()];
        List<Candidate> candidates=new ArrayList<>();
        //sol.initialize(2);
        boolean stop=false;
        for(int i=0;i<instance.getNumNodes();i++){
            candidates.add(new Candidate(i,instance.getAdjacencyList()[i].size()));
        }
        while(!stop && !candidates.isEmpty()){
            gMin=0x3F3F3F3F;
            gMax=0;
            int [] g=candidatesValue(sol,candidates,gMin,gMax);
            int limit=updateRcl(candidates,rcl,g[0], g[1]);
            if(limit==0){
                stop=true;
            }else{
                int selected=rcl[RandomManager.getRandom().nextInt(limit)];
                int node=candidates.get(selected).getNode();
                sol.setLabel(node,3, true);
                candidates.remove(selected);
            }
        }
        sol.clean();
        return sol;
    }
    private int[] candidatesValue(RDPSolution sol, List<Candidate> candidates,int gMin, int gMax) {
        for (Candidate candidate : candidates) {
            if (!sol.isLabeled(candidate.getNode())) {
                int value = sol.calculateNodeValue(candidate.getNode());
                candidate.setValue(value);
                if (value < gMin) {
                    gMin = value;
                }
                if (value > gMax) {
                    gMax = value;
                }
            } else {
                candidate.setValue(0);
            }
        }
        return new int[]{gMin, gMax};
    }
    private int updateRcl(List<Candidate> candidates, int [] rcl, int gMin, int gMax){
        int limit=0;
        float threshold=gMax-alpha*(gMax-gMin);
        for(int i=0; i<candidates.size();i++){
            if(candidates.get(i).getValue()>=threshold /*&& candidates.get(i).getValue()>0*/){
                rcl[limit]=i;
                limit++;
            }
        }
        return limit;
    }
    public String toString(){
        String alpStr=!rnd?""+alpha:"Rnd";
        return this.getClass().getSimpleName()+"_alpha"+alpStr;
    }
}
