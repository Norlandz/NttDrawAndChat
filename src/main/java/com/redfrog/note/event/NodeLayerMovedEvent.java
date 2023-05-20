package com.redfrog.note.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;
import com.redfrog.note.util.JavafxUtil.MoveLayerMode;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("NodeLayerMovedEvent")
public class NodeLayerMovedEvent extends NodeEvent {

  public int ind_Move;
  public MoveLayerMode moveLayerMode;

  public NodeLayerMovedEvent(NodeSig nodeMain, int ind_Move, MoveLayerMode moveLayerMode) {
    super(nodeMain);
    this.ind_Move      = ind_Move;
    this.moveLayerMode = moveLayerMode;
  }

}