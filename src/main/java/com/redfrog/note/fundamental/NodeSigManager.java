package com.redfrog.note.fundamental;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javafx.scene.Node;

public class NodeSigManager {

  public final HashSet<NodeSig> gp_AllNode = new HashSet<>(); //_________________________
  public final HashMap<String, NodeSig> mpp_AllNode = new HashMap<>(); //_________________________
  //__________________________
  public final LinkedList<EventSig> arr_AllEvent = new LinkedList<>(); //_________________________

  public final HashMap<Node, NodeSig> mpp_jfxNode_vs_nodeSig = new HashMap<>();
  
//_____________________________________________________________________________________________________________________________________________________________________________________________

  public static void estRel_A_contain_B(NodeSig nodeSig_containing, NodeSig nodeSig_contained) {
    get_nodeRelationship(nodeSig_containing, nodeSig_contained).containmentStatus = NodeRelationship.ContainmentStatus.Containing;
    get_nodeRelationship(nodeSig_contained, nodeSig_containing).containmentStatus = NodeRelationship.ContainmentStatus.Contained;

  }

  private static NodeRelationship get_nodeRelationship(NodeSig nodeSig_AA, NodeSig nodeSig_BB) {
    HashMap<NodeSig, NodeRelationship> mpp_relationship = nodeSig_AA.mpp_relationship;
    NodeRelationship nodeRelationship = mpp_relationship.get(nodeSig_BB);
    if (nodeRelationship == null) {
      nodeRelationship = new NodeRelationship();
      mpp_relationship.put(nodeSig_BB, nodeRelationship);
    }
    return nodeRelationship;
  }

  //___________________________________________________________________
  public NodeSig node_Focused;

  public NodeSig get_LocalNonDeserialized_NodeSig_from_NodeSigManager(NodeSig nodeMain_deserialized) {
    NodeSig nodeMain_reside = mpp_AllNode.get(nodeMain_deserialized.idJava);
    if (nodeMain_reside == null) {
      throw new Error("Cannot find NodeSig :: " + nodeMain_deserialized + " :: " + mpp_AllNode); //_
    }
    return nodeMain_reside; //_
  }

  //____________________________
  public void groupup_and_associate(Node node, NodeSig nodeSig) {
    System.out.printf(">>- %5s %s%n    %s%n    %s%n", "3 || 6.5", "public static void associate(Node node, NodeSig nodeSig) {", node, nodeSig);

    gp_AllNode.add(nodeSig);
    mpp_AllNode.put(nodeSig.getIdJava(), nodeSig);

    nodeSig.fxNode_AssocConvert       = node;
    nodeSig.class_fxNode_AssocConvert = node.getClass();
    mpp_jfxNode_vs_nodeSig.put(node, nodeSig);
  }

}
