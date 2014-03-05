package com.mengruojun.pure.model.algorithm.anneal.tsplib_att48;

import com.mengruojun.pure.model.algorithm.anneal.helper.AnnealModelHelper;
import com.mengruojun.pure.model.algorithm.anneal.helper.Solution;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by clyde on 2/24/14.
 */
public class Att48SolutionHelper implements AnnealModelHelper<Att48Solution>{

  private static Logger logger = Logger.getLogger(Att48SolutionHelper.class);



  @Override
  public double evaluation(Att48Solution att48Solution) {
    double distance = 0;
    List<String> tourLine = att48Solution.getTourLine();
    for(int i =0 ;i < tourLine.size() -1 ;i++){
      Node node1 = Data.nodeDataMap.get(tourLine.get(i));
      Node node2 = Data.nodeDataMap.get(tourLine.get(i+1));
      distance += node1.distanceFrom(node2);
    }
    Node node1 = Data.nodeDataMap.get(tourLine.get(tourLine.size() -1));
    Node node2 = Data.nodeDataMap.get(tourLine.get(0));

    distance += node1.distanceFrom(node2);

    return distance;
  }

  /**
   * generate neighbour solution by 2-opt
   * @param att48Solution
   * @return
   */
  @Override
  public Att48Solution solutionGenerator(Att48Solution att48Solution){
    Att48Solution newSolution = null;
    try {
      //step 1: copy
      newSolution = (Att48Solution)att48Solution.clone();

      //step 2: do a little change
      // choose 2 nodes randomly and switch the position.
      int node1 = (int)Math.floor(48 * Math.random());  //[0,48)
      int node2 = (int)Math.floor(48 * Math.random());  //[0,48)
      if(node1 > node2){
        int tmp = node1;
        node1 = node2;
        node2 = tmp;
      }

      int i = 0;
      while(i < node1){
        newSolution.getTourLine().set(i, att48Solution.getTourLine().get(i));
        i++;
      }

      int j = node2;
      while( i < node2 + 1){
        newSolution.getTourLine().set(i, att48Solution.getTourLine().get(j));
        i++;
        j--;
      }

      while(i < att48Solution.getTourLine().size()){
        newSolution.getTourLine().set(i, att48Solution.getTourLine().get(i));
        i++;
      }




    } catch (CloneNotSupportedException e) {
      logger.error("", e);
    }
    return newSolution;
  }
}

class Node{
  private String nodeName;
  private int nodeX;
  private int nodeY;

  Node(String nodeName, int nodeX, int nodeY) {
    this.nodeName = nodeName;
    this.nodeX = nodeX;
    this.nodeY = nodeY;
  }

  /**
   * return the distance between this node and giving node
   */
  public long distanceFrom(Node node){
    return (long)Math.ceil(Math.sqrt(
            (  (this.getNodeX() - node.getNodeX())*(this.getNodeX() - node.getNodeX()) +
            (this.getNodeY() - node.getNodeY())*(this.getNodeY() - node.getNodeY())  )/10.0
    ));
  }

  public String getNodeName() {
    return nodeName;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public int getNodeX() {
    return nodeX;
  }

  public void setNodeX(int nodeX) {
    this.nodeX = nodeX;
  }

  public int getNodeY() {
    return nodeY;
  }

  public void setNodeY(int nodeY) {
    this.nodeY = nodeY;
  }
}
class Data{
  public final static Map<String, Node> nodeDataMap = new HashMap<String, Node>();

  static {
    nodeDataMap.put("node1", new Node("node1",6734,1453));
    nodeDataMap.put("node2", new Node("node2",2233,10));
    nodeDataMap.put("node3", new Node("node3",5530,1424));
    nodeDataMap.put("node4", new Node("node4",401,841));
    nodeDataMap.put("node5", new Node("node5",3082,1644));
    nodeDataMap.put("node6", new Node("node6",7608,4458));
    nodeDataMap.put("node7", new Node("node7",7573,3716));
    nodeDataMap.put("node8", new Node("node8",7265,1268));
    nodeDataMap.put("node9", new Node("node9",6898,1885));
    nodeDataMap.put("node10", new Node("node10",1112,2049));
    nodeDataMap.put("node11", new Node("node11",5468,2606));
    nodeDataMap.put("node12", new Node("node12",5989,2873));
    nodeDataMap.put("node13", new Node("node13",4706,2674));
    nodeDataMap.put("node14", new Node("node14",4612,2035));
    nodeDataMap.put("node15", new Node("node15",6347,2683));
    nodeDataMap.put("node16", new Node("node16",6107,669));
    nodeDataMap.put("node17", new Node("node17",7611,5184));
    nodeDataMap.put("node18", new Node("node18",7462,3590));
    nodeDataMap.put("node19", new Node("node19",7732,4723));
    nodeDataMap.put("node20", new Node("node20",5900,3561));
    nodeDataMap.put("node21", new Node("node21",4483,3369));
    nodeDataMap.put("node22", new Node("node22",6101,1110));
    nodeDataMap.put("node23", new Node("node23",5199,2182));
    nodeDataMap.put("node24", new Node("node24",1633,2809));
    nodeDataMap.put("node25", new Node("node25",4307,2322));
    nodeDataMap.put("node26", new Node("node26",675,1006));
    nodeDataMap.put("node27", new Node("node27",7555,4819));
    nodeDataMap.put("node28", new Node("node28",7541,3981));
    nodeDataMap.put("node29", new Node("node29",3177,756));
    nodeDataMap.put("node30", new Node("node30",7352,4506));
    nodeDataMap.put("node31", new Node("node31",7545,2801));
    nodeDataMap.put("node32", new Node("node32",3245,3305));
    nodeDataMap.put("node33", new Node("node33",6426,3173));
    nodeDataMap.put("node34", new Node("node34",4608,1198));
    nodeDataMap.put("node35", new Node("node35",23,2216));
    nodeDataMap.put("node36", new Node("node36",7248,3779));
    nodeDataMap.put("node37", new Node("node37",7762,4595));
    nodeDataMap.put("node38", new Node("node38",7392,2244));
    nodeDataMap.put("node39", new Node("node39",3484,2829));
    nodeDataMap.put("node40", new Node("node40",6271,2135));
    nodeDataMap.put("node41", new Node("node41",4985,140));
    nodeDataMap.put("node42", new Node("node42",1916,1569));
    nodeDataMap.put("node43", new Node("node43",7280,4899));
    nodeDataMap.put("node44", new Node("node44",7509,3239));
    nodeDataMap.put("node45", new Node("node45",10,2676));
    nodeDataMap.put("node46", new Node("node46",6807,2993));
    nodeDataMap.put("node47", new Node("node47",5185,3258));
    nodeDataMap.put("node48", new Node("node48",3023,1942));
  }
}
