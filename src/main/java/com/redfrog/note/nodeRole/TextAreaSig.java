package com.redfrog.note.nodeRole;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.fundamental.NodeSig;

//_____________________

@Entity
@DiscriminatorValue("TextAreaSig")
public class TextAreaSig extends NodeSig {

  private String content;

  public String getContent() { return content; }

  public void setContent(String content) { this.content = content; }
  
}
