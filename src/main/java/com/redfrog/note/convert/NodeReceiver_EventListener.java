package com.redfrog.note.convert;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.redfrog.note.DrawAndChatAppSessionInit;
import com.redfrog.note.communication.KafkaConfig;
import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.session.StaticValueDebug;
import com.redfrog.note.session.WindowSession;
import com.redfrog.note.session.WindowSessionExecutor;

import javafx.application.Platform;

public class NodeReceiver_EventListener implements WindowSessionExecutor {

  private final WindowSession windowSession_corr;
  public final NodeReceiver_ConvertHandler nodeReceiver_ConvertHandler;
  //___________________________________________________
  //_______________________________________________________________________________________________________________________

  private final KafkaConfig kafkaConfig;
  private final String kTopicName_Receive_NodeEvent_All;

  private final String dbName;

  public NodeReceiver_EventListener(WindowSession windowSession_corr) {
    this.windowSession_corr               = windowSession_corr;
    this.nodeReceiver_ConvertHandler      = new NodeReceiver_ConvertHandler(windowSession_corr);
    //______________________________________________________________________________
    //_________________________________________________________________________________________________________________________________

    this.kafkaConfig                      = windowSession_corr.kafkaConfig;
    this.kTopicName_Receive_NodeEvent_All = windowSession_corr.kafkaConfig.kTopicName_Receive_NodeEvent_All;
    this.dbName                           = windowSession_corr.dbOccupation.dbName.toString();
  }

  @Override
  public WindowSession getWindowSession() { return windowSession_corr; }

  //_______________________

  //______________________________________________________________________________________

  public static long interval_Poll = 50;

  private Consumer<String, ? extends NodeEvent> consumer;
  private Set<TopicPartition> kPartition_asg;
  private boolean det_Pause = false;

  public boolean det_NoNeedToQualifyEvent_AcceptAll_debug;

  private ScheduledExecutorService executor;
  private boolean det_PollBlocking;

