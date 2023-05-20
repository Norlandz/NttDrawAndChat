package com.redfrog.note.session;

import java.util.ArrayList;
import java.util.HashMap;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.redfrog.note.DrawAndChatAppSessionInit;
import com.redfrog.note.communication.KafkaConfig;
import com.redfrog.note.database.DbThreadHelper;
import com.redfrog.note.exception.TypeError;
import com.redfrog.note.session.DbOccupation.DbName;

import javafx.stage.Stage;

//________________________________________________
//________________________________________________________

@Component
public class SessionManager {

  //____________
  //________________________
  public EntityManager em_01;
  //
  //____________
  //________________________
  public EntityManager em_02;

  @Autowired
  public DbThreadHelper dbThreadHelper;

  public WindowSession windowSession_curr;

  public final ArrayList<WindowSession> arr_windowSession = new ArrayList<>();
  public final HashMap<Stage, WindowSession> mpp_jfxStage_vs_windowSession = new HashMap<>();

  private DbOccupation[] arr_dbOccupation;

  @Autowired
  private StaticValueDebug staticValueDebug;

  public SessionManager(
                        @Autowired @Qualifier("lcemf_01") EntityManager em_01,
                        @Autowired @Qualifier("lcemf_02") EntityManager em_02) {
    System.out.println(">> public SessionManager( " + em_01);
    System.out.println(">> public SessionManager( " + em_02);

    this.em_01       = em_01;
    this.em_02       = em_02;

    arr_dbOccupation = new DbOccupation[] { //
        new DbOccupation(DbName.db_drawandchat_01, em_01, null), //
        new DbOccupation(DbName.db_drawandchat_02, em_02, null), //
        //_________________________________________________________________
        //_________________________________________________________________
    };
    //________________________________________________________
    //_____________________________________________________
    //____________________________________________________

    System.out.println("> make sure the static @Value is loaded: " + staticValueDebug + " :: " + StaticValueDebug.det_DisableLoadSaveFile_Mysql); //____________________________________________________________
  }

  public DbOccupation occupy_AvailableDb(WindowSession windowSession) {
    for (DbOccupation dbOccupation : arr_dbOccupation) {
      if (dbOccupation.windowSession == null) {
        dbOccupation.windowSession = windowSession;
        return dbOccupation;
      }
    }
    throw new Error("No more Available Db :: " + arr_dbOccupation);
  }

  //_______________________________________________________________________________
  //________________________________________________________
  //_______________________________________________
  //_______________________________________________________________
  //________________________
  //___

  public DbOccupation occupy_Db(WindowSession windowSession, DbName dbName) {
    if (dbName == DbName.AutoPick_UnusedDb_InThisGrandAppSession) {
      return occupy_AvailableDb(windowSession);
    }
    else {
      for (DbOccupation dbOccupation : arr_dbOccupation) {
        if (dbOccupation.dbName == dbName) {
          dbOccupation.windowSession = windowSession;
          System.out.println("Debug force using :: " + dbOccupation);
          return dbOccupation;
        }
      }
      throw new Error("No Such Db :: " + dbName);

    }
  }

  public final static boolean det_PersistLess_DbEventLessSyncWithNetworkEvent = true;

  //_________

  public static void listen_ChangeWindowSessionCurr_whenWindowIsFocused(Stage stage, SessionManager sessionManager) {
    stage.focusedProperty().addListener((ov, oldValue, det_Focus) -> {
      if (det_Focus) {
        WindowSession windowSession_curr_L = sessionManager.mpp_jfxStage_vs_windowSession.get(stage);
        sessionManager.windowSession_curr = windowSession_curr_L;
        System.out.println("Focusing on window :: " + windowSession_curr_L + " :: " + windowSession_curr_L.dbOccupation);
      }
    });

    //__________________________________________________________________
  }

  //_______________________________________________________________________________________________________________________
  //_______________________________________________________________________________________________
  //____________________________
  //__________________________________________________________
  //_______________________________________
  //_____
  //_______________________________________________________________
  //_______________________________________
  //_____
  //__________
  //____________________________________________________________________________________________________________________________________________________________________________________________________
  //_____
  //
  //_____________________
  //___

  //___________________________________________________________________
  //_________________________________________________________________________
  //_________________________________________________________________________________
  //________________________________________________________________________________
  //______________________________________
  //_______
  //_____
  //____________________________________________________________________________
  //___

  //_________________________________________________________________________________
  //___________________________________________________________
  //_______________________________________________
  //_________________________________
  //_______
  //_____
  //________________
  //___

  //_________

  public enum SessionNewOrLoadOrReceive {
    NewUseSend,
    ReceiveNetworkMsg,
    LoadSaveFile,
  }

  public enum MsgConnRole {
    Host,
    Guest,
  }

