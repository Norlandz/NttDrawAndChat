package com.redfrog.note.convert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.redfrog.note.exception.NotSupportedException;
import com.redfrog.note.fundamental.NodeSig;
import com.redfrog.note.fundamental.NodeSigManager;
import com.redfrog.note.nodeRole.ArrowSig;
import com.redfrog.note.nodeRole.ButtonSig;
import com.redfrog.note.nodeRole.CanvasSig;
import com.redfrog.note.nodeRole.PanelSig;
import com.redfrog.note.nodeRole.TextAreaSig;
import com.redfrog.note.nodeShape.Arrow;
import com.redfrog.note.nodeShape.Point;
import com.redfrog.note.nodeShape.Rectangle;
import com.redfrog.note.nodeShape.Shape;
import com.redfrog.note.session.WindowSession;
import com.redfrog.note.session.WindowSessionExecutor;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
____________________________________
_____________________________

_____________________________________________________

____________________

___________________________________
________________________________________

___________________________________________________________
_______________________________________________________________________________
_________

__________________________________________________
_______________________________________
________________________________


__
_________________________________________________________________________________________________
__
_____________________________________________________________________________________

__
_________________________
__
______________________________________________________________________________________________
_____________________


____________________________________________________________________________________________________________________
_____________________________________________________

_________________________________________________________________________________________________________________
_____________________________________________________________________________________________________________________________

_______________________________________________________________________________
___________________________________________________________________________________________________

_________________________________________________________________________________________________________
____________________________________________________________________________________________________________________________

_________________________________________________________________________________

_________________________________________________________________________________________________________________________________________________________________________________________________________________________
______


_____________________________________________________________________________
__________________________________________________________________
___
*/

public class NodeCreatorAndConvertor implements WindowSessionExecutor {

  private final WindowSession windowSession_corr;
  private final NodeSigManager nodeSigManager_corr;

  public NodeCreatorAndConvertor(WindowSession windowSession_corr) {
    this.windowSession_corr  = windowSession_corr;
    this.nodeSigManager_corr = windowSession_corr.nodeSigManager;
  }

  @Override
  public WindowSession getWindowSession() { return windowSession_corr; }

  //_________

  public NodeSig jfxNode2nodeSig(Node jfxNode) {
    Class<? extends Node> clazzJfxNode = jfxNode.getClass();

    NodeSig nodeSig;
    if (AnchorPane.class.isAssignableFrom(clazzJfxNode)) {
      nodeSig = new PanelSig();
    }
    else if (StackPane.class.isAssignableFrom(clazzJfxNode)) {
      nodeSig = new PanelSig();
    }
    else if (Button.class.isAssignableFrom(clazzJfxNode)) { //__________________
      //__________________________________________________________________
      nodeSig = new ButtonSig();
    }
    else if (TextArea.class.isAssignableFrom(clazzJfxNode)) {
      nodeSig = new TextAreaSig(); //____
    }
    else if (Canvas.class.isAssignableFrom(clazzJfxNode)) {
      //_____________________________________________________________________________________
      //___________________________________
      nodeSig = new CanvasSig(); //____
    }
    else if (Arrow.class.isAssignableFrom(clazzJfxNode)) {
      nodeSig = new ArrowSig(); //______________________________________
    }
    else {
      throw new NotSupportedException(clazzJfxNode.toGenericString());
    }

    nodeSigManager_corr.groupup_and_associate(jfxNode, nodeSig);

    Rectangle shape = new Rectangle(); //_____
    shape.set_pt_TopLeftFront(new Point(0, 0)); //______________________________
    nodeSig.shape = shape;

    return nodeSig;
  }

  //_________

  public void nodeSig2jfxNode_adjust(NodeSig nodeSig) {
    Class<? extends NodeSig> clazz = nodeSig.getClass();

    Node jfxNode;

    if (PanelSig.class.isAssignableFrom(clazz)) {
      Pane pane = NodeCreatorAndConvertor.<Pane>nodeSig2jfxNode(nodeSig);
      jfxNode = pane;
      System.out.printf(">>- %5s %s%n    %s%n    %s%n", 7, "Pane pane = NodeConvertor.nodeSig2panel(nodeMain);", pane, nodeSig);
      //__________________________________________________________________________________________________________________________________________________________________________
      //________________________________________________________________________________________________________________________________
      //________________________________________________________________________________
    }
    else if (ButtonSig.class.isAssignableFrom(clazz)) {
      //____
      Button button = NodeCreatorAndConvertor.<Button>nodeSig2jfxNode(nodeSig);
      jfxNode = button;
      //_______________________________________________________
    }
    else if (TextAreaSig.class.isAssignableFrom(clazz)) {
      //____
      TextArea textArea = NodeCreatorAndConvertor.<TextArea>nodeSig2jfxNode(nodeSig);
      jfxNode = textArea;
    }
    else if (CanvasSig.class.isAssignableFrom(clazz)) {
      //____
      Canvas canvas = NodeCreatorAndConvertor.<Canvas>nodeSig2jfxNode(nodeSig);
      jfxNode = canvas;
      //_____________________________________________________
    }
    else if (ArrowSig.class.isAssignableFrom(clazz)) {
      //____
      Arrow arrow = NodeCreatorAndConvertor.<Arrow>nodeSig2jfxNode(nodeSig);
      jfxNode = arrow;
    }
    else {
      throw new NotSupportedException(nodeSig.getClass().toGenericString());
    }

    nodeSigManager_corr.groupup_and_associate(jfxNode, nodeSig);
  }

  //_______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________

  //______________________________________________________________________
  public static <T extends Node> T nodeSig2jfxNode(NodeSig nodeSig) {
    //______
    T jfxNode;

    Class<? extends Node> class_M = nodeSig.class_fxNode_AssocConvert;
    Constructor<? extends Node> cstu = null;
    try {
      cstu    = class_M.getConstructor();
      jfxNode = (T) cstu.newInstance();
    } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new Error("panel = (Pane) cstu.newInstance();  :: " + cstu);
    }

    System.out.printf(">>- %5s %s%n    %s%n    %s%n", "*", "nodeSig2fxNode", jfxNode, nodeSig);

    //______
    Shape shape = nodeSig.shape;

    Point pt_TopLeftFront = shape.get_pt_TopLeftFront();
    jfxNode.setLayoutX(pt_TopLeftFront.getX());
    jfxNode.setLayoutY(pt_TopLeftFront.getY());

    return jfxNode;
  }

}
