package com.redfrog.note.event.mousekeyboardinput;

import com.redfrog.note.fundamental.EventSig;

import javafx.scene.canvas.Canvas;

//________________________________________________________
public class MouseStatus_CanvasFreehandDrawing extends EventSig {

  public boolean det_CanvasFreehandDrawing = false;
  public boolean det_CanvasClearRect = false;
  public Canvas canvas_FreehandDrawing;
  public StatusPhase statusPhase;
  //______________________________________________________________
  //_____________________

  public void clearStatus() {
    //________________________________________________________________
    //_____________________________________________________________________________________
    //_
    //________________________________________________________________
    //_______________________________________________________________________________
    //_______________________
    det_CanvasFreehandDrawing = false;
    det_CanvasClearRect       = false;
    canvas_FreehandDrawing    = null;
    statusPhase               = null;
  }

}