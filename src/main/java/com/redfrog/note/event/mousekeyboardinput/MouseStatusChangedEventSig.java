package com.redfrog.note.event.mousekeyboardinput;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.fundamental.EventSig;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("MouseStatusChangedEventSig")
public class MouseStatusChangedEventSig extends EventSig {

  public enum MousePressStatus {
    Pressed,
    Held,
    Released,
  }

  public enum MouseMovementStatus {
    Moved,
    Stopped, //_________
  }

  public enum PhysicalSourcePosition {
    Physical,
    Logical,
    Logical02,
    ProgramaticIntentional,
  }

  public double x;
  public double y;
  //______________________________________________
  public MousePressStatus mousePressStatus;
  public MouseMovementStatus mouseMovementStatus;

  public double getX() { return x; }

  public void setX(double x) { this.x = x; }

  public double getY() { return y; }

  public void setY(double y) { this.y = y; }

  public MousePressStatus getMousePressStatus() { return mousePressStatus; }

  public void setMousePressStatus(MousePressStatus mousePressStatus) { this.mousePressStatus = mousePressStatus; }

  public MouseMovementStatus getMouseMovementStatus() { return mouseMovementStatus; }

  public void setMouseMovementStatus(MouseMovementStatus mouseMovementStatus) { this.mouseMovementStatus = mouseMovementStatus; }

}