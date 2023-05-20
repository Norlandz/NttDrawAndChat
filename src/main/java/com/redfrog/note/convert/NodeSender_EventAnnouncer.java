package com.redfrog.note.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.util.SerializationUtils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.redfrog.note.communication.KafkaConfig;
import com.redfrog.note.database.DbThreadHelper;
import com.redfrog.note.database.MultipleDbSpringConfig;
import com.redfrog.note.event.ArrowMovedEvent;
import com.redfrog.note.event.FunctionExecutionEvent;
import com.redfrog.note.event.NodeCreatedEvent;
import com.redfrog.note.event.NodeLayerMovedEvent;
import com.redfrog.note.event.NodeMovedEvent;
import com.redfrog.note.event.NodeRelationshipContainmentStatusChangedEvent;
import com.redfrog.note.event.NodeRemovedEvent;
import com.redfrog.note.event.NodeShapeScaleChangedEvent;
import com.redfrog.note.event.NodeShapeSizeChangedEvent;
import com.redfrog.note.event.PaintDotChangedEvent;
import com.redfrog.note.event.PaintRectClearEvent;
import com.redfrog.note.event.TextChangedEvent;
import com.redfrog.note.event.mousekeyboardinput.StatusPhase;
import com.redfrog.note.event.nodestatus.NodeFocusStatusChangedEvent;
import com.redfrog.note.event.session.SessionBeignEvent;
import com.redfrog.note.event.traversal.RedoCompensationStepAddedEvent;
import com.redfrog.note.event.traversal.RedoEvent;
import com.redfrog.note.event.traversal.UndoCheckpointPlacedEvent;
import com.redfrog.note.event.traversal.UndoCompensationStepAddedEvent;
import com.redfrog.note.event.traversal.UndoEvent;
import com.redfrog.note.exception.NotSupportedException;
import com.redfrog.note.fundamental.EventSig;
import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.fundamental.NodeSig;
import com.redfrog.note.fundamental.NodeSigManager;
import com.redfrog.note.nodeShape.Point;
import com.redfrog.note.serialization.SerializableMethod;
import com.redfrog.note.session.DbOccupation;
import com.redfrog.note.session.DbOccupation.DbName;
import com.redfrog.note.session.SessionManager;
import com.redfrog.note.session.StaticValueDebug;
import com.redfrog.note.session.WindowSession;
import com.redfrog.note.session.WindowSessionExecutor;
import com.redfrog.note.util.JavafxUtil.MoveLayerMode;

//__________
public class NodeSender_EventAnnouncer implements WindowSessionExecutor {

  private final WindowSession windowSession_corr;
  private final NodeSigManager nodeSigManager_corr;
  private final DbThreadHelper dbThreadHelper_corr;

  private final KafkaConfig kafkaConfig_corr;
  private final String kTopicName_Send_NodeEvent_All;
  private final String kPartitionOrderKey_OrderAllNodeEvent;

  public NodeSender_EventAnnouncer(WindowSession windowSession_corr) {
    this.windowSession_corr                   = windowSession_corr;
    this.nodeSigManager_corr                  = windowSession_corr.nodeSigManager;
    this.dbThreadHelper_corr                  = windowSession_corr.sessionManager.dbThreadHelper;
    this.kafkaConfig_corr                     = windowSession_corr.kafkaConfig;
    this.kTopicName_Send_NodeEvent_All        = windowSession_corr.kafkaConfig.kTopicName_Send_NodeEvent_All;
    this.kPartitionOrderKey_OrderAllNodeEvent = windowSession_corr.kafkaConfig.kPartitionOrderKey_OrderAllNodeEvent;

    System.out.println("public NodeSender_EventAnnouncer(WindowSession windowSession_corr) { :: " + executor.toString());
    cstuInit();
  }

  @Override
  public WindowSession getWindowSession() { return windowSession_corr; }

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

  private ConcurrentLinkedQueue<NodeEvent> queue_NodeEventBatch_db01 = new ConcurrentLinkedQueue<>();
  //___________________________________________________________________________________
  //______________________________________
  //_______________________________________________________________________________
  //_______________
  //______________________________________________________________________

