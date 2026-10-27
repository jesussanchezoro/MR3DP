package proposal.structure;

import grafo.optilib.metaheuristics.Constructive;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Utils {
    public static void diversity(List<RDPSolution> solutions){
        int matrix[][]= new int[solutions.size()][solutions.size()];
        String type="of";
        RDPInstance instance=solutions.get(0).getInstance();
        for (int i = 0; i < solutions.size(); i++) {
            for (int j = i+1; j < solutions.size(); j++) {
                int difference=solutions.get(i).calculateDifference(type,solutions.get(j));
                matrix[i][j]=difference;
                matrix[j][i]=difference;
            }
        }
        try (PrintWriter pw = new PrintWriter("experiments\\differences\\"+instance.getName())) {
            for (int i = 0; i < solutions.size(); i++) {
                for (int j = 0; j < solutions.size(); j++) {
                    pw.print(matrix[i][j]+"\t");
                }
                pw.println();
            }
            pw.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveInfoToDraw(RDPSolution bestSol, String name){
        try (PrintWriter pw = new PrintWriter("draw\\"+name+"\\"+bestSol.getInstance().getName())) {
            pw.println(bestSol.getInstance().getNumNodes()+"\t"+bestSol.getInstance().getNumEdges());
            for (int i = 0; i < bestSol.getInstance().getNumNodes(); i++) {
                pw.println(i+"\t"+bestSol.getLabel(i));
            }
            for (int i = 0; i < bestSol.getInstance().getNumNodes(); i++) {
                for (int j = i+1; j < bestSol.getInstance().getNumNodes(); j++) {
                    if(bestSol.getInstance().getAdjacencyList()[i].contains(j)){
                        pw.println(i + "\t" + j);
                    }
                }
            }
            pw.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveBoundsToFile(RDPInstance instance){
        try (PrintWriter pw = new PrintWriter("bounds\\"+instance.getName())) {
            pw.println(instance.getLowerBound()+"\t"+ instance.getUpperBound());
            pw.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
