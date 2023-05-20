package com.redfrog.note.session;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.redfrog.note.controller.NodeEventController;

public class DbOccupation {

  public static enum DbName {
    db_drawandchat_01,
    db_drawandchat_02,
    //______________________
    //______________________
    AutoPick_UnusedDb_InThisGrandAppSession,
    NA,
  }

  public final DbOccupation.DbName dbName;
  public final EntityManager em;
  WindowSession windowSession;

  public DbOccupation(DbOccupation.DbName dbName, EntityManager em, WindowSession windowSession) {
    this.dbName        = dbName;
    this.em            = em;
    this.windowSession = windowSession;
    System.out.println("Null? " + " :: " + this + " :: " + em);
  }

  //__________
  public boolean weak_check_IfOldDataExist() {
    if (!StaticValueDebug.det_DisableLoadSaveFile_Mysql) {
      System.out.println("Null? " + " :: " + this + " :: " + em);
      String queryStr_CheckDataExist = "SELECT EXISTS (SELECT 1 FROM `" + NodeEventController.entity_general + "`);";
      Query query_CheckDataExist = em.createNativeQuery(queryStr_CheckDataExist);
      List resultList_CheckDataExist = query_CheckDataExist.getResultList();
      //_________________________________________________________________________________________________________________________________________________________________________________________________________
      BigInteger det_ExistDataIn_EntityGeneral = (BigInteger) resultList_CheckDataExist.get(0);
      if (det_ExistDataIn_EntityGeneral.compareTo(BigInteger.valueOf(0)) != 0) {
        return true;
      }
      else {
        return false;
      }
    }
    else {
      //_____________
      System.out.println(">> weak_check_IfOldDataExist() :: det_DisableLoadSaveFile_Mysql :: " + StaticValueDebug.det_DisableLoadSaveFile_Mysql);
      return false;
    }

  }

}