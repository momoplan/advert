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
@RooEntity(versionField="", table="scorebind", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class ScoreBind {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name = "mac")
	private String mac;
	
	@Column(name = "mobileid")
	private String mobileid;
	
	@Column(name = "createtime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	
	public static List<String> findByMobileid(String mobileid) {
		TypedQuery<String> q = entityManager().createQuery(
				"SELECT distinct o.mac FROM ScoreBind o where o.mobileid=?", String.class);
		q.setParameter(1, mobileid);
		return q.getResultList();
	}
	
}
