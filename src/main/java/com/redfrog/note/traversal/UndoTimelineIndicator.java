package com.redfrog.note.traversal;

public class UndoTimelineIndicator extends UndoTimelineComponent {
  
  public enum UndoTimelineIndicatorProperty {
    GrandHead,
    GrandTail,
    SubHead, //_________________________________________________
  }

  public UndoTimelineIndicatorProperty undoTimelineIndicatorProperty;

  public UndoTimelineIndicator(UndoTimelineIndicatorProperty undoTimelineIndicatorProperty) {
    super();
    this.undoTimelineIndicatorProperty = undoTimelineIndicatorProperty;
  }
}
