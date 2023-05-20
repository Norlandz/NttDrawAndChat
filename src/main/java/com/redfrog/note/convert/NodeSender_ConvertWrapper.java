package com.redfrog.note.convert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.redfrog.note.event.mousekeyboardinput.StatusPhase;
import com.redfrog.note.exception.NotSupportedException;
import com.redfrog.note.exception.TypeError;
import com.redfrog.note.fundamental.NodeSig;
import com.redfrog.note.fundamental.NodeSigManager;
import com.redfrog.note.nodeRole.ButtonSig;
import com.redfrog.note.nodeRole.CanvasSig;
import com.redfrog.note.nodeRole.PanelSig;
import com.redfrog.note.nodeRole.TextAreaSig;
import com.redfrog.note.nodeShape.Arrow;
import com.redfrog.note.nodeShape.Point;
import com.redfrog.note.serialization.SerializableMethod;
import com.redfrog.note.session.WindowSession;
import com.redfrog.note.session.WindowSessionExecutor;
import com.redfrog.note.traversal.UndoManager;
import com.redfrog.note.util.JavafxUtil;
import com.redfrog.note.util.JavafxUtil.MoveLayerMode;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class NodeSender_ConvertWrapper implements WindowSessionExecutor {

  private final WindowSession windowSession_corr;
  private final NodeSigManager nodeSigManager_corr;
  public final NodeSender_EventAnnouncer nodeSender_EventAnnouncer;
  private final NodeCreatorAndConvertor nodeCreatorAndConvertor;
  private final UndoManager undoManager_corr;

  //_______________________________________________________________________________

  public NodeSender_ConvertWrapper(WindowSession windowSession_corr) {
    this.windowSession_corr        = windowSession_corr;
    this.nodeSigManager_corr       = windowSession_corr.nodeSigManager;
    this.nodeSender_EventAnnouncer = new NodeSender_EventAnnouncer(windowSession_corr);
    this.nodeCreatorAndConvertor   = new NodeCreatorAndConvertor(windowSession_corr);
    this.undoManager_corr          = windowSession_corr.undoManager;
    //_______________________________________________________________________________________________________________________
  }

  @Override
  public WindowSession getWindowSession() { return windowSession_corr; }

  public AnchorPane create_Panel() {
    //______________________________________________________________________________________________________________
    return this.<AnchorPane>create_Node(AnchorPane.class);
  }

  public <T extends Node> T create_Node(Class<T> clazzJfxNode) {
    T jfxNode = null;
    Constructor<T> cstu;
    try {
      cstu    = clazzJfxNode.getConstructor();
      jfxNode = cstu.newInstance();
    } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new Error(e);
    }

    NodeSig nodeSig = nodeCreatorAndConvertor.jfxNode2nodeSig(jfxNode);

    //___________________________________________________________________
    //_______________________________
    //___________________________________________________________________
    //__________________________

    nodeSender_EventAnnouncer.send_NodeCreatedEvent(nodeSig);

    return jfxNode;
  }

  public void move_Node(Node node, double posX, double posY) { move_Node(node, posX, posY, StatusPhase.End_begin); }

  public void move_Node(Node node, double posX, double posY, StatusPhase statusPhase) {
    //__________________________________________________________________
    node.setLayoutX(posX);
    node.setLayoutY(posY);
    //__________________________
    //________________________________

    NodeSig nodeSig = nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(node);
    Point point = new Point(posX, posY);
    nodeSig.shape.set_pt_TopLeftFront(point);

    nodeSender_EventAnnouncer.send_NodeMovedEvent(nodeSig, statusPhase);
  }

  public void change_Shape(Node node, double width, double height) {
    //__________________________________________________________________
    Class<? extends Node> clazz = node.getClass();
    //___________________________
    if (Region.class.isAssignableFrom(clazz)) {
      //_________________________________________________________
      ((Region) node).setPrefSize(width, height);
    }
    else if (Canvas.class.isAssignableFrom(clazz)) {
      ((Canvas) node).setWidth(width);
      ((Canvas) node).setHeight(height);
    }
    else {
      throw new NotSupportedException();
    }
    //__________________________
    //________________________________

    NodeSig nodeSig = nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(node);
    //__________________________________________________
    nodeSig.shape.setGrandXLength(width);
    nodeSig.shape.setGrandYLength(height);

    nodeSender_EventAnnouncer.send_NodeShapeSizeChangedEvent(nodeSig, width, height);
  }

  public void establish_NodeRelationshipContainmentStatus(Pane pane_parent, Node node_child) {
    establish_NodeRelationshipContainmentStatus(pane_parent, node_child, false); //_
  }

  //_____________________________________________________________________________________________________________________
  //______________________________________________
  //
  //_________
  //________________________________________________________________________________________________________________________________________________________
  //______________________________________________________________________________________________________________
  //____________________________________________________________________________________________________________
  //___________
  //____________________________________________________________________________________
  //_____________________________________________________________________________________________
  //_____________________________________
  //__________________________________
  //___________________________________________________________
  //_________________________________________________
  //______________________________________________________________________
  //_______
  //____________
  //__________________________________________________________________
  //_______
  //_____
  //___________________________________________________________________________________________
  //_________________________________________________________
  //_____________________________________________________________________________
  //
  //________________________________________________________________________________________________________________________
  //___

  public void establish_NodeRelationshipContainmentStatus(Pane pane_parent, Node node_child, boolean det_SetupRoot) {
    //__________________________________________________________________
    pane_parent.getChildren().add(node_child);
    //__________________________
    //_______________________________
    //_____________________________________________________________________
    //__________________________
    //______________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________________________________________
    //____________________________________________
    //_____________________________________
    //___________________________________________________________________________________
    //_______________________________________________________________________
    //_________________________________________________________________________
    //________________________________________________________________________________________________________
    //______________________________________________________________________________
    //_______________________________________________________________
    //_______________________________________________________________________________________________________________________________________________________________________________
    //___________________________________
    //_______________________________________________________________________________________

    NodeSig nodeSig_containing;
    if (det_SetupRoot) {
      if (pane_parent == windowSession_corr.pane_JavafxRoot) {
        nodeSig_containing                  = new PanelSig(); //_____________
        nodeSig_containing.det_IsJavafxRoot = true;
      }
      else {
        throw new Error("specified det_SetupRoot == true; But, this is not the root :: " + pane_parent);
      }
    }
    else {
      nodeSig_containing = nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(pane_parent);
      if (nodeSig_containing == null) { throw new Error("null corresponding nodesig, is this a Root node? If so, specify det_SetupRoot = true; :: " + pane_parent); }
    }

    NodeSig nodeSig_contained = nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(node_child);
    if (nodeSig_contained == null) { throw new Error(); }

    NodeSigManager.estRel_A_contain_B(nodeSig_containing, nodeSig_contained);

    //____________________________________________________________________
    nodeSender_EventAnnouncer.send_NodeRelationshipContainmentStatusChangedEvent(nodeSig_containing, nodeSig_contained);
  }

  //_____________________________________________________________
  public void change_Text(Node fxNode, String content_old, String content_new) {
    //__________________________________________________________________
    //_____________________
    //__________________________
    //________________________________

    Class<? extends Node> clazz = fxNode.getClass();
    if (TextArea.class.isAssignableFrom(clazz)) {
      TextAreaSig nodeSig = (TextAreaSig) nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(fxNode);
      nodeSig.setContent(content_new);
      nodeSender_EventAnnouncer.send_TextChangedEvent(nodeSig, content_old, content_new);
    }
    else if (Button.class.isAssignableFrom(clazz)) {
      ButtonSig nodeSig = (ButtonSig) nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(fxNode);
      //_______________________________
      //__________________________________

      throw new NotSupportedException();
    }
    else {
      throw new NotSupportedException();
    }
  }

  //______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________

  public void paint_Dot(Canvas canvas, double posX, double posY, boolean det_AddedorRemoved, StatusPhase statusPhase, List<Point> arr_BatchLinePoints_Ongoing) {
    //__________________________________________________________________
    if (det_AddedorRemoved == true) {
      final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
      if (statusPhase == StatusPhase.Begin_begin) {
        graphicsContext.beginPath();
        graphicsContext.moveTo(posX, posY);
        graphicsContext.stroke();
      }
      else if (statusPhase == StatusPhase.Ongoing_begin) {
        graphicsContext.lineTo(posX, posY);
        graphicsContext.stroke();
        graphicsContext.closePath();
        graphicsContext.beginPath(); //___________________________________________________________________________
        graphicsContext.moveTo(posX, posY);
      }
      else if (statusPhase == StatusPhase.End_begin) {
        graphicsContext.lineTo(posX, posY);
        graphicsContext.stroke();
        graphicsContext.closePath();
      }
      else {
        throw new TypeError();
      }
    }
    else {
      throw new NotSupportedException();
    }
    //__________________________
    //________________________________

    //__________________________________________________
    Point pt_PaintDot = new Point(posX, posY);
    CanvasSig canvasSig = (CanvasSig) nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(canvas);
    canvasSig.get_gp_PaintDots().add(pt_PaintDot);

    nodeSender_EventAnnouncer.send_PaintDotChangedEvent(canvasSig, pt_PaintDot, true, statusPhase, arr_BatchLinePoints_Ongoing);

  }

  public void remove_Node(Node node) {
    //__________________________________________________________________
    Parent node_parnet = node.getParent();
    Class<? extends Parent> clazz = node_parnet.getClass();

    if (Pane.class.isAssignableFrom(clazz)) {
      ((Pane) node_parnet).getChildren().remove(node); //_
    }
    else {
      throw new NotSupportedException("Idk ... dk parent/child org in javafx :: " + clazz.toGenericString());
    }
    //__________________________
    //________________________________

    NodeSig nodeSig = nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(node);
    //____________________________________

    //_________________________________________________
    //_
    //_
    //__________________________________________
    //____________________________________________________________________
    //____________________________
    //________________
    //_
    //__________________________________________
    //__________________________________________________________
    //_______________________________________________________________________________
    //______
    //__
    //__
    //____________________________________
    //_
    //_____________________________________
    //______________________________________________________________________
    //________________________________________________________________
    //____________________
    //___________________________________________

    nodeSender_EventAnnouncer.send_NodeRemovedEvent(nodeSig);
  }

  public void focusOn_Node(Node node) {
    //__________________________________________________________________
    if (!node.isFocused()) {
      //______________________________________________________________________
      node.requestFocus();
    }

    NodeSig nodeSig = nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(node);

    //________________

    //_____________________________________________________________________
    //__________________________________
    //___________________________________________________________________
    //_____
    //__________________________________________________
    //__________________________________________________________________________

    NodeSig node_focus_prev = nodeSigManager_corr.node_Focused;
    if (node_focus_prev != null) {
      boolean fp_1 = node_focus_prev.is_det_Focused();
      boolean fc_1 = false;
      node_focus_prev.set_det_Focused(fc_1); //_______

      //_______________________________________________________________________________________________________
      //________________________________________________
      //________________________________________________
      //
      //__________________________________________________________________________________________________________________________________________________
      nodeSender_EventAnnouncer.send_NodeFocusStatusChangedEvent(node_focus_prev, fp_1, fc_1);
    }

    nodeSigManager_corr.node_Focused = nodeSig;
    boolean fp_2 = nodeSig.is_det_Focused();
    boolean fc_2 = true;
    nodeSig.set_det_Focused(fc_2);

    //_____________________________________________________________________________________________
    //______________________________________________
    //______________________________________________
    //
    //________________________________________________________________________________________________________________________________________________
    nodeSender_EventAnnouncer.send_NodeFocusStatusChangedEvent(nodeSig, fp_2, fc_2);
    //__________________________
    //_______________________________
    //__________________________________________________________________________________________________
    //___________________________________________________________________
    //_______________________________________________
    //_________________________________________________________
    //___________________________________________________________________________________________________________
    //__________________________
    //_______________________________________________

  }

  public Pair<NodeSig, SerializableMethod> wrap_FunctionExecutionEvent(Node node, String methodName,
                                                                       Class<?>[] clazz_args, Object[] args,
                                                                       Object ins_MethodOwner, Class<?> clazz_ins_MethodOwner) {
    System.out.println("windowSession_corr.msgConnRole=" + windowSession_corr.msgConnRole);
    //_________________________________________________________________________________________________

    System.out.println(">> wrap_FunctionExecutionEvent :: " + methodName + " :: " + node);
    NodeSig nodeSig = nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(node); //__________________

    int i = -1;
    for (Object arg_curr : args) {
      i++;
      System.out.println("++ " + methodName + " :: " + i + " :: " + arg_curr);
      Class<? extends Object> clazz_arg = arg_curr.getClass();
      if (Node.class.isAssignableFrom(clazz_arg)) {
        args[i] = nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(arg_curr); //_
      }
      //________________________________________________________________________________________________________
    }

    Method method = null;
    try {
      method = clazz_ins_MethodOwner.getDeclaredMethod(methodName, clazz_args);
    } catch (NoSuchMethodException | SecurityException e) {
      throw new Error(e);
    }
    SerializableMethod method_serializable = new SerializableMethod(method);

    return new ImmutablePair<NodeSig, SerializableMethod>(nodeSig, method_serializable);
  }

  public void wrap_and_send_FunctionExecutionEvent(Node node, String methodName, Class<?>[] clazz_args, Object[] args, Object ins_MethodOwner, Class<?> clazz_ins_MethodOwner) {
    Pair<NodeSig, SerializableMethod> pair = wrap_FunctionExecutionEvent(node, methodName, clazz_args, args, ins_MethodOwner, clazz_ins_MethodOwner);
    nodeSender_EventAnnouncer.send_FunctionExecutionEvent(pair.getLeft(), pair.getRight(), args, ins_MethodOwner, clazz_ins_MethodOwner);
  }

  public void wrap_and_send_UndoCompensationStepAddedEvent(Node node, String methodName, Class<?>[] clazz_args, Object[] args, Object ins_MethodOwner, Class<?> clazz_ins_MethodOwner) {
    Pair<NodeSig, SerializableMethod> pair = wrap_FunctionExecutionEvent(node, methodName, clazz_args, args, ins_MethodOwner, clazz_ins_MethodOwner);
    nodeSender_EventAnnouncer.send_UndoCompensationStepAddedEvent(pair.getLeft(), pair.getRight(), args, ins_MethodOwner, clazz_ins_MethodOwner);
  }

  public void wrap_and_send_RedoCompensationStepAddedEvent(Node node, String methodName, Class<?>[] clazz_args, Object[] args, Object ins_MethodOwner, Class<?> clazz_ins_MethodOwner) {
    Pair<NodeSig, SerializableMethod> pair = wrap_FunctionExecutionEvent(node, methodName, clazz_args, args, ins_MethodOwner, clazz_ins_MethodOwner);
    nodeSender_EventAnnouncer.send_RedoCompensationStepAddedEvent(pair.getLeft(), pair.getRight(), args, ins_MethodOwner, clazz_ins_MethodOwner);
  }

  public void place_UndoCheckpoint() {
    //__________________________________________________________________
    undoManager_corr.place_UndoCheckpoint();
    //__________________________
    //_______________________________
    //________________________________________________________________________________________________
    //_________________________________________________________________________________
    //______________________________________________________________________________________________________________
    //________________________________________________________________________________________________________
    //____________________________________________________________________________________________________________________________________
    //______________________________________________________________________________
    //____________________________________________________
    //__________________________________________________________________________________________
    //_______________________________________________
    //___________________________________________________________________________________________________________________________________________________________________________________________
    //_____________________________________________________________________________________________________________________________________________________________
    //_________________________________________________________________
    //__________________________________________________________
    //________________________________________________________________________________________________________
    //____________________________________________________________________________________________
    //______________________________________________________________________________________________
    //_____________________________________________________________________________________________________________________________
    //___________________________________________________________________________________________________
    //____________________________________________________________________________________
    //____________________________________________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________
    //____________________________________________________________________________________________________________
    //______________________________________
    //____________________________________
    //__________________________
    nodeSender_EventAnnouncer.send_UndoCheckpointPlacedEvent(null);
    System.out.println("Undo specific to a Node is not yet supported"); //____
  }

  public void undoTo_LastUndoCheckpoint() {
    //__________________________________________________________________
    undoManager_corr.undoTo_LastUndoCheckpoint();
    //__________________________
    //________________________________
    nodeSender_EventAnnouncer.send_UndoEvent(null);
  }

  public void redoTo_LastUndoCheckpoint() {
    //__________________________________________________________________
    undoManager_corr.redoTo_LastUndoCheckpoint();
    //__________________________
    //________________________________
    nodeSender_EventAnnouncer.send_RedoEvent(null);
  }

  public void move_Arrow(Arrow arrow, double posX, double posY, boolean det_ArrowStartOrEnd, StatusPhase statusPhase) {
    NodeSig nodeSig = nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(arrow);
    Point point = new Point(posX, posY);

    if (det_ArrowStartOrEnd) {
      //____________________________________________________________________
      arrow.setStartX(posX);
      arrow.setStartY(posY);
      //____________________________
      //__________________________________
      nodeSig.shape.set_pt_Start(point);
    }
    else {
      //____________________________________________________________________
      arrow.setEndX(posX);
      arrow.setEndY(posY);
      //____________________________
      //__________________________________
      nodeSig.shape.set_pt_End(point);
    }

    nodeSender_EventAnnouncer.send_ArrowMovedEvent(nodeSig, null, point, det_ArrowStartOrEnd, statusPhase);
  }

  public void paint_RectClear(Canvas canvas, double posX, double posY, double posW, double posH) {
    //__________________________________________________________________
    final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    graphicsContext.clearRect(posX, posY, posW, posH);
    //__________________________
    //________________________________

    NodeSig nodeSig = nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(canvas);
    nodeSender_EventAnnouncer.send_PaintRectClearEvent(nodeSig, posX, posY, posW, posH);
  }

  public Pair<Integer, Integer> move_NodeLayer(Node node, int ind_Move, MoveLayerMode moveLayerMode) {
    Pair<Integer, Integer> resultTup2 = JavafxUtil.move_NodeLayer(node, ind_Move, moveLayerMode);

    NodeSig nodeSig = nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(node);
    nodeSender_EventAnnouncer.send_NodeLayerMovedEvent(nodeSig, ind_Move, moveLayerMode);

    return resultTup2;
  }

  public void scale_NodeShapeScale(Node node, double scaleX_new, double scaleY_new) {
    JavafxUtil.scale_NodeShapeScale(node, scaleX_new, scaleY_new);

    NodeSig nodeSig = nodeSigManager_corr.mpp_jfxNode_vs_nodeSig.get(node);
    nodeSender_EventAnnouncer.send_NodeShapeScaleChangedEvent(nodeSig, scaleX_new, scaleY_new);
  }

}
