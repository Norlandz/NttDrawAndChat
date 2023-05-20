package com.redfrog.note.event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;

import lombok.NoArgsConstructor;

//_________________________________________________________

@Entity
@NoArgsConstructor
@DiscriminatorValue("TextChangedEvent")
public class TextChangedEvent extends NodeEvent {

  public String content_old;
  public String content_new;

  public TextChangedEvent(NodeSig nodeMain, String content_old, String content_new) {
    super(nodeMain);
    this.content_old = content_old;
    this.content_new = content_new;
  }

}