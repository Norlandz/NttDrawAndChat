package com.redfrog.note;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.redfrog.note.communication.KafkaConfig;
import com.redfrog.note.controller.NodeEventController;
import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.session.DbOccupation;
import com.redfrog.note.session.StaticValueDebug;
import com.redfrog.note.session.StaticValueDebug_Mysql;
import com.redfrog.note.session.WindowSession;
import com.redfrog.note.session.WindowSessionExecutor;
import com.redfrog.note.util.JavafxUtil;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DrawAndChatVm_LoadSaveFile implements WindowSessionExecutor {

  private final WindowSession windowSession_corr;
  //________________________________________________________________________

  private final KafkaConfig kafkaConfig_corr;
  private final String kTopicName_Send_NodeEvent_All;
  private final String kPartitionOrderKey_OrderAllNodeEvent;

  //___________________________________________________

  public DrawAndChatVm_LoadSaveFile(WindowSession windowSession_corr) {
    this.windowSession_corr                   = windowSession_corr;
    //____________________________________________________________________________________________________

    this.kafkaConfig_corr                     = windowSession_corr.kafkaConfig;
    this.kTopicName_Send_NodeEvent_All        = windowSession_corr.kafkaConfig.kTopicName_Send_NodeEvent_All;
    this.kPartitionOrderKey_OrderAllNodeEvent = windowSession_corr.kafkaConfig.kPartitionOrderKey_OrderAllNodeEvent;

    //_________________________________________________________________________________________________
  }

  @Override
  public WindowSession getWindowSession() { return windowSession_corr; }

  //_______________________

  public static String initSaveFile(String dbName_drcFileSave, String path_drcFileSave) {
    if (!StaticValueDebug.det_DisableLoadSaveFile_Mysql) {
      //______________________________________________________________________________________________________
      //___________________________________________________________________________________________________

      //________________________________________________________________________________________________________________________
      //___________________________________________________________________________________________________________________________________________
      //
      //____________________________________________________________________________________________________________________________________________
      //___________________________________________________________________________________________________________________________________________________

      //________________________________________________________________________________________
      //____________________________________________________________________________________
      //__________________________________________________________________________________________
      //_________________________________________________________________________________________________________________________
      //_____________________________________________________________________________________________________________
      //_______________________________________________________________________________________________________________________

      //___________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
      //______________________________________________________________________________________________________________________________________________________________________________________________________________________________________
      //_________________________________________________________________
      //___________________________________________________________________________________

      //__________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________

      //___
      //_________________________________________________
      //___
      //_____________________________________________________________________________________________________________________
      //___________________________________________________________________________________________________________

      System.out.println(DrawAndChatAppSessionInit.Text_SaveLoadFile_NotReliable);
      System.out.println(DrawAndChatAppSessionInit.Text_SaveLoadFile);
      ArrayList<String> cmdArr_SaveFile = new ArrayList<>(Arrays.asList("cmd", "/c", "cmd", "/c",
                                                                        StaticValueDebug_Mysql.pathStr_mysqldump,
                                                                        "-u", StaticValueDebug_Mysql.sqlUserName,
                                                                        "-p" + StaticValueDebug_Mysql.sqlUserPassword,
                                                                        dbName_drcFileSave,
                                                                        "--default-character-set=" + StaticValueDebug_Mysql.CharSet_utf8mb4,
                                                                        ">",
                                                                        path_drcFileSave));
      System.out.println(cmdArr_SaveFile);
      String cmdStr_SaveFile = "cmd.exe /c cmd.exe /c "
                               + "\"" + StaticValueDebug_Mysql.pathStr_mysqldump + "\" "
                               + "-u " + StaticValueDebug_Mysql.sqlUserName + " "
                               + "-p" + StaticValueDebug_Mysql.sqlUserPassword + " "
                               + dbName_drcFileSave + " "
                               + "--default-character-set=" + StaticValueDebug_Mysql.CharSet_utf8mb4 + " "
                               + "> "
                               + "\"" + path_drcFileSave + "\"";
      System.out.println(cmdStr_SaveFile);
      System.out.println(DrawAndChatAppSessionInit.Java_Process_cmd_Parsing);

      ProcessBuilder processBuilder = new ProcessBuilder(cmdArr_SaveFile);
      processBuilder.redirectOutput(Redirect.INHERIT);
      processBuilder.redirectError(Redirect.INHERIT);
      try {
        Process process = processBuilder.start();
        try {
          System.out.println(">> Saving could take more than 5 seconds.");
          process.waitFor(5, TimeUnit.SECONDS);
          System.out.println("Saved <<");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      return cmdArr_SaveFile + "\n" + cmdStr_SaveFile;
    }
    else {
      //_____________
      System.out.println(">> initSaveFile() :: det_DisableLoadSaveFile_Mysql :: " + StaticValueDebug.det_DisableLoadSaveFile_Mysql);
      return null;
    }

  }

  public void initLoadFile(String path_drcFileSave, Stage stage) {

    //_________

    DbOccupation dbOccupation = windowSession_corr.dbOccupation;
    String dbName = dbOccupation.dbName.toString();

    if (!StaticValueDebug.det_DisableLoadSaveFile_Mysql) {

      System.out.println(DrawAndChatAppSessionInit.Text_SaveLoadFile_NotReliable);
      System.out.println(DrawAndChatAppSessionInit.Text_SaveLoadFile);
      ArrayList<String> cmdArr_LoadFile = new ArrayList<>(Arrays.asList("cmd", "/c", "cmd", "/c",
                                                                        StaticValueDebug_Mysql.pathStr_mysql,
                                                                        "-u", StaticValueDebug_Mysql.sqlUserName,
                                                                        "-p" + StaticValueDebug_Mysql.sqlUserPassword,
                                                                        "--database", dbName,
                                                                        "--default-character-set=" + StaticValueDebug_Mysql.CharSet_utf8mb4,
                                                                        "<",
                                                                        path_drcFileSave));
      System.out.println(cmdArr_LoadFile);
      String cmdStr_LoadFile = "cmd.exe /c cmd.exe /c "
                               + "\"" + StaticValueDebug_Mysql.pathStr_mysql + "\" "
                               + "-u " + StaticValueDebug_Mysql.sqlUserName + " "
                               + "-p" + StaticValueDebug_Mysql.sqlUserPassword + " "
                               + "--database " + dbName + " "
                               + "--default-character-set=" + StaticValueDebug_Mysql.CharSet_utf8mb4 + " "
                               + "< "
                               + "\"" + path_drcFileSave + "\"";
      System.out.println(cmdStr_LoadFile);
      System.out.println(DrawAndChatAppSessionInit.Java_Process_cmd_Parsing);

      ProcessBuilder processBuilder = new ProcessBuilder(cmdArr_LoadFile);
      processBuilder.redirectOutput(Redirect.INHERIT);
      processBuilder.redirectError(Redirect.INHERIT);
      try {
        Process process = processBuilder.start();
        try {
          System.out.println(">> Loading could take more than 20 seconds.");
          process.waitFor(20, TimeUnit.SECONDS);
          System.out.println("Loaded <<");
        } catch (InterruptedException e) {
          e.printStackTrace();
          throw new Error(e);
        }
      } catch (IOException e) {
        throw new Error(e);
      }
    }
    else {
      //_____________
      System.out.println(">> initLoadFile() :: det_DisableLoadSaveFile_Mysql :: " + StaticValueDebug.det_DisableLoadSaveFile_Mysql);
    }

    //_________

    //___________________________________________________________________
    //_______________________________________________________________________________________________________________________________________________________________________________
    AnchorPane pane_JavafxRoot = new AnchorPane();
    //_______________________________________________________________
    //_____________________________________________________________
    windowSession_corr.pane_JavafxRoot = pane_JavafxRoot;

    stage.setTitle("3 " + windowSession_corr.sessionNewOrLoadOrReceive + " :: " + dbName);
    stage.setScene(new Scene(pane_JavafxRoot, 650, 450));
    windowSession_corr.listen_ConfirmClose(stage);
    stage.show();

    //_________

    if (!StaticValueDebug.det_DisableLoadSaveFile_Mysql) {
      String queryStr = "select nn from " + NodeEventController.NodeEvent + " nn order by nn.idSql"; //_________________________________________

      EntityManager em = dbOccupation.em; //_______________________________________________________________________________________________________________________
      TypedQuery<NodeEvent> query = em.createQuery(queryStr, NodeEvent.class);
      List<NodeEvent> resultList = query.getResultList();

      //___
      //______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
      //___
      //___________
      //___________________________________________________________________________
      //
      //___________________________________________________
      //___________________________________________________________________________________
      //______________________________________________________________
      //_
      //_________________________________________________________________________________________________________________________

      //________________________________________________________________
      //____________________________
      for (NodeEvent nodeEvent : resultList) {
        System.out.println("Loading :: " + nodeEvent);
        //_______________________________________________________________________
        //______________________________________________________________________________________________________________________________________________________
        //_____________________________________________________________________________________________________________________________________________________________
        //__________________________________________________________________________________________________________________________
        //__________________________________________________________________________________________________________________________________________
        //___________________________________________
        //_______________________________________________________________________________________________________________
        windowSession_corr.drawAndChatVm.nodeReceiver_EventListener.nodeReceiver_ConvertHandler.determine_EventType_and_handle_EventCorrespondingly((NodeEvent) nodeEvent);

        if (!StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka) {
          //_________________________________
          final Producer<String, NodeEvent> producer = kafkaConfig_corr.kProducer_NodeEvent_LoadSaveFile;
          ProducerRecord<String, NodeEvent> producerRecord = new ProducerRecord<>(kTopicName_Send_NodeEvent_All, kPartitionOrderKey_OrderAllNodeEvent, nodeEvent);
          producer.send(producerRecord);
          producer.flush();
        }
        else {
          System.out.println(">> initLoadFile() > Send_Kafka :: det_DisableReceiveNetworkMsg_Kafka :: " + StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka);
        }

      }
    }
    else {
      //_____________
      System.out.println(">> initLoadFile() :: det_DisableLoadSaveFile_Mysql :: " + StaticValueDebug.det_DisableLoadSaveFile_Mysql);
    }
    //_________

  }

}