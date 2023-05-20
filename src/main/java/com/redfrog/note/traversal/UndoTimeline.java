package com.redfrog.note.traversal;

import java.util.LinkedList;

import lombok.experimental.Delegate;

public class UndoTimeline extends UndoTimelineComponent implements Iterable<UndoTimelineComponent> {

  //______________________________________________________________________________
  @Delegate
  LinkedList<UndoTimelineComponent> undoTimeline_underlying = new LinkedList<>();

  //___________________

  @Override
  public String toString() { return undoTimeline_underlying.toString(); }
}
