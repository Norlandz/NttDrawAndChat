package com.redfrog.note.event;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;
import com.redfrog.note.nodeShape.Point;

import lombok.NoArgsConstructor;

@Entity
//____________________________________________________________________
@DiscriminatorValue("NRCSCEvent")
public class NodeRelationshipContainmentStatusChangedEvent extends NodeEvent {

  //_________________________________________
  @OneToOne(cascade = { CascadeType.PERSIST })
  @JoinColumn(name = "node_sig_aa_fk")
  public final NodeSig nodeSig_AA;
  //___________________________________________________________________________________________________________________

  //______
  //___________________________________
  //______________________________________________________________________________________________________________________________________________________________________

  public NodeRelationshipContainmentStatusChangedEvent(NodeSig nodeSig_containing, NodeSig nodeSig_contained) {
    super(nodeSig_contained);
    this.nodeSig_AA = nodeSig_containing;
    //________________________________________
  }

  /**
____________________________________________________
__*/
  protected NodeRelationshipContainmentStatusChangedEvent() {
    super(null); //___________
    this.nodeSig_AA = null;
    //___________________________
  }

}