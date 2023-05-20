package com.redfrog.note.traversal;

import com.redfrog.note.exception.TypeError;
import com.redfrog.note.traversal.UndoTimelineIndicator.UndoTimelineIndicatorProperty;

public class UndoManager {

  private UndoTimeline undoTimeline_curr = new UndoTimeline();
  private UndoTimelineComponent currPointer; //____
  private UndoTimelineComponent grandHeadDummy = new UndoTimelineIndicator(UndoTimelineIndicator.UndoTimelineIndicatorProperty.GrandHead);
  private UndoTimelineComponent grandTailDummy = new UndoTimelineIndicator(UndoTimelineIndicator.UndoTimelineIndicatorProperty.GrandTail);

  {
    grandHeadDummy.undoTimeline_host = undoTimeline_curr;
    grandTailDummy.undoTimeline_host = undoTimeline_curr;

    grandHeadDummy.utlc_next         = grandTailDummy; //________________________________
    grandTailDummy.utlc_prev         = grandHeadDummy; //____________________________________________________________________________________________________________________
    //__________________________________
    //__________________________________________________________________________
    //________________________________________________
  }

  public void place_UndoCheckpoint() { add_UndoTimelineComponent(new UndoCheckpoint()); }

  public void add_UndoCompensationStep(Runnable runnable_underlying) { add_UndoTimelineComponent(new UndoCompensationStep(runnable_underlying)); }

  public void add_RedoCompensationStep(Runnable runnable_underlying) { add_UndoTimelineComponent(new RedoCompensationStep(runnable_underlying)); }

  private void add_UndoTimelineComponent(UndoTimelineComponent utlc_Add) {
    UndoTimelineComponent currPointer_AtInvoke = currPointer;
    //_____________________
    if (currPointer_AtInvoke == null) {
      if (!(utlc_Add instanceof UndoCheckpoint)) { throw new Error("First add must be a UndoCheckpoint -- a UndoCheckpoint must exist before adding add_UndoCompensationStep() :: " + utlc_Add.getClass().toGenericString()); }

      UndoTimeline undoTimeline_AtInvoke = undoTimeline_curr;
      undoTimeline_AtInvoke.add(utlc_Add);
      utlc_Add.undoTimeline_host = undoTimeline_AtInvoke;
      currPointer                = utlc_Add;

      grandHeadDummy.utlc_next   = utlc_Add;
      utlc_Add.utlc_prev         = grandHeadDummy;

      grandTailDummy.utlc_prev   = utlc_Add;
      utlc_Add.utlc_next         = grandTailDummy;
    }
    //_________________________________
    else {
      UndoTimelineComponent utlc_next_inUndoTimelineSuper = currPointer_AtInvoke.utlc_next;
      //______________________________________
      if (utlc_next_inUndoTimelineSuper == grandTailDummy) {
        //______________________________________________
        UndoTimeline undoTimeline_AtInvoke = undoTimeline_curr;
        undoTimeline_AtInvoke.add(utlc_Add);
        utlc_Add.undoTimeline_host     = undoTimeline_AtInvoke;
        currPointer                    = utlc_Add;

        currPointer_AtInvoke.utlc_next = utlc_Add;
        utlc_Add.utlc_prev             = currPointer_AtInvoke;

        utlc_Add.utlc_next             = grandTailDummy;
        grandTailDummy.utlc_prev       = utlc_Add;
      }
      //___________________
      //____________________________________________________
      //__________________________________________________________________
      else {
        //________________________
        //_______________________________________________________________
        //__________________________________________________________
        UndoTimeline undoTimeline_super_AtInvoke = undoTimeline_curr;
        UndoTimeline undoTimeline_sub = new UndoTimeline();
        //______________________________________________________________________________
        undoTimeline_super_AtInvoke.add(undoTimeline_super_AtInvoke.indexOf(currPointer_AtInvoke) + 1, undoTimeline_sub); //______________________________________________________________

        undoTimeline_sub.undoTimeline_host      = undoTimeline_super_AtInvoke; //________________________________
        undoTimeline_curr                       = undoTimeline_sub;

        currPointer_AtInvoke.utlc_next          = undoTimeline_sub;
        undoTimeline_sub.utlc_prev              = currPointer_AtInvoke;

        utlc_next_inUndoTimelineSuper.utlc_prev = undoTimeline_sub;
        undoTimeline_sub.utlc_next              = utlc_next_inUndoTimelineSuper;

        //___________________
        undoTimeline_sub.add(utlc_Add);
        utlc_Add.undoTimeline_host = undoTimeline_sub;
        currPointer                = utlc_Add;

        UndoTimelineIndicator subHeadDummy = new UndoTimelineIndicator(UndoTimelineIndicator.UndoTimelineIndicatorProperty.SubHead);
        //_________________________________________________
        subHeadDummy.utlc_next         = utlc_Add;
        utlc_Add.utlc_prev             = subHeadDummy;
        subHeadDummy.undoTimeline_host = undoTimeline_sub;

        utlc_Add.utlc_next             = grandTailDummy;
        grandTailDummy.utlc_prev       = utlc_Add;
      }
    }

  }

