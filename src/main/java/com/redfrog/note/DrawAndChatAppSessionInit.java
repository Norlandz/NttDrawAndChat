package com.redfrog.note;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.output.TeeOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.redfrog.note.DrawAndChatAppJavafxBoot.StageReadyEvent;
import com.redfrog.note.communication.KafkaConfig;
import com.redfrog.note.database.DbThreadHelper;
import com.redfrog.note.exception.TypeError;
import com.redfrog.note.session.DbOccupation.DbName;
import com.redfrog.note.session.SessionManager;
import com.redfrog.note.session.SessionManager.MsgConnRole;
import com.redfrog.note.session.SessionManager.SessionNewOrLoadOrReceive;
import com.redfrog.note.util.KafkaUtil;
import com.redfrog.note.session.StaticValueDebug;
import com.redfrog.note.session.StaticValueDebug_Kafka;
import com.redfrog.note.session.StaticValueDebug_Mysql;
import com.redfrog.note.session.WindowSession;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Component
public class DrawAndChatAppSessionInit implements ApplicationListener<StageReadyEvent> {

  @Autowired
  public SessionManager sessionManager;

  //__________________________________________

  public final static String Font_Consolas = "Consolas";

  private static final boolean det_EnableRedirectOutputStream_FromConsoleToTextArea = true;
  //____________________________________________________________________________
  @Value("${cpy.rf.ntt.drc.det-WhenRedirectOutputStream-RedirectStderrToo}")
  private Boolean det_WhenRedirectOutputStream_RedirectStderrToo; //______________________________________________

  @Autowired
  private DbThreadHelper dbThreadHelper;

  @Autowired
  Environment environment;

  //_____________

  //_______________
  //____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  public static final String Text_TextAreaConsole =
"""
You should only click *once* -- select only 1 session out of 3: [NewUseSend, ReceiveNetworkMsg, LoadSaveFile] 
- (unless you are debugging)
- After one session is init-ed, a Database will be occupied, for Load Save File functionality
""";

  public static final String Text_SaveLoadFile_NotReliable = 
"""
The functionality of Save Load file is supported poorly at current stage. 
Do not rely on this -- you may or may not experience datalost. 
""";
  
  public static final String Text_SaveLoadFile = 
"""
The *internal* of how save & load works is::
  mysqldump -u USERNAME -pPASSWORD DBNAME > /path/to/location/backup.sql
  mysql -u USERNAME -pPASSWORD --database DBNAME < /path/to/location/backup.sql
see https://stackoverflow.com/questions/4283168/how-can-i-backup-a-mysql-database-from-java
""";

  public static final String Text_ClearAllTableRows_InGivenDb = 
"""
Make sure you are using an *Empty* database for a Session (that you are going to init) 
--ie: *clear* the data in the database every time before you init a Session.
      (for New Session / Receive Session only; Load Session will automatically overwrite the existing database)
Otherwise, Existing Old data will be _ cleared / mixed with New Data _ for the file in this Session 
-- the Saved file will be in a mess (Unless you dont care about saving the file). 
""";
  
  public static final String Text_ClearAllMsg_InKafkaTopic = 
"""
If there are old Kafka Event *remained* in the Message Queue (Kafka Topic) (-- didnt get consumed)
You will need to Clear those Msg in Kafka Topic --> otherwise the Receiver will have Old & New Msg mix together
""";

  public final static String Java_Process_cmd_Parsing = 
"""
[Warning 2%] Note that: Java Process cmd Parsing may not implemented well -- when space are involved (but should be fine in this App).
https://stackoverflow.com/questions/66045955/java-processbuilder-in-windows-spaces-in-exe-path-and-in-argument/76193225
https://stackoverflow.com/questions/15464111/run-cmd-commands-through-java/76157848
""";

  public static final String TextWarn_OverwriteOrAppendDb =
"""
[Warning: 80% - @datalost] det_ExistDataIn_EntityGeneral != 0; for db :: %s
Detected Existing Old Data in this Db.
"""
+ Text_ClearAllTableRows_InGivenDb +
""" 
Basing on your selection on det_AllowDbOverwriteOrAppend :: %s
If true: 
-> New Data will be Appended to the Existing Old Data - for New session / Receive session. 
-> Existing Old Data will be Cleared, New Data will be Written to an empty/cleared database - for Load session. 
If false: 
-> Throw an Error, Session Abort, No data are cleared or written. 
""";


  public static final String Text_CloseConfirm = 
"""
Close confirm?

Sql em.persist() is slow & may still be going in the background (just for Saving your file)
-- can take 10s ~ 40s+ ~ ?s to finish all persist. 
You need to wait for that to be finished. 
Check the Log to see if its done.
""";

