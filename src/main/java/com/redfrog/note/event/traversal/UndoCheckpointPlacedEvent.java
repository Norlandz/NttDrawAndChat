package com.redfrog.note.event.traversal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("UndoCheckpointPlacedEvent")
public class UndoCheckpointPlacedEvent extends NodeEvent {

  public UndoCheckpointPlacedEvent(NodeSig nodeMain) { super(nodeMain); }

}