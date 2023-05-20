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
@DiscriminatorValue("NodeShapeSizeChangedEvent")
public class NodeShapeSizeChangedEvent extends NodeEvent {

  public Double grandXLength_curr;
  public Double grandYLength_curr;
  public Double grandZLength_curr;
  public Dimension dimension;

  public NodeShapeSizeChangedEvent(NodeSig nodeMain, Double grandXLength_curr, Double grandYLength_curr) {
    super(nodeMain);
    this.grandXLength_curr = grandXLength_curr;
    this.grandYLength_curr = grandYLength_curr;
    dimension              = Dimension.D2;
  }

  public NodeShapeSizeChangedEvent(NodeSig nodeMain, Double grandXLength_curr, Double grandYLength_curr, Double grandZLength_curr) {
    super(nodeMain);
    this.grandXLength_curr = grandXLength_curr;
    this.grandYLength_curr = grandYLength_curr;
    this.grandZLength_curr = grandZLength_curr;
    dimension              = Dimension.D3;
  }

}