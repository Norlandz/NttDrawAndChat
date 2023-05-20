package com.redfrog.note;

import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.redfrog.note.convert.NodeReceiver_EventListener;
import com.redfrog.note.convert.NodeSender_ConvertWrapper;
import com.redfrog.note.event.mousekeyboardinput.ClipboardStatus_PanelCopyPaste;
import com.redfrog.note.event.mousekeyboardinput.MouseStatus_ArrowDrawing;
import com.redfrog.note.event.mousekeyboardinput.MouseStatus_CanvasFreehandDrawing;
import com.redfrog.note.event.mousekeyboardinput.MouseStatus_PanelMoving;
import com.redfrog.note.event.mousekeyboardinput.ResizeStatus;
import com.redfrog.note.event.mousekeyboardinput.StatusPhase;
import com.redfrog.note.exception.TypeError;
import com.redfrog.note.nodeShape.Arrow;
import com.redfrog.note.nodeShape.Point;
import com.redfrog.note.session.WindowSession;
import com.redfrog.note.session.WindowSessionExecutor;
import com.redfrog.note.traversal.UndoManager;
import com.redfrog.note.util.ImageUtil;
import com.redfrog.note.util.JavafxUtil;
import com.redfrog.note.util.JavafxUtil.MoveLayerMode;
import com.redfrog.note.util.MethodReferenceUtils;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DrawAndChatVm implements WindowSessionExecutor {

  private final WindowSession windowSession_corr;
  //___________________________________________________
  public final NodeSender_ConvertWrapper nodeSender_ConvertWrapper;
  private final UndoManager undoManager_corr;

  private final MouseStatus_ArrowDrawing mouseStatus_ArrowDrawing;
  private final MouseStatus_CanvasFreehandDrawing mouseStatus_CanvasFreehandDrawing;
  private final MouseStatus_PanelMoving mouseStatus_PanelMoving;
  private final ClipboardStatus_PanelCopyPaste clipboardStatus_PanelCopyPaste;

  //____________________________________________________________________________________

  public final NodeReceiver_EventListener nodeReceiver_EventListener; //_______
  //____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //____
  //________________________________________________________________
  //________________________________________________________

  @Deprecated
  private static final Boolean det_NeedRequestQualificationFirst = false;

  //____________________________________________________________________________________________________________________
  public DrawAndChatVm(WindowSession windowSession_corr) {
    this.windowSession_corr                = windowSession_corr;
    //_______________________________________________________________________________
    this.nodeSender_ConvertWrapper         = new NodeSender_ConvertWrapper(windowSession_corr);
    this.undoManager_corr                  = windowSession_corr.undoManager;

    this.mouseStatus_ArrowDrawing          = new MouseStatus_ArrowDrawing();
    this.mouseStatus_CanvasFreehandDrawing = new MouseStatus_CanvasFreehandDrawing();
    this.mouseStatus_PanelMoving           = new MouseStatus_PanelMoving();
    this.clipboardStatus_PanelCopyPaste    = new ClipboardStatus_PanelCopyPaste();

    //_______________________________________________________________________________________________________________________

    this.nodeReceiver_EventListener        = new NodeReceiver_EventListener(windowSession_corr);

    cstuInit__executor_BrustDetect_Zoom(); //_______
    cstuInit__executor_BrustDetect_TextInput();
  }

  public DrawAndChatVm() {
    System.out.println("> Using this? :: public DrawAndChatVm()");
    this.windowSession_corr                = null;
    this.nodeSender_ConvertWrapper         = null;
    this.undoManager_corr                  = null;
    this.mouseStatus_ArrowDrawing          = null;
    this.mouseStatus_CanvasFreehandDrawing = null;
    this.mouseStatus_PanelMoving           = null;
    this.clipboardStatus_PanelCopyPaste    = null;
    this.nodeReceiver_EventListener        = null;
  }

  @Override
  public WindowSession getWindowSession() { return windowSession_corr; }

  //_______________________

  public void initAppPanel(Stage stage) {
    //_______
    double w_CanwasBackground = 900;
    double h_CanvasBackground = 600;
    double w_PanelSemanticRoot = w_CanwasBackground + 50;
    double h_PanelSemanticRoot = h_CanvasBackground + 50;
    double w_Scene = w_PanelSemanticRoot + 50;
    double h_Scene = h_PanelSemanticRoot + 50;

    //____
    AnchorPane pane_JavafxRoot = new AnchorPane();
    //_______________________________________________________________
    //_____________________________________________________________
    //____________________________________________
    //_______________________________________________________________________
    windowSession_corr.pane_JavafxRoot = pane_JavafxRoot;

    AnchorPane panel_SemanticRoot = nodeSender_ConvertWrapper.create_Panel(); //_______________________________________________________________________________________________________________________
    windowSession_corr.panel_SemanticRoot = panel_SemanticRoot;

    nodeSender_ConvertWrapper.move_Node(panel_SemanticRoot, 10, 10);
    nodeSender_ConvertWrapper.change_Shape(panel_SemanticRoot, w_PanelSemanticRoot, h_PanelSemanticRoot);
    //____________________________________________________________________________________________________________________________________________
    String cssStyleStr_Normal = ""
                                + "-fx-border-style: solid;"
                                + "-fx-border-width: 1;"
                                + "-fx-border-color: " + JavafxUtil.rgba_Teal + ";";
    panel_SemanticRoot.setStyle(cssStyleStr_Normal);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_SemanticRoot, "setStyle", new Class[] { String.class }, new Object[] { cssStyleStr_Normal }, panel_SemanticRoot, Node.class);

    setup_Zoomable(panel_SemanticRoot);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_SemanticRoot, "setup_Zoomable", new Class[] { Node.class }, new Object[] { panel_SemanticRoot }, this, DrawAndChatVm.class);

    hotkey_UndoRedo(panel_SemanticRoot);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_SemanticRoot, "hotkey_UndoRedo", new Class[] { Node.class }, new Object[] { panel_SemanticRoot }, this, DrawAndChatVm.class);

    String cssStyleStr_Focus = ""
                               + "-fx-border-style: solid;"
                               + "-fx-border-width: 2;"
                               + "-fx-border-color: " + JavafxUtil.rgba_Teal + ";"; //_________________________
    listen_Focus(panel_SemanticRoot, cssStyleStr_Focus);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_SemanticRoot, "listen_Focus", new Class[] { Node.class, String.class }, new Object[] { panel_SemanticRoot, cssStyleStr_Focus }, this, DrawAndChatVm.class);

    hotkey_CreateNewPanel(panel_SemanticRoot);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_SemanticRoot, "hotkey_CreateNewPanel", new Class[] { Pane.class }, new Object[] { panel_SemanticRoot }, this, DrawAndChatVm.class);
    hotkey_CreateNewTextArea(panel_SemanticRoot);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_SemanticRoot, "hotkey_CreateNewTextArea", new Class[] { Pane.class }, new Object[] { panel_SemanticRoot }, this, DrawAndChatVm.class);
    hotkey_CreateNewCanvas(panel_SemanticRoot);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_SemanticRoot, "hotkey_CreateNewCanvas", new Class[] { Pane.class }, new Object[] { panel_SemanticRoot }, this, DrawAndChatVm.class);
    hotkey_CreateNewIndexCircle(panel_SemanticRoot);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_SemanticRoot, "hotkey_CreateNewIndexCircle", new Class[] { Pane.class }, new Object[] { panel_SemanticRoot }, this, DrawAndChatVm.class);

    //_________________________________
    hotkey_PasteObj(panel_SemanticRoot);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_SemanticRoot, "hotkey_PasteObj", new Class[] { Node.class }, new Object[] { panel_SemanticRoot }, this, DrawAndChatVm.class);

    nodeSender_ConvertWrapper.establish_NodeRelationshipContainmentStatus(pane_JavafxRoot, panel_SemanticRoot, true); //_______________________________

    //____
    AnchorPane panel_CanvasBorder_wrapper = nodeSender_ConvertWrapper.create_Panel();
    String cssStyleStr_Normal_Pcbw = ""
                                     + "-fx-border-style: solid;"
                                     + "-fx-border-width: 1;"
                                     + "-fx-border-color: " + JavafxUtil.rgba_Grey + ";";
    panel_CanvasBorder_wrapper.setStyle(cssStyleStr_Normal_Pcbw);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_CanvasBorder_wrapper, "setStyle", new Class[] { String.class }, new Object[] { cssStyleStr_Normal_Pcbw }, panel_CanvasBorder_wrapper, Node.class);

    String cssStyleStr_Focus_Pcbw = ""
                                    + "-fx-border-style: solid;"
                                    + "-fx-border-width: 2;"
                                    + "-fx-border-color: " + JavafxUtil.rgba_Grey + ";";
    listen_Focus(panel_CanvasBorder_wrapper, cssStyleStr_Focus_Pcbw);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_CanvasBorder_wrapper, "listen_Focus", new Class[] { Node.class, String.class }, new Object[] { panel_CanvasBorder_wrapper, cssStyleStr_Focus }, this, DrawAndChatVm.class);
    hotkey_MoveLayer(panel_CanvasBorder_wrapper);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_CanvasBorder_wrapper, "hotkey_MoveLayer", new Class[] { Node.class }, new Object[] { panel_CanvasBorder_wrapper }, this, DrawAndChatVm.class);

    Canvas canvas_Background = nodeSender_ConvertWrapper.<Canvas>create_Node(Canvas.class);
    nodeSender_ConvertWrapper.change_Shape(canvas_Background, w_CanwasBackground, h_CanvasBackground);
    initDraw(canvas_Background);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(canvas_Background, "initDraw", new Class[] { Canvas.class }, new Object[] { canvas_Background }, this, DrawAndChatVm.class);
    draw_and_send_MouseEvent(canvas_Background);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(canvas_Background, "draw_and_send_MouseEvent", new Class[] { Canvas.class }, new Object[] { canvas_Background }, this, DrawAndChatVm.class);
    listen_SetCursor(canvas_Background, "Cursor.CROSSHAIR");
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(canvas_Background, "listen_SetCursor", new Class[] { Node.class, String.class }, new Object[] { canvas_Background, "Cursor.CROSSHAIR" }, this, DrawAndChatVm.class);

    nodeSender_ConvertWrapper.establish_NodeRelationshipContainmentStatus(panel_CanvasBorder_wrapper, canvas_Background);

    AnchorPane handler_Resize = nodeSender_ConvertWrapper.create_Panel(); //____________________________________________________________________
    setup_Resizable(canvas_Background, panel_CanvasBorder_wrapper, handler_Resize); //______________________________________
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(canvas_Background, "setup_Resizable", new Class[] { Node.class, Pane.class, Pane.class }, new Object[] { canvas_Background, panel_CanvasBorder_wrapper, handler_Resize }, this, DrawAndChatVm.class);

    //_________________________________
    hotkey_CutObj(panel_CanvasBorder_wrapper);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_CanvasBorder_wrapper, "hotkey_CutObj", new Class[] { Node.class }, new Object[] { panel_CanvasBorder_wrapper }, this, DrawAndChatVm.class);

    nodeSender_ConvertWrapper.establish_NodeRelationshipContainmentStatus(panel_SemanticRoot, panel_CanvasBorder_wrapper);

    //____
    //________________________________________
    Button button_AddPanel = nodeSender_ConvertWrapper.<Button>create_Node(Button.class);
    button_AddPanel.setText("add_Panel");
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(button_AddPanel, "setText", new Class[] { String.class }, new Object[] { "add_Panel" }, button_AddPanel, Labeled.class);
    button_AddPanel.setMnemonicParsing(false);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(button_AddPanel, "setMnemonicParsing", new Class[] { boolean.class }, new Object[] { false }, button_AddPanel, Labeled.class);
    make_Draggable(button_AddPanel, true);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(button_AddPanel, "make_Draggable", new Class[] { Node.class, boolean.class }, new Object[] { button_AddPanel, true }, this, DrawAndChatVm.class);

    //_____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
    onclick_CreateNewPanel_AtRoot(button_AddPanel);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(button_AddPanel, "onclick_CreateNewPanel_AtRoot", new Class[] { Node.class }, new Object[] { button_AddPanel }, this, DrawAndChatVm.class);

    //__________________________________________
    nodeSender_ConvertWrapper.establish_NodeRelationshipContainmentStatus(panel_SemanticRoot, button_AddPanel);

    //____
    stage.setTitle("1 " + windowSession_corr.sessionNewOrLoadOrReceive + " :: " + windowSession_corr.dbOccupation.dbName);
    Scene scene = new Scene(pane_JavafxRoot, w_Scene, h_Scene);
    stage.setScene(scene);
    windowSession_corr.listen_ConfirmClose(stage);
    stage.show();

    //____
    make_Draggable_ForRoot(panel_SemanticRoot, true);     //__________________________
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_SemanticRoot, "make_Draggable_ForRoot", new Class[] { Node.class, boolean.class }, new Object[] { panel_SemanticRoot, true }, this, DrawAndChatVm.class);

    //____
    setup_MiniMap_UsingRoot();
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_SemanticRoot, "setup_MiniMap_UsingRoot", new Class[] {}, new Object[] {}, this, DrawAndChatVm.class);
  }

  //_______________________________
  public void listen_SetCursor(Node node, String cursorName) {
    Cursor cursor;
    if (cursorName.equals("Cursor.MOVE")) {
      cursor = Cursor.MOVE;
    }
    else if (cursorName.equals("Cursor.SE_RESIZE")) {
      cursor = Cursor.SE_RESIZE;
    }
    else if (cursorName.equals("Cursor.CROSSHAIR")) { //___________________________________
      cursor = Cursor.CROSSHAIR;
    }
    else {
      throw new TypeError(cursorName);
    }
    node.hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
      if (newValue) { node.setCursor(cursor); }
    });
  }

  //_____________________

  private static final Random rand = new Random();
  //________________________________________________________

  public void create_NewPanel_underGivenPanel(Pane panel_host) {
    //______________________________
    nodeSender_ConvertWrapper.place_UndoCheckpoint();

    //_______________
    AnchorPane panel_M = nodeSender_ConvertWrapper.create_Panel();

    double posX = rand.nextInt(50, 101);
    double posY = rand.nextInt(50, 101);
    nodeSender_ConvertWrapper.move_Node(panel_M, posX, posY);
    nodeSender_ConvertWrapper.change_Shape(panel_M, 100, 100);
    //_________________________________________________________________________________________________________________________________
    String cssStyleStr_Normal = ""
                                + "-fx-border-style: solid;"
                                + "-fx-border-width: 1;"
                                + "-fx-border-color: " + JavafxUtil.rgba_Red_dim + ";";
    if (!det_NeedRequestQualificationFirst) { //
      panel_M.setStyle(cssStyleStr_Normal);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_M, "setStyle", new Class[] { String.class }, new Object[] { cssStyleStr_Normal }, panel_M, Node.class);

    if (!det_NeedRequestQualificationFirst) { //
      make_Draggable(panel_M, true);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_M, "make_Draggable", new Class[] { Node.class, boolean.class }, new Object[] { panel_M, true }, this, DrawAndChatVm.class); //_______
    setup_Zoomable(panel_M);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_M, "setup_Zoomable", new Class[] { Node.class }, new Object[] { panel_M }, this, DrawAndChatVm.class);
    if (!det_NeedRequestQualificationFirst) { //
      hotkey_Common(panel_M);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_M, "hotkey_Common", new Class[] { Node.class }, new Object[] { panel_M }, this, DrawAndChatVm.class);
    if (!det_NeedRequestQualificationFirst) { //
      setup_ArrowDrawingLinkingListener(panel_M);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_M, "setup_ArrowDrawingLinkingListener", new Class[] { Node.class }, new Object[] { panel_M }, this, DrawAndChatVm.class);
    if (!det_NeedRequestQualificationFirst) { //
      hotkey_CreateNewPanel(panel_M);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_M, "hotkey_CreateNewPanel", new Class[] { Pane.class }, new Object[] { panel_M }, this, DrawAndChatVm.class);
    if (!det_NeedRequestQualificationFirst) { //
      hotkey_CreateNewTextArea(panel_M);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_M, "hotkey_CreateNewTextArea", new Class[] { Pane.class }, new Object[] { panel_M }, this, DrawAndChatVm.class);
    if (!det_NeedRequestQualificationFirst) { //
      hotkey_CreateNewCanvas(panel_M);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_M, "hotkey_CreateNewCanvas", new Class[] { Pane.class }, new Object[] { panel_M }, this, DrawAndChatVm.class);
    hotkey_CreateNewIndexCircle(panel_M);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_M, "hotkey_CreateNewIndexCircle", new Class[] { Pane.class }, new Object[] { panel_M }, this, DrawAndChatVm.class);

    //_________________________________
    hotkey_CutObj(panel_M);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_M, "hotkey_CutObj", new Class[] { Node.class }, new Object[] { panel_M }, this, DrawAndChatVm.class);
    hotkey_PasteObj(panel_M);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(panel_M, "hotkey_PasteObj", new Class[] { Node.class }, new Object[] { panel_M }, this, DrawAndChatVm.class);

    //__________________________________________________________________________
    nodeSender_ConvertWrapper.establish_NodeRelationshipContainmentStatus(panel_host, panel_M);

    //__________________
    create_NewTextArea_underGivenPanel(panel_M);
    //______________________________

    //_______________

    //______________________________________________________________________________________________
    //____________________________________________________
    //_________________________________________________________________
    //_________________________________________
    //_______________________________________________
    //___________________________________________________________________________
    //___________
    //________________________________________________________________
    //______________________________________________________________

    //_________________________________________________________________________________
    add_UndoOrRedoCompensationStepEvent_for_AddOrRemoveNode(panel_M, true, (Pane) panel_M.getParent());
    //_________________________________________________________________________________________________
  }

  //_____________________

  private AnchorPane create_PanelWrapper_helper(Pane panel_host) { return create_PanelWrapper_helper(panel_host, false); }

  //_______________________
  private AnchorPane create_PanelWrapper_helper(Pane panel_host, boolean det_NoStyle) {
    AnchorPane paneWrap = nodeSender_ConvertWrapper.create_Panel();

    double posX = rand.nextInt(50, 101);
    double posY = rand.nextInt(50, 101);
    nodeSender_ConvertWrapper.move_Node(paneWrap, posX, posY);
    nodeSender_ConvertWrapper.change_Shape(paneWrap, 20, 20);

    if (!det_NoStyle) {
      String cssStyleStr_Normal = ""
                                  + "-fx-border-style: solid;"
                                  + "-fx-border-width: 1;"
                                  + "-fx-border-color: " + JavafxUtil.rgba_Grey + ";";
      if (!det_NeedRequestQualificationFirst) { //
        paneWrap.setStyle(cssStyleStr_Normal);
      }
      nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(paneWrap, "setStyle", new Class[] { String.class }, new Object[] { cssStyleStr_Normal }, paneWrap, Node.class);
    }
    if (!det_NeedRequestQualificationFirst) { //
      make_Draggable(paneWrap, true);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(paneWrap, "make_Draggable", new Class[] { Node.class, boolean.class }, new Object[] { paneWrap, true }, this, DrawAndChatVm.class); //_______
    setup_Zoomable(paneWrap);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(paneWrap, "setup_Zoomable", new Class[] { Node.class }, new Object[] { paneWrap }, this, DrawAndChatVm.class);
    if (!det_NeedRequestQualificationFirst) { //
      hotkey_Common(paneWrap);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(paneWrap, "hotkey_Common", new Class[] { Node.class }, new Object[] { paneWrap }, this, DrawAndChatVm.class);
    //_________________________________
    hotkey_CutObj(paneWrap);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(paneWrap, "hotkey_CutObj", new Class[] { Node.class }, new Object[] { paneWrap }, this, DrawAndChatVm.class);
    if (!det_NeedRequestQualificationFirst) { //
      setup_ArrowDrawingLinkingListener(paneWrap);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(paneWrap, "setup_ArrowDrawingLinkingListener", new Class[] { Node.class }, new Object[] { paneWrap }, this, DrawAndChatVm.class);

    nodeSender_ConvertWrapper.establish_NodeRelationshipContainmentStatus(panel_host, paneWrap);

    return paneWrap;
  }

  //_____________________

  public void create_NewTextArea_underGivenPanel(Pane panel_host) {
    nodeSender_ConvertWrapper.place_UndoCheckpoint();

    AnchorPane panel_Wrapper = create_PanelWrapper_helper(panel_host);

    //_________________________________________
    //_____________________________________
    //__________________________________________
    //___________________________________________
    TextArea textArea = nodeSender_ConvertWrapper.<TextArea>create_Node(TextArea.class);
    nodeSender_ConvertWrapper.change_Shape(textArea, 100, 40);
    //___________________________________________________________________________________________________
    //_______________________________
    //____________________________________________________________________________________________________________________________________________________________________________
    //_________________________________________________________________________
    if (!det_NeedRequestQualificationFirst) { //
      setup_TextArea(textArea);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(textArea, "setup_TextArea", new Class[] { TextArea.class }, new Object[] { textArea }, this, DrawAndChatVm.class);

    if (!det_NeedRequestQualificationFirst) { //
      listen_TextChange(textArea);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(textArea, "listen_TextChange", new Class[] { TextArea.class }, new Object[] { textArea }, this, DrawAndChatVm.class);
    //______________________________________________________________________

    nodeSender_ConvertWrapper.establish_NodeRelationshipContainmentStatus(panel_Wrapper, textArea);

    AnchorPane handler_Resize = nodeSender_ConvertWrapper.create_Panel(); //____________________________________________________________________
    if (!det_NeedRequestQualificationFirst) { //
      setup_Resizable(textArea, panel_Wrapper, handler_Resize); //______________________________________
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(textArea, "setup_Resizable", new Class[] { Node.class, Pane.class, Pane.class }, new Object[] { textArea, panel_Wrapper, handler_Resize }, this, DrawAndChatVm.class);

    //_______________________________________________________________________________________
    add_UndoOrRedoCompensationStepEvent_for_AddOrRemoveNode(panel_Wrapper, true, (Pane) panel_Wrapper.getParent());
    //___________________________________________________________________________________________________
  }

  //_____________________

  //____________________________________________________________
  //_
  //_______________________________________________________
  //_
  //____________________________________________
  //_
  //_________________________________________________________
  //______________________________________________________________________
  //_
  //____________
  //___________

  //__________________________________
  //__________________________________________________
  //____________________________________________________________________________________

  //___
  //_______________________________________________________________________________________
  //_____________________________________________________________________________________________________________________________________________
  //___________________________________________________________________________________________________________________________________
  //_________________________________________________________________________________________________________________
  //_____________
  //_____________________________________________________
  //_________________________________________________________________________
  //____________________________________________________________________
  //______________________________________________________________
  //_
  //______
  //_________________________________

  public void create_NewCanvas_underGivenPanel(Pane panel_host) {
    nodeSender_ConvertWrapper.place_UndoCheckpoint();

    AnchorPane panel_Wrapper = create_PanelWrapper_helper(panel_host);

    Canvas canvas = nodeSender_ConvertWrapper.<Canvas>create_Node(Canvas.class);
    nodeSender_ConvertWrapper.change_Shape(canvas, 100, 100);
    if (!det_NeedRequestQualificationFirst) { //
      initDraw(canvas);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(canvas, "initDraw", new Class[] { Canvas.class }, new Object[] { canvas }, this, DrawAndChatVm.class);
    if (!det_NeedRequestQualificationFirst) { //
      draw_and_send_MouseEvent(canvas);
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(canvas, "draw_and_send_MouseEvent", new Class[] { Canvas.class }, new Object[] { canvas }, this, DrawAndChatVm.class);
    if (!det_NeedRequestQualificationFirst) { //
      listen_SetCursor(canvas, "Cursor.CROSSHAIR");
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(canvas, "listen_SetCursor", new Class[] { Node.class, String.class }, new Object[] { canvas, "Cursor.CROSSHAIR" }, this, DrawAndChatVm.class);
    //______________________________________________________

    nodeSender_ConvertWrapper.establish_NodeRelationshipContainmentStatus(panel_Wrapper, canvas);

    AnchorPane handler_Resize = nodeSender_ConvertWrapper.create_Panel(); //____________________________________________________________________
    if (!det_NeedRequestQualificationFirst) { //
      setup_Resizable(canvas, panel_Wrapper, handler_Resize); //______________________________________
    }
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(canvas, "setup_Resizable", new Class[] { Node.class, Pane.class, Pane.class }, new Object[] { canvas, panel_Wrapper, handler_Resize }, this, DrawAndChatVm.class);

    //_________________________________________________
    //_______________________________________________________________________________________
    add_UndoOrRedoCompensationStepEvent_for_AddOrRemoveNode(panel_Wrapper, true, (Pane) panel_Wrapper.getParent());
    //_____________________________________________________________________________________________
    //__________
    //___
    //_________________________________________________
    //_____________________________________
    //_______________________________________
    //______________________________________________________________________
    //____________________________________________
    //_____________________________
    //_____________________________________________________________________________________________________________________________________________
    //_
    //_____________________________________________________
  }

  //_____________________

  public void add_UndoOrRedoCompensationStepEvent_for_AddOrRemoveNode(Node node, boolean det_AddOrRemove, Pane node_parnet_beforeRemove) {
    if (node_parnet_beforeRemove == null) { throw new Error("null parent when redo_AddNode() :: " + node); }
    //__________
    //___
    //_________________________________________________
    //_____________________________________
    //_______________________________________
    //______________________________________________________________________
    //____________________________________________
    //_____________________________
    //_____________________________________________________________________________________________________________________________________________
    //_
    //_____________________________________________________

    if (det_AddOrRemove) {
      //__________________________________________________________________
      if (!det_NeedRequestQualificationFirst) { //
        undoManager_corr.add_UndoCompensationStep(() -> { DrawAndChatVm.undo_AddNode(node); });
      }
      nodeSender_ConvertWrapper.wrap_and_send_UndoCompensationStepAddedEvent(node, "undo_AddNode", new Class[] { Node.class }, new Object[] { node }, this, DrawAndChatVm.class);

      //_______________________________________________________
      //_________________________________________________________________________________________________
      if (!det_NeedRequestQualificationFirst) { //
        undoManager_corr.add_RedoCompensationStep(() -> { DrawAndChatVm.redo_AddNode(node, node_parnet_beforeRemove); });
      }
      nodeSender_ConvertWrapper.wrap_and_send_RedoCompensationStepAddedEvent(node, "redo_AddNode", new Class[] { Node.class, Pane.class }, new Object[] { node, node_parnet_beforeRemove }, this, DrawAndChatVm.class);
    }
    else {
      //_________________________________________________________________________________
      //__________________________________________________________________
      if (!det_NeedRequestQualificationFirst) { //
        undoManager_corr.add_UndoCompensationStep(() -> { DrawAndChatVm.undo_RemoveNode(node, node_parnet_beforeRemove); });
      }
      nodeSender_ConvertWrapper.wrap_and_send_UndoCompensationStepAddedEvent(node, "undo_RemoveNode", new Class[] { Node.class, Pane.class }, new Object[] { node, node_parnet_beforeRemove }, this, DrawAndChatVm.class);

      if (!det_NeedRequestQualificationFirst) { //_
        undoManager_corr.add_RedoCompensationStep(() -> { DrawAndChatVm.redo_RemoveNode(node); });
      }
      nodeSender_ConvertWrapper.wrap_and_send_RedoCompensationStepAddedEvent(node, "redo_RemoveNode", new Class[] { Node.class }, new Object[] { node }, this, DrawAndChatVm.class);
    }
  }

  public static void undo_AddNode(Node node) {
    ((Pane) node.getParent()).getChildren().remove(node); //_
  }

  public static void undo_RemoveNode(Node node_WasRemoved, Pane pane_OriParentOf_node_WasRemoved) {
    //__________________________________________________________________________________________
    //___________________________________________________________________________________________________________
    //_______________________________________________________________________________________________________________________________________________________________
    //_______________________________________________________________________________________________________
    //__________________________________________________________________
    //___________________________________________________________________________________________________________
    pane_OriParentOf_node_WasRemoved.getChildren().add(node_WasRemoved);
  }

  public static void redo_AddNode(Node node_WasRemoved, Pane pane_OriParentOf_node_WasRemoved) {
    undo_RemoveNode(node_WasRemoved, pane_OriParentOf_node_WasRemoved); //_
  }

  public static void redo_RemoveNode(Node node_WasUnRemoved) {
    undo_AddNode(node_WasUnRemoved);    //_
    //___________________________________________________
  }

  //________________________________________________
  //______________________________________________________________________________________
  //____________________________________________________
  //_________________________________________________________________
  //________________________
  //___
  //
  //_________________________________________________________________________________________________
  //___________________________________________________________________________
  //___
  //
  //__________________________________________________________
  //__________________________________________________
  //____________________________________________________________________
  //________________________
  //___
  //
  //___________________________________________________________________________________________________
  //___________________________________________________________________________
  //___

  //_____________________

  public static final Method method__DrawAndChatVm__create_IndexCircle = MethodReferenceUtils.getReferencedMethod(DrawAndChatVm.class, DrawAndChatVm::create_IndexCircle);
  //__________________________________________________________________________________________________________________________________________
  public static final Method method__DrawAndChatVm__setText_withoutTriggeringListener = MethodReferenceUtils.getReferencedMethod(DrawAndChatVm.class, DrawAndChatVm::setText_withoutTriggeringListener);

  public void create_NewIndexCircle_underGivenPanel(Pane panel_host, String content) {
    nodeSender_ConvertWrapper.place_UndoCheckpoint();

    //_______________________________________________________________________________________________________
    //_______________________________________________________________________________
    AnchorPane paneWrap_IndexCircle = create_PanelWrapper_helper(panel_host, true);

    TextArea textArea = nodeSender_ConvertWrapper.<TextArea>create_Node(TextArea.class);
    create_IndexCircle(content, paneWrap_IndexCircle, textArea);
    nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(paneWrap_IndexCircle, method__DrawAndChatVm__create_IndexCircle.getName(), method__DrawAndChatVm__create_IndexCircle.getParameterTypes(), new Object[] { content, paneWrap_IndexCircle, textArea }, this, method__DrawAndChatVm__create_IndexCircle.getDeclaringClass());
    //________________________________________________________________________________________________________________________
    //____________________________________________________________________________________________________________________________________________________________________________________________________________________________________

    add_UndoOrRedoCompensationStepEvent_for_AddOrRemoveNode(paneWrap_IndexCircle, true, panel_host);
  }

  //_____________________

  public void hotkey_CreateNewPanel(Pane panel_host) {
    panel_host.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
      if (!keyEvent.isConsumed()) {
        if (keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.N) {
          keyEvent.consume();
          create_NewPanel_underGivenPanel(panel_host); //_
        }
      }
    });
  }

  public void onclick_CreateNewPanel_AtRoot(Node node) {
    Pane panel_SemanticRoot = windowSession_corr.panel_SemanticRoot;

    node.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
      if (!event.isConsumed()) {
        if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
          event.consume();
          System.out.printf(">>- %5s %s%n    %s%n    %s%n", 1, "btn_add_Panel.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {", event, null);
          create_NewPanel_underGivenPanel(panel_SemanticRoot);
        }
      }
    });
  }

  public void hotkey_CreateNewTextArea(Pane panel_host) {
    panel_host.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
      if (!keyEvent.isConsumed()) {
        if (keyEvent.isControlDown() && !keyEvent.isAltDown() && keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.B) {
          keyEvent.consume();
          create_NewTextArea_underGivenPanel(panel_host);
        }
      }
    });
  }

  public void hotkey_CreateNewCanvas(Pane panel_host) {
    panel_host.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
      if (!keyEvent.isConsumed()) {
        if (keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.M) {
          keyEvent.consume();
          create_NewCanvas_underGivenPanel(panel_host);
        }
      }
    });
  }

  public void hotkey_CreateNewIndexCircle(Pane panel_host) {
    AtomicInteger i = new AtomicInteger(0);
    panel_host.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
      if (!keyEvent.isConsumed()) {
        if (keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.I) {
          keyEvent.consume();
          create_NewIndexCircle_underGivenPanel(panel_host, Integer.toString(i.incrementAndGet()));
        }
        else if (keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.DIGIT1) {
          keyEvent.consume();
          int cc = 1;
          i.set(cc);
          create_NewIndexCircle_underGivenPanel(panel_host, Integer.toString(cc));
        }
        else if (keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.DIGIT2) {
          keyEvent.consume();
          int cc = 2;
          i.set(cc);
          create_NewIndexCircle_underGivenPanel(panel_host, Integer.toString(cc));
        }
        else if (keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.DIGIT3) {
          keyEvent.consume();
          int cc = 3;
          i.set(cc);
          create_NewIndexCircle_underGivenPanel(panel_host, Integer.toString(cc));
        }
        else if (keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.DIGIT4) {
          keyEvent.consume();
          int cc = 4;
          i.set(cc);
          create_NewIndexCircle_underGivenPanel(panel_host, Integer.toString(cc));
        }
        else if (keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.DIGIT5) {
          keyEvent.consume();
          int cc = 5;
          i.set(cc);
          create_NewIndexCircle_underGivenPanel(panel_host, Integer.toString(cc));
        }
      }
    });
  }

  public void hotkey_Delete(Node node) {
    node.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
      if (!keyEvent.isConsumed()) {
        //___________
        if (!keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.DELETE) {
          keyEvent.consume();
          System.out.println(node + " :: " + keyEvent);

          nodeSender_ConvertWrapper.place_UndoCheckpoint();

          Parent node_parnet_beforeRemove = node.getParent(); //____________________________
          //_____________________________________________________________________

          //__________________________________________________________________________________________________________________________
          nodeSender_ConvertWrapper.remove_Node(node);

          //_________________________________________________________
          //__________________________________________________
          //_______________________________________________
          add_UndoOrRedoCompensationStepEvent_for_AddOrRemoveNode(node, false, (Pane) node_parnet_beforeRemove);
        }
      }
    });
  }

  //__________________________________________________________________________________________________________________
  public void hotkey_UndoRedo(Node node) {
    node.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
      if (!keyEvent.isConsumed()) {
        //_________
        if (keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.Z) {
          keyEvent.consume();
          System.out.println("Undo specific to a Node is not_supported");
          nodeSender_ConvertWrapper.undoTo_LastUndoCheckpoint();
        }
        //_________
        else if (keyEvent.isControlDown() && !keyEvent.isAltDown() && keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.Z) {
          keyEvent.consume();
          System.out.println("Redo specific to a Node is not_supported");
          nodeSender_ConvertWrapper.redoTo_LastUndoCheckpoint();
        }
      }
    });
  }

  public void listen_Focus(Node node, String cssStyleStr_in) {
    //__________________________
    if (Arrow.class.isAssignableFrom(node.getClass())) {
      Arrow arrow = (Arrow) node;
      arrow.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
          private String cssStyleStr_prev;

          @Override
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
              cssStyleStr_prev = arrow.getStyle_Arrow();

              arrow.setStyle_Arrow(cssStyleStr_in); //________
            }
            else {
              arrow.setStyle_Arrow(cssStyleStr_prev);
            }
          }
        });
    }
    else {
      node.focusedProperty().addListener(new ChangeListener<Boolean>()
        {

          private String cssStyleStr_prev;

          @Override
          public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
              cssStyleStr_prev = node.getStyle();

              node.setStyle(cssStyleStr_in); //__________________________________________________________________________________________
            }
            else {
              node.setStyle(cssStyleStr_prev);
            }
          }

        });
    }
    //_________________________________________________________________________________________________________________
    node.addEventHandler(MouseEvent.MOUSE_PRESSED, (mouseEvent) -> {
      //_____________________________________________________________________________________________________________________
      //_______________
      if (!mouseEvent.isConsumed()) {
        //_____________________________________________________________________________
        if (!mouseEvent.isControlDown() && !mouseEvent.isAltDown() && !mouseEvent.isShiftDown() && mouseEvent.isPrimaryButtonDown()) {
          mouseEvent.consume();
          System.out.println("Focus on :: " + node);
          nodeSender_ConvertWrapper.focusOn_Node(node);
        }
        //________________________________
        //________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        //__________________________________________________________________________
        else if (!mouseEvent.isControlDown() && !mouseEvent.isAltDown() && !mouseEvent.isShiftDown() && mouseEvent.isMiddleButtonDown()) {
          //_______________________________
          System.out.println("Focus on :: " + windowSession_corr.panel_SemanticRoot);
          nodeSender_ConvertWrapper.focusOn_Node(windowSession_corr.panel_SemanticRoot);
        }
      }
    });
  }

  public void hotkey_Common(Node node) {
    hotkey_Delete(node);
    listen_Focus(node, ""
                       + "-fx-border-color: " + JavafxUtil.rgba_Cyan + ";" //__________
                       + "-fx-stroke: " + JavafxUtil.rgba_Cyan + ";" //__________
    );
    hotkey_MoveLayer(node);
  }
  //______________________________________________________________

  public final ConcurrentHashMap<TextArea, AtomicInteger> mpp_textArea_vs_cdet_DontSendText = new ConcurrentHashMap<>();

  public static final long interval_BrustInputText = 160;

  private final ScheduledExecutorService executor_BrustDetect_TextInput = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("thd-listen_TextChange_brust-%d").build());
  private final Semaphore det_BrustEvent_TextInput = new Semaphore(0);

  private void cstuInit__executor_BrustDetect_TextInput() {
    ScheduledFuture<?> future = executor_BrustDetect_TextInput.scheduleWithFixedDelay(() -> {
      try {
        det_BrustEvent_TextInput.acquire();
      } catch (InterruptedException e) {
        System.out.println("Seems shutdown requested :: " + e);
        //_____________________
        executor_BrustDetect_TextInput.shutdown();
        try {
          executor_BrustDetect_TextInput.awaitTermination(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    }, 0, interval_BrustInputText, TimeUnit.MILLISECONDS);
    windowSession_corr.arr_ThreadPool_OnlyForSimpleShutdown.add(executor_BrustDetect_TextInput);
    windowSession_corr.mpp_ThreadPool_vs_Future_OnlyForSimpleShutdown.put(executor_BrustDetect_TextInput, future);
  }

  public void listen_TextChange(TextArea textArea) {
    //________________________________________________
    //
    //___________________________________________________________________________________________________________________________________________________________________________________________________
    //_____________________________________________________________________________________________
    //___________
    //_________________________________
    //________________________________________
    //_______________________________________________________________
    //_______________________________
    //__________________________________________________
    //_____________
    //______________________________________________________________________________________
    //___________________________________________
    //_______________________________
    //_________
    //_______
    //__________________________________________________________
    //________________________________________________________________________________________________
    //__________________________________________________________________________________________________________________

    AtomicInteger cdet_DontSendText_recursiveNetwork = new AtomicInteger(0); //_______________________________
    mpp_textArea_vs_cdet_DontSendText.put(textArea, cdet_DontSendText_recursiveNetwork);
    textArea.textProperty()
            .addListener(new ChangeListener<String>()
              {
                @Override
                public void changed(ObservableValue<? extends String> observable, String content_old, String content_new) {
                  if (cdet_DontSendText_recursiveNetwork.get() == 0) {
                    if (det_BrustEvent_TextInput.availablePermits() == 0) {
                      nodeSender_ConvertWrapper.place_UndoCheckpoint(); //________________________________________________________________________________________________________________________________________
                      det_BrustEvent_TextInput.release(); //______________________________________________________________________________________________________________________________________________________________________________________________
                    }
                    det_BrustEvent_TextInput.release();

                    //__________________________________________________________________________________________
                    //___________________________________________
                    nodeSender_ConvertWrapper.change_Text(textArea, content_old, content_new); //

                    //
                    //____
                    //_______________________________
                    //______________
                    //_________________________________
                    //________________________________________________
                    //___________________________________________________________
                    undoManager_corr.add_UndoCompensationStep(() -> { setText_withoutTriggeringListener(textArea, content_old); });
                    nodeSender_ConvertWrapper.wrap_and_send_UndoCompensationStepAddedEvent(textArea, method__DrawAndChatVm__setText_withoutTriggeringListener.getName(), method__DrawAndChatVm__setText_withoutTriggeringListener.getParameterTypes(), new Object[] { textArea, content_old }, this, method__DrawAndChatVm__setText_withoutTriggeringListener.getDeclaringClass());

                    undoManager_corr.add_RedoCompensationStep(() -> { setText_withoutTriggeringListener(textArea, content_new); });
                    nodeSender_ConvertWrapper.wrap_and_send_RedoCompensationStepAddedEvent(textArea, method__DrawAndChatVm__setText_withoutTriggeringListener.getName(), method__DrawAndChatVm__setText_withoutTriggeringListener.getParameterTypes(), new Object[] { textArea, content_new }, this, method__DrawAndChatVm__setText_withoutTriggeringListener.getDeclaringClass());
                  }
                  else {
                    cdet_DontSendText_recursiveNetwork.decrementAndGet();
                  }
                }
              }); //_
  }

  //___
  //___________________________________________________________________________________________________________
  //___
  //_____________________________________________________________________________________________________
  //___
  //___________________________________________________________________________________________________________________
  //_
  //__________________________________________________________________
  //________________________________________________________________
  //___________________________________
  //______________________________________
  //_
  //___
  //_________________________________________
  //__________________________________________________
  //___
  //____________________________
  //_
  //___
  //__________________________________________________
  //___
  //__________________
  //_
  //_________________________________________________________________________________________
  public void setText_withoutTriggeringListener(TextArea textArea, String content) {
    AtomicInteger cdet_DontSendText = mpp_textArea_vs_cdet_DontSendText.get(textArea);
    cdet_DontSendText.incrementAndGet();
    textArea.setText(content);
  }

  public void setup_TextArea(TextArea textArea) {
    textArea.setWrapText(true);
    textArea.setFont(Font.font(DrawAndChatAppSessionInit.Font_Consolas));
  }

  //___
  //_____________________________________________________________________________________________________________________
  //_
  //_______________________________________________________________________________________________________________________________________________________
  //___
  //___________________________________________________________________
  //_
  //___________________________
  //_____________________________________________________________________
  //____________________
  public void hotkey_MoveLayer(Node node) {
    node.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
      if (!keyEvent.isConsumed()) {
        //_________
        if (!keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.PAGE_UP) {
          keyEvent.consume();
          Pair<Integer, Integer> resultTup2 = nodeSender_ConvertWrapper.move_NodeLayer(node, 1, MoveLayerMode.PosIncrementRel);
          int ind_ori = resultTup2.getLeft();
          int ind_new = resultTup2.getRight();

          place_UndoCheckpoint__add_UndoOrRedoCompensationStepEvent_for_MoveLayer__ifNeeded(node, ind_ori, ind_new);
        }
        //___________
        else if (!keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.PAGE_DOWN) {
          keyEvent.consume();
          Pair<Integer, Integer> resultTup2 = nodeSender_ConvertWrapper.move_NodeLayer(node, -1, MoveLayerMode.PosIncrementRel);
          int ind_ori = resultTup2.getLeft();
          int ind_new = resultTup2.getRight();

          place_UndoCheckpoint__add_UndoOrRedoCompensationStepEvent_for_MoveLayer__ifNeeded(node, ind_ori, ind_new);
        }
        //__________
        else if (!keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.HOME) {
          keyEvent.consume();
          Pair<Integer, Integer> resultTup2 = nodeSender_ConvertWrapper.move_NodeLayer(node, 1, MoveLayerMode.TopBottom);
          int ind_ori = resultTup2.getLeft();
          int ind_new = resultTup2.getRight();

          place_UndoCheckpoint__add_UndoOrRedoCompensationStepEvent_for_MoveLayer__ifNeeded(node, ind_ori, ind_new);
        }
        //_____________
        else if (!keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.END) {
          keyEvent.consume();
          Pair<Integer, Integer> resultTup2 = nodeSender_ConvertWrapper.move_NodeLayer(node, -1, MoveLayerMode.TopBottom);
          int ind_ori = resultTup2.getLeft();
          int ind_new = resultTup2.getRight();

          place_UndoCheckpoint__add_UndoOrRedoCompensationStepEvent_for_MoveLayer__ifNeeded(node, ind_ori, ind_new);
        }
      }
    });
  }

  private void place_UndoCheckpoint__add_UndoOrRedoCompensationStepEvent_for_MoveLayer__ifNeeded(Node node, int ind_ori, int ind_new) {
    if (ind_ori != ind_new) {
      nodeSender_ConvertWrapper.place_UndoCheckpoint();

      undoManager_corr.add_UndoCompensationStep(() -> { JavafxUtil.move_NodeLayer(node, ind_ori, MoveLayerMode.PosAbs); });
      nodeSender_ConvertWrapper.wrap_and_send_UndoCompensationStepAddedEvent(node, "move_NodeLayer", new Class[] { Node.class, int.class, MoveLayerMode.class }, new Object[] { node, ind_ori, MoveLayerMode.PosAbs }, null, JavafxUtil.class);

      undoManager_corr.add_RedoCompensationStep(() -> { JavafxUtil.move_NodeLayer(node, ind_new, MoveLayerMode.PosAbs); });
      nodeSender_ConvertWrapper.wrap_and_send_RedoCompensationStepAddedEvent(node, "move_NodeLayer", new Class[] { Node.class, int.class, MoveLayerMode.class }, new Object[] { node, ind_new, MoveLayerMode.PosAbs }, null, JavafxUtil.class);
    }
  }

  public void hotkey_CutObj(Node node) {
    node.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
      if (!keyEvent.isConsumed()) {
        //_________
        if (keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.X) {
          keyEvent.consume();
          nodeSender_ConvertWrapper.place_UndoCheckpoint();
          clipboardStatus_PanelCopyPaste.panel_CopyPaste = (Pane) node;

          Parent node_parnet_beforeRemove = node.getParent();
          nodeSender_ConvertWrapper.remove_Node(node);
          add_UndoOrRedoCompensationStepEvent_for_AddOrRemoveNode(node, false, (Pane) node_parnet_beforeRemove);
        }
      }
    });
  }

  public void hotkey_PasteObj(Node node) {
    node.addEventHandler(KeyEvent.KEY_PRESSED, (keyEvent) -> {
      if (!keyEvent.isConsumed()) {
        //___________
        if (keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.V) {
          keyEvent.consume();
          nodeSender_ConvertWrapper.place_UndoCheckpoint();
          Pane panel_CopyPaste = clipboardStatus_PanelCopyPaste.panel_CopyPaste;
          clipboardStatus_PanelCopyPaste.panel_CopyPaste = null;

          nodeSender_ConvertWrapper.establish_NodeRelationshipContainmentStatus((Pane) node, panel_CopyPaste);
          add_UndoOrRedoCompensationStepEvent_for_AddOrRemoveNode(panel_CopyPaste, true, (Pane) panel_CopyPaste.getParent());
          //_____________________________________________________________________________________
        }
      }
    });
  }

  //_____________________

  public void initDraw(Canvas canvas) {
    //________________________
    GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

    //_____________________________________________
    //___________________________

    //_________________________________________________________
    //__________________________________________
    //____________________________________________________________
    graphicsContext.setStroke(Color.rgb(0, 0, 255, 0.5));
    //____________________________________

    //_____________________________________________________________________________________
    canvas.setFocusTraversable(true);

    canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
      //_________________________________
      //__________________________
      if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
        canvas.requestFocus(); //_
      }
    });

    canvas.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
      //____________________________________________________________________________________________________
      //________________________________
      if (!event.isConsumed()) {
        KeyCode keyCode = event.getCode();
        //________________________________________________________________________________________________
        if (!event.isControlDown() && event.isAltDown() && !event.isShiftDown()
            && (keyCode == KeyCode.DIGIT0
                || keyCode == KeyCode.DIGIT1
                || keyCode == KeyCode.DIGIT2
                || keyCode == KeyCode.DIGIT3
                || keyCode == KeyCode.DIGIT4
                || keyCode == KeyCode.DIGIT5
                || keyCode == KeyCode.DIGIT6
                || keyCode == KeyCode.DIGIT7
                || keyCode == KeyCode.DIGIT8)) {
          event.consume();

          String rgba = null;
          //_______________
               if (keyCode == KeyCode.DIGIT0) { rgba = JavafxUtil.rgba_Blue       ; }
          else if (keyCode == KeyCode.DIGIT1) { rgba = JavafxUtil.rgba_Red_dim    ; }
          else if (keyCode == KeyCode.DIGIT2) { rgba = JavafxUtil.rgba_Green_dim  ; }
          else if (keyCode == KeyCode.DIGIT3) { rgba = JavafxUtil.rgba_Blue_dim   ; }
          else if (keyCode == KeyCode.DIGIT4) { rgba = JavafxUtil.rgba_Yellow_dim ; }
          else if (keyCode == KeyCode.DIGIT5) { rgba = JavafxUtil.rgba_Orange     ; }
          else if (keyCode == KeyCode.DIGIT6) { rgba = JavafxUtil.rgba_Purple     ; }
          else if (keyCode == KeyCode.DIGIT7) { rgba = JavafxUtil.rgba_Teal       ; }
          else if (keyCode == KeyCode.DIGIT8) { rgba = JavafxUtil.rgba_Grey       ; }
          //______________
          change_PaintColor(canvas, rgba);
          nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(canvas, "change_PaintColor", new Class[] { Canvas.class, String.class }, new Object[] { canvas, rgba }, null, DrawAndChatVm.class);
        }
      }
    });
  }

  public static void change_PaintColor(Canvas canvas, String rgbaStr) {
    GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    if (rgbaStr.equals(JavafxUtil.rgba_Blue)) {}
    else if (rgbaStr.equals(JavafxUtil.rgba_Red_dim)) {}
    else if (rgbaStr.equals(JavafxUtil.rgba_Green_dim)) {}
    else if (rgbaStr.equals(JavafxUtil.rgba_Blue_dim)) {}
    else if (rgbaStr.equals(JavafxUtil.rgba_Yellow_dim)) {}
    else if (rgbaStr.equals(JavafxUtil.rgba_Orange)) {}
    else if (rgbaStr.equals(JavafxUtil.rgba_Purple)) {}
    else if (rgbaStr.equals(JavafxUtil.rgba_Teal)) {}
    else if (rgbaStr.equals(JavafxUtil.rgba_Grey)) {}
    else {
      throw new TypeError();
    }

    double[] rgbaArr = rgbaStr2rgbaArr(rgbaStr);
    graphicsContext.setStroke(Color.rgb((int) rgbaArr[0], (int) rgbaArr[1], (int) rgbaArr[2], rgbaArr[3]));
  }

  public static double[] rgbaStr2rgbaArr(String rgbaStr) {
    double[] rgbaArr = new double[4];

    String regexStr = "[\\d\\.]+";
    Pattern pattern = Pattern.compile(regexStr);
    Matcher matcher = pattern.matcher(rgbaStr);

    int i = -1;
    while (matcher.find()) {
      i++;
      rgbaArr[i] = Double.parseDouble(matcher.group(0));
    }

    return rgbaArr;
  }

  public void draw_and_send_MouseEvent(Canvas canvas) {
    final WritableImage[] snapshot_ori_wrapper = new WritableImage[1];

    //____________________
    //_____________________________________________
    //_____________________________________________

    LinkedList<Point>[] arr_BatchLinePoints_Ongoing_wrap = new LinkedList[1]; //____________

    canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
      if (!event.isConsumed()) {
        if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
          event.consume();
          nodeSender_ConvertWrapper.place_UndoCheckpoint();

          mouseStatus_CanvasFreehandDrawing.clearStatus();
          mouseStatus_CanvasFreehandDrawing.det_CanvasFreehandDrawing = true;
          mouseStatus_CanvasFreehandDrawing.statusPhase               = StatusPhase.Begin_begin;

          //____________________________________________________________________________________________________________________________________________________________________________
          //______________________________________________
          snapshot_ori_wrapper[0]                                     = save_CanvasSnapshot(canvas);

          double x = event.getX();
          double y = event.getY();
          arr_BatchLinePoints_Ongoing_wrap[0] = new LinkedList<Point>();
          arr_BatchLinePoints_Ongoing_wrap[0].add(new Point(x, y));
          nodeSender_ConvertWrapper.paint_Dot(canvas, x, y, true, StatusPhase.Begin_begin, null);
        }
      }
    });

    canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
      if (!event.isConsumed()) {
        if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
          event.consume();
          if (mouseStatus_CanvasFreehandDrawing.det_CanvasFreehandDrawing) {
            mouseStatus_CanvasFreehandDrawing.statusPhase = StatusPhase.Ongoing_begin;

            double x = event.getX();
            double y = event.getY();
            arr_BatchLinePoints_Ongoing_wrap[0].add(new Point(x, y));
            nodeSender_ConvertWrapper.paint_Dot(canvas, x, y, true, StatusPhase.Ongoing_begin, null);
          }
          else {
            System.err.println("Dont think any functionality should go here.");
          }
        }
      }
    });

    canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
      if (!event.isConsumed()) {
        //________________________________________________________________________________________________________
        //_______________________________________________________________
        //________________________________________________________________________________________________________________
        //_______________________________________________________________________________________________________________________
        if (mouseStatus_CanvasFreehandDrawing.det_CanvasFreehandDrawing) {
          event.consume();
          //_________________________________________________________________________________________
          mouseStatus_CanvasFreehandDrawing.statusPhase = StatusPhase.End_begin;

          double x = event.getX();
          double y = event.getY();
          arr_BatchLinePoints_Ongoing_wrap[0].add(new Point(x, y));
          nodeSender_ConvertWrapper.paint_Dot(canvas, x, y, true, StatusPhase.End_begin, Collections.unmodifiableList(arr_BatchLinePoints_Ongoing_wrap[0])); //________________________________________
          //_____________________________________________________________________________________________________________________________________

          mouseStatus_CanvasFreehandDrawing.det_CanvasFreehandDrawing = false;

          //________________________________________________________
          //_______________
          //___________________________________________________________________________________________
          //___________________________________________________
          //__________________________
          //_________________________________________________________________________________________________________
          WritableImage snapshot_ori = snapshot_ori_wrapper[0];
          add_UndoOrRedoCompensationStepEvent_for_PaintOrEraseOnCanvas(canvas, snapshot_ori);
          //_____________
          //___________________________________________________________________________________________________________________________________________________
          //_____________________________________________________________________________
          //__________________
          //_____________________________
          //_____________
        }
      }
    });

    //______________________
    AtomicDouble posX_ClearRect_TopLeft = new AtomicDouble();
    AtomicDouble posY_ClearRect_TopLeft = new AtomicDouble();

    canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
      if (!event.isConsumed()) {
        //__________________
        if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isSecondaryButtonDown()) {
          event.consume();
          nodeSender_ConvertWrapper.place_UndoCheckpoint();

          //__________________________________________________________________
          mouseStatus_CanvasFreehandDrawing.clearStatus();
          mouseStatus_CanvasFreehandDrawing.det_CanvasClearRect = true;
          mouseStatus_CanvasFreehandDrawing.statusPhase         = StatusPhase.Begin_begin;

          //_________________________________________________________________________
          //______________________________________________________
          //____________________________________________________________________________
          snapshot_ori_wrapper[0]                               = save_CanvasSnapshot(canvas);

          posX_ClearRect_TopLeft.set(event.getX());
          posY_ClearRect_TopLeft.set(event.getY());
        }
      }
    });

    canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
      if (!event.isConsumed()) {
        //_______________________________
        if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isSecondaryButtonDown()) {
          event.consume();
          if (mouseStatus_CanvasFreehandDrawing.det_CanvasClearRect) {
            mouseStatus_CanvasFreehandDrawing.statusPhase = StatusPhase.Ongoing_begin;
            double x = posX_ClearRect_TopLeft.get();
            double y = posY_ClearRect_TopLeft.get();
            double w = event.getX() - x;
            double h = event.getY() - y;

            if (w < 0) {
              w = -w;
              x = x - w;
            }
            if (h < 0) {
              h = -h;
              y = y - h;
            }
            //______________________________________________________________________

            final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

            //_________________________________________________________________________________
            //___________________________________________________________________
            WritableImage snapshot_ori = snapshot_ori_wrapper[0];
            restore_CanvasSnapshot(canvas, snapshot_ori);
            //____________________________________________________________________________________
            //___________________________________________________________________________________________________________________
            //___________________________________________________
            //__________________________________________________________________________________________________________
            //______________________________________________________________________________________________________________________________________________

            Paint paint_Stroke_prev = graphicsContext.getStroke();
            graphicsContext.setStroke(Color.DARKRED);

            double paint_LineWidth_prev = graphicsContext.getLineWidth();
            graphicsContext.setLineWidth(1);

            graphicsContext.beginPath();
            graphicsContext.rect(x, y, w - 0.9, h - 0.9);
            //______________________________________________________________________________________________________________________
            //______________________________________
            graphicsContext.stroke();
            graphicsContext.closePath();

            graphicsContext.setStroke(paint_Stroke_prev);
            graphicsContext.setLineWidth(paint_LineWidth_prev);
          }
          else {
            System.err.println("Dont think any functionality should go here.");
          }
        }
      }
    });

    canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
      if (!event.isConsumed()) {
        if (mouseStatus_CanvasFreehandDrawing.det_CanvasClearRect) {
          event.consume();
          double x = posX_ClearRect_TopLeft.get();
          double y = posY_ClearRect_TopLeft.get();
          double w = event.getX() - x;
          double h = event.getY() - y;

          if (w < 0) {
            w = -w;
            x = x - w;
          }
          if (h < 0) {
            h = -h;
            y = y - h;
          }

          WritableImage snapshot_ori = snapshot_ori_wrapper[0];
          restore_CanvasSnapshot(canvas, snapshot_ori);
          //______________________________________________
          //_______________________________________________________________________________________
          //____________________________________________________________________________
          nodeSender_ConvertWrapper.paint_RectClear(canvas, x, y, w, h);

          StatusPhase statusPhase_prev = mouseStatus_CanvasFreehandDrawing.statusPhase;
          mouseStatus_CanvasFreehandDrawing.statusPhase         = StatusPhase.End_begin;
          mouseStatus_CanvasFreehandDrawing.det_CanvasClearRect = false;

          if (statusPhase_prev != StatusPhase.Begin_begin) {
            //_______________
            //___________________
            //___________________________________________________________________________________________________________
            //_________________________________________
            //__________

            //___________________________________________________________________________________________________________________________________________________________
            //__________________
            //___________________________________________________________________________________________________

            //_________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
            add_UndoOrRedoCompensationStepEvent_for_PaintOrEraseOnCanvas(canvas, snapshot_ori);
          }
        }
      }
    });

  }

  public static WritableImage save_CanvasSnapshot(Canvas canvas) {
    SnapshotParameters snapshotParameters = new SnapshotParameters();
    snapshotParameters.setFill(Color.TRANSPARENT);
    return canvas.snapshot(snapshotParameters, null);
  }

  public void add_UndoOrRedoCompensationStepEvent_for_PaintOrEraseOnCanvas(Canvas canvas, WritableImage snapshot_ori) {
    if (!det_NeedRequestQualificationFirst) { //
      undoManager_corr.add_UndoCompensationStep(() -> { DrawAndChatVm.restore_CanvasSnapshot(canvas, snapshot_ori); });
    }
    byte[] image_byteArr_ori = ImageUtil.fximg2byte(snapshot_ori);
    nodeSender_ConvertWrapper.wrap_and_send_UndoCompensationStepAddedEvent(canvas, "restore_CanvasSnapshot",
                                                                           new Class[] { Canvas.class, byte[].class }, new Object[] { canvas, image_byteArr_ori }, this, DrawAndChatVm.class);

    WritableImage snapshot_redoOri = save_CanvasSnapshot(canvas);
    if (!det_NeedRequestQualificationFirst) { //
      undoManager_corr.add_RedoCompensationStep(() -> { DrawAndChatVm.restore_CanvasSnapshot(canvas, snapshot_redoOri); });
    }
    byte[] image_byteArr_redoOri = ImageUtil.fximg2byte(snapshot_redoOri);
    nodeSender_ConvertWrapper.wrap_and_send_RedoCompensationStepAddedEvent(canvas, "restore_CanvasSnapshot",
                                                                           new Class[] { Canvas.class, byte[].class }, new Object[] { canvas, image_byteArr_redoOri }, this, DrawAndChatVm.class);

  }

  public static void restore_CanvasSnapshot(Canvas canvas, WritableImage snapshot) {
    if (snapshot == null) { throw new Error("Image to restore is null"); }

    final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
    graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    graphicsContext.drawImage(snapshot, 0, 0);
  }

  public static void restore_CanvasSnapshot(Canvas canvas, byte[] image_byteArr) {
    if (image_byteArr == null || image_byteArr.length == 0) { throw new Error("image_byteArr to restore is null/empty"); }

    WritableImage image = ImageUtil.byte2fximg(image_byteArr);
    restore_CanvasSnapshot(canvas, image);
  }

  //_____________________

  //_________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //______________________________________________________________________________
  public void make_Draggable(final Node node, boolean det_PreventMovePassLeft) { make_Draggable(node, det_PreventMovePassLeft, false); }

  //_
  //_______________________________________________________
  //________________
  public void make_Draggable(final Node node, boolean det_PreventMovePassLeft, boolean det_ForResizeHandler) {
    //_____________________________________________________________________________________
    final AtomicDouble posX_offset = new AtomicDouble();
    final AtomicDouble posY_offset = new AtomicDouble();

    final AtomicDouble posX_ori = new AtomicDouble();
    final AtomicDouble posY_ori = new AtomicDouble();

    //_____________________________________
    //________________________________
    //______________________________________
    node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
      if (!event.isConsumed()) {
        if (event.isControlDown() && event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
          event.consume();
          mouseStatus_PanelMoving.det_PanelMoving = true;
          mouseStatus_PanelMoving.statusPhase     = StatusPhase.Begin_begin;

          //__________________________________________________________________________________
          //______________________________________
          //_________________________________________________________________
          nodeSender_ConvertWrapper.place_UndoCheckpoint();

          //______________________________________________
          double x = event.getSceneX();
          double y = event.getSceneY();
          posX_offset.set(node.getLayoutX() - x);
          posY_offset.set(node.getLayoutY() - y);
          //______________________________________
          //_______________________________________________________________

          posX_ori.set(node.getLayoutX());
          posY_ori.set(node.getLayoutY());
        }
      }
    });
    node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
      if (!event.isConsumed()) {
        if (event.isControlDown() && event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
          event.consume();
          mouseStatus_PanelMoving.statusPhase = StatusPhase.Ongoing_begin;

          double x = event.getSceneX() + posX_offset.get();
          double y = event.getSceneY() + posY_offset.get();

          if (det_PreventMovePassLeft) {
            if (x < 0) { x = 0; }
            if (y < 0) { y = 0; }
          } //______________________

          //________________________________________________________________________________________________________________________________________________________________________________________________________
          nodeSender_ConvertWrapper.move_Node(node, x, y, StatusPhase.Ongoing_begin);
        }
      }
    });
    node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
      if (!event.isConsumed()) {
        //_________________________________________________________________________________________________________
        //_______________________________________________________
        //__________________________________________________________________________________
        //__________________________________________________________________________________________
        if (mouseStatus_PanelMoving.det_PanelMoving) {
          event.consume();
          mouseStatus_PanelMoving.statusPhase = StatusPhase.End_begin;

          double x = event.getSceneX() + posX_offset.get();
          double y = event.getSceneY() + posY_offset.get();

          if (det_PreventMovePassLeft) {
            if (x < 0) { x = 0; }
            if (y < 0) { y = 0; }
          }

          nodeSender_ConvertWrapper.move_Node(node, x, y, StatusPhase.End_begin);

          double x_ori = posX_ori.get();
          double y_ori = posY_ori.get();
          //___________________________________________________________________________________________________________________________________________
          if (!det_NeedRequestQualificationFirst) { //
            undoManager_corr.add_UndoCompensationStep(() -> { DrawAndChatVm.undo_MoveNode(node, x_ori, y_ori); });
          }
          nodeSender_ConvertWrapper.wrap_and_send_UndoCompensationStepAddedEvent(node, "undo_MoveNode",
                                                                                 new Class[] { Node.class, Double.class, Double.class }, new Object[] { node, x_ori, y_ori }, this, DrawAndChatVm.class);

          final double x_redo = x;
          final double y_redo = y;
          if (!det_NeedRequestQualificationFirst) { //
            undoManager_corr.add_RedoCompensationStep(() -> { DrawAndChatVm.redo_MoveNode(node, x_redo, y_redo); });
          }
          nodeSender_ConvertWrapper.wrap_and_send_RedoCompensationStepAddedEvent(node, "redo_MoveNode",
                                                                                 new Class[] { Node.class, Double.class, Double.class }, new Object[] { node, x_redo, y_redo }, this, DrawAndChatVm.class);

          mouseStatus_PanelMoving.det_PanelMoving = false;

          //____

          if (det_ForResizeHandler) {
            //_________________________________________________________________________________________
            //___________________________
            ResizeStatus resizeStatus = new ResizeStatus(ResizeStatus.End_end);
            node.fireEvent(resizeStatus);
          }
        }
      }
    });

    node.hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
      if (newValue) { node.setCursor(Cursor.MOVE); }
    });

  }

  public void make_Draggable_ForRoot(Node node, boolean det_UseScene) {
    final AtomicDouble posX_offset = new AtomicDouble();
    final AtomicDouble posY_offset = new AtomicDouble();

    //_____________________________________________________

    if (det_UseScene) {
      Scene scene = node.getScene();

      scene.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
        if (!event.isConsumed()) {
          if (event.isControlDown() && event.isAltDown() && !event.isShiftDown() && event.isMiddleButtonDown()) {
            event.consume();
            mouseStatus_PanelMoving.det_PanelMoving = true;
            posX_offset.set(node.getLayoutX() - event.getSceneX());
            posY_offset.set(node.getLayoutY() - event.getSceneY());
          }
        }
      });
      scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
        if (!event.isConsumed()) {
          if (event.isControlDown() && event.isAltDown() && !event.isShiftDown() && event.isMiddleButtonDown()) {
            event.consume();
            double posX = event.getSceneX() + posX_offset.get();
            double posY = event.getSceneY() + posY_offset.get();

            double pw = ((Pane) node).getWidth();
            double ph = ((Pane) node).getHeight();
            double sw = scene.getWidth();
            double sh = scene.getHeight();
            if (posX + pw < 10) {
              posX = 10 - pw;
            }
            else if (posX > sw - 10) { posX = sw - 10; }
            if (posY + ph < 10) {
              posY = 10 - ph;
            }
            else if (posY > sh - 10) { posY = sh - 10; }

            node.setLayoutX(posX);
            node.setLayoutY(posY);
            node.setCursor(Cursor.MOVE);
          }
        }
      });
      scene.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
        //________________________________
        if (mouseStatus_PanelMoving.det_PanelMoving) {
          //__________________________
          node.setCursor(Cursor.DEFAULT);
          mouseStatus_PanelMoving.det_PanelMoving = false;
        }
        //_______
      });
      scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
        //________________________________
        if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.getCode() == KeyCode.F4) {
          node.setLayoutX(10);
          node.setLayoutY(10);
        }
        //_______
      });
    }
    //________________________________________________________
    else {
      node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
        if (!event.isConsumed()) {
          if (event.isControlDown() && event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
            event.consume();
            mouseStatus_PanelMoving.det_PanelMoving = true;
            posX_offset.set(node.getLayoutX() - event.getSceneX());
            posY_offset.set(node.getLayoutY() - event.getSceneY());
          }
        }
      });

      Class<? extends Node> clazz = node.getClass();
      node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
        if (!event.isConsumed()) {
          if (event.isControlDown() && event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
            event.consume();
            double posX = event.getSceneX() + posX_offset.get();
            double posY = event.getSceneY() + posY_offset.get();

            Scene scene = node.getScene();
            double pw = 0;
            double ph = 0;
            if (Region.class.isAssignableFrom(clazz)) {
              pw = ((Region) node).getWidth();
              ph = ((Region) node).getHeight();
            }
            else if (ImageView.class.isAssignableFrom(clazz)) {
              pw = ((ImageView) node).getFitWidth();
              ph = ((ImageView) node).getFitHeight();
            }
            double sw = scene.getWidth();
            double sh = scene.getHeight();
            if (posX + pw < 10) {
              posX = 10 - pw;
            }
            else if (posX > sw - 10) { posX = sw - 10; }
            if (posY + ph < 10) {
              posY = 10 - ph;
            }
            else if (posY > sh - 10) { posY = sh - 10; }

            node.setLayoutX(posX);
            node.setLayoutY(posY);
            node.setCursor(Cursor.MOVE);
          }
        }
      });
      node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
        //________________________________
        if (mouseStatus_PanelMoving.det_PanelMoving) {
          //__________________________
          node.setCursor(Cursor.DEFAULT);
          mouseStatus_PanelMoving.det_PanelMoving = false;
        }
        //_______
      });

    }

  }

  public static void undo_MoveNode(Node node, Double x, Double y) {
    node.setLayoutX(x);
    node.setLayoutY(y);
  }

  public static void redo_MoveNode(Node node, Double x, Double y) {
    node.setLayoutX(x);
    node.setLayoutY(y);
  }

  //_____________________

  //_______________________________________________________________________________
  //
  //_______________________________________________________________________
  //________________________________________
  //___________________________________________________________
  //____________________________________
  //_
  //___________________________________________________________________________________________________________________________________________________________________________________________________________________________
  public void setup_Resizable(final Node node_textArea_or_canvas, final Pane panel_Wrapper, final Pane handler_Resize) {
    Class<? extends Node> clazz = node_textArea_or_canvas.getClass();

    //_________________________________________________
    //_________________________________________________________________________
    //______________________________________________________________________________________________________________________________________________________________________________________
    //__________________
    //______________________________________________________________________
    //_________________________________________________________________
    //__
    //________________________________________________________________
    //___________________________________________________________________________________
    //____________________________________________________________
    //_____________________________________________________
    //_____________________________________________________
    //__
    //__
    //____________________________________________
    //_______________________________________________________
    //__
    //__
    //_________________________________________________________________
    //__________________________
    //____________
    //__________________________________________
    //________________________________________________________

    handler_Resize.setStyle(""
                            + "-fx-border-style: solid;"
                            + "-fx-border-width: 1;"
                            + "-fx-border-color: " + JavafxUtil.rgba_Grey + ";"); //_____________________

    //_______________________________________________________________________________________________________________

    if (Region.class.isAssignableFrom(clazz)) {
      handler_Resize.setLayoutX(((Region) node_textArea_or_canvas).getPrefWidth());
      handler_Resize.setLayoutY(((Region) node_textArea_or_canvas).getPrefHeight());
    }
    else if (Canvas.class.isAssignableFrom(clazz)) {
      handler_Resize.setLayoutX(((Canvas) node_textArea_or_canvas).getWidth());
      handler_Resize.setLayoutY(((Canvas) node_textArea_or_canvas).getHeight());
    }
    else {
      throw new TypeError();
    }
    handler_Resize.setPrefSize(10, 10);
    //___________________________________________________________________

    make_Draggable(handler_Resize, true, true);
    //_______________________________________________________________________________________________________________________________________________________________________________________________________________________________________

    handler_Resize.hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
      if (newValue) { handler_Resize.setCursor(Cursor.SE_RESIZE); }
    });
    //_______________________________________________________________________________________________

    panel_Wrapper.getChildren().add(handler_Resize);
    //________________________________________________________________________________________________________________________________________________________

    //_______________________________________________________________________________________________________________
    //______________________________________________________________
    //_______

    //_________________________________________________________________________________________________________________
    //_____________________________________________________
    if (Region.class.isAssignableFrom(clazz)) {
      Region textArea = (Region) node_textArea_or_canvas;
      handler_Resize.addEventHandler(ResizeStatus.End_end, event -> {
        //__________________________________________________________________________
        //__________________________________
        //________________________________________________________________________________________________________________
        event.consume();
        nodeSender_ConvertWrapper.place_UndoCheckpoint();

        double w_undo = textArea.getWidth();
        double h_undo = textArea.getHeight();
        double w_redo = handler_Resize.getLayoutX();
        double h_redo = handler_Resize.getLayoutY();

        //_______________________________________________________________
        //________________________________________________________________
        nodeSender_ConvertWrapper.change_Shape(textArea, w_redo, h_redo);

        add_UndoOrRedoCompensationStepEvent_for_ResizeNode_commonHelper(node_textArea_or_canvas, w_undo, h_undo, w_redo, h_redo);

        //_________
        //_________
      });
    }
    else if (Canvas.class.isAssignableFrom(clazz)) {
      Canvas canvas = (Canvas) node_textArea_or_canvas;
      handler_Resize.addEventHandler(ResizeStatus.End_end, event -> {
        //__________________________________________________________________________
        //__________________________________
        //________________________________________________________________________________________________________________
        event.consume();
        nodeSender_ConvertWrapper.place_UndoCheckpoint();

        double w_undo = canvas.getWidth();
        double h_undo = canvas.getHeight();
        double w_redo = handler_Resize.getLayoutX();
        double h_redo = handler_Resize.getLayoutY();

        WritableImage snapshot_ori = save_CanvasSnapshot(canvas);

        //_________________________________________________________
        //__________________________________________________________
        nodeSender_ConvertWrapper.change_Shape(canvas, w_redo, h_redo);

        add_UndoOrRedoCompensationStepEvent_for_PaintOrEraseOnCanvas(canvas, snapshot_ori); //______________________________________________

        add_UndoOrRedoCompensationStepEvent_for_ResizeNode_commonHelper(node_textArea_or_canvas, w_undo, h_undo, w_redo, h_redo);
        //_________
        //_________
      });
    }
    else {
      throw new TypeError();
    }

  }

  //___________________________________________________________________________
  public static void resizeNode(Node node_textArea_or_canvas, double w, double h) {
    Class<? extends Node> clazz = node_textArea_or_canvas.getClass();
    if (Region.class.isAssignableFrom(clazz)) {
      Region textArea = (Region) node_textArea_or_canvas;
      textArea.setPrefSize(w, h);
    }
    else if (Canvas.class.isAssignableFrom(clazz)) {
      Canvas canvas = (Canvas) node_textArea_or_canvas;
      canvas.setWidth(w);
      canvas.setHeight(h);
    }
    else {
      throw new TypeError();
    }
  }

  public void add_UndoOrRedoCompensationStepEvent_for_ResizeNode_commonHelper(Node node_textArea_or_canvas, double w_undo, double h_undo, double w_redo, double h_redo) {
    if (!det_NeedRequestQualificationFirst) { //
      undoManager_corr.add_UndoCompensationStep(() -> { DrawAndChatVm.resizeNode(node_textArea_or_canvas, w_undo, h_undo); });
    }
    nodeSender_ConvertWrapper.wrap_and_send_UndoCompensationStepAddedEvent(node_textArea_or_canvas, "resizeNode",
                                                                           new Class[] { Node.class, double.class, double.class }, new Object[] { node_textArea_or_canvas, w_undo, h_undo }, null, DrawAndChatVm.class);

    if (!det_NeedRequestQualificationFirst) { //
      undoManager_corr.add_RedoCompensationStep(() -> { DrawAndChatVm.resizeNode(node_textArea_or_canvas, w_redo, h_redo); });
    }
    nodeSender_ConvertWrapper.wrap_and_send_RedoCompensationStepAddedEvent(node_textArea_or_canvas, "resizeNode",
                                                                           new Class[] { Node.class, double.class, double.class }, new Object[] { node_textArea_or_canvas, w_redo, h_redo }, null, DrawAndChatVm.class);
  }

  //_____________________

  private final ScheduledExecutorService executor_BrustDetect_Zoom = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("thd-setup_Zoomable_brust-%d").build());
  private LinkedBlockingQueue<Integer> det_BrustEvent_Zoom = new LinkedBlockingQueue<>();

  private void cstuInit__executor_BrustDetect_Zoom() {
    ScheduledFuture<?> future = executor_BrustDetect_Zoom.scheduleWithFixedDelay(() -> {
      try {
        det_BrustEvent_Zoom.take(); //______________
      } catch (InterruptedException e) {
        System.out.println("Seems shutdown requested :: " + e);
        //_____________________
        executor_BrustDetect_Zoom.shutdown();
        try {
          executor_BrustDetect_Zoom.awaitTermination(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    }, 0, 75, TimeUnit.MILLISECONDS);
    windowSession_corr.arr_ThreadPool_OnlyForSimpleShutdown.add(executor_BrustDetect_Zoom);
    windowSession_corr.mpp_ThreadPool_vs_Future_OnlyForSimpleShutdown.put(executor_BrustDetect_Zoom, future);
  }

  public void setup_Zoomable(Node node) {
    //_________________________________________________________________________________________
    //______________________________________________________________________________

    //________________________________________________________________________________
    //_______________________________________________________________________________
    //_________________________________________________________________________________
    //_____________________________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________
    //_____________________________________________
    //_________________________________________________
    //_______________
    //___________
    //_______________________________________________
    //________________________________________
    //_______________________________________________________________
    //_______________________________
    //_____________________________________________
    //_____________
    //_________________________________________________________________________________
    //___________________________________________
    //_______________________________
    //_________
    //_______
    //_____________________________________
    //___________________________________________________________________________________________
    //_____________________________________________________________________________________________________________

    node.addEventHandler(ScrollEvent.SCROLL, event -> {
      if (!event.isConsumed()) {
        if (event.isControlDown() && !event.isAltDown() && !event.isShiftDown()) {
          if (node.isFocused()) {
            event.consume();

            //__________________________________________
            //_____________________________________________
            //_____________________________________________________________
            //___________

            if (det_BrustEvent_Zoom.isEmpty()) {
              nodeSender_ConvertWrapper.place_UndoCheckpoint();
              det_BrustEvent_Zoom.add(0); //________________________________________________________________________
            }
            det_BrustEvent_Zoom.add(1);

            //_______________________________________________________________
            //_________________________________________________________
            //____
            //________________________________________________________________________________________________________________________________________________

            double dy = event.getDeltaY(); //________________________________________________________________________
            double scaleX_ori = node.getScaleX();
            double scaleY_ori = node.getScaleY();
            double scaleX_new;
            double scaleY_new;
            double incScale = 0.05;
            if (dy < 0) { //_____
              scaleX_new = scaleX_ori - incScale;
              scaleY_new = scaleY_ori - incScale;
            }
            else {
              scaleX_new = scaleX_ori + incScale;
              scaleY_new = scaleY_ori + incScale;
            }
            nodeSender_ConvertWrapper.scale_NodeShapeScale(node, scaleX_new, scaleY_new);

            add_UndoOrRedoCompensationStepEvent_for_ScaleNode_commonHelper(node, scaleX_ori, scaleY_ori, scaleX_new, scaleY_new);
          }
        }
      }
    });
    node.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
      if (!event.isConsumed()) {
        if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.getCode() == KeyCode.F1) {
          event.consume();
          nodeSender_ConvertWrapper.place_UndoCheckpoint();

          double scaleX_ori = node.getScaleX();
          double scaleY_ori = node.getScaleY();
          double scaleX_new = 1;
          double scaleY_new = 1;
          nodeSender_ConvertWrapper.scale_NodeShapeScale(node, scaleX_new, scaleY_new);

          add_UndoOrRedoCompensationStepEvent_for_ScaleNode_commonHelper(node, scaleX_ori, scaleY_ori, scaleX_new, scaleY_new);
        }
      }
    });

  }

  private void add_UndoOrRedoCompensationStepEvent_for_ScaleNode_commonHelper(Node node, double scaleX_ori, double scaleY_ori, double scaleX_new, double scaleY_new) {
    undoManager_corr.add_UndoCompensationStep(() -> { JavafxUtil.scale_NodeShapeScale(node, scaleX_ori, scaleY_ori); });
    nodeSender_ConvertWrapper.wrap_and_send_UndoCompensationStepAddedEvent(node, "scale_NodeShapeScale",
                                                                           new Class[] { Node.class, double.class, double.class },
                                                                           new Object[] { node, scaleX_ori, scaleY_ori },
                                                                           null, JavafxUtil.class);

    undoManager_corr.add_RedoCompensationStep(() -> { JavafxUtil.scale_NodeShapeScale(node, scaleX_new, scaleY_new); });
    nodeSender_ConvertWrapper.wrap_and_send_RedoCompensationStepAddedEvent(node, "scale_NodeShapeScale",
                                                                           new Class[] { Node.class, double.class, double.class },
                                                                           new Object[] { node, scaleX_new, scaleY_new },
                                                                           null, JavafxUtil.class);
  }

  //_____________________

  //____________
  //_______________________________________

  //__________________________________________________________
  //
  //_____________________________________________________________________________
  //_________________________________________________________________________________
  public void setup_ArrowDrawingLinkingListener(Node node) {

    //____________________________________________________________________________________________________________________________________________________
    //_____________________________________________________________________________________________
    //
    //____________________________________________________________________
    //________________________________________________________________________________________________________
    //____________________
    //_________________________________________________________________________________________________
    //_________________________________________________________________________________________

    final Pane panel_SemanticRoot = windowSession_corr.panel_SemanticRoot;

    node.addEventHandler(MouseEvent.MOUSE_PRESSED, (mouseEvent) -> {
      if (!mouseEvent.isConsumed()) {
        //_________________
        if (mouseEvent.isControlDown() && !mouseEvent.isAltDown() && !mouseEvent.isShiftDown() && mouseEvent.isPrimaryButtonDown()) {
          mouseEvent.consume();
          boolean det_Drawing = mouseStatus_ArrowDrawing.det_ArrowDrawing;
          //______________________
          if (!det_Drawing) {
            nodeSender_ConvertWrapper.place_UndoCheckpoint();

            mouseStatus_ArrowDrawing.det_ArrowDrawing = true;
            mouseStatus_ArrowDrawing.statusPhase      = StatusPhase.Begin_begin;

            //____________________________________
            //____________________________________
            Arrow arrow = nodeSender_ConvertWrapper.<Arrow>create_Node(Arrow.class);
            //_____________________________________________________________________________________________________________________
            String cssStyleStr = "-fx-stroke: " + JavafxUtil.rgba_Green_dim + ";";
            arrow.setStyle_Arrow(cssStyleStr);
            //______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
            nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(arrow, "setStyle_Arrow", new Class[] { String.class }, new Object[] { cssStyleStr }, arrow, Arrow.class);

            mouseStatus_ArrowDrawing.arrow_Drawing = arrow;

            //_____________________________________________________________
            //____________________________________________________________________________________________________________________
            //______________________________________________________________________________________________________
            //___________________________________________________________________________________________________________________________________
            //__________________________________________________________
            //____________________________________________________________________________________________________________________
            nodeSender_ConvertWrapper.establish_NodeRelationshipContainmentStatus(panel_SemanticRoot, arrow);

            //_____________________________________________________
            if (!det_NeedRequestQualificationFirst) { //
              hotkey_Common(arrow);
            }
            nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(arrow, "hotkey_Common",
                                                                           new Class[] { Node.class }, new Object[] { arrow }, this, DrawAndChatVm.class);

            //_____________________________________________
            //_____________________________________________
            //__________________________________________
            //__________________________________________
            double x1 = JavafxUtil.get_Pos_relToGivenPane(node, panel_SemanticRoot, "X");
            double y1 = JavafxUtil.get_Pos_relToGivenPane(node, panel_SemanticRoot, "Y");
            nodeSender_ConvertWrapper.move_Arrow(arrow, x1, y1, true, StatusPhase.End_begin); //_______

            //______________________________________________________________

            if (!det_NeedRequestQualificationFirst) { //
              arrow.setEndX(mouseEvent.getX());
              arrow.setEndY(mouseEvent.getY());
            }

            //___________________________________________________________________________________________________________________________
            listen_ArrowLinkageMovement(node, panel_SemanticRoot, arrow, true);
            nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(node, "listen_ArrowLinkageMovement",
                                                                           new Class[] { Node.class, Pane.class, Arrow.class, boolean.class },
                                                                           new Object[] { node, panel_SemanticRoot, arrow, true }, this, DrawAndChatVm.class);

            //_______________________________________
            Scene scene_L = node.getScene();
            EventHandler<MouseEvent> evh_UpdateArrowPointToMouse_L = new EventHandler<MouseEvent>()
              {
                @Override
                public void handle(MouseEvent mouseEvent_Move) {
                  double x = mouseEvent_Move.getX();
                  double y = mouseEvent_Move.getY();
                  //_________________________________
                  //_________________________________
                  nodeSender_ConvertWrapper.move_Arrow(arrow, x, y, false, StatusPhase.Ongoing_begin);
                }
              };

            scene_L.addEventHandler(MouseEvent.MOUSE_MOVED, evh_UpdateArrowPointToMouse_L);
            mouseStatus_ArrowDrawing.evh_UpdateArrowPointToMouse = evh_UpdateArrowPointToMouse_L;
            mouseStatus_ArrowDrawing.scene                       = scene_L;

            //_____________
            EventHandler<MouseEvent> evh_ArrowCancel_L = new EventHandler<MouseEvent>()
              {
                @Override
                public void handle(MouseEvent event_ArrowCancel) {
                  //______________________________________________________________________________________
                  if (!event_ArrowCancel.isControlDown() && !event_ArrowCancel.isAltDown() && !event_ArrowCancel.isShiftDown() && event_ArrowCancel.isSecondaryButtonDown()) {
                    scene_L.removeEventHandler(MouseEvent.MOUSE_MOVED, evh_UpdateArrowPointToMouse_L);
                    scene_L.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
                    nodeSender_ConvertWrapper.remove_Node(arrow);
                    mouseStatus_ArrowDrawing.det_ArrowDrawing = false;
                  }
                }
              };
            scene_L.addEventHandler(MouseEvent.MOUSE_PRESSED, evh_ArrowCancel_L);
            mouseStatus_ArrowDrawing.evh_ArrowCancel = evh_ArrowCancel_L;
          }
          //____________________
          else {
            mouseStatus_ArrowDrawing.statusPhase = StatusPhase.End_begin;

            //_____________________________________
            mouseStatus_ArrowDrawing.scene.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseStatus_ArrowDrawing.evh_UpdateArrowPointToMouse);
            mouseStatus_ArrowDrawing.scene.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseStatus_ArrowDrawing.evh_ArrowCancel);

            Arrow arrow = mouseStatus_ArrowDrawing.arrow_Drawing;

            //___________________________________________
            //___________________________________________
            //__________________________________________
            //__________________________________________
            //____________________________________________________________________________________________________________________
            double x1 = JavafxUtil.get_Pos_relToGivenPane(node, panel_SemanticRoot, "X");
            double y1 = JavafxUtil.get_Pos_relToGivenPane(node, panel_SemanticRoot, "Y");
            nodeSender_ConvertWrapper.move_Arrow(arrow, x1, y1, false, StatusPhase.End_begin);

            if (!det_NeedRequestQualificationFirst) { //
              listen_ArrowLinkageMovement(node, panel_SemanticRoot, arrow, false);
            }
            nodeSender_ConvertWrapper.wrap_and_send_FunctionExecutionEvent(node, "listen_ArrowLinkageMovement",
                                                                           new Class[] { Node.class, Pane.class, Arrow.class, boolean.class },
                                                                           new Object[] { node, panel_SemanticRoot, arrow, false }, this, DrawAndChatVm.class);

            System.out.println("Arrow ?-> " + undoManager_corr.get_undoTimeline_curr());

            //_______________________________________________________________________________________________________________________________

            //_______________________________________________________________________________________
            add_UndoOrRedoCompensationStepEvent_for_AddOrRemoveNode(arrow, true, (Pane) arrow.getParent());

            mouseStatus_ArrowDrawing.det_ArrowDrawing = false;
          }
        }
      }
    });
  }

  public void listen_ArrowLinkageMovement(Node node, Pane panel_SemanticRoot, Arrow arrow, boolean det_ArrowStartOrEnd) {
    node.localToSceneTransformProperty()
        .addListener((observable, oldValue, newValue) -> {
          //_______________________________________________
          //_______________________________________________
          double posX = JavafxUtil.get_Pos_relToGivenPane(node, panel_SemanticRoot, "X");
          double posY = JavafxUtil.get_Pos_relToGivenPane(node, panel_SemanticRoot, "Y");
          //________________________________________________________________________
          //________________________________________________________________________

          if (det_ArrowStartOrEnd) {
            arrow.setStartX(posX);
            arrow.setStartY(posY);
          }
          else {
            arrow.setEndX(posX);
            arrow.setEndY(posY);
          }
          //_________________________________________________________________________________
          //_____________________________________________________
        });
  }

  //_____________________

  //_______________________________________________________________________________________________
  //_______________________________________________________
  public void create_IndexCircle(String content, AnchorPane paneWrap_IndexCircle, TextArea textArea) {
    //____
    //__________________
    final StackPane paneWrapPrime_IndexCircle = new StackPane();    //_______________________________________________

    //_____________
    final Circle circle = new Circle();
    final double radius_ori = 13;
    final double opacity = 0.5;
    circle.setRadius(radius_ori);
    circle.setFill(Color.rgb(191, 86, 255, opacity));
    //____________________________________________________________________

    //__________
    final Text text = new Text(content);
    text.setFill(Color.rgb(0, 0, 128, opacity));
    final Font font_ori = Font.font("Arial", FontWeight.BLACK, radius_ori * 1.35);
    text.setFont(font_ori);
    //
    //_____________________________________________
    //______________________________
    //_______________________________
    //_______________________________
    //_________________________________________________
    //_______________________________
    //______________________________________________________
    //_____________________________
    //__________________________________________________
    //____________________________________________________________________________________________________________________________________________________________________________________________________
    //___________________________________________________________________________________________________________________________________
    //_______
    //__________________________________________________________________________________
    //_______________________________________________________________________________________________________

    //____
    //_____
    //__________________________________________
    //_______________________________________
    textArea.setMaxSize(1, 1);
    textArea.setPadding(new Insets(0, 0, 0, 0));
    textArea.setFont(Font.font("Arial", 1));
    //
    //___
    //________________________________________________
    //_____________________________________________________________________________________________
    //___
    //____________________________________________________________________________________________________________________________
    //_______________________________________________________________________________________
    //_
    //___________________
    //
    //_____________________________________________________
    //_
    //__________________________
    //________________________
    //___________________
    //_____________________
    //____________________________
    //_____________________________

    //_____
    //__________
    textArea.textProperty()
            .addListener((ChangeListener<String>) (observable, content_old, content_new) -> {
              text.setText(content_new);
            });
    listen_TextChange(textArea);

    //_____________________________________________________________________________________________________
    //________________________________________________________________________
    //_______
    //__________________________________________________________________________________________
    //______________________________________________________________
    //
    //_________________
    //_______________________________________________________________________________________________________________
    //_______________________________________________________
    //____________________________________________
    //____________________________________________________
    //______________________________________________________
    //__________________________________________________________________________________
    //___________________________________________________
    //
    //____________________________________________________
    //______________________________________________________
    //__________________________________________________________________________________
    //___________________________________________________
    //
    //________________________________________________________________________________________________
    //
    //______________________________________________________________________
    //_________________________________________________
    //___________________________________________
    //___________________________________________________________________________________
    //_____________
    //___________________________________________________________________________
    //_________________________________________________
    //___________________________________________
    //_____________________________________
    //_____________
    //__________________
    //__________________________________________________
    //_____________
    //___________
    //________________
    //_______________________________________________
    //___________
    //_________
    //_________

    //_________________________________________________________
    //_____________________________________________________________________________________________________
    text.layoutBoundsProperty().addListener(new ChangeListener<Bounds>()
      {
        //________________________________________________________________________________
        AtomicInteger cdet_DoNotResize = new AtomicInteger(0);

        double radius_prev;
        Font font_prev;
        Font font_smaller = Font.font("Arial", FontWeight.BLACK, radius_ori * 1.1);

        //___________________________________________________________________________
        //____________________________
        @Override
        public void changed(ObservableValue<? extends Bounds> observable, Bounds bound_old, Bounds bound_new) {
          if (cdet_DoNotResize.get() == 0) {
            double width_new = bound_new.getWidth();
            double height_new = bound_new.getHeight();
            double diameter_new = width_new > height_new ? width_new : height_new;
            double radius_new = diameter_new / 2.0;

            double radius_curr;
            Font font_curr;

            if (radius_new > radius_ori) {
              radius_curr = radius_new;
              font_curr   = font_smaller;
            }
            else {
              radius_curr = radius_ori;
              font_curr   = font_ori;
            }

            if (radius_curr != radius_prev) {
              circle.setRadius(radius_curr);
              radius_prev = radius_curr;
            }
            if (font_curr != font_prev) {
              cdet_DoNotResize.incrementAndGet();
              text.setFont(font_curr);
              font_prev = font_curr;
            }
          }
          else {
            cdet_DoNotResize.decrementAndGet();
          }
        }
      });

    //____________________
    paneWrapPrime_IndexCircle.getChildren().addAll(circle, text);
    paneWrap_IndexCircle.getChildren().addAll(paneWrapPrime_IndexCircle, textArea);
    //_____
    paneWrap_IndexCircle.setBackground(Background.EMPTY);

    //____
    //___
    //__________________________________________________________________________________________________
    //______________________________
    //________________________________________
    //__________________________________________________________________________________
    //__________________________________________________________________________________________________
    //_____________________________________________________________________________________________________________
    //__________________________________________
    //_________________________________________________
    //_____________________________________________________________________________________________________
    //________
    //___
    //____________________________________________________________________________________
    //______________________________________________________________________________________________________
    //_____________________________________________________________________
    //_____________________________________________
    //________________________________________________________________
    //_________________________________________
    //_________________________________________________________
    //_________________
    //___________
    //___________________________________________________________
    //___________________________________________________________
    //___________________________________________________________
    //___________________________________________________________
    //___________________________________________________________
    //___________________________________________________________
    //___________________________________________________________
    //___________________________________________________________
    //___________________________________________________________
    //___________________________________________________________
    //__________________________________________________________
    //__________________________________________________________
    //___________________________________________________________
    //_________________________________________________________
    //__________________________________________________________
    //__________________________________________________________
    //__________________________________________________________
    //_____________________________________________________________________
    //______________________________________________________________________
    //________________
    //___
    //___________
    //___
    //_________
    //_________________
    //_________

  }

  //_____________________

  public AnchorPane setup_MiniMap(Node node) {
    AnchorPane paneWrap = new AnchorPane();
    String cssStyleStr_Normal = ""
                                + "-fx-border-style: solid;"
                                + "-fx-border-width: 1;"
                                + "-fx-border-color: " + JavafxUtil.rgba_Grey + ";";
    paneWrap.setStyle(cssStyleStr_Normal);

    //
    ImageView imageView = new ImageView();

    SnapshotParameters snapshotParameters = new SnapshotParameters();
    snapshotParameters.setFill(Color.TRANSPARENT);

    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("thd-miniMap_update-%d").build());
    windowSession_corr.arr_ThreadPool_OnlyForSimpleShutdown.add(executor);
    executor.scheduleWithFixedDelay(() -> {
      Platform.runLater(() -> {
        WritableImage snapshot = node.snapshot(snapshotParameters, null);
        imageView.setImage(snapshot);
      }); //__________________________
    }, 0, 150, TimeUnit.MILLISECONDS);

    //_____________________________
    //_______________________________________________________________________________________________________

    imageView.setFitWidth(200);
    imageView.setPreserveRatio(true);

    //______________________________
    //______________________________
    //
    //_____________________________________________

    paneWrap.setLayoutX(700);
    paneWrap.setLayoutY(15);

    make_Draggable_ForRoot(paneWrap, false);

    //
    paneWrap.getChildren().add(imageView);

    return paneWrap;
  }

  public void setup_MiniMap_UsingRoot() {
    //___________________________________________________________
    //_______________________________________________

    AnchorPane miniMap = setup_MiniMap(windowSession_corr.panel_SemanticRoot);
    windowSession_corr.pane_JavafxRoot.getChildren().add(miniMap);
  }

  //_____________________

}