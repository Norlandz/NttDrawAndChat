package com.redfrog.note.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.redfrog.note.DrawAndChatAppSessionInit;
import com.redfrog.note.DrawAndChatVm_LoadSaveFile;
import com.redfrog.note.session.SessionManager;
import com.redfrog.note.session.StaticValueDebug_Mysql;
import com.redfrog.note.util.TimeUtil;

import javafx.application.Platform;

@Controller
public class JavafxStageController {

  //_____________________
  //__________________________

  //____________
  //_______________________________________

  //_______________________________________
  //_________________________________________________
  //
  //____________________________________
  //_______
  //_________________
  //___________________________
  //_____________________________________________
  //
  //_____________________________________________
  //_________________________________________
  //______________________________________________
  //
  //___________________________________________
  //
  //____________________________________
  //______________________________________
  //____________________________________________________
  //_______________________
  //_________
  //_________
  //
  //______________________________________________________________________
  //___
  //
  //______________________________________________
  //___________________________________________________
  //
  //____________________________________
  //_______
  //_________________
  //___________________________
  //______________________________________________________
  //_________________________________________________________________________________
  //________________________________________________________________________
  //_____________________________________________________________________________________
  //
  //__________________________________
  //_________________________
  //______________________________________________________________________________________________
  //________________
  //_________________________________
  //______________________________________________________________________________________________________________________________________________________________________
  //
  //_______________________________________________________________________________________
  //
  //_____________________________________________
  //________________________________________________________________________
  //______________________________________________
  //
  //__________________________________________
  //________________________________________________________________________________________________________________
  //_____________________________________________________________________________________________________________________________________________________________________________________
  //
  //_____________________________________________________________________________________________________________________________________
  //
  //____________________________________
  //_______________________________________________
  //____________________________________________________
  //_______________________
  //
  //_____________________________________________________________________________________________________________
  //______________________________________________________________________________________________________________
  //__________________________________________________________________________________________________________
  //_____________________________________________________________________________________
  //___________________________
  //____________________________________________________________________________________________________________________
  //________________________________________________________________________________________________________
  //_____________________________________________________
  //_______________________________________
  //___________________________________________________________________________________
  //____________
  //______________________________________________________________
  //______________________________
  //__________________________________________________________________________________________________________________________________
  //_______________________
  //_________________________________________________________
  //________________________________________________________
  //_______________________________________________________
  //___________________________________
  //
  //______________________________________
  //
  //__________________
  //______________________________________________________________________________________
  //______________________________________________________________________________________________________
  //
  //____________________
  //________________________________________________________________________________________
  //_______________________________________________
  //____________________________________________
  //________________________
  //
  //_______________
  //________________________________________________________________________________________________________
  //____________________________________________________________________________________
  //__________________________________________________
  //
  //_______________________________________________
  //_________________________________________________________________________
  //____________________
  //____________________________________________________________________________________________________________________________________________________
  //_____________
  //__________________
  //_______________________________________________________________________________________________________
  //_____________
  //___________
  //
  //_________
  //
  //_________
  //
  //_______________________________________________________________________________
  //___
  //
  //__________________________________________
  //________________
  //___________________________________________________
  //_____________________________________________________
  //____________________________________________________
  //__________________________
  //
  //___________________________________________________________________
  //___
  //
  //____________
  //________________________
  //____________________________________________
  //________________________________________________
  //___________________________________________________
  //_____________________________
  //
  //____________
  //________________________
  //____________________________________________
  //________________________________________________
  //___________________________________________________
  //_____________________________
  //
  //________________________________________________________________________________
  //________________________________________________________________________________
  //________________________________________________________________________________
  //________________________________________________________________________________
  //
  //___________________
  //
  //_______________________________________________
  //__________________________
  //________________________________________________________
  //________________________
  //_____________________________________________________________
  //___________________________________________________________________________
  //_________________________________________________________
  //____________________________________________________________________________
  //________________________________________________________________
  //________________________________________________________________
  //______________________________________________________________
  //________________________________________________________________________
  //______________________________________________________
  //______________________________________________________________________________________________
  //____________________________________________________
  //__________________________
  //
  //___________________________________________________________________
  //___
  //
  //______________________________________________
  //____________________
  //____________________________________________________________________________________________________________________________________________
  //_______________________________________________________
  //________________________
  //___________________________________________________________
  //_____________________________________
  //_________________________________
  //___________________________________________________________________________________________
  //_________________________________________________________________________________________________________________________________
  //____________________________________________
  //___________________________________
  //_________________________________
  //____________________________________________________________________________________________________
  //_________________________________________________________________________________________________________________________________
  //____________________________________________
  //___________________________________
  //_________________________________
  //___________________________________________________________________________________________
  //_________________________________________________________________________________________________________________________________
  //____________________________________________
  //___________________________________
  //_________________________________
  //________________________________________________________________________________________________
  //_________________________________________________________________________________________________________________________________
  //____________________________________________
  //___________________________________
  //_________________________________
  //___________________________________________________________________________________________
  //_________________________________________________________________________________________________________________________________
  //____________________________________________
  //___________________________________
  //_________________________________
  //______________________________________________________________________________________________________________________
  //_________________________________________________________________________________________________________________________________
  //____________________________________________
  //__________________________________
  //___________________________
  //____________________________________________________
  //__________________________
  //
  //___________________________________________________________________
  //___
  //
  //____________________________________________________
  //__________________________
  //_____________________________________________________________
  //________________________________________________________________
  //__________________________________________________________________
  //________________________________________________________________________
  //__________________________________________________________
  //__________________________________________________
  //_____________________________
  //_____________________________________________________________________________
  //_______________________________________________________________________
  //_____________________________________________________________________________________________________________________________________________________________________________________________________________________
  //_____________________________________________________________________________________________________________________________
  //_________________________________________________________________
  //
  //__________________________________________
  //_____
  //________________________________________________________________________________________
  //___________________________________________________________________________________________
  //___________________________________________________________________
  //
  //_______________________________________________________________________________
  //_____
  //_____
  //____________________________________________________________________________________________________
  //
  //_________________________________________________________________________________
  //_________________________________________________________
  //
  //__________________________________________________________________________________________________________________________________________________________________________
  //_____
  //
  //____________________________________
  //_______
  //_________________
  //___________________________
  //___________
  //______________________________________________________________________________________________
  //_________________________________________________________________________________________________
  //_________________________________________________________________________
  //
  //_____________________________________________________________________________________
  //___________
  //___________
  //__________________________________________________________________________________________________________
  //
  //_______________________________________________________________________________________
  //_______________________________________________________________
  //
  //________________________________________________________________________________________________________________________________________________________________________________
  //___________
  //_________
  //_________
  //
  //_________
  //_________________________
  //______________________________________
  //__________________________
  //_____
  //________________________________________________________________________
  //___
  //
  //____________________________________________________
  //__________________________
  //_____________________________________________________________
  //__________________________________________
  //_____
  //________________________________________________________________________________________
  //___________________________________________________________________________________________
  //___________________________________________________________________
  //
  //_______________________________________________________________________________________
  //_____
  //_____
  //____________________________________________________________________________________________________
  //
  //_________________________________________________________________________________
  //_________________________________________________________
  //
  //__________________________________________________________________________________________________________________________________________________________________________________
  //_____
  //
  //__________________________________________
  //_____________
  //_______________________
  //_________________________________
  //_________________
  //____________________________________________________________________________________________________
  //_______________________________________________________________________________________________________
  //_______________________________________________________________________________
  //______
  //____________________________________________________________________________________________________
  //_________________
  //_________________
  //________________________________________________________________________________________________________________
  //______
  //_____________________________________________________________________________________________
  //_____________________________________________________________________
  //______
  //______________________________________________________________________________________________________________________________________________________________________________________________
  //_________________
  //_______________
  //_______________
  //
  //_________________________________________
  //_______
  //_________________
  //___________________________
  //___________
  //______________________________________________________________________________________________
  //_________________________________________________________________________________________________
  //_________________________________________________________________________
  //
  //_____________________________________________________________________________________________
  //___________
  //___________
  //__________________________________________________________________________________________________________
  //
  //_______________________________________________________________________________________
  //_______________________________________________________________
  //
  //________________________________________________________________________________________________________________________________________________________________________________________
  //___________
  //_________
  //_________
  //_______________
  //
  //_________
  //_________________________________
  //________________
  //______________________________________
  //__________________________
  //_____
  //
  //________________________________________________________________________
  //___
  //
  //_________________________________________
  //____________________________________________________________________________________________
  //_________________________________________________
  //_____________________________________________________________________________________
  //___________________________________________________________________________________
  //
  //____________________________________________________________________________________________________________
  //
  //__________________________________________________________________________________________________________________________________________________________________________________________________________________
  //____________________________________________________
  //
  //_________________________________________________________________________________________________________
  //
  //______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //
  //_____________________________________
  //_____________________________________________________________________________________________________________________
  //__________________________________________________________
  //_____
  //
  //____________________________________________________________________________________________________________________________
  //
  //______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //___________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //
  //___________________
  //________________________________________________________
  //__________________________________________________________________________________________________
  //_______________________________________________________________________________________________________________________
  //_______________________
  //_________________________________________________________________________________
  //__________________________________________
  //
  //____________________________
  //
  //_________
  //___________________________________________________________________
  //___________
  //________________________________________________________________________
  //_____________________________________________
  //_______________________________________
  //________________________________________
  //____________________________
  //_______
  //_____________________________
  //__________________________
  //_____
  //
  //______________________________________________________________________________________________________________________________
  //______
  //_________________________________________________________________________________________________________________________________________________
  //______
  //___________________________________________________________________________________________________________________________________
  //
  //________________________________________________________________
  //___
  //
  //________________________________________________
  //______________________________________________________________________________________________________________________________________
  //________
  //_______
  //______
  //________________________________________________
  //___________________________________________________________
  //________
  //_______
  //____
  //________________________________________________________________________
  //________
  //_______
  //______
  //___________________________________________
  //________
  //_______
  //
  //____________
  //_______________________________________
  //
  //________________________________________________________________________________
  //
  //_________________________________________
  //__________________________
  //_________________________________________________
  //__________________________________________________________________________________
  //__________________________________________________________________________________________
  //______________________________________________________________________________________
  //
  //_________________________________________________
  //________________________________________________
  //
  //____________________________________
  //_______
  //_________________
  //___________________________
  //________________________________________________________
  //____________________________________
  //
  //___________________________________________________________________________________
  //________________________________________________________________________
  //_________________________________________________________________________________________________
  //____________
  //______________________________________________________________________________________________________________________________
  //__________________________________________________________________________________________
  //___________________________________________________________________________________________________
  //____________
  //_____________________________________________________________________________________
  //____________
  //_______________________________________________________________________________________________________
  //______________________________________________
  //____________________________________________________________________________
  //__________________________________________
  //_______________________
  //_________________________________________________________________________________
  //__________________________________________
  //_______________________
  //____________________________
  //__________________________________________________________
  //_______________________
  //
  //_________________________________________________________
  //________________________________________________________
  //
  //_____________________________________________________________
  //____________________________________________________________
  //
  //_______________________________________________________________________
  //___________________________________________
  //_________________________________________
  //__________________________________
  //_______________________________________________________________________________________________________________________________
  //_________________________________
  //___________
  //________________
  //________________________________________________________________________________________________________________________________
  //________________________________________________________________________________________________________________________
  //___________
  //
  //_______________________
  //_____________________________________________________________________________________________
  //____________________________________________________________________________________________
  //________________________________________________________________________________
  //___________________________________________________________________________________________________
  //____________________________________________________________________________________
  //_______________________________
  //___________
  //________________
  //___________________________________________________________________________________________________
  //______________________________________________________________________________________________________________________________________________
  //_____________________________________________________________________________________________________________________
  //___________________________________________________________________________________________________________________________
  //__________________________________________________________________________________________________________________________________________
  //_____________________________________________________________________________________________________________________________________________________________
  //______________________________________________________________________________________________________________
  //______________________________________________________________________________________
  //________________________________________________________________________________________________________________________________________
  //____________________________________
  //____________________________________
  //______________________________________________
  //__________________
  //________________________________
  //_____________
  //___________
  //
  //____________
  //
  //_______________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
  //______________________________________________
  //
  //_______________
  //_________________________________________________________________________
  //_________________
  //________________________________________________________________________________
  //____________________________________________________
  //______________________________________________
  //______________________________________________
  //__________________________________
  //_________________________________
  //_____________
  //___________________________________
  //_______________________________
  //___________
  //
  //_________________
  //_______________
  //
  //_____________________________________________
  //_________________________________________________________________________
  //_____________________________________________________________________________________________________________________________________________________________________________________
  //
  //_________________________________________________________________________
  //____________________________________________________
  //___________________________________________________________
  //_______________________
  //
  //________________
  //
  //___________________________
  //________________________________________
  //______________________________________
  //______________________________________________
  //__________________________________
  //_________________________________________________________________________________________
  //__________________________________________________________________________
  //_____________________________
  //__________________________________________________________________________________________________________
  //________________________________________________________________________
  //_________________________________________________
  //__________________________________________________________________
  //________________________________________________________
  //____________________________________________________________________________
  //______________________________________________
  //_________________________
  //____________
  //_____________________________
  //_____________________________________________
  //__________________________________________________________
  //______________________________________________
  //_____________________________________________
  //_________________________
  //_______________________
  //
  //_______________
  //____________________________________________________________________________________________________________________
  //____________
  //____________________________________________________________________________________________________
  //______________________________________________________________
  //____________
  //___________________________________________________________
  //______________________________________________________________________
  //___________________________________________________________________________________________
  //_____________________________________________________________________________
  //__________________________________________________________________________________________________________________________________________________________
  //_________________________
  //__________________________________________________________________________________________
  //________________________________
  //_______________________________________________________________________________________________________________________________________________________________
  //__________________________________________________________________________________________________________________________________________________________
  //_________________________
  //______________________________
  //___________________________________________________________________________________________________________________
  //_________________________
  //_______________________
  //
  //________________________________________________________________________________________________________
  //
  //_________________________________________________________________________________________________________________________
  //_____________________________________________________________
  //
  //__________________________________________________
  //__________________________________________________________
  //_____________________________________________________________________________________________________________________________________
  //________________________________________________________________________________________________________________________________
  //_____________________________________________________________________________________________________________________________________________
  //___________
  //_________
  //_________
  //
  //______________________________
  //___________________________________________________________________
  //________________________________________________________________________________________________________________________
  //_______________________________________________________________________________________________
  //___