  public static final String Text_AutoClose = 
"""
Auto Close When Sql persist is Done 
(If persist is still not done after ~3min, will force shutdown & experience datalost) 
(Normal pick)
""";

  public static final String Text_ForceClose = 
"""
Force Close Right Now
(If you dont care about Saving the file)
""";

  public static final String Text_CloseWait = 
"""
Please wait for Sql Persist

In the meantime, dont do more actions. 
- eg: click inside on the Closing Window. 
- eg: start another session. 
- eg: close this window. 
Just wait only (& watch the log if needed).
If persist is still not done after ~3min, will force shutdown & experience datalost 
If the process is taking too long, you may Force Terminate it, but will experience datalost.
""";
//___________
  
//______________

  //_____________

  @Override
  public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
    System.out.printf("Port: %s=%s %s=%s %n",
                      "local.server.port", environment.getProperty("local.server.port"),
                      "server.port", environment.getProperty("server.port"));

    Stage primaryStage = stageReadyEvent.getStage();

    double width = 600;

    //_______________________________________________________
    VBox root = new VBox();
    ScrollPane root_ScrollPane = new ScrollPane(root);
    //________________________________________
    //_________________________________________
    //_________________________
    //__________________________
    //____________________________________
    //_____________________________________________________
    //_
    //______________________________________
    //_
    //_________________________________
    //__________________

    //_____
    final CheckBox cb_det_AllowDbOverwriteOrAppend_New = new CheckBox("det_AllowDbOverwriteOrAppend (debug)"); //_________________________
    cb_det_AllowDbOverwriteOrAppend_New.setSelected(true);
    cb_det_AllowDbOverwriteOrAppend_New.setMnemonicParsing(false);
    //__________________________________________________________________________________________________
    //____________________________________________________
    //____________________________________________________________
    final ChoiceBox<DbName> cb_ForceUseGivenDb_New = new ChoiceBox<>();
    cb_ForceUseGivenDb_New.getItems().addAll(DbName.db_drawandchat_01, DbName.db_drawandchat_02, DbName.AutoPick_UnusedDb_InThisGrandAppSession);
    cb_ForceUseGivenDb_New.getSelectionModel().select(0);
    HBox hbox_ForceUseGivenDb_New = new HBox(new Text("Force use given db: "), cb_ForceUseGivenDb_New);