  //_________________________________
  //__________________________________________________
  //____________________________________________________
  //______________________________________________________
  //
  //________________________________________________________________
  //___________________________________________________________________
  //__________________________________________________
  //____________________________
  //______________________
  //__________
  //____________________________________________________________________________
  //_________________________________________________________________
  //____________________________________________________
  //___________________________________________________
  //_________
  //______________________________________________________________________
  //_________________________________________
  //_________
  //________________________________________________________________
  //_____________________________
  //___________________________________________________
  //______________________________________________________
  //______________________
  //_______________
  //_______________________________________________________________________________________
  //______________________________
  //_______________
  //_____________
  //__________________
  //____________________
  //_____________
  //___________
  //_____________________________________________________________________________________________________________________________
  //__________________________
  //___________
  //_________
  //______________________________________________________________
  //_________________________________________________________________________________________________________________________________________________________
  //________________________________________________________________________________________________
  //_________
  //______________
  //________________________________
  //_________
  //
  //_________________________________________________________
  //__________________________________________
  //________________________________
  //__________________
  //
  //____________________________________________________
  //____________________________________________________________________
  //________________________________________________________________________________
  //____________________________________________________________________________________________
  //__________________
  //___________
  //________________
  //__________________________________________________________
  //____________________________________
  //___________
  //_________
  //
  //____________________________________________
  //_____________________
  //_____
  //__________
  //_____________________________________________________________________________________________________________________________________________________________
  //______________________________________________________________________________________________
  //_____
  //___
  //
  //__________________________________________________
  //____________________________________________________
  //______________________________________________________
  //
  //__________________________________________________
  //____________________________
  //______________________
  //__________
  //____________________________________________________________________________
  //_________________________________________________________________
  //_________________________
  //_________
  //______________________________________________________________________
  //____________________________________________________
  //___________________________________________________
  //_________
  //________________________________________________________________
  //_____________________________
  //___________________________________________________
  //______________________________________________________
  //______________________
  //_______________
  //_______________________________________________________________________________________
  //______________________________
  //_______________
  //_____________
  //__________________
  //____________________
  //_____________
  //___________
  //_____________________________________________________________________________________________________________________________
  //__________________________
  //___________
  //_________
  //______________________________________________________________
  //______________________________________________________________
  //__________________________________
  //______________________________________________________________
  //_________
  //______________
  //________________________________
  //_________
  //
  //__________________________________________
  //________________________________
  //__________________
  //
  //_________________________________________________________________________________________________________________________________________
  //___________________________________________________________________________________________________
  //________________
  //_________
  //_____________________
  //_____
  //__________
  //_______________________________________________________________________________________________________________________________________________
  //_____
  //___

  boolean det_SkipContinousUndoCheckpoints = true;

