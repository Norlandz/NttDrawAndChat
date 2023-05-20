package com.redfrog.note.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;
import com.redfrog.note.nodeShape.Dimension;
import com.redfrog.note.nodeShape.Point;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("NodeShapeScaleChangedEvent")
public class NodeShapeScaleChangedEvent extends NodeEvent {

  public double scaleX_new;
  public double scaleY_new;
  
  public NodeShapeScaleChangedEvent(NodeSig nodeMain, double scaleX_new, double scaleY_new) {
    super(nodeMain);
    this.scaleX_new = scaleX_new;
    this.scaleY_new = scaleY_new;
  }

}