  //_____________________________________

  private ScheduledExecutorService executor_ExecIdleBatch;

  private void cstuInit() {
    if (!StaticValueDebug.det_DisableLoadSaveFile_Mysql) {
      //___________________________________________________________________________________________________
      executor_ExecIdleBatch = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("thd-dbPersist_IdelBatch-%d").build());
      executor_ExecIdleBatch.scheduleWithFixedDelay(
                                                    () -> {
                                                      //______________________________________________________________________________
                                                      //_____________________________________________________________________________________
                                                      //_____________________________________________________________________________
                                                      //______________________________________________________________________________________
                                                      //_______________________________
                                                      if (!queue_NodeEventBatch_db01.isEmpty() && System.currentTimeMillis() - dbThreadHelper_corr.time_LastPersist_db01 > 2000) {
                                                        //_____________________________________________________________________________
                                                        executor.execute(() -> {
                                                          dbThreadHelper_corr.persist_01(queue_NodeEventBatch_db01);
                                                        });
                                                      }
                                                    }, 0, 1, TimeUnit.SECONDS);
      //______________________________________________________________________________
      //______________________________________________________________
      //____________________________________________________________________________
      //________________________________________________
      //__________________________________________________________________________________________________
      System.out.println("public NodeSender_EventAnnouncer(WindowSession windowSession_corr) { private void cstuInit() { :: " + executor_ExecIdleBatch.toString());
    }
    else {
      //_____________
      System.out.println(">> com.redfrog.note.convert.NodeSender_EventAnnouncer.cstuInit()() :: det_DisableLoadSaveFile_Mysql :: " + StaticValueDebug.det_DisableLoadSaveFile_Mysql);
    }

  }

  private final AtomicLong sn_Persist_db01_atom = new AtomicLong(0);

  //_____________________________________________________________________________________________________________________
  private void detDb_and_batch_and_persist__delegateTo_DbThreadHelper(NodeEvent event, DbOccupation dbOccupation) {
    try {
      //_____________________________________________________________________________________________________________________________
      //____________________________________________________________________________________________________________

      if (dbOccupation.dbName == DbName.db_drawandchat_01) {
        long sn_Persist_01 = sn_Persist_db01_atom.incrementAndGet();
        queue_NodeEventBatch_db01.add(event);
        if (sn_Persist_01 % MultipleDbSpringConfig.BatchSize == 0) {
          //_________________________________________________________________________________________________________________________________
          //_____________________________________________________________________________
          dbThreadHelper_corr.persist_01(queue_NodeEventBatch_db01);
          //_________________________________
          //__________________________________________________________________
          //_____________________________________________________________
        }
      }
      else if (dbOccupation.dbName == DbName.db_drawandchat_02) {
        //_________________________________________________________
        dbThreadHelper_corr.persist_02(event);
      }
      else {
        throw new NotSupportedException("only 2 db for save & load file is supported currently");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //_____________

  @Deprecated
  public static final boolean det_ManualSetIdSql_ForTryBatchInMysql = false; //________________________________________________________________________
  //___________________________________________________________________________________________
  //_________________________
  //_______________________________

  //_________________________________________________________________
  ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
                                                       0L, TimeUnit.MILLISECONDS,
                                                       new LinkedBlockingQueue<Runnable>(),
                                                       new ThreadFactoryBuilder().setNameFormat("thd-dbPersist-%d").build());
  //________________________________________________________________________________________________

  //_________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________

  //_______________________________________________________________________________________________
  //_________________________
  //_________________________________________
  //_______________________________________________

  public void persist_InSingleThreadExecutor(final NodeSig nodeMain, final NodeEvent event) {
    if (!StaticValueDebug.det_DisableLoadSaveFile_Mysql) {
      //_____________________________________________________________________________________________________________________
      //___________________________________________________________________________________________________
      //___________________________________
      //___________________________
      //___________________________________________________________________________________________________________________________________________
      //__________________________________________________________________________________________________________________
      NodeEvent event_clone;
      if (!det_ManualSetIdSql_ForTryBatchInMysql) {
        //______________
        //_____________
        //_________
        //__
        //
        //_______________________________________________________________________________________________________________________________________________________________
        //
        //_________________________________________________________________________________________________________________________________________________________________________
        //________________________________________________________________
        //________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        //__________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
        //______________________________________________________________________________________________________________________________________________________________________________________
        if (nodeMain != null) {
          //___________________________________________________________________________________________________________________________________________
          if (nodeMain.getIdSql() != null) { //____________________________________________________________________________________________________________________________________________________________________________
            //_________________________________________________________________________________________________________________________________________
            throw new Error("Detach pb re-appear? :: " + event + "\n"
                            + "      // @note-imp::{ \n"
                            + "      // must clone, \n"
                            + "      // cuz the state_snapshot_of_NodeSig wanted to save \n"
                            + "      // should be `at the time pass persist job to thread`; \n"
                            + "      // not `at the time thread actually call`; \n"
                            + "      //   (the set id null not sufficient -- cuz \n"
                            + "      //   1. even set it, state would may have already changed \n"
                            + "      //   1. not at right before em.persist() -- inside the executor thread\n"
                            + "      //   1. the queue_Batch async the process even more (-- aga, >\"inside the executor thread\" is not enough, need even more/closer_at >\"\"right before em.persist()\"\n"
                            + "      //   )\n"
                            + "      // }"); //_
          }
        }
        event_clone = (NodeEvent) SerializationUtils.deserialize(SerializationUtils.serialize(event)); //______________
        //___________________________________________________________________________________________________________________________________________________
      }
      else {
        //______________________________________________________________________________________________________
        event_clone = (NodeEvent) SerializationUtils.deserialize(SerializationUtils.serialize(event));
        event_clone.setIdSql(windowSession_corr.seqIdSql.incrementAndGet());
        NodeSig nodeMain_clone = event_clone.nodeMain;
        if (nodeMain_clone != null) {
          nodeMain_clone.setIdSql(windowSession_corr.seqIdSql.incrementAndGet()); //_
        }
        if (NodeRelationshipContainmentStatusChangedEvent.class.isAssignableFrom(event_clone.getClass())) { //_
          ((NodeRelationshipContainmentStatusChangedEvent) event_clone).nodeSig_AA.setIdSql(windowSession_corr.seqIdSql.incrementAndGet());
        }
      }

      final DbOccupation dbOccupation_L = windowSession_corr.dbOccupation;
      Future<EventSig> future = executor.submit(() -> {
        //___________________________________
        //_____________________________________________________
        //__________________________________
        detDb_and_batch_and_persist__delegateTo_DbThreadHelper(event_clone, dbOccupation_L);
        return event_clone;
      });
    }
    else {
      //_____________
      System.out.println(">> persist_InSingleThreadExecutor() :: det_DisableLoadSaveFile_Mysql :: " + StaticValueDebug.det_DisableLoadSaveFile_Mysql);
    }
  }

  //__________________________________________________________________________________________________________________________

  //___
  //___________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //___
  //__________________________________________________________________________
  //______________________

  //______________________________________
  //_____________________
  //______________________________________________________________________________________________________________________________________________________________
  //_____________________________________________________________________________________________
  //_____________________________________________________________________
  //_________________________
  //____________________________________________________________________________________________________________________________________________________________________________
  //___________________________________________________________________________________________________________________________________________________
  //________________________________________________________________________________
  //_____________________
  //________________________________________________________________________________________________________________________
  //_____________________________________________________________________________________________________________________________________________________________________________
  //______________________________________________________________________________________________________________________
  //__________________________________________
  //_____________________
  //________________________________________________________________________________________________________________________________________________________________________________________________
  //________________________

  //_____________

  public void shutdown_LoadSavdFile_PersistToDb(boolean det_ForceShutdownRightNow_StopPersist) {
    System.out.println(">> shutdown_LoadSavdFile_PersistToDb()");
    executor_ExecIdleBatch.shutdown();
    try {
      executor_ExecIdleBatch.awaitTermination(1000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    if (!queue_NodeEventBatch_db01.isEmpty()) { //________________________
      executor.execute(() -> {
        dbThreadHelper_corr.persist_01(queue_NodeEventBatch_db01);
      });
    }

    System.out.printf(">> shutdown_LoadSavdFile_PersistToDb() -- remaining TaskCount in ThreadPool %d / %d %n", (executor.getTaskCount() - executor.getCompletedTaskCount()), executor.getTaskCount());
    //
    executor.shutdown();
    if (!det_ForceShutdownRightNow_StopPersist) {
      boolean det_Terminated = false;
      try {
        det_Terminated = executor.awaitTermination(10, TimeUnit.SECONDS);
        if (!det_Terminated) {
          String msg = String.format("Not shutdowned after 10s, wait 10s more -- seems a lot of things not yet persisted -- remaining TaskCount in ThreadPool %d / %d %n", (executor.getTaskCount() - executor.getCompletedTaskCount()), executor.getTaskCount());
          System.err.println(msg);
          System.out.println(msg);
          det_Terminated = executor.awaitTermination(10, TimeUnit.SECONDS);
          if (!det_Terminated) {
            msg = String.format("Still Not shutdowned, wait 180s more; if still not done after that, you may have to Force Terminate & experience datalost -- seems a lot of things not yet persisted -- remaining TaskCount in ThreadPool %d / %d %n", (executor.getTaskCount() - executor.getCompletedTaskCount()), executor.getTaskCount());
            System.err.println(msg);
            System.out.println(msg);
            det_Terminated = executor.awaitTermination(180, TimeUnit.SECONDS);
          }
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("<<# shutdown_LoadSavdFile_PersistToDb() :: det_Terminated=" + det_Terminated);
    }
    else {
      boolean det_Terminated = false;
      try {
        det_Terminated = executor.awaitTermination(500, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (!det_Terminated) {
        ArrayList<Runnable> arr_Task = new ArrayList<Runnable>();
        executor.getQueue().drainTo(arr_Task);
        String msg = "Draining all future task :: " + arr_Task + " :: " + executor;
        System.out.println(msg);
        System.err.println(msg);
        try {
          det_Terminated = executor.awaitTermination(8000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        if (!det_Terminated) {
          String msg3 = "[Error 100%] Drained, but still not Terminated, force shutdown now, may cause Pipe Broken :: " + executor;
          System.out.println(msg3);
          System.err.println(msg3);
          executor.shutdownNow();
          try {
            det_Terminated = executor.awaitTermination(500, TimeUnit.MILLISECONDS);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          if (!det_Terminated) {
            //_______________________
            String msg2 = "Unable to shutdown executor, could due to some Task is blocking inside & not responding to Interrupt? :: " + executor;
            System.out.println(msg2);
            System.err.println(msg2);
          }
        }
      }
      System.out.println("<<# shutdown_LoadSavdFile_PersistToDb() :: det_Terminated=" + det_Terminated);
    }

  }

  //_____________

  //___
  //______________________________________________________________________________________________________________________________
  //___
  //__________________________________________________________________________________________________________________________________
  //___________________________________________________________________________________________________
  private void send_commonHelper(Producer producer, NodeEvent event) {
    if (!StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka) {
      ProducerRecord<String, ? extends NodeEvent> producerRecord = new ProducerRecord<>(kTopicName_Send_NodeEvent_All, kPartitionOrderKey_OrderAllNodeEvent, event);
      producer.send(producerRecord);
      producer.flush();
    }
    else {
      System.out.println(">> send_commonHelper() :: det_DisableReceiveNetworkMsg_Kafka :: " + StaticValueDebug.det_DisableReceiveNetworkMsg_Kafka);
    }
  }

  //__________________________

  public void send_SessionBeignEvent() {
    SessionBeignEvent event = new SessionBeignEvent(windowSession_corr);
    nodeSigManager_corr.arr_AllEvent.add(event);
    //__________________________________________________________________________
    send_commonHelper(kafkaConfig_corr.kProducer_SessionBeignEvent, event);
    persist_InSingleThreadExecutor(null, event);
  }

  //_________

  public void send_NodeCreatedEvent(NodeSig nodeMain) {
    NodeCreatedEvent event = new NodeCreatedEvent(nodeMain);
    nodeSigManager_corr.arr_AllEvent.add(event);

    //____________________________________________________________________________________________________
    //_______________________________________________________________________________________________________________________________________________________________
    //__________________________________
    //_____________________
    send_commonHelper(kafkaConfig_corr.kProducer_NodeCreatedEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
    //____________________________________________________________________________________________________________

    System.out.printf(">>- %5s %s%n    %s%n    %s%n", 4, "private static void send_NodeCreatedEvent(NodeSig nodeSig) {", event, nodeMain);
  }

  //_____________________________________________________________________________
  public void send_NodeMovedEvent(NodeSig nodeMain, StatusPhase statusPhase) {
    NodeMovedEvent event = new NodeMovedEvent(nodeMain);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_NodeMovedEvent, event);
    if (!SessionManager.det_PersistLess_DbEventLessSyncWithNetworkEvent || statusPhase == StatusPhase.End_begin) { //___________________________________________________
      persist_InSingleThreadExecutor(nodeMain, event);
    }
  }

  public void send_NodeShapeSizeChangedEvent(NodeSig nodeMain, double width, double height) {
    //_____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
    NodeShapeSizeChangedEvent event = new NodeShapeSizeChangedEvent(nodeMain, width, height);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_NodeShapeSizeChangedEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

  public void send_NodeRelationshipContainmentStatusChangedEvent(NodeSig nodeSig_AA, NodeSig nodeMain) {
    NodeRelationshipContainmentStatusChangedEvent event = new NodeRelationshipContainmentStatusChangedEvent(nodeSig_AA, nodeMain);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_NodeRelationshipContainmentStatusChangedEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event); //________________________________________________________________
  }

  //______________________________________________________
  //________
  //_________________________________________________________________________________________________________________________________
  //__________________________________
  //__________________________________
  //____________________________________________________________________
  //_______
  //__________________________________________________________________________________________________
  //_______
  //_________________________________________________________________________________
  //________________________________________________________
  //_________________________
  //_________________________
  //_______________________________________________
  //______________________________________________________________________________________________
  //___________
  //____________________________________________________
  //___________________________________________________________________________________________
  //__________________________________________________________________________________________________
  //___________
  //_____________________________________________________
  //_______________________________________________________________________________________________
  //___________
  //________________
  //______________________________________________
  //___________
  //_______
  //____________________________
  //_______________________________________________________________________________________________________________________________________
  //_______
  //________________________________________
  //___________________________
  //_________________________________
  //_________

  public void send_TextChangedEvent(NodeSig nodeMain, String content_old, String content_new) {
    TextChangedEvent event = new TextChangedEvent(nodeMain, content_old, content_new);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_TextChangedEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

  //____________________________________________________________________________________________

  public void send_PaintDotChangedEvent(NodeSig nodeMain, Point pt_PaintDot, boolean det_AddedorRemoved, StatusPhase statusPhase, List<Point> arr_BatchLinePoints_Ongoing) {
    PaintDotChangedEvent event = new PaintDotChangedEvent(nodeMain, pt_PaintDot, det_AddedorRemoved, statusPhase);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_PaintDotChangedEvent, event);
    //_____________________________________________________________________
    //_____________________________________________________________________________
    if (!SessionManager.det_PersistLess_DbEventLessSyncWithNetworkEvent || statusPhase == StatusPhase.End_begin) { //___________________________________________________
      if (arr_BatchLinePoints_Ongoing == null) { throw new Error("If statusPhase == StatusPhase.End_begin, the arr_BatchLinePoints_Ongoing shouldnt be null."); }
      event.arr_BatchLinePoints = arr_BatchLinePoints_Ongoing;
      persist_InSingleThreadExecutor(nodeMain, event);
    }
  }

  public void send_NodeRemovedEvent(NodeSig nodeMain) {
    NodeRemovedEvent event = new NodeRemovedEvent(nodeMain);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_NodeRemovedEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

  public void send_NodeFocusStatusChangedEvent(NodeSig nodeMain, boolean det_Focused_prev, boolean det_Focused_curr) {
    NodeFocusStatusChangedEvent event = new NodeFocusStatusChangedEvent(nodeMain, det_Focused_prev, det_Focused_curr);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_NodeFocusStatusChangedEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

  public void send_FunctionExecutionEvent(NodeSig nodeMain, SerializableMethod method_serializable, Object[] args, Object ins_MethodOwner, Class<?> clazz_ins_MethodOwner) {
    FunctionExecutionEvent event = new FunctionExecutionEvent(nodeMain, method_serializable, args, ins_MethodOwner, clazz_ins_MethodOwner);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_FunctionExecutionEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

  public void send_UndoCompensationStepAddedEvent(NodeSig nodeMain, SerializableMethod method_serializable, Object[] args, Object ins_MethodOwner, Class<?> clazz_ins_MethodOwner) {
    UndoCompensationStepAddedEvent event = new UndoCompensationStepAddedEvent(nodeMain, method_serializable, args, ins_MethodOwner, clazz_ins_MethodOwner);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_UndoCompensationStepAddedEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

  public void send_RedoCompensationStepAddedEvent(NodeSig nodeMain, SerializableMethod method_serializable, Object[] args, Object ins_MethodOwner, Class<?> clazz_ins_MethodOwner) {
    RedoCompensationStepAddedEvent event = new RedoCompensationStepAddedEvent(nodeMain, method_serializable, args, ins_MethodOwner, clazz_ins_MethodOwner);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_RedoCompensationStepAddedEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

  public void send_UndoCheckpointPlacedEvent(NodeSig nodeMain) {
    UndoCheckpointPlacedEvent event = new UndoCheckpointPlacedEvent(nodeMain);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_UndoCheckpointPlacedEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

  public void send_UndoEvent(NodeSig nodeMain) {
    UndoEvent event = new UndoEvent(nodeMain);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_UndoEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

  public void send_RedoEvent(NodeSig nodeMain) {
    RedoEvent event = new RedoEvent(nodeMain);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_RedoEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

  public void send_ArrowMovedEvent(NodeSig nodeMain, Point point_prev, Point point_curr, boolean det_StartOrEnd, StatusPhase statusPhase) {
    ArrowMovedEvent event = new ArrowMovedEvent(nodeMain, point_prev, point_curr, det_StartOrEnd);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_ArrowMovedEvent, event);
    if (!SessionManager.det_PersistLess_DbEventLessSyncWithNetworkEvent || statusPhase == StatusPhase.End_begin) { //___________________________________________________
      persist_InSingleThreadExecutor(nodeMain, event);
    }
  }

  public void send_PaintRectClearEvent(NodeSig nodeMain, double posX, double posY, double posW, double posH) {
    PaintRectClearEvent event = new PaintRectClearEvent(nodeMain, posX, posY, posW, posH);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_PaintRectClearEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

  public void send_NodeLayerMovedEvent(NodeSig nodeMain, int ind_Move, MoveLayerMode moveLayerMode) {
    NodeLayerMovedEvent event = new NodeLayerMovedEvent(nodeMain, ind_Move, moveLayerMode);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_NodeLayerMovedEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

  public void send_NodeShapeScaleChangedEvent(NodeSig nodeMain, double scaleX_ori, double scaleY_ori) {
    NodeShapeScaleChangedEvent event = new NodeShapeScaleChangedEvent(nodeMain, scaleX_ori, scaleY_ori);
    nodeSigManager_corr.arr_AllEvent.add(event);
    send_commonHelper(kafkaConfig_corr.kProducer_NodeShapeScaleChangedEvent, event);
    persist_InSingleThreadExecutor(nodeMain, event);
  }

}
