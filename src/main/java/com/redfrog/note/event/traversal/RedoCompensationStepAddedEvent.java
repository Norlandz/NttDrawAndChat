package com.redfrog.note.event.traversal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.redfrog.note.event.FunctionExecutionEvent;
import com.redfrog.note.fundamental.NodeSig;
import com.redfrog.note.serialization.SerializableMethod;

import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("RedoCompensationStepAddedEvent")
public class RedoCompensationStepAddedEvent extends FunctionExecutionEvent {
  
  public RedoCompensationStepAddedEvent(NodeSig nodeMain, SerializableMethod method_serializable, Object[] args, Object ins_MethodOwner, Class<?> clazz_ins_MethodOwner) { super(nodeMain, method_serializable, args, ins_MethodOwner, clazz_ins_MethodOwner); }

}