package com.redfrog.note.event.traversal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("UndoEvent")
public class UndoEvent extends NodeEvent {

  public UndoEvent(NodeSig nodeMain) { super(nodeMain); }

}