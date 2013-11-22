// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.advert.domain;

import com.ruyicai.advert.domain.ScoreBind;
import java.lang.Integer;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import org.springframework.transaction.annotation.Transactional;

privileged aspect ScoreBind_Roo_Entity {
    
    declare @type: ScoreBind: @Entity;
    
    declare @type: ScoreBind: @Table(name = "scorebind");
    
    @PersistenceContext(unitName = "persistenceUnit")
    transient EntityManager ScoreBind.entityManager;
    
    @Transactional("transactionManager")
    public void ScoreBind.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional("transactionManager")
    public void ScoreBind.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            ScoreBind attached = ScoreBind.findScoreBind(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional("transactionManager")
    public void ScoreBind.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional("transactionManager")
    public void ScoreBind.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional("transactionManager")
    public ScoreBind ScoreBind.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ScoreBind merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager ScoreBind.entityManager() {
        EntityManager em = new ScoreBind().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long ScoreBind.countScoreBinds() {
        return entityManager().createQuery("SELECT COUNT(o) FROM ScoreBind o", Long.class).getSingleResult();
    }
    
    public static List<ScoreBind> ScoreBind.findAllScoreBinds() {
        return entityManager().createQuery("SELECT o FROM ScoreBind o", ScoreBind.class).getResultList();
    }
    
    public static ScoreBind ScoreBind.findScoreBind(Integer id) {
        if (id == null) return null;
        return entityManager().find(ScoreBind.class, id);
    }
    
    public static List<ScoreBind> ScoreBind.findScoreBindEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM ScoreBind o", ScoreBind.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}