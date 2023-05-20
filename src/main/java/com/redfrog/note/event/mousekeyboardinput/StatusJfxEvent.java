package com.redfrog.note.event.mousekeyboardinput;

import javafx.event.Event;
import javafx.event.EventType;

//________________________________________________________
//_________________________________________
//
//_______________________________________________
//__
//_________________________________________________________________________________
//
//______________________________________________________________________________________________
//
//_____________________________________
//
//_
public abstract class StatusJfxEvent extends Event {
  public StatusJfxEvent(EventType<? extends Event> eventType) { super(eventType); }

  //_______________
  public static final EventType<? extends StatusJfxEvent> Begin_begin           = new EventType<>(Event.ANY, StatusPhase.Begin_begin       .toString());
  public static final EventType<? extends StatusJfxEvent> Begin_end             = new EventType<>(Event.ANY, StatusPhase.Begin_end         .toString());
  public static final EventType<? extends StatusJfxEvent> Ongoing_begin         = new EventType<>(Event.ANY, StatusPhase.Ongoing_begin     .toString());
  public static final EventType<? extends StatusJfxEvent> Ongoing_end           = new EventType<>(Event.ANY, StatusPhase.Ongoing_end       .toString());
  public static final EventType<? extends StatusJfxEvent> End_begin             = new EventType<>(Event.ANY, StatusPhase.End_begin         .toString());
  public static final EventType<? extends StatusJfxEvent> End_end               = new EventType<>(Event.ANY, StatusPhase.End_end           .toString());
  //______________________________________________________________________________________________________________________________________

}