  //_____________________________
  public void undoTo_LastUndoCheckpoint() {
    boolean det_UndoCompensationStep_Executed = false;

    UndoTimelineComponent utlc_curr = currPointer;

    //__________________________________________
    //____________________________________

    if (utlc_curr == null) {
      System.out.println("Nothing is in the UndoTimeline -- no UndoCheckpoint -- no undo available");
    }
    else {
      utlc_curr = utlc_curr.utlc_next; //_______________________________________________________________________________________________________________

      int sn_loop = 0;
      while (true) {
        sn_loop++;
        utlc_curr   = utlc_curr.utlc_prev;
        currPointer = utlc_curr;
        UndoTimeline utlc_UndoTimeline_sub = utlc_curr.undoTimeline_host;

        Class<? extends UndoTimelineComponent> clazz = utlc_curr.getClass();
        if (UndoCompensationStep.class.isAssignableFrom(clazz)) {
          ((UndoCompensationStep) utlc_curr).call();
          det_UndoCompensationStep_Executed = true;
        }
        else if (RedoCompensationStep.class.isAssignableFrom(clazz)) {
          //_____________________________
        }
        else if (UndoCheckpoint.class.isAssignableFrom(clazz)) {
          if (sn_loop == 1) { //_________________________________________________________________________________________________________
            //____________
          }
          else {
            if (!det_SkipContinousUndoCheckpoints || det_UndoCompensationStep_Executed) {
              break;
            }
            else { //________________________________________________________________
              //____________
            }
          }
        }
        else if (UndoTimelineIndicator.class.isAssignableFrom(clazz)) {
          UndoTimelineIndicatorProperty undoTimelineIndicatorProperty = ((UndoTimelineIndicator) utlc_curr).undoTimelineIndicatorProperty;

          if (undoTimelineIndicatorProperty == UndoTimelineIndicatorProperty.GrandHead) { //_________________________
            System.out.println("Reach GrandHead -- no more undo available");
            currPointer = utlc_curr.utlc_next; //_____________________________________________
            break;
          }
          else if (undoTimelineIndicatorProperty == UndoTimelineIndicatorProperty.GrandTail) {
            throw new Error("Impossible");
          }
          else if (undoTimelineIndicatorProperty == UndoTimelineIndicatorProperty.SubHead) { //_________________________________
            utlc_curr   = utlc_UndoTimeline_sub.utlc_prev;
            currPointer = utlc_curr;
          }
        }
        else if (UndoTimeline.class.isAssignableFrom(clazz)) {
          throw new Error("Should not reach when Undo -- should always inside UndoTimeline; only reachable in Redo -- where you check for stepping in.");
          //____________________________________________________________________________________
        }
        else {
          throw new TypeError();
        }
      }
    }
  }

  public void redoTo_LastUndoCheckpoint() {
    boolean det_RedoCompensationStep_Executed = false;

    UndoTimelineComponent utlc_curr = currPointer;

    if (utlc_curr == null) {
      System.out.println("Nothing is in the UndoTimeline -- no UndoCheckpoint -- no redo available");
    }
    else {
      utlc_curr = utlc_curr.utlc_prev;

      int sn_loop = 0;
      while (true) {
        sn_loop++;
        utlc_curr   = utlc_curr.utlc_next;
        currPointer = utlc_curr;

        Class<? extends UndoTimelineComponent> clazz = utlc_curr.getClass();
        if (UndoCompensationStep.class.isAssignableFrom(clazz)) {
          //_____________________________
        }
        else if (RedoCompensationStep.class.isAssignableFrom(clazz)) {
          //_________________
          if (sn_loop == 1 && utlc_curr.utlc_next == grandTailDummy) {
            //________________________________________________________________
            //____________________________________________
            //____________
            System.out.println("Reach GrandTail -- no more redo available");
            break;
          }
          //_________________
          else {
            ((RedoCompensationStep) utlc_curr).call();
            det_RedoCompensationStep_Executed = true;

          }
        }
        else if (UndoCheckpoint.class.isAssignableFrom(clazz)) {
          //_______________________________________________________________________________________________________________________
          //___________________________________________________________________________________________________________________________________________________________________________________________
          //__________________________________
          //_____________________________________________________________________________________________________________________________________________________________________________
          if (sn_loop == 1) { //_________________________________________________________________________________________________________
            //____________
          }
          else {
            if (!det_SkipContinousUndoCheckpoints || det_RedoCompensationStep_Executed) {
              break;
            }
            else { //________________________________________________________________
              //____________
            }
          }
        }
        else if (UndoTimelineIndicator.class.isAssignableFrom(clazz)) {
          UndoTimelineIndicatorProperty undoTimelineIndicatorProperty = ((UndoTimelineIndicator) utlc_curr).undoTimelineIndicatorProperty;

          if (undoTimelineIndicatorProperty == UndoTimelineIndicatorProperty.GrandHead) {
            throw new Error("Impossible");
          }
          else if (undoTimelineIndicatorProperty == UndoTimelineIndicatorProperty.GrandTail) {
            System.out.println("Reach GrandTail -- no more redo available");
            currPointer = utlc_curr.utlc_prev; //____________________________________________
            break;
          }
          else if (undoTimelineIndicatorProperty == UndoTimelineIndicatorProperty.SubHead) { throw new Error("Impossible"); }
        }
        else if (UndoTimeline.class.isAssignableFrom(clazz)) {
          utlc_curr   = ((UndoTimeline) utlc_curr).getFirst();
          currPointer = utlc_curr;
          //_____________________________________________________________
        }
        else {
          throw new TypeError();
        }
      }
    }
  }

  public UndoTimeline get_undoTimeline_curr() { return undoTimeline_curr; }

  //_________________________________________________________________________________________________________________________________

  //___________________________________________________
  //__________________________________________
  //___

}
