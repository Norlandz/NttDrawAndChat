package com.redfrog.note.user;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class User implements Serializable {

  public Long id;
  
  public String name;

}