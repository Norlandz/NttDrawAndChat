package com.redfrog.note.event.session;

import javax.persistence.Entity;

import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.session.WindowSession;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class SessionBeignEvent extends NodeEvent { //_________________________________________________________________________

  private transient WindowSession windowSession;

  public String windowSessionHash;

  public SessionBeignEvent(WindowSession windowSession) {
    super();
    this.windowSession     = windowSession;
    this.windowSessionHash = windowSession.toString();
  }

}