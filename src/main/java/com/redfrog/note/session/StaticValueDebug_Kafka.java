package com.redfrog.note.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({ "Enable_ReceiveNetworkMsg", "prod" })
@Configuration
public class StaticValueDebug_Kafka {
  //_________

  public static String ip_bootstrapServers; //____________________

  public static String kTopicName_NodeEvent_All_Prepend; //________________________

  //___
  //________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //______________________________________________________________________________________________
  //__________________________________________________
  //_____________________________________________________________________
  @Value("${cpy.rf.ntt.drc.kafka.bootstrap.servers}")
  public void set_ip_bootstrapServers(String value) {
    ip_bootstrapServers = value;
    System.out.println("@Value(\"${cpy.rf.ntt.drc.kafka.bootstrap.servers}\")" + " :: " + ip_bootstrapServers);
  }

  @Value("${cpy.rf.ntt.drc.kafka.kTopicName.NodeEvent-All-Prepend}")
  public void set_kTopicName_NodeEvent_All_Prepend(String value) {
    kTopicName_NodeEvent_All_Prepend = value;
    System.out.println("@Value(\"${cpy.rf.ntt.drc.kafka.kTopicName.NodeEvent-All-Prepend}\")" + kTopicName_NodeEvent_All_Prepend);
  }

  //_________

}