  public void listen_forGeneralNodeEvent() {
    if (!StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka) {
      consumer = kafkaConfig.kConsumer_NodeEvent_All;
      consumer.subscribe(Arrays.asList(kTopicName_Receive_NodeEvent_All));

      //___________________________________________________
      //___________________________________________
      //__________________________________________________________________________________________________________________
      //
      //______________________________________________________________________________________________________________________________________________________
      //_________________________________________________________________________________________________
      //____________________________________________________________________________________________________________________________________
      //______________________________________________________________________________
      //____________________________________________________________________________
      //_______________________________________________________________________________________________
      //__________________________________________________________________________________________________________________________________
      //________________________________________________________________
      //_________________________________________________________________________________________________________________
      //___________________________________________________________________________________________________________
      //______________________________________________________________________________________________________________
      //__________________________________________________________________________
      //________________________________________________________________________________________________________________________________
      //________________________________________
      //________________________________________
      //_______________________________________________________________________________________
      //_________________________________________________
      //____________________

      //___________________________________________________________________________
      //___________________________________________________________________
      //__________________________________________________________________________________________________________________________________________
      //________________________
      //______________________________________________________________________________________________________________________________________________________________________________
      //_________________________________________________________________________________________________________________________
      //____________________________________________________________________________________________________________________________________________________________
      //______________________________________________________________________________________________________
      //____________________________________________________________________________________________________
      //_______________________________________________________________________________________________________________________
      //__________________________________________________________________________________________________________________________________________________________
      //________________________________________________________________________________________
      //_________________________________________________________________________________________________________________________________________
      //________________________
      //____________________________________________________________________________________________________________________________________
      //_______________________________________________________________________________________
      //_____________________________________________________________________________________________
      //_________________________________________________________________________________________________________
      //________________________________________________________________
      //_____________________________________________________________________
      //_____________________________________________________________________________________________________________________________________________
      //________________________________________________________________
      //________________________
      //___________________________________________________________________________________________________________________________________
      //__________________________________________________________________________________________________
      //________________________________________________________________________________________________________________
      //__________________________________________________________________________________________________________________________________________________________
      //__________________________________________________________________
      //_______________________________________________________________________
      //________________________________________________________________________________________________________________________________________________
      //___________________________________________________________________________________
      //______________________________________________________________________________________________________________________________________________________________________
      //_______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
      //________________________________________________________________________________________________________________________________________
      //__________________________________________________________________________________________________________
      //___________________________________________________________________________________________________________________________________________________________________________________
      //__________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
      //____________________________________________________________________
      //_________________________________________________________________________
      //_____________________________________________________________________________________________________________________
      //______________________________________________________________________________________________
      //____________________________________________________________________
      //__________________________________________________________________
      //________________________________________________________________
      //________________________________________________________________

      //_________________________________________________________________________________________________________________________________________
      //___________________________________________________________________
      //_______________________________________________________________________________________________________________
      //___________________________________________________________________________________________________________________________________________________________________
      //______________________________
      //_________________________________________________________________________________________________
      //____________________________________________________________________________________________________________________
      //______________________________________________________________________________________________________________
      //___________________________________________________________________________________________
      //________________________________________________________________________________________________________________
      //___________________________________________________________________

      //_______________________________________________________________________________________
      executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("thd-kConsumer_poll-%d").build());
      executor.scheduleWithFixedDelay(new Runnable()
        {
          long sn_poll = 0; //___________________________________________________________________
          //_____________________________________________________________________________________

          @Override
          public void run() {
            sn_poll++;
            //___________________________________________________________________________________________________________________________________________________
            if (!det_Pause) { System.out.println(">> polling NodeEvent - " + sn_poll + " :: " + dbName); }

            //_____________________________________________________________________________________________________________
            //________________________________________________________
            //_________________________________________________________________________________
            ConsumerRecords<String, ? extends NodeEvent> records = null;
            try {
              det_PollBlocking = true;
              records          = consumer.poll(Duration.ofMillis(4000));
              det_PollBlocking = false;
              kPartition_asg   = consumer.assignment();
              //___________________________________
              //______________________________________________________
              //_________________________________________________________________________________________
              //_______________________
              //________________________________________________________________________
              //__________________________________________________________________
            } catch (WakeupException e) {
              System.out.println("Seems we are shutting down :: " + e);
              //____________________________________
              //__________________________________
              //___________________________________________________________________________________
              consumer.close();
            }
            if (records.count() == 0) {
              System.out.println("poll() timeouted, cuz Msg Queue is Empty :: " + consumer);
            }
            else {
              for (ConsumerRecord<String, ? extends NodeEvent> record : records) {
                final NodeEvent event = record.value();
                Platform.runLater(() -> {
                  nodeReceiver_ConvertHandler.determine_EventType_and_handle_EventCorrespondingly(event);
                  //___________________________________________________________________________________
                });
                //______________________________________________________________________________________
                windowSession_corr.drawAndChatVm.nodeSender_ConvertWrapper.nodeSender_EventAnnouncer.persist_InSingleThreadExecutor(event.nodeMain, event);
              }
            }
          }
        }, 0, interval_Poll, TimeUnit.MILLISECONDS);
    }
    else {
      System.out.println(">> listen_forGeneralNodeEvent() :: det_DisableReceiveNetworkMsg_Kafka :: " + StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka);
    }
  }

  //___________________________________________________
  //___________________________________________________________________________________________________________________
  //_______
  //
  //___
  //____________________________________________________________
  //________________________________
  //___
  //_____________________________________________________________________________________________________________
  //_
  //_
  //___
  //_________________________________________________________
  //___
  //___________________________________________________________________________________________________________________________________________________________________________________
  //_
  //_______________________________________________________________________________________________________________________________________
  //___
  //_________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //_
  //___________________________________________________________________________________________________________________________________________
  //_
  //______________________________________________________________________________________________________________________________
  //___
  //___________________________________________________________________________
  //_
  //___
  //________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //___
  //_____________________________________________________________________________________________________________
  //_
  //_
  //________________________________________________
  //_

  //_______________________________________________________________________________________________________________________________________________
  //____________________________________________________
  //_______________________________________________________________________________________________________________________________________________________________
  //_________________________________________________________________________________________________________________________________
  //_______________________________________________________________________________________________________________________________________________________________________________
  //_____________________________________________________________________________________________________________________________________________________________
  //_________________________________________________________________________________
  //__________________________________________________________________
  //_________________________________________________
  //_____________________________________________________
  //_________________________________________________
  //______________________________________________________________________________
  //______________________________________________________
  //_____________________________________________________________
  //__________________________________________________________________________________________
  //________________________________________________________________________
  //________________________________________________________________________
  //________________________________________________________________________________________________________________________________________________
  //________________________________________________________________________
  //______________________________________________________
  //________________________________________________________________________________________________________________________________
  //_________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //_________________________________________________
  //____________________________________________________________________________________________________________________________________________________________________________
  //___________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //_____________________________________________________________________________________________
  //______________________________________________________________________________________________________
  //_____________________________________________________________________________________________________________
  //_________________________________________________________________________________________________________
  //___________________________________________________________________________________________________
  //____________________________________________________________________________________
  //__________________________________________________________________________________________________________________________________________
  //_____________________________________________________________________________________________________
  //_______________________________________________________________________________________________
  //_______________________________________________________________________
  //___________________________________________________________________
  //____________________________________________________________________________________________
  //_____________________________________________________
  //____________________________________________________________________________
  //__________________________________________________________________
  //_____________________________________________________
  //__________________________________________________________
  //__________________________________________________________________________________________________________________________
  //_____________________________________________________
  //___________________________________________________

  public void pause_ReceiveNetworkMessage_tog(boolean det_PauseReceiveNetworkMessage) {
    if (det_PauseReceiveNetworkMessage) {
      det_Pause = true;
      //_________________________________________________________
      System.out.println("Pausing kConsumer - " + dbName + " :: " + kPartition_asg + " :: " + kPartition_asg.size());
      //_________________________________________________________________________________________________________________________________
      consumer.pause(kPartition_asg);
    }
    else {
      det_Pause = false;
      //______________________
      consumer.resume(kPartition_asg);
    }
  }

  public void shutdown_ReceiveNetworkMessage() {
    System.out.println(">> shutdown_ReceiveNetworkMessage()");
    //
    if (det_PollBlocking) {
      consumer.wakeup();
    }
    else {
      consumer.close();
    }
    //
    executor.shutdown();
    try {
      executor.awaitTermination(2500, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("<<# shutdown_ReceiveNetworkMessage()");
  }

}