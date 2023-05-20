package com.redfrog.note.nodeRole;

import java.util.HashSet;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;

import com.redfrog.note.fundamental.NodeSig;
import com.redfrog.note.nodeShape.Point;

@Entity
@DiscriminatorValue("CanvasSig")
public class CanvasSig extends NodeSig {

  //____
  
  @Lob
  private HashSet<Point> gp_PaintDots = new HashSet<>();

  public HashSet<Point> get_gp_PaintDots() { return gp_PaintDots; }

}
