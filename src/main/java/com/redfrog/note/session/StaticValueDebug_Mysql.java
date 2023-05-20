package com.redfrog.note.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({ "Enable_LoadSaveFile", "prod" })
@Configuration
public class StaticValueDebug_Mysql {
  //_________

  public static String pathStr_drcFileSave; //___________________________
  public static String pathStr_mysqldump; //____________________________________________________________________
  public static String pathStr_mysql; //________________________________________________________________

  public static String sqlUserName; //__________
  public static String sqlUserPassword; //___________

  public final static String CharSet_utf8mb4 = "utf8mb4";

  //________________________________
  
  @Value("${cpy.rf.ntt.drc.loadSaveFile.pathStr-drcFileSave}")
  public void set_pathStr_drcFileSave(String pathStr_drcFileSave) { StaticValueDebug_Mysql.pathStr_drcFileSave = pathStr_drcFileSave; }

  @Value("${cpy.rf.ntt.drc.loadSaveFile.pathStr-mysqldump}")
  public void set_pathStr_mysqldump(String pathStr_mysqldump) { StaticValueDebug_Mysql.pathStr_mysqldump = pathStr_mysqldump; }

  @Value("${cpy.rf.ntt.drc.loadSaveFile.pathStr-mysql}")
  public void set_pathStr_mysql(String pathStr_mysql) { StaticValueDebug_Mysql.pathStr_mysql = pathStr_mysql; }

  @Value("${spring.ds-mysql-01.username}")
  public void set_sqlUserName(String sqlUserName) { StaticValueDebug_Mysql.sqlUserName = sqlUserName; }

  @Value("${spring.ds-mysql-01.password}")
  public void set_sqlUserPassword(String sqlUserPassword) { StaticValueDebug_Mysql.sqlUserPassword = sqlUserPassword; }

  //_________

}