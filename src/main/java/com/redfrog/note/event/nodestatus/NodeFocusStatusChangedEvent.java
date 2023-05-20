package com.redfrog.note.event.nodestatus;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("NodeFocusStatusChangedEvent")
public class NodeFocusStatusChangedEvent extends NodeEvent {

  //______________________________________
  //______________________________________
  public boolean focusStatus_prev;
  public boolean focusStatus_curr;

  public NodeFocusStatusChangedEvent(NodeSig nodeMain, boolean focusStatus_prev, boolean focusStatus_curr) {
    super(nodeMain);
    this.focusStatus_prev = focusStatus_prev;
    this.focusStatus_curr = focusStatus_curr;
  }

}