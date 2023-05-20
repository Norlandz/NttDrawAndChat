package com.redfrog.note.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StaticValueDebug {
  //_________

  public static Boolean det_DisableReceiveNetworkMsg_Kafka;
  public static Boolean det_DisableLoadSaveFile_Mysql;

  @Value("${cpy.rf.ntt.drc.det-DisableReceiveNetworkMsg-Kafka}")
  public void set_det_DisableReceiveNetworkMsg_Kafka(boolean value) {
    det_DisableReceiveNetworkMsg_Kafka = value;
    System.out.println("@Value(\"${cpy.rf.ntt.drc.det-DisableReceiveNetworkMsg-Kafka}\")" + " :: " + det_DisableReceiveNetworkMsg_Kafka);
  }

  @Value("${cpy.rf.ntt.drc.det-DisableLoadSaveFile-Mysql}")
  public void set_det_DisableLoadSaveFile_Mysql(boolean value) {
    det_DisableLoadSaveFile_Mysql = value;
    System.out.println("@Value(\"${cpy.rf.ntt.drc.det-DisableLoadSaveFile-Mysql}\")" + " :: " + det_DisableLoadSaveFile_Mysql);
  }

  //_________

  //_________________________________________________________________________________________
  @Autowired(required = false)
  public StaticValueDebug_Kafka staticValueDebug_Kafka; //____________________________

  @Autowired(required = false)
  public StaticValueDebug_Mysql staticValueDebug_Mysql; //____________________________

}