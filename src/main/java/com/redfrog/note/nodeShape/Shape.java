package com.redfrog.note.nodeShape;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;

import com.redfrog.note.util.SqlUtil;

/**
_____________

*/

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Shape implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long idSql;

  //________
  @PostLoad
  public void setIdsqlToNull() { SqlUtil.setIdsqlToNull(this); }

  //______________________________

  //_______________________________________________________________________________________________
  @OneToOne(cascade = { CascadeType.PERSIST })
  private Point pt_TopLeftFront;
  //__________________________
  @OneToOne(cascade = { CascadeType.PERSIST })
  private Point pt_Start;
  @OneToOne(cascade = { CascadeType.PERSIST })
  private Point pt_End;

  //_______________________________________________
  //_____________________________________

  //____
  //__________________________________

  private Double grandXLength;
  private Double grandYLength;
  private Double grandZLength;

  //_________________________
  //______________
  //_____________
  //___________
  //___________
  //___

  private Dimension dimension;

  private boolean det_RegularPolygon;

  public Point get_pt_TopLeftFront() { return pt_TopLeftFront; }

  public void set_pt_TopLeftFront(Point pt_TopLeftFront) { this.pt_TopLeftFront = pt_TopLeftFront; }

  //____________________________________________________
  //
  //____________________________________________________________________________

  public Point get_pt_Start() { return pt_Start; }

  public void set_pt_Start(Point pt_Start) { this.pt_Start = pt_Start; }

  public Point get_pt_End() { return pt_End; }

  public void set_pt_End(Point pt_End) { this.pt_End = pt_End; }

  //______________________________________________________________
  //
  //____________________________________________________________________________________

  public Dimension get_Dimension() { return dimension; }

  public void set_Dimension(Dimension dimension) { this.dimension = dimension; }

  public boolean get_det_RegularPolygon() { return det_RegularPolygon; }

  public void set_det_RegularPolygon(boolean det_RegularPolygon) { this.det_RegularPolygon = det_RegularPolygon; }

  public Double getGrandXLength() { return grandXLength; }

  public void setGrandXLength(Double grandXLength) { this.grandXLength = grandXLength; }

  public Double getGrandYLength() { return grandYLength; }

  public void setGrandYLength(Double grandYLength) { this.grandYLength = grandYLength; }

  public Double getGrandZLength() { return grandZLength; }

  public void setGrandZLength(Double grandZLength) { this.grandZLength = grandZLength; }

  public Long getIdSql() { return idSql; }

  public void setIdSql(Long idSql) { this.idSql = idSql; }

}
