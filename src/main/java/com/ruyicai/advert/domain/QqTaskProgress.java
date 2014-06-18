package com.ruyicai.advert.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="qqtaskprogress", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class QqTaskProgress {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name = "userno")
	private String userno;
	
	@Column(name = "step")
	private String step;
	
	@Column(name = "createtime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	
	public static List<QqTaskProgress> findByUsernoStep(String userno, String step) {
		TypedQuery<QqTaskProgress> q = entityManager().createQuery(
				"SELECT o FROM QqTaskProgress o where o.mobileid=?", QqTaskProgress.class);
		q.setParameter(1, userno).setParameter(2, step);
		return q.getResultList();
	}
	
}
