package com.redfrog.note.event.traversal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("RedoEvent")
public class RedoEvent extends NodeEvent {

  public RedoEvent(NodeSig nodeMain) { super(nodeMain); }

}