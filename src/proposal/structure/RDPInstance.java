package proposal.structure;

import grafo.optilib.structure.Instance;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class RDPInstance implements Instance {
    private String name;
    private int numNodes;
    private int numEdges;
    private int lowerBound;
    private int upperBound;
    private ArrayList<Integer> [] adjacencyList;
    public RDPInstance(String path){
        String separator = Pattern.quote(File.separator);
        String[] tokens = path.split(separator);
        name=tokens[tokens.length-1];
        System.out.print(name+" ");
        readInstance(path);
    }
    @Override
    public void readInstance(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;
            String[] lineContent;
            line = br.readLine();
            while (line.startsWith("%")) {
                line = br.readLine();
            }
            lineContent = line.split("\\s");
            if(lineContent.length>2) {
                numNodes = Integer.parseInt(lineContent[0]);
                numEdges = Integer.parseInt(lineContent[2]);
            } else{
                numNodes = Integer.parseInt(lineContent[0]);
                numEdges = Integer.parseInt(lineContent[1]);
            }
            if(lineContent.length>3){
                lowerBound = Integer.parseInt(lineContent[3]);
                upperBound = Integer.parseInt(lineContent[4]);
            }
            adjacencyList=new ArrayList[numNodes];
            for (int i=0; i<numNodes; i++){
                adjacencyList[i]=new ArrayList<>();
            }
            int count=0;
            for (int i=0; i< numEdges;i++){
                line= br.readLine();
                lineContent = line.split("\\s");
                int node1 = Integer.parseInt(lineContent[0])-1;
                int node2 = Integer.parseInt(lineContent[1])-1;
                if(node2!=node1){
                    count++;
                    adjacencyList[node1].add(node2);
                    adjacencyList[node2].add(node1);
                }
            }
            numEdges=count;
        } catch (FileNotFoundException e){
            System.out.println(("File not found " + path));
        } catch (IOException e){
            System.out.println("Error reading line");
        }
    }

    public String getName() {
        return name;
    }

    public int getNumNodes() {
        return numNodes;
    }

    public int getNumEdges() {
        return numEdges;
    }

    public ArrayList<Integer>[] getAdjacencyList() {
        return adjacencyList;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }
}
