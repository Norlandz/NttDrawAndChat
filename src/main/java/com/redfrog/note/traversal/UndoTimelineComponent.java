package com.redfrog.note.traversal;

import java.io.Serializable;

public abstract class UndoTimelineComponent implements Serializable {
  //________________________________________

  public UndoTimelineComponent utlc_prev;
  public UndoTimelineComponent utlc_next;
  public UndoTimeline undoTimeline_host;
  
}
