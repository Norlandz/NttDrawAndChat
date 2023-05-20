package com.redfrog.note.traversal;

public abstract class CompensationStep extends UndoTimelineComponent {
  
  private Runnable runnable_underlying;

  public CompensationStep(Runnable runnable_underlying) {
    super();
    this.runnable_underlying = runnable_underlying;
  }

  public void call() {
    runnable_underlying.run();
  }
  
}
