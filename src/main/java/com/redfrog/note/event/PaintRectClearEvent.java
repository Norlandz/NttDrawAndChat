package com.redfrog.note.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.event.mousekeyboardinput.StatusPhase;
import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;
import com.redfrog.note.nodeShape.Point;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("PaintRectClearEvent")
public class PaintRectClearEvent extends NodeEvent {

  public double posX;
  public double posY;
  public double posW;
  public double posH;

  public PaintRectClearEvent(NodeSig nodeMain, double posX, double posY, double posW, double posH) {
    super(nodeMain);
    this.posX = posX;
    this.posY = posY;
    this.posW = posW;
    this.posH = posH;
  }
}