  @Autowired
  public SessionManager sessionManager;

  @Autowired
  public DrawAndChatAppSessionInit drawAndChatAppSessionInit;

  @Deprecated
  @GetMapping("/javafx/save_file_drcapp")
  public ResponseEntity<String> save_file_drcapp(
                                                 @RequestParam(name = "filename", required = false) String path_drcFileSave,
                                                 @RequestParam(name = "dbName", required = false) String dbName_drcFileSave) {

    //______________________________________________
    if (dbName_drcFileSave == null) { dbName_drcFileSave = sessionManager.windowSession_curr.dbOccupation.dbName.toString(); }
    if (path_drcFileSave == null) { path_drcFileSave = StaticValueDebug_Mysql.pathStr_drcFileSave + "\\" + "DrawAndChat_" + TimeUtil.time2str(Instant.now()) + ".sql"; }

    String cmdArr_cmdStr_SaveFile = DrawAndChatVm_LoadSaveFile.initSaveFile(dbName_drcFileSave, path_drcFileSave);

    return ResponseEntity.ok().body("<pre>" + cmdArr_cmdStr_SaveFile + "\n</pre>");
  }

  //_________________________________________________________________________
  @Deprecated
  @GetMapping("/javafx/load_file_drcapp")
  @Transactional //_______
  public ResponseEntity<String> load_file_drcapp(
                                                 @RequestParam(name = "path", required = true) String path_drcFileSave,
                                                 @RequestParam(name = "allowoverwrite", required = false) boolean det_AllowOverwrite,
                                                 @RequestParam(name = "use_db_02", required = false) boolean det_ForceUseDb02_debug) {

    //_______________________________________________________________________________________________________________________________________________
    //
    //______________________________
    //_________________________________________________________________________
    //________________________________________________________________________________________________________________________
    //_________________________________________________________________________________________________

    return ResponseEntity.ok()
                         .body("Deprecated");
  }

}
