package com.redfrog.note.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.redfrog.note.event.ArrowMovedEvent;
import com.redfrog.note.fundamental.EntityGeneral;
import com.redfrog.note.fundamental.EventSig;
import com.redfrog.note.fundamental.NodeEvent;
import com.redfrog.note.util.SqlUtil;

@Controller
public class NodeEventController {

  //_____________________
  //__________________________

  @Autowired
  @Qualifier("lcemf_01")
  //________________________________________
  //____________________________________________
  //_______________________________________________
  public EntityManager em_01;

  //______________________________________________________________________________________________________________________________
  //
  //____________________________
  //_________________________________________________________________________________________________________________________________
  //_________________________________________________
  //______________________________________________________________
  //____________________________________________________________________________
  //_______________________________________________________
  //____
  //___________________________________
  //____
  //_________________________________________________
  //____
  //__________________
  //___
  //
  //____________________________________
  //___________________________________________________________________________________________________________________________
  //________________________________________
  //__________________________________________________________________
  //____
  //__________________
  //___
  //
  //_____________________________
  //_________________________________
  //__________________________________
  //___________________
  //___

  //_________________________________
  //_______________
  //_____________________________________________________________________________________________________________________________________
  //_________________________________________________
  //____________________________________________________________
  //____________________________________________________________________________
  //_______________________________________________________
  //
  //_________________________________
  //_________________________________________
  //_______________________________________________________________________________________
  //________________
  //_________
  //_________________________________________________________
  //_______________________________
  //_________________________________________
  //_________________________
  //_____
  //
  //________________
  //___

  //______________________________________

  public final static String entity_general = "entity_general";
  public final static String node_sig = "node_sig";
  public final static String event_sig = "event_sig";

  public final static String node_event = "node_event";
  public final static String node_created_event = "node_created_event";
  public final static String node_shape_size_changed_event = "node_shape_size_changed_event";
  public final static String node_moved_event = "node_moved_event";

  public final static String NodeEvent = NodeEvent.class.getName();
  public final static String EventSig = EventSig.class.getName();
  public final static String EntityGeneral = EntityGeneral.class.getName();
  public static final String ArrowMovedEvent = ArrowMovedEvent.class.getName();

  private List<NodeEvent> get_NodeEvent(int amount) {
    String queryStr = "select nn from " + NodeEvent + " nn"; //__________________________________________
    TypedQuery<NodeEvent> query = em_01.createQuery(queryStr, NodeEvent.class);
    query.setMaxResults(amount);
    List<NodeEvent> resultList = query.getResultList();
    //___________________________________
    return resultList;
  }

  @GetMapping("/java/node_event/db_drawandchat_01")
  public ResponseEntity<String> get_Java_NodeEvent(@RequestParam(name = "amount", required = false, defaultValue = "50") Integer amount, Model model) {
    List<NodeEvent> resultList = get_NodeEvent(amount);
    String result = "";
    for (NodeEvent nodeEvent : resultList) { result += nodeEvent + "\n"; }
    return ResponseEntity.ok().body("<pre>" + result + "</pre>");
  }

  //_____________________________________________________________________________________________________________________________________________________
  //________________________________________________________________________________________________________________
  @GetMapping("/json/node_event/db_drawandchat_01")
  @ResponseBody
  public List<NodeEvent> get_Json_NodeEvent(@RequestParam(name = "amount", required = false, defaultValue = "50") Integer amount, Model model) {
    List<NodeEvent> resultList = get_NodeEvent(amount);
    return resultList;
  }

  @GetMapping("/sql/node_event__node_sig/db_drawandchat_01")
  public ResponseEntity<String> get_Sql_NodeEvent(@RequestParam(name = "amount", required = false, defaultValue = "50") Integer amount, Model model) {
    String result = "";
    String queryStr;

    //_______________
    queryStr = "select * from " + entity_general                       + " order by id_sql limit " + amount; result += "\n## " + queryStr + "\n"; result += SqlUtil.get_Sql_LiteralTable(em_01, queryStr);
    queryStr = "select * from " + node_sig                             + " order by id_sql limit " + amount; result += "\n## " + queryStr + "\n"; result += SqlUtil.get_Sql_LiteralTable(em_01, queryStr);
    queryStr = "select * from " + event_sig                            + " order by id_sql limit " + amount; result += "\n## " + queryStr + "\n"; result += SqlUtil.get_Sql_LiteralTable(em_01, queryStr);
    queryStr = "select * from " + node_event                           + " order by id_sql limit " + amount; result += "\n## " + queryStr + "\n"; result += SqlUtil.get_Sql_LiteralTable(em_01, queryStr);
    queryStr = "select * from " + node_created_event                   + " order by id_sql limit " + amount; result += "\n## " + queryStr + "\n"; result += SqlUtil.get_Sql_LiteralTable(em_01, queryStr);
    queryStr = "select * from " + node_shape_size_changed_event        + " order by id_sql limit " + amount; result += "\n## " + queryStr + "\n"; result += SqlUtil.get_Sql_LiteralTable(em_01, queryStr);
    queryStr = "select * from " + node_moved_event                     + " order by id_sql limit " + amount; result += "\n## " + queryStr + "\n"; result += SqlUtil.get_Sql_LiteralTable(em_01, queryStr);
    //______________

    queryStr  = "SELECT * FROM `entity_general`\r\n"
                + "JOIN `event_sig` USING(id_sql)\r\n"
                + "LEFT JOIN `node_event` USING(id_sql)\r\n"
                + "LEFT JOIN `node_created_event` USING(id_sql)\r\n"
                + "LEFT JOIN `node_shape_size_changed_event` USING(id_sql)\r\n"
                + "LEFT JOIN `node_moved_event` USING(id_sql)\r\n"
                + "ORDER BY `creation_time` ASC, `id_sql` ASC\r\n"
                + "limit " + amount;
    result   += "\n## " + queryStr + "\n";
    result   += SqlUtil.get_Sql_LiteralTable(em_01, queryStr);

    //_______________________________
    //________________________________________________________________________________________________________________________
    return ResponseEntity.ok().body("<pre>" + result + "</pre>");
  }

}
