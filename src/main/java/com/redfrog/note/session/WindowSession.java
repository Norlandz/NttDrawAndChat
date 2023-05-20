package com.redfrog.note.session;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.redfrog.note.DrawAndChatAppSessionInit;
import com.redfrog.note.DrawAndChatVm;
import com.redfrog.note.DrawAndChatVm_LoadSaveFile;
import com.redfrog.note.DrawAndChatVm_ReceiveNetworkMsg;
import com.redfrog.note.communication.KafkaConfig;
import com.redfrog.note.exception.TypeError;
import com.redfrog.note.fundamental.NodeSigManager;
import com.redfrog.note.session.SessionManager.MsgConnRole;
import com.redfrog.note.session.SessionManager.SessionNewOrLoadOrReceive;
import com.redfrog.note.traversal.UndoManager;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

//__________
//___________________
//______________________________________
public class WindowSession {

  private static int seqNum = 0;

  public final SessionNewOrLoadOrReceive sessionNewOrLoadOrReceive;
  public final MsgConnRole msgConnRole;

  public WindowSession(SessionNewOrLoadOrReceive sessionNewOrLoadOrReceive, MsgConnRole msgConnRole) {
    seqNum++;
    //__________________________________________________
    //___________________
    this.sessionNewOrLoadOrReceive = sessionNewOrLoadOrReceive;
    this.msgConnRole               = msgConnRole;
  }

  public final NodeSigManager nodeSigManager = new NodeSigManager();   //___________________________________________
  public final UndoManager undoManager = new UndoManager();
  public final KafkaConfig kafkaConfig = new KafkaConfig();

  //_____________________________________________
  public DrawAndChatVm drawAndChatVm;
  public DrawAndChatVm_ReceiveNetworkMsg drawAndChatVm_ReceiveNetworkMsg;
  public DrawAndChatVm_LoadSaveFile drawAndChatVm_LoadSaveFile;

  public DbOccupation dbOccupation;

  public Stage javafxStage;
  public Pane pane_JavafxRoot;
  public Pane panel_SemanticRoot;

  public SessionManager sessionManager;

  public final AtomicLong seqIdSql = new AtomicLong(0);

  public Boolean det_NeedRequestQualificationFirst;

  //_____________

  @Override
  public String toString() {
    return "‘" + super.toString() + " :: " + sessionNewOrLoadOrReceive + " :: " + msgConnRole + "’"; //_
  }

  //_____________

  //_____________________
  //_________________________________

  //______________________________________________________
  //____________________________________________
  //__
  //_______
  //_________________________________________________________________________________________________________________________________________________________
  //_______
  //_______________________________________________________
  //_____
  //______
  //____________________________________
  //_____________________________________
  //____________________________________________
  //____________________________________________________________
  //
  //_______________________________________________________________________________________________________________
  //___________________________________________________________________

  //___
  //___________________________________________________________________________________________________
  //_
  //______________________________________________________________________________________________________________________________________
  //___
  //____________________________________________________________________________________________________________________________
  //_
  //_
  //_
  //___
  //____________________________________________________________________________________________________________________________________________________________________________________
  //_
  //___________________________________________________________________________________________________________________________________________________
  //___
  //______________________________________________________________________________________________________________________________
  //_

  //_____________________
  //__________________________

  //_____________

  private boolean det_DontPromotCloseConfirmAgain_JustClose = false;

