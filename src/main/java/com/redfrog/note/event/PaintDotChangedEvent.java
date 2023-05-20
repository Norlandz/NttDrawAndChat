package com.redfrog.note.event;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.redfrog.note.event.mousekeyboardinput.StatusPhase;
import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;
import com.redfrog.note.nodeShape.Point;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("PaintDotChangedEvent")
public class PaintDotChangedEvent extends NodeEvent {

  @OneToOne(cascade = { CascadeType.PERSIST })
  public Point pt_PaintDot; //___________________________________________________
  //____________________________
  public boolean det_AddedorRemoved;
  public StatusPhase statusPhase;

  //_______________________________________________
  //___
  //______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //___
  //___________
  //___________________________________________________________________________
  //
  //___________________________________________________
  //___________________________________________________________________________________
  //______________________________________________________________
  //_
  @OneToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
  //______________________________________________________________________________________________________________
  public List<Point> arr_BatchLinePoints = null; //_____________________________________________________

  public PaintDotChangedEvent(NodeSig nodeMain, Point pt_PaintDot, boolean det_AddedorRemoved, StatusPhase statusPhase) {
    super(nodeMain);
    this.pt_PaintDot        = pt_PaintDot;
    this.det_AddedorRemoved = det_AddedorRemoved;
    this.statusPhase        = statusPhase;
  }

}