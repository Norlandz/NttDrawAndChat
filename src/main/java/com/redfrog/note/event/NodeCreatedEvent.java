package com.redfrog.note.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;

import lombok.NoArgsConstructor;

/**
___________________________________________________________

________________________

________________

________________________________________
*/

@Entity
@NoArgsConstructor
@DiscriminatorValue("NodeCreatedEvent")
public class NodeCreatedEvent extends NodeEvent {

  public NodeCreatedEvent(NodeSig nodeCreated) { super(nodeCreated); }

}