  //________________________________________________________________________
  //________________
  public void listen_ConfirmClose(Stage stage) {
    Window window = stage.getScene().getWindow();
    window.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (event) -> {
      if (!det_DontPromotCloseConfirmAgain_JustClose) {
        System.out.println("Window close request ...");

        //_______________________________________________________________________________________________________
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getButtonTypes().remove(ButtonType.OK);
        alert.getButtonTypes().add(ButtonType.CANCEL);
        ButtonType buttonType_AutoClose = new ButtonType(DrawAndChatAppSessionInit.Text_AutoClose);
        alert.getButtonTypes().add(buttonType_AutoClose);
        ButtonType buttonType_ForceClose = new ButtonType(DrawAndChatAppSessionInit.Text_ForceClose);
        alert.getButtonTypes().add(buttonType_ForceClose);
        alert.setTitle("Quit application");
        alert.setContentText(DrawAndChatAppSessionInit.Text_CloseConfirm);
        alert.initOwner(stage.getOwner());

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent()) {
          ButtonType buttonType = res.get();
          if (buttonType == ButtonType.CANCEL) { //_____________________
            event.consume();
          }
          else if (buttonType == buttonType_AutoClose) {
            ScheduledExecutorService executor_Time = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("thd-Time-%d").build());

            Thread thd_HandleStageShutdown = new Thread(
                                                        () -> {
                                                          handle_StageShutdown(false);
                                                          executor_Time.shutdown();
                                                          boolean det_Terminated = false;
                                                          try {
                                                            det_Terminated = executor_Time.awaitTermination(100, TimeUnit.MILLISECONDS);
                                                          } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                          } finally {
                                                            if (!det_Terminated) { System.out.println(">> Not terminated :: " + executor_Time); }
                                                          }
                                                          Platform.runLater(() -> {
                                                            det_DontPromotCloseConfirmAgain_JustClose = true;
                                                            //___________________________________________________________________________________________
                                                            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
                                                            //_________________________________________________________
                                                            //______________________________________________________

                                                          });
                                                        },
                                                        "thd-handle_StageShutdown(false)");
            thd_HandleStageShutdown.start();
            //_________________________________________________

            //____________________________________________________________________________
            Stage dialogstage = new Stage();
            //__________________________________________________________
            dialogstage.initModality(Modality.WINDOW_MODAL);
            dialogstage.initOwner(stage);
            Text text_Info = new Text(DrawAndChatAppSessionInit.Text_CloseWait);
            Text text_Timer = new Text();
            Scene scene = new Scene(new FlowPane(Orientation.VERTICAL, text_Info, text_Timer), 400, 200);
            dialogstage.setScene(scene);
            dialogstage.show();

            executor_Time.scheduleWithFixedDelay(() -> {
              Platform.runLater(() -> {
                text_Timer.setText(Instant.now().toString());
              });
            }, 0, 1500, TimeUnit.MILLISECONDS);

            dialogstage.xProperty().addListener((observable, oldValue, newValue) -> {
              stage.setX((double) newValue); //_
            });
            dialogstage.yProperty().addListener((observable, oldValue, newValue) -> {
              stage.setY((double) newValue); //_
            });

            event.consume(); //_________________________________________________________________________________
          }
          else if (buttonType == buttonType_ForceClose) {
            handle_StageShutdown(true); //_______________________________

            Stage dialogstage = new Stage();
            //__________________________________________________________
            dialogstage.initModality(Modality.WINDOW_MODAL);
            dialogstage.initOwner(stage);
            dialogstage.setScene(new Scene(new VBox(), 400, 200));
            dialogstage.show();

            //____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________

            //______________________________________________
            //____________________________
            //_
            //___
            //____________________________________________________________________________
            //_
            //_______________________________________________________
            //_________________________________________________
            //________________________________
            //___
            //_______________________________________________________________________________________________
            //_
            //_
            //___
            //_________________________________________________________________________________________________________
            //___
            //____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
            //___
            //_______________________________________________________________________________________________
            //_
            //________________________________________________________________________________
            //_
            //_______________________________________________________
          }
          else {
            throw new TypeError("Never Reach :: " + buttonType);
          }
        }
      }
      else {
        //____________
        System.out.println("Just close stage :: " + stage);
      }
    });
  }

  //___

  public ArrayList<ExecutorService> arr_ThreadPool_OnlyForSimpleShutdown = new ArrayList<>();
  public HashMap<ExecutorService, ScheduledFuture<?>> mpp_ThreadPool_vs_Future_OnlyForSimpleShutdown = new HashMap<>(); //____________

  private void handle_StageShutdown(boolean det_ForceShutdownRightNow_StopPersist) {
    System.out.println(">> stage.setOnCloseRequest() :: " + WindowSession.this.dbOccupation.dbName); //____________________
    if (!StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka) {
      WindowSession.this.drawAndChatVm.nodeReceiver_EventListener.shutdown_ReceiveNetworkMessage();
    }
    else {
      System.out.println(">> shutdown_ReceiveNetworkMessage() :: det_DisableReceiveNetworkMsg_Kafka :: " + StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka);
    }

    //______________________________________________________________________________________________________
    if (!StaticValueDebug.det_DisableLoadSaveFile_Mysql) {
      WindowSession.this.drawAndChatVm.nodeSender_ConvertWrapper.nodeSender_EventAnnouncer.shutdown_LoadSavdFile_PersistToDb(det_ForceShutdownRightNow_StopPersist);
    }
    else {
      //_____________
      System.out.println(">> shutdown_LoadSavdFile_PersistToDb() :: det_DisableLoadSaveFile_Mysql :: " + StaticValueDebug.det_DisableLoadSaveFile_Mysql);
    }

    for (ExecutorService executorService : arr_ThreadPool_OnlyForSimpleShutdown) {
      System.out.println("Bit dangerous, sometimes you need to do it in an order; some already dealt with; some may not registered with this. :: " + arr_ThreadPool_OnlyForSimpleShutdown);
      if (!executorService.isShutdown()) {
        executorService.shutdown(); //_
      }
    }
    for (ExecutorService executorService : arr_ThreadPool_OnlyForSimpleShutdown) {
      try {
        if (!executorService.isTerminated()) {
          boolean det_Terminated = executorService.awaitTermination(50, TimeUnit.MILLISECONDS); //_____________________________________
          //__________________________________________________________________________________________________________________________________________________________
          if (!det_Terminated) {
            System.out.println("Not shutdowned yet, could be blocking, try Interrupt now :: " + executorService);
            //______________________________________________________________________________________
            mpp_ThreadPool_vs_Future_OnlyForSimpleShutdown.get(executorService).cancel(true);
            det_Terminated = executorService.awaitTermination(250, TimeUnit.MILLISECONDS); //____________________________________________________________________
            if (!det_Terminated) {
              System.out.println("Still not shutdowned, force shutdown now (could be bad) :: " + executorService);
              //__________________________________________________
            }
          }
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    System.out.println("<<# stage.setOnCloseRequest() :: " + WindowSession.this.dbOccupation.dbName);

    //_________________________________________________________________________________________________________________________________
    //_________________________________________________
    //__________________________________________________________________
  }

  //____________________________________________________________________
  //_______________________________
  //_______
  //_________________
  //_______________________________________________
  //____________________________________________________
  //
  //_________
  //________
  //___

  //_____________

}