  public static WindowSession setup_WindowSession(SessionManager sessionManager, Stage stage, SessionNewOrLoadOrReceive sessionNewOrLoadOrReceive, boolean det_AllowDbOverwriteOrAppend, DbName dbName) {
    System.out.println(">> setup_WindowSession " + sessionManager.em_01);
    System.out.println(">> setup_WindowSession " + sessionManager.em_02);
    if (sessionNewOrLoadOrReceive == SessionNewOrLoadOrReceive.NewUseSend) {
      WindowSession windowSession = new WindowSession(sessionNewOrLoadOrReceive, MsgConnRole.Host);
      sessionManager.arr_windowSession.add(windowSession);

      if (!StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka) {
        windowSession.kafkaConfig.init(StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Host, StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Guest);
        System.out.println("Using Topic :: " + StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Host + " :: " + StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Guest);
        //___________________________________________________________________________________________________________________________
        //________________________________________________________________________________________________________________
      }
      else {
        System.out.println(">> setup_WindowSession() :: det_DisableReceiveNetworkMsg_Kafka :: " + StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka);
      }

      DbOccupation dbOccupation = sessionManager.occupy_Db(windowSession, dbName);
      windowSession.dbOccupation = dbOccupation;

      windowSession.javafxStage  = stage;
      sessionManager.mpp_jfxStage_vs_windowSession.put(stage, windowSession);

      sessionManager.windowSession_curr = windowSession;
      windowSession.sessionManager      = sessionManager;
      SessionManager.listen_ChangeWindowSessionCurr_whenWindowIsFocused(stage, sessionManager);

      //__________
      if (dbOccupation.weak_check_IfOldDataExist()) {
        //______________________________________________________________________________________________________________________
        //_________________________________________________________________________________________________________________________________________________________
        //_______________________________________________________________________
        //_______________________________________________________________________________________________
        //__________________________________________________________________________________________________________________________
        //______________________________________________________________________________________________________________________________________
        System.out.println(DrawAndChatAppSessionInit.TextWarn_OverwriteOrAppendDb + " :: " + dbOccupation.dbName);
        System.err.println(DrawAndChatAppSessionInit.TextWarn_OverwriteOrAppendDb + " :: " + dbOccupation.dbName);

        if (!det_AllowDbOverwriteOrAppend) {
          throw new Error();
        }
        else {
          //_____________________
        }
      }

      return windowSession;
    }
    else if (sessionNewOrLoadOrReceive == SessionNewOrLoadOrReceive.ReceiveNetworkMsg) {
      WindowSession windowSession = new WindowSession(sessionNewOrLoadOrReceive, MsgConnRole.Guest);
      sessionManager.arr_windowSession.add(windowSession);

      if (!StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka) {
        windowSession.kafkaConfig.init(StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Guest, StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Host);
        System.out.println("Using Topic :: " + StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Guest + " :: " + StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Host);
      }
      else {
        System.out.println(">> setup_WindowSession() :: det_DisableReceiveNetworkMsg_Kafka :: " + StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka);
      }

      DbOccupation dbOccupation = sessionManager.occupy_Db(windowSession, dbName);
      windowSession.dbOccupation = dbOccupation;

      windowSession.javafxStage  = stage;
      sessionManager.mpp_jfxStage_vs_windowSession.put(stage, windowSession);

      sessionManager.windowSession_curr = windowSession;
      windowSession.sessionManager      = sessionManager;
      SessionManager.listen_ChangeWindowSessionCurr_whenWindowIsFocused(stage, sessionManager);

      //__________
      if (dbOccupation.weak_check_IfOldDataExist()) {
        //______________________________________________________________________________________________________________________
        //_________________________________________________________________________________________________________________________________________________________
        //________________________________________________________________________
        System.out.println(DrawAndChatAppSessionInit.TextWarn_OverwriteOrAppendDb + " :: " + dbOccupation.dbName);
        System.err.println(DrawAndChatAppSessionInit.TextWarn_OverwriteOrAppendDb + " :: " + dbOccupation.dbName);

        if (!det_AllowDbOverwriteOrAppend) {
          throw new Error();
        }
        else {
          //_____________________
        }
      }

      return windowSession;

    }
    else if (sessionNewOrLoadOrReceive == SessionNewOrLoadOrReceive.LoadSaveFile) {
      WindowSession windowSession = new WindowSession(sessionNewOrLoadOrReceive, MsgConnRole.Host);
      sessionManager.arr_windowSession.add(windowSession);

      if (!StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka) {
        windowSession.kafkaConfig.init(StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Host, StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Guest);
        System.out.println("Using Topic :: " + StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Host + " :: " + StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Guest);
      }
      else {
        System.out.println(">> setup_WindowSession() :: det_DisableReceiveNetworkMsg_Kafka :: " + StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka);
      }

      //____________________________________________
      //_________________________________________
      //________________________________________________________________________
      //_______
      //____________
      //___________________________________________________________________________________________
      //_______
      DbOccupation dbOccupation = sessionManager.occupy_Db(windowSession, dbName);
      windowSession.dbOccupation = dbOccupation;

      windowSession.javafxStage  = stage;
      sessionManager.mpp_jfxStage_vs_windowSession.put(stage, windowSession);

      sessionManager.windowSession_curr = windowSession;
      windowSession.sessionManager      = sessionManager;
      SessionManager.listen_ChangeWindowSessionCurr_whenWindowIsFocused(stage, sessionManager);

      //__________
      if (dbOccupation.weak_check_IfOldDataExist()) {
        //_____________________________________________________________________________________
        //________________________________________________________________________________________________________________________________
        //_______________________________________________________________________________________________________
        //_______________________________________________________________________________________________________________________
        //______________________________________________________________________________________
        //_________________________________________________________________________________________________________________________________________________________
        //__________________________________________________________________________________________________________
        //__________________________________________________________________________________
        //____________________________________________________________________________________________________________________________________
        System.out.println(DrawAndChatAppSessionInit.TextWarn_OverwriteOrAppendDb + " :: " + dbOccupation.dbName);
        System.err.println(DrawAndChatAppSessionInit.TextWarn_OverwriteOrAppendDb + " :: " + dbOccupation.dbName);

        if (!det_AllowDbOverwriteOrAppend) {
          throw new Error();
        }
        else {
          //_____________________
        }
      }

      return windowSession;
    }
    else {
      throw new TypeError();
    }
  }

}
