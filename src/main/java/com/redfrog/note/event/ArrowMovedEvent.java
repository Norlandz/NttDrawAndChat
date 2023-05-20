package com.redfrog.note.event;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;
import com.redfrog.note.nodeShape.Point;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("ArrowMovedEvent")
public class ArrowMovedEvent extends NodeEvent {

  //___________
  //___________________________
  //____________________________________________________________________________________________
  //____________________________________________________________________________________________
  //____________________________________________________________________________________________
  //____________________________________________________________________________________________
  //____
  //__________________________
  //__
  //___________
  //___________________________
  //__________________________________________________________________________________________
  //__________________________________________________________________________________________
  //__________________________________________________________________________________________
  //__________________________________________________________________________________________
  //____
  //__________________________

  //____________________________________________________________________________________________________________________________________________________________________________
//______
  @OneToOne(cascade = { CascadeType.PERSIST })
  public Point point_prev;
  @OneToOne(cascade = { CascadeType.PERSIST })
  public Point point_curr;

  //____________________________________________________________________________________________________________________________________________________________________________________
  //______________________________________________________________________________________
  //________________________________

  public enum ArrowDir {
    Start,
    End,
  }

  public ArrowDir arrowDir;

  public ArrowMovedEvent(NodeSig nodeMain, Point point_prev, Point point_curr, boolean det_StartOrEnd) {
    super(nodeMain);
    this.point_prev = point_prev;
    this.point_curr = point_curr;
    //_________________________________________
    arrowDir        = det_StartOrEnd ? ArrowDir.Start : ArrowDir.End;
  }

  public ArrowMovedEvent(NodeSig nodeMain, Point point_prev, Point point_curr, ArrowDir arrowDir) {
    super(nodeMain);
    this.point_prev = point_prev;
    this.point_curr = point_curr;
    this.arrowDir   = arrowDir;
  }

  //____________________________________________________________________________________
  //_______________________________________________________________

  @Override
  public String toString() { return "[" + arrowDir + " :: " + point_curr.toString() + " :: " + super.toString() + "]"; }

}