    Button button_New = new Button();
    button_New.setText(SessionNewOrLoadOrReceive.NewUseSend + " - " + MsgConnRole.Host);
    button_New.setMnemonicParsing(false);
    button_New.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
      if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
        //__________________________________________________________________________________________________________________________
        System.out.println(">> clicked :: " + SessionNewOrLoadOrReceive.NewUseSend + " :: " + Instant.now());
        //__________________________________
        //__________________________________________________________________________________________________________________
        //______________________________________
        init_Session_NewUseSend(cb_det_AllowDbOverwriteOrAppend_New.isSelected(), cb_ForceUseGivenDb_New.getSelectionModel().getSelectedItem());
      }
    });
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(5);
    dropShadow.setOffsetX(0);
    dropShadow.setOffsetY(0);
    dropShadow.setColor(Color.LIGHTGREEN);
    button_New.setEffect(dropShadow);

    //_____
    final CheckBox cb_det_AllowDbOverwriteOrAppend_Receive = new CheckBox("det_AllowDbOverwriteOrAppend (debug)"); //_________________________
    cb_det_AllowDbOverwriteOrAppend_Receive.setSelected(true);
    cb_det_AllowDbOverwriteOrAppend_Receive.setMnemonicParsing(false);
    final ChoiceBox<DbName> cb_ForceUseGivenDb_Receive = new ChoiceBox<>();
    cb_ForceUseGivenDb_Receive.getItems().addAll(DbName.db_drawandchat_01, DbName.db_drawandchat_02, DbName.AutoPick_UnusedDb_InThisGrandAppSession);
    cb_ForceUseGivenDb_Receive.getSelectionModel().select(1);
    HBox hbox_ForceUseGivenDb_Receive = new HBox(new Text("Force use given db: "), cb_ForceUseGivenDb_Receive);

    Button button_Receive = new Button();
    button_Receive.setText(SessionNewOrLoadOrReceive.ReceiveNetworkMsg + " - " + MsgConnRole.Guest);
    button_Receive.setMnemonicParsing(false);
    button_Receive.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
      if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
        System.out.println(">> clicked :: " + SessionNewOrLoadOrReceive.ReceiveNetworkMsg + " :: " + Instant.now());
        init_Session_ReceiveNetworkMsg(cb_det_AllowDbOverwriteOrAppend_Receive.isSelected(), cb_ForceUseGivenDb_Receive.getSelectionModel().getSelectedItem()); //
      }
    });

    //_____
    Button button_PauseReceiveNetworkMessage = new Button();
    button_PauseReceiveNetworkMessage.setText("Pause Receive Network Message (for current (/ last) focusing window)");
    button_PauseReceiveNetworkMessage.setMnemonicParsing(false);
    button_PauseReceiveNetworkMessage.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
      if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
        System.out.println(">> clicked :: " + "Pause Receive Network Message" + " :: " + Instant.now());
        pause_ReceiveNetworkMessage_tog(sessionManager.windowSession_curr, true);
      }
    });
    Button button_ResumeReceiveNetworkMessage = new Button();
    button_ResumeReceiveNetworkMessage.setText("Resume Receive Network Message (for current (/ last) focusing window)");
    button_ResumeReceiveNetworkMessage.setMnemonicParsing(false);
    button_ResumeReceiveNetworkMessage.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
      if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
        System.out.println(">> clicked :: " + "Resume Receive Network Message" + " :: " + Instant.now());
        pause_ReceiveNetworkMessage_tog(sessionManager.windowSession_curr, false);
      }
    });

    //_____
    final TextArea textArea_path_drcFileSave_Load = new TextArea();
    textArea_path_drcFileSave_Load.setPrefSize(width, 25);
    textArea_path_drcFileSave_Load.setWrapText(true);
    textArea_path_drcFileSave_Load.setFont(Font.font(Font_Consolas));
    //________________________________________________________
    //______________________________________________________________________________________________________________________________________________________________________________
    textArea_path_drcFileSave_Load.appendText(StaticValueDebug_Mysql.pathStr_drcFileSave + "\\" + "DrawAndChat_test.sql");

    final CheckBox cb_det_AllowDbOverwriteOrAppend_Load = new CheckBox("det_AllowDbOverwriteOrAppend (debug)"); //_________________________
    cb_det_AllowDbOverwriteOrAppend_Load.setSelected(true);
    cb_det_AllowDbOverwriteOrAppend_Load.setMnemonicParsing(false);
    final ChoiceBox<DbName> cb_ForceUseGivenDb_Load = new ChoiceBox<>();
    cb_ForceUseGivenDb_Load.getItems().addAll(DbName.db_drawandchat_01, DbName.db_drawandchat_02, DbName.AutoPick_UnusedDb_InThisGrandAppSession);
    cb_ForceUseGivenDb_Load.getSelectionModel().select(1);
    HBox hbox_ForceUseGivenDb_Load = new HBox(new Text("Force use given db: "), cb_ForceUseGivenDb_Load);

    Button button_Load = new Button();
    button_Load.setText(SessionNewOrLoadOrReceive.LoadSaveFile + " - " + MsgConnRole.Host);
    button_Load.setMnemonicParsing(false);
    button_Load.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
      if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
        System.out.println(">> clicked :: " + SessionNewOrLoadOrReceive.LoadSaveFile + " :: " + Instant.now());
        init_Session_LoadSaveFile(textArea_path_drcFileSave_Load.textProperty().getValue(), cb_det_AllowDbOverwriteOrAppend_Load.isSelected(), cb_ForceUseGivenDb_Load.getSelectionModel().getSelectedItem()); //
      }
    });

    //_____
    final TextArea textArea_path_drcFileSave_Save = new TextArea();
    textArea_path_drcFileSave_Save.setPrefSize(width, 25);
    textArea_path_drcFileSave_Save.setWrapText(true);
    textArea_path_drcFileSave_Save.setFont(Font.font(Font_Consolas));
    //_____________________________________________________________________________________________
    textArea_path_drcFileSave_Save.appendText(StaticValueDebug_Mysql.pathStr_drcFileSave + "\\" + "DrawAndChat_test.sql");

    Button button_Save = new Button();
    button_Save.setText("Save file (for current (/ last) focusing window)"); //___________________________________
    button_Save.setMnemonicParsing(false);
    button_Save.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
      if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
        System.out.println(">> clicked :: " + ">> Saving" + " :: " + Instant.now());
        String dbName_drcFileSave = sessionManager.windowSession_curr.dbOccupation.dbName.toString();
        String cmdArr_cmdStr_SaveFile = DrawAndChatVm_LoadSaveFile.initSaveFile(dbName_drcFileSave, textArea_path_drcFileSave_Save.textProperty().getValue());
        System.out.println(cmdArr_cmdStr_SaveFile + " :: " + Instant.now());
        System.out.println(">> clicked :: " + "Saved <<" + " :: " + Instant.now());
      }
    });

    //_____
    final ChoiceBox<DbName> cb_ClearAllTableRows_InGivenDb = new ChoiceBox<>();
    cb_ClearAllTableRows_InGivenDb.getItems().addAll(DbName.NA, DbName.db_drawandchat_01, DbName.db_drawandchat_02);
    cb_ClearAllTableRows_InGivenDb.getSelectionModel().select(0);

    Button button_ClearAllTableRows_InGivenDb = new Button();
    button_ClearAllTableRows_InGivenDb.setText("ClearAllTableRows_InGivenDb");
    button_ClearAllTableRows_InGivenDb.setMnemonicParsing(false);
    button_ClearAllTableRows_InGivenDb.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
      if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
        System.out.println(">> clicked :: " + ">> ClearAllTableRows_InGivenDb" + " :: " + Instant.now());
        if (!StaticValueDebug.det_DisableLoadSaveFile_Mysql) {
          dbThreadHelper.clear_TabelRows(cb_ClearAllTableRows_InGivenDb.getSelectionModel().getSelectedItem());
        }
        else {
          //_____________
          System.out.println(">> clear_TabelRows() :: det_DisableLoadSaveFile_Mysql :: " + StaticValueDebug.det_DisableLoadSaveFile_Mysql);
        }
        System.out.println(">> clicked :: " + "<<# ClearAllTableRows_InGivenDb" + " :: " + Instant.now());
      }
    });

    Text text_ClearAllTableRows_InGivenDb = new Text(Text_ClearAllTableRows_InGivenDb);

    //_____
    final ChoiceBox<String> cb_ClearAllMsg_InKafkaTopic = new ChoiceBox<>();
    String na_MsgComm = "NA";
    String str_BothTopicName = StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Host + " & " + StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Guest;
    cb_ClearAllMsg_InKafkaTopic.getItems().addAll(na_MsgComm, str_BothTopicName);
    cb_ClearAllMsg_InKafkaTopic.getSelectionModel().select(0);

    Button button_ClearAllMsg_InKafkaTopic = new Button();
    button_ClearAllMsg_InKafkaTopic.setText("ClearAllMsg_InKafkaTopic");
    button_ClearAllMsg_InKafkaTopic.setMnemonicParsing(false);
    button_ClearAllMsg_InKafkaTopic.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
      if (!event.isControlDown() && !event.isAltDown() && !event.isShiftDown() && event.isPrimaryButtonDown()) {
        System.out.println(">> clicked :: " + ">> ClearAllMsg_InKafkaTopic" + " :: " + Instant.now());
        if (!StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka) {

          String str_Select = cb_ClearAllMsg_InKafkaTopic.getSelectionModel().getSelectedItem();
          if (str_Select == na_MsgComm) {
            //_____________
            System.out.println("NA picked, do_nothing.");
          }
          else if (str_Select == str_BothTopicName) {
            final KafkaConfig kafkaConfig = new KafkaConfig();
            kafkaConfig.init(StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Host, StaticValueDebug_Kafka.kTopicName_NodeEvent_All_Prepend + MsgConnRole.Guest);
            KafkaUtil.clear_AllMsg_debug(kafkaConfig);
          }

        }
        else {
          //_____________
          System.out.println(">> clear_AllMsg() :: det_DisableReceiveNetworkMsg_Kafka :: " + StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka);
        }
        System.out.println(">> clicked :: " + "<<# ClearAllMsg_InKafkaTopic" + " :: " + Instant.now());
      }
    });

    Text text_ClearAllMsg_InKafkaTopic = new Text(Text_ClearAllMsg_InKafkaTopic);

    //_____

    AtomicBoolean det_NoAutoScroll = new AtomicBoolean(false);

    final CheckBox cb_det_NoAutoScroll = new CheckBox("det_NoAutoScroll (debug)");
    cb_det_NoAutoScroll.setMnemonicParsing(false);
    cb_det_NoAutoScroll.selectedProperty().addListener((observable, oldValue, newValue) -> {
      det_NoAutoScroll.set(newValue);
    });

    final TextArea textArea_info = new TextArea()
      {
        //____________________________________________________________________________________________
        //________________________________________________________________
        @Override
        public void appendText(String text) {
          if (!det_NoAutoScroll.get()) {
            super.appendText(text);
          }
          else {
            //______________________________________________
            //_____________________________________________________________________
            //______________________________________
            //____________________________________
            //______________________________________________________________
            int caretPosition = getCaretPosition();
            int anchorPosition = getAnchor();
            super.appendText(text);
            //_________________________________________
            selectRange(anchorPosition, caretPosition);
          }
        }

      };
    //________________________________________
    textArea_info.setMinWidth(width);
    textArea_info.setMinHeight(550);
    //____________________________________
    textArea_info.setFont(Font.font(Font_Consolas));
    textArea_info.appendText(Text_TextAreaConsole);
    //_________________________________________________________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________________________________________________________

    if (det_EnableRedirectOutputStream_FromConsoleToTextArea) {
      setup_OutputStream(textArea_info, det_WhenRedirectOutputStream_RedirectStderrToo);
      //_______________________________________
      //___________________________________________
      //________________________________________
      //___________________________________________
      //______________________________________________
    }

    //_____
    root.getChildren().add(cb_det_AllowDbOverwriteOrAppend_New);
    root.getChildren().add(hbox_ForceUseGivenDb_New);
    root.getChildren().add(button_New);
    root.getChildren().add(new Rectangle(100, 25, Color.TRANSPARENT)); //______________
    root.getChildren().add(cb_det_AllowDbOverwriteOrAppend_Receive);
    root.getChildren().add(hbox_ForceUseGivenDb_Receive);
    root.getChildren().add(button_Receive);
    root.getChildren().add(new Rectangle(100, 25, Color.TRANSPARENT));
    root.getChildren().add(button_PauseReceiveNetworkMessage);
    root.getChildren().add(button_ResumeReceiveNetworkMessage);
    root.getChildren().add(new Rectangle(100, 25, Color.TRANSPARENT));
    root.getChildren().add(textArea_path_drcFileSave_Load);
    root.getChildren().add(cb_det_AllowDbOverwriteOrAppend_Load);
    root.getChildren().add(hbox_ForceUseGivenDb_Load);
    root.getChildren().add(button_Load);
    root.getChildren().add(new Rectangle(100, 25, Color.TRANSPARENT));
    root.getChildren().add(textArea_path_drcFileSave_Save);
    root.getChildren().add(button_Save);
    root.getChildren().add(new Rectangle(100, 25, Color.TRANSPARENT));
    root.getChildren().add(new VBox(cb_ClearAllTableRows_InGivenDb, button_ClearAllTableRows_InGivenDb, text_ClearAllTableRows_InGivenDb));
    root.getChildren().add(new Rectangle(100, 25, Color.TRANSPARENT));
    root.getChildren().add(new VBox(cb_ClearAllMsg_InKafkaTopic, button_ClearAllMsg_InKafkaTopic, text_ClearAllMsg_InKafkaTopic));
    root.getChildren().add(new Rectangle(100, 50, Color.TRANSPARENT));
    root.getChildren().add(new VBox(cb_det_NoAutoScroll, textArea_info));

    Scene scene = new Scene(root_ScrollPane, width + 50, 900);
    primaryStage.setTitle("DrawAndChatApp - Session init & control Page");
    primaryStage.setScene(scene);
    primaryStage.show();

    textArea_info.prefWidthProperty().bind(scene.widthProperty()); //__________________________________________________________________
  }

  //___________________________________________________

  public void init_Session_NewUseSend(boolean det_AllowDbOverwriteOrAppend, DbName dbName) {
    Stage stage = new Stage();
    //________________________________________
    //_____________________________________________________________
    WindowSession windowSession = SessionManager.setup_WindowSession(sessionManager, stage, SessionNewOrLoadOrReceive.NewUseSend, det_AllowDbOverwriteOrAppend, dbName);
    windowSession.drawAndChatVm = new DrawAndChatVm(windowSession);
    //_____________________________________________________________________________________
    windowSession.drawAndChatVm.nodeSender_ConvertWrapper.nodeSender_EventAnnouncer.send_SessionBeignEvent();
    windowSession.drawAndChatVm.initAppPanel(stage);

    if (!StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka) {
      //_______________________________________________________________________________________________________________________________________
      windowSession.drawAndChatVm.nodeReceiver_EventListener.listen_forGeneralNodeEvent();
    }
    else {
      System.out.println(">> init_Session() :: det_DisableReceiveNetworkMsg_Kafka :: " + StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka);
    }
  }

  public void init_Session_ReceiveNetworkMsg(boolean det_AllowDbOverwriteOrAppend, DbName dbName) {
    Stage stage = new Stage();
    WindowSession windowSession = SessionManager.setup_WindowSession(sessionManager, stage, SessionNewOrLoadOrReceive.ReceiveNetworkMsg, det_AllowDbOverwriteOrAppend, dbName);
    windowSession.drawAndChatVm = new DrawAndChatVm(windowSession);
    windowSession.drawAndChatVm.nodeSender_ConvertWrapper.nodeSender_EventAnnouncer.send_SessionBeignEvent();
    windowSession.drawAndChatVm_ReceiveNetworkMsg = new DrawAndChatVm_ReceiveNetworkMsg(windowSession);
    windowSession.drawAndChatVm_ReceiveNetworkMsg.initListenNetworkMsg(stage);
  }

  public void init_Session_LoadSaveFile(String path_drcFileSave, boolean det_AllowDbOverwriteOrAppend, DbName dbName) {
    //____________________________________________
    //______________________________________________________________________________________________________
    //_______________________________________________________________________________

    Stage stage = new Stage();
    WindowSession windowSession = SessionManager.setup_WindowSession(sessionManager, stage, SessionNewOrLoadOrReceive.LoadSaveFile, det_AllowDbOverwriteOrAppend, dbName);
    windowSession.drawAndChatVm = new DrawAndChatVm(windowSession); //___________________________________________________
    windowSession.drawAndChatVm.nodeSender_ConvertWrapper.nodeSender_EventAnnouncer.send_SessionBeignEvent();
    windowSession.drawAndChatVm_LoadSaveFile = new DrawAndChatVm_LoadSaveFile(windowSession);
    windowSession.drawAndChatVm_LoadSaveFile.initLoadFile(path_drcFileSave, stage);

    if (!StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka) {
      //_______________________________________________________________________________________________________________________
      windowSession.drawAndChatVm.nodeReceiver_EventListener.listen_forGeneralNodeEvent();
    }
    else {
      System.out.println(">> init_Session() :: det_DisableReceiveNetworkMsg_Kafka :: " + StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka);
    }

  }

  //_____________________________________________________________________________________
  //_____________________
  //______________________________________________________________
  //___________________________________________________
  //____________________________________________________________________________________________________________________________________________________________________
  //_____________________________________________________________________________________________________________________________________________________________________
  //____________________________________________________________________________________________________________
  //_____________________
  //______________________________________________________________________
  //_____________________________________________________________________________________________________________________________________________________________________________________
  //___________________________________________________________________________________________________________________________________
  //__________________________________________________________________________________________________________________________________________
  //____________________________________________________________________
  //_______________________________________________________________________________________________________________
  //__________________________
  //_____________________________________
  //_________________________________________________________________________________________
  //________________________________
  //_____________________
  //_________________________________________________________________________
  //________________________
  //_____________________
  //_________________________________________________________________________________________________________________________________________
  //___________________________________________________
  //____________________________________________________________________________________________________________________________________________________________________________________________________________________
  //__________________________________________________________________________________________________________________________________________________
  //______________________________________________________________________________________________________________________________________________________________________________
  //________________________________________________________________________________________________________________________________________________________________________________________________
  //____________________________________________________________________________________________________________________________________________________________
  //___________________________________________________________________________________________________
  //________________________
  //_____________________
  //_____________________________________________________________________________________________________________________________________________________________
  //_______________________________________________________________________
  //_________________________________________________________________________________________________________________________________
  //__________________________________________________________________________________________________________
  //_____________________
  //___________________________________________________
  //_______________________________________________________________________________________________________________________________________________________________________________________________________________
  //_________________________________________________________________________________
  //__________________________________________________________________________________________________________________________________________________________________
  //_____________________
  //______________________________________________________________________
  //_____________________________________________________________________________________________________________________________________________________________________________________
  //___________________________________________________________________________________________________________________________________
  //_______________________________________________________________________________________________________________
  //__________________________
  //_____________________
  //__________________________________________________________________________________________________________________
  //________________________________________________________________________________________________________
  //________________________

  //_____________
  //_____________

  private void pause_ReceiveNetworkMessage_tog(WindowSession windowSession, boolean det_PauseReceiveNetworkMessage) {
    //___________________________________________________________________________________________________________________________________________________
    windowSession.drawAndChatVm.nodeReceiver_EventListener.pause_ReceiveNetworkMessage_tog(det_PauseReceiveNetworkMessage);
  }

  //_____________
  //_____________
  //_____________

  private static final int max_TextAllowed_InTextArea_ToAvoidLag = 24000;
  private static final int amount_TextPreserve_InTextArea_WhenMaxTextReached = 6000;

  public static Runnable runnable_ShutdownHookForStream_messy;

  //______________________________________
  //
  //___________________________________________________________
  //___________________________________________________________

  private static void setup_OutputStream(TextArea textArea_info, boolean det_WhenRedirectOutputStream_RedirectStderrToo) {
    //____________________________________________________________________________________________________________________
    //________________________________________________________________________________________________________
    //__________________________________________________________________
    //___
    //______________________________________________________________________________________________
    //__________________________________________________________________________
    //___
    //______________________________
    //___
    //______________
    //______________________________________
    //__________________________
    //___________________________________________
    //______
    //___
    //____
    //___________________________________________________________________
    //_______________________________________________________
    //___
    //______________
    //_____________________________________________________________________________
    //___
    //____
    //____________________________________________________________________________________________
    //________________________________________________________________________________________
    //
    //__________________________________________________________________
    //_______
    //___________________________________________________________
    //
    //________________________________________
    //
    //_________________
    //_____________________________________________________
    //_____________________________________________________________________________________________________________________
    //
    //______________________________
    //
    //__________________________________________________________________
    //_______________________________
    //__________________________________
    //_____________
    //__________________________________________
    //___________
    //_________
    //
    //__________________________________________
    //___________________________________________
    //_____________________________________________
    //__________________________________________________________________________________________________________
    //__________________________________________________________________________________
    //_____________
    //__________________________
    //______________________________________________________
    //_________
    //
    //_____________________________________________________________
    //___________________________________________________
    //_____________________________________________________________________________________________________________________
    //___________________________________________________________________________________
    //
    //____________________________________________________
    //____________________________________________________
    //__________________________________________
    //_________________________________________________
    //_____________________________________________________________________________________
    //_____________________________________________________________________________________________________
    //_____________________________________________
    //________________________________________________________________________________________________________________________________________
    //_______________________________________________________________________________________________________________________________________________________________________________
    //____________________________________________________________________
    //_______________________________________________
    //_____________________________________________
    //___________________________________________________________________________________________________
    //_____________________________________________________________
    //_____________________________________________
    //______________________________________________________________________________________
    //_________________________________________________________________________
    //_________
    //
    //________
    //___________________________________________________________
    //___________
    //_____________________________________________
    //_______________________________
    //____________________________
    //_______
    //__________________________________________
    //__________________________________________________________________
    //____________________________________________________________________________________________________
    //____________________________________________________________________________________________
    //____________________________________________________________________________________

    //___________________________________________________________________________________
    //_______________________________________________________________________________________________________
    //_
    //__________________________________________________________________________
    //_______________________________________________________________________________________________
    //
    //___
    //______________________________________________________________________________________
    //___
    //______________________________________________________________________________________________________________

    PipedInputStream pipe_in = new PipedInputStream();
    PipedOutputStream pipe_out = null;
    try {
      pipe_out = new PipedOutputStream(pipe_in);
    } catch (IOException e) {
      e.printStackTrace();
    }
    final PipedOutputStream pipe_out_finalCopy = pipe_out;
    final PrintStream sysout_ori = System.out;
    final PrintStream syserr_ori = System.err;
    TeeOutputStream teeOutputStream = new TeeOutputStream(sysout_ori, pipe_out);
    //______________________________________________________________________________________________________________________
    System.setOut(new PrintStream(teeOutputStream, true, StandardCharsets.UTF_8)); //_______________________________
    if (det_WhenRedirectOutputStream_RedirectStderrToo) {
      System.setErr(new PrintStream(teeOutputStream, true, StandardCharsets.UTF_8)); //_______________________________________
    }

    //__________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________

    //___
    //_______________________________________________________________________________________________________________________________________________________________________________________________________________
    //_
    //________________________________________________________________________________________________________________________________________________________
    //___
    //__________________________________________________________________________________
    //
    //__________________________________________________________________________________

    //________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________

    final BufferedReader reader = new BufferedReader(new InputStreamReader(pipe_in, StandardCharsets.UTF_8));

    final ScheduledExecutorService executor_ExecBatchUpdate = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("thd-RedirectOutputStream_Batch-%d").build());

    //___________________
    Thread thread_OutputStreamRedirectToJavafx = new Thread("thd-RedirectOutputStream")
      {
        private final StringBuffer sb = new StringBuffer();

        {
          executor_ExecBatchUpdate.scheduleWithFixedDelay(
                                                          () -> {
                                                            if (sb.length() != 0) { //____________________________________________________
                                                              final String str; //______________________
                                                              synchronized (sb) {
                                                                str = sb.toString();
                                                                sb.setLength(0);
                                                              }
                                                              Platform.runLater(() -> {
                                                                String content_existing = textArea_info.getText();
                                                                int length = content_existing.length();
                                                                if (length > max_TextAllowed_InTextArea_ToAvoidLag) {
                                                                  textArea_info.setText((content_existing + str).substring(length - amount_TextPreserve_InTextArea_WhenMaxTextReached));
                                                                  //_______________________________________________________
                                                                }
                                                                else {
                                                                  textArea_info.appendText(str);
                                                                }
                                                              });
                                                            }

                                                          }, 0, 200, TimeUnit.MILLISECONDS);
        }

        @Override
        public void run() {
          int b;
          int sn_loopError = 0;
          while (true) {
            try {
              b = reader.read();
            } catch (IOException e) {
              sn_loopError++;
              //_______
              b = -2;
              //____________________________________________________________________________________________________
              //____________________________________________________________________________________________________________________________________________________________________________
              //_________________________________________________
              //
              //___________________
              //___________________________________________
              //________________________________
              //_______________________________
              //________________________________________
              //
              //___________________________________
              //__________________________________________________________________________________________________
              //______________________
              //________________________________________
              //_____________________________________
              //_________________________________________________
              //_______________

              //_________________________________________________________
              System.err.println("Could be you close the Stage without Closing the Pipe properly? "
                                 + "\n" + "- This only affects the TextArea (as a console), it may not output properly, (its not important, just for log). "
                                 + "\n" + "- (Happened when use `stage.close()` in `listen_ConfirmClose(Stage)` //dk ;  "
                                 + "\n" + "  tried to graceful (less aggresive) shutdown, reduced the pb a bit, but still pop up sometimes.) "
                                 + "\n" + ":: " + e);
              //___
              //__________________________________________________________________________________________________
              //_________________________________________________________________________________
              //________________________________________________________________
              //_____________________________________________________________
              if (sn_loopError == 100) { throw new Error("Looped 100 times. Stream broken, idk how. Now the TextArea (as a console) will not Output anything more."); }
            }
            if (b == -1) {
              executor_ExecBatchUpdate.shutdown();
              try {
                executor_ExecBatchUpdate.awaitTermination(200, TimeUnit.MILLISECONDS);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              //___
              //______________________________________________________________________
              //______________________________________________________
              //_______________________________
              //_
              //___
              //___________________________
              //__________
              //__________________________________________________________________
              //______________________________________
              //__________
              //________________________________________
              //__________________________________
              //____________________________________________
              //______
              //___
              //________________________________________________________
              //_
              break;

              //__________________________________________________________________

              //____________________________________________________
              //__________________________________________________________________________________________________________________________________

              //____________________________________________________________________________________________
              //________________________________________________________________________
              //___
              //____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
              //___
              //____________________________________________________________________________________________
              //___
              //_________________________________________________________________________________________________________________
              //___
              //______________________________________________________________________________________________________________
              //_
              //____________________________________
              //_____________________________________________________________________________________
              //_____________________________________________
            }
            else if (b >= 0) {
              //_________________________
              //________________________________________
              //__________________________________________________________
              synchronized (sb) {
                sb.append((char) b);
              }
            }
            else if (b == -2) {
              //____________________________________________
            }
            else {
              throw new TypeError("Never Reached :: " + b);
            }

          }
        }

      };
    thread_OutputStreamRedirectToJavafx.start();

    //_____________________________________________________
    runnable_ShutdownHookForStream_messy = new Runnable()
      {
        public void run() {
          try {
            sysout_ori.println(">> runnable_ShutdownHookForStream_messy");

            executor_ExecBatchUpdate.shutdown();
            boolean det_Terminated = false;
            try {
              det_Terminated = executor_ExecBatchUpdate.awaitTermination(200, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
              e.printStackTrace();
            } finally {
              if (!det_Terminated) {
                sysout_ori.println(">> Not terminated :: " + executor_ExecBatchUpdate);
                executor_ExecBatchUpdate.shutdownNow(); //_______
              }
            }

            pipe_in.close();
            pipe_out_finalCopy.close(); //_________
            reader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }

        }
      };
  }

  //___________________________________________________
  //_________
  //___________________________________________________
  //___________________________________________________________________________________________________
  //____________________________________________________________________________________
  //
  //_____________________________________________________________
  //_____________________________
  //_____________________
  //______________________________
  //_________________________________________
  //_____
  //___
  //
  //_________________________________________________________
  //
  //_____________________________________________________________
  //__________________________________
  //
  //________________________________________
  //____________________________________
  //
  //________________________________________________________
  //_______________________________________________________________________________________________
  //_______________________________
  //_____
  //
  //_____________
  //_______________________
  //____________________
  //______________________
  //___________________________________________________
  //_________________________________
  //____________________________________________
  //_________________________
  //_____________________________
  //____________________________________________________________
  //_____________
  //___________
  //_________
  //________________________________
  //___________________________________________
  //_______
  //_____
  //
  //_________________________________________
  //_____________________________
  //_______________________________________________
  //__________________________________________
  //______________________
  //_______
  //_____
  //___

}
