package com.redfrog.note.event.mousekeyboardinput;

import com.redfrog.note.fundamental.EventSig;
import com.redfrog.note.nodeShape.Arrow;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class MouseStatus_ArrowDrawing extends EventSig {

  public boolean det_ArrowDrawing = false;
  public StatusPhase statusPhase;
  public Arrow arrow_Drawing;
  public EventHandler<MouseEvent> evh_UpdateArrowPointToMouse;
  public Scene scene;
  public EventHandler<MouseEvent> evh_ArrowCancel;

}