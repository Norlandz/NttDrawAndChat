package com.redfrog.note.util;

import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.redfrog.note.exception.TypeError;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class JavafxUtil {

  //_________________________________________________________
  //______________________
  //_
  //________________________________________________
  //
  //__________________________________________________________________________________________________________________
  public static double get_Pos_relToGivenPane(Node node, Pane pane_UpperHost, String axis) {
    double x = 0;
    Node pane_curr = node;

    do {

      if (axis.equals("X")) {
        x += pane_curr.getLayoutX();
      }
      else if (axis.equals("Y")) {
        x += pane_curr.getLayoutY();
      }
      else {
        throw new TypeError();
      }

      pane_curr = (Pane) pane_curr.getParent(); //________
      if (pane_curr == null) {
        System.out.println("This node (seems) is removed from the Scene -- thus its Parent is null -- Listeners still working cuz may need later when Undo.");
        return x;
        //______________________________________________________________________________________________________________________________________________________
        //_____________________________________________________
      }
    } while (pane_curr != pane_UpperHost);

    return x;
  }

  public enum MoveLayerMode {
    PosIncrementRel,
    TopBottom,
    PosAbs,
  }

  public static Pair<Integer, Integer> move_NodeLayer(Node node, int ind_Move, MoveLayerMode moveLayerMode) {
    Pane node_parent = (Pane) node.getParent();

    Integer ind_new = null;
    ObservableList<Node> children = node_parent.getChildren();
    int ind_ori = children.indexOf(node);
    if (ind_ori == -1) { throw new Error("No such element"); }
    int size_BeforeRemove = children.size(); //______________________________________________________________________________________________

    if (moveLayerMode == MoveLayerMode.PosIncrementRel) {
      ind_new = ind_ori + ind_Move;
      if (ind_new > size_BeforeRemove - 1) { ind_new = size_BeforeRemove - 1; }
      if (ind_new < 0) { ind_new = 0; }
      if (ind_new != ind_ori) {
        children.remove(node);
        children.add(ind_new, node);
      }
    }
    else if (moveLayerMode == MoveLayerMode.TopBottom) {
      if (ind_Move == -1 && ind_ori != 0) {
        ind_new = 0;
        node.toBack(); //______________________________________________
      }
      else if (ind_Move == 1 && ind_ori != size_BeforeRemove - 1) { //_____________________________________________________________________________________________________
        ind_new = size_BeforeRemove - 1;
        node.toFront(); //______________________________________________
      }
      else {
        throw new TypeError();
      }
    }
    else if (moveLayerMode == MoveLayerMode.PosAbs) {
      if (ind_Move != ind_ori) {
        ind_new = ind_Move;
        children.remove(node);
        children.add(ind_new, node);
        if (ind_Move >= size_BeforeRemove) { throw new IndexOutOfBoundsException("Index out of bound -- cuz this is not just Add, this is *Remove* then Add. To add to end, Index must 1 less than Size, not equal to Size."); }
      }
    }
    else {
      throw new TypeError();
    }

    return new ImmutablePair<>(ind_ori, ind_new);
  }

  //______________________________________________________________________________________________________________________________________
  //______________
  //_____________________________________________
  //_______________________________________
  //____________________________________
  //__________________________________________
  //
  //_______________________________________________
  //
  //___________________________
  //______________________________________________________________
  //_________________________________________
  //______________________________________________________________
  //_____________________________________________________________________________________________________________________________________________
  //
  //__________________________________________________________________
  //______________________________________________
  //_______________________________________________________________________________
  //_______________________________________
  //_______________________________
  //______________________________
  //____________________________________
  //_______
  //_____
  //_____________________________________
  //________________________________________________
  //____________________
  //_______________________________________________________________________
  //_______
  //________________________________________________________________________________________________________________________________________________________________________________
  //________________________________________
  //________________________________________________________________________
  //_______
  //____________
  //__________________________________________________________________________
  //_______
  //_____
  //___________________________________________________________
  //___________________________
  //____________________________
  //__________________________________
  //________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //_____
  //
  //_________________________________________________
  //___

  //_____________

  public static void scale_NodeShapeScale(Node node, double scaleX, double scaleY) {
    node.setScaleX(scaleX);
    node.setScaleY(scaleY);
  }

  //_____________

  public final static String rgba_Blue = "rgba(0,0,255,0.5)";
  public final static String rgba_Red_dim = "rgba(128,0,0,0.5)";
  public final static String rgba_Green_dim = "rgba(0,128,0,0.5)";
  public final static String rgba_Blue_dim = "rgba(0,0,128,0.5)";
  public final static String rgba_Yellow_dim = "rgba(128,128,0,0.5)";
  public final static String rgba_Orange = "rgba(255,165,0,0.5)";
  public final static String rgba_Purple = "rgba(128,0,128,0.5)";
  public final static String rgba_Cyan = "rgba(0,255,255,0.5)";
  public final static String rgba_Teal = "rgba(0,128,128,0.5)";
  public final static String rgba_Grey = "rgba(128,128,128,0.5)";
  public final static double rgba_opacity = 0.5;

}
