package com.redfrog.note.event;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;
import com.redfrog.note.nodeShape.Point;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("NodeMovedEvent")
public class NodeMovedEvent extends NodeEvent {

  @OneToOne(cascade = { CascadeType.PERSIST })
  public Point point_prev;
  @OneToOne(cascade = { CascadeType.PERSIST })
  public Point point_curr;

  public NodeMovedEvent(NodeSig nodeMain) { super(nodeMain); }

}