package com.redfrog.note.fundamental;

import java.util.HashMap;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.redfrog.note.event.NetworkPosition;
import com.redfrog.note.nodeShape.Shape;
import com.redfrog.note.session.SessionManager;
import com.redfrog.note.user.User;
import com.redfrog.note.util.SqlUtil;
import com.redfrog.note.util.StringUtil;

import javafx.scene.Node;

/**
_____________________

__
______________________________________________________________________
__
___________________________________________________________________________________________________________________________

________________________________________________
*/

//______________________________________________________________
//________________________________________________________________________________
//_
//__________________________________________________________________________________
//____________________________________________________
//_
//________________________________________________________________________________
//______________________________________________________________________________________________________
//_
//_________________________________________
//_______________________________________________
//_
//______________________________________________________________
//_________________________________________________________________________________
//_
//_______________________________________________________________
//____________________________________________________________________________________
//_
//_____________________________________

//_________________

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//_______________________________________________
//_________________________________________________________________________________________
public abstract class NodeSig extends EntityGeneral implements com.redfrog.note.fundamental.Container {

  //_______________________________
  //__
  //________________________________________________________
  //
  //____________________________________________________________________________________________________

  //_____
  //____________________________________________________________________________________________________________________
  @Lob
  public HashMap<NodeSig, NodeRelationship> mpp_relationship = new HashMap<>(); //_________________________
  @Lob
  public HashMap<NodeRelationship, NodeSig> mpp__nodeRelationship_vs_nodePeer = new HashMap<>();

  //______
  @OneToOne(cascade = { CascadeType.PERSIST })
  public Shape shape;

  public transient Node fxNode_AssocConvert; //____________________________
  public Class<? extends Node> class_fxNode_AssocConvert;

  //__________
  //___________
  //______________________________________________________
  //________________
  //________________________________________
  //_____________________________________________________
  //_______________________________________________________________
  //_____
  //___

  //____________
  //_________________________________________________

  //_________
  public NodeSig() {
    super();
    System.out.println(">> public NodeSig() { :: " + this + " :: Is this invoked internally by other Lib when Deserializing? ... should .. ( but what if no NoArgCstu ? ) // Seems not internally Called... & if it does the creation time will be a mess ...  ");
    //_______________________________________________________________________________
    //_____________________________________________________________________________________________
    networkPosition = NetworkPosition.FromLocal;
  }

  //_________
  private boolean det_Removed;
  private boolean det_Focused;

  //______
  //___________________________________________________________

  public boolean is_det_Removed() { return det_Removed; }

  public void set_det_Removed(boolean det_Removed) { this.det_Removed = det_Removed; }

  public boolean is_det_Focused() { return det_Focused; }

  public void set_det_Focused(boolean det_Focused) { this.det_Focused = det_Focused; }

  //_________
  //_________________________________________________________________

  //_________
  public NetworkPosition networkPosition;

  public boolean det_IsJavafxRoot;
  //________________________________
  //________________________

  //_______________________________________________________________

  //____________________________________________________________________________________

  //_____________
  //_________________________________________________________________________
  //_________________________________________________________________________________________________________
  //______________________________________________________________________________________________________
  //_______________________
  //___

  @Override
  public String toString() { return "‘" + super.toString() + " :: " + fxNode_AssocConvert + "’"; }

  //_________

  //________________________________________________
  //____________________________________________________________
  //_
  //_____________________________________
  //______________________________________________________________________________________________________________
  //_
  //_____________________________________________________________________________
  //__________________________________________________________________________________________________
  //_
  //__________________________________________________________________________________
  //_____________________________________________________________________________________________________________
  //
  //________
  @PostLoad
  public void setIdsqlToNull() { SqlUtil.setIdsqlToNull(this); }

}
