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
@RooEntity(versionField="", table="tregisterinfo", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class TregisterInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name = "imei")
	private String imei;
	
	@Column(name = "mac")
	private String mac;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "userno")
	private String userno;
	
	@Column(name = "platform")
	private String platform;
	
	@Column(name = "machine")
	private String machine;
	
	@Column(name = "softwareversion")
	private String softwareversion;
	
	@Column(name = "createtime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	
	@Column(name = "channel")
	private String channel;
	
	public static List<TregisterInfo> findByImeiPlatform(String imei, String platform) {
		TypedQuery<TregisterInfo> q = entityManager().createQuery(
				"SELECT o FROM TregisterInfo o where o.imei=? and o.platform=?", TregisterInfo.class);
		q.setParameter(1, imei).setParameter(2, platform);
		return q.getResultList();
	}
	
}
