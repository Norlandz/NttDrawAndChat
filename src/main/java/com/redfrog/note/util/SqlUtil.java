package com.redfrog.note.util;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.redfrog.note.exception.TypeError;
import com.redfrog.note.fundamental.EntityGeneral;
import com.redfrog.note.nodeShape.Point;
import com.redfrog.note.nodeShape.Shape;

public class SqlUtil {

  public static String get_Sql_LiteralTable(EntityManager em, String queryStr) {
    //______________________________________________________________________________________________
    Query query = em.createNativeQuery(queryStr);
    List<Object> resultLIst = query.getResultList();

    String result = "";
    result += "########\n";
    //___________________________________
    //______________________________________________
    for (Object row : resultLIst) {
      if (Object[].class.isAssignableFrom(row.getClass())) {
        Object[] row_arr = (Object[]) row;
        for (Object col : row_arr) {
          //__________________________________
          //__________________________________________________________________

          String colStr = col == null ? "null" : col.toString();

          int length = colStr.length();
          if (length >= 35) { colStr = StringUtil.omitString(colStr, 35, 10); }
          result += String.format("%35.35s | ", colStr);
        }
        result += "\n";
      }
      else if (Object.class.isAssignableFrom(row.getClass())) { //_________________________________________________________________________________________
        result += row;
        result += "\n";
      }
    }

    return result;
  }

  //________
  public static void setIdsqlToNull(Object entity) {
    Class<? extends Object> clazz = entity.getClass();
    if (EntityGeneral.class.isAssignableFrom(clazz)) {
      ((EntityGeneral) entity).setIdSql(null);
    }
    else if (Shape.class.isAssignableFrom(clazz)) {
      ((Shape) entity).setIdSql(null);
    }
    else if (Point.class.isAssignableFrom(clazz)) {
      ((Point) entity).setIdSql(null);
    }
    else {
      throw new TypeError(entity.getClass().toGenericString());
    }

  }

}
