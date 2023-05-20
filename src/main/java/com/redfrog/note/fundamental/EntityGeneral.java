package com.redfrog.note.fundamental;

import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.annotations.DiscriminatorOptions;
import org.springframework.data.annotation.Version;

import com.redfrog.note.util.StringUtil;
import com.redfrog.note.util.SystemMetric;
import com.redfrog.note.util.TimeUtil;

//__________________
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //____________
//_______________________________________________
@DiscriminatorColumn(name = "EntityGeneral_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorOptions(force = true)
public abstract class EntityGeneral implements Serializable {

  //__________________________________________

  //_________________________________________________________________________________________________________
  //______________________________________

  //_____________________________________________________________________________________________________________________________

  //_____________________________________________________________________________________________________________
  //___________________________________________________________________________________________

  //___________________________________________________________________________________________________________________________________________________

  @Id
  //_________________________________________________
  @GeneratedValue(strategy = GenerationType.IDENTITY) //_______________________________________________________________________________________________________________________________________________________
  //________________________________________________________________________
  //__________________________________
  //_________________________________________
  //____________________________________________
  //________________________________________________
  //___________________________________________
  private Long idSql;
  //_____________________________________
  //_______________________________________________________________________________
  //_____________________

  @Basic(optional = false)
  @Column(nullable = false, updatable = false)
  protected final String idJava;

  //______________________________________________________________________________________
  @Basic(optional = false)
  @Column(nullable = false, updatable = false)
  protected final Long creationTime;

  protected transient static final AtomicLong seqNumAtom = new AtomicLong(0L);

  protected String versionNum; //____
  protected Long version; //____

  @Version
  private Integer verLock;

  public final Class<? extends EntityGeneral> clazz_sqlDebug = this.getClass();

  public EntityGeneral() {
    Instant creationTimeInstant = Instant.now();
    creationTime = TimeUnit.SECONDS.toNanos(creationTimeInstant.getEpochSecond()) + creationTimeInstant.getNano();
    long seqNum = seqNumAtom.incrementAndGet();
    idJava = String.format("%s--%s--%d", TimeUtil.time2strnano(SystemMetric.appBootTimeInstant), TimeUtil.time2strnano(creationTimeInstant), seqNum);
  }

  public Long getIdSql() { return idSql; }

  public void setIdSql(Long idSql) { this.idSql = idSql; }

  public String getVersionNum() { return versionNum; }

  public void setVersionNum(String versionNum) { this.versionNum = versionNum; }

  public Long getVersion() { return version; }

  public void setVersion(Long version) { this.version = version; }

  public String getIdJava() { return idJava; }

  public Long getCreationTime() { return creationTime; }

  public static AtomicLong getSeqnumatom() { return seqNumAtom; }

  @Override
  public String toString() { return "‘" + super.toString() + " :: idSql=" + idSql + " :: " + StringUtil.omitString(idJava, 35, 10) + "’"; }

}
