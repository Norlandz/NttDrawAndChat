package com.redfrog.note.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("NodeRemovedEvent")
public class NodeRemovedEvent extends NodeEvent {

  public boolean det_Removed_prev;
  public boolean det_Removed_curr;

  public NodeRemovedEvent(NodeSig nodeMain) { super(nodeMain); }

}