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
@RooEntity(versionField="", table="advertiseinfo", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class AdvertiseInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name = "mac")
	private String mac;
	
	@Column(name = "appid")
	private String appid;
	
	@Column(name = "advertiseid")
	private String advertiseid;
	
	@Column(name = "drkey")
	private String drkey;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "createtime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	
	@Column(name = "updatetime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatetime;
	
	@Column(name = "state")
	private String state;
	
	
	public static List<AdvertiseInfo> getListByMac(String mac) {
		TypedQuery<AdvertiseInfo> q = entityManager().createQuery(
				"SELECT o FROM AdvertiseInfo o where o.mac=? order by o.createtime desc", AdvertiseInfo.class)
				.setParameter(1, mac);
		return q.getResultList();
	}
	
	public static List<AdvertiseInfo> getListByMacSource(String mac, String source) {
		TypedQuery<AdvertiseInfo> q = entityManager().createQuery(
				"SELECT o FROM AdvertiseInfo o where o.mac=? and o.source=? order by o.createtime desc", AdvertiseInfo.class)
				.setParameter(1, mac).setParameter(2, source);
		return q.getResultList();
	}
	
	public static List<AdvertiseInfo> getListByMacSourceDrkey(String mac, String source, String drkey) {
		TypedQuery<AdvertiseInfo> q = entityManager().createQuery(
				"SELECT o FROM AdvertiseInfo o where o.mac=? and o.source=? and o.drkey=? order by o.createtime desc", AdvertiseInfo.class)
				.setParameter(1, mac).setParameter(2, source).setParameter(3, drkey);
		return q.getResultList();
	}
	
	public static List<AdvertiseInfo> getListByMacSourceAppid(String mac, String source, String appId) {
		TypedQuery<AdvertiseInfo> q = entityManager().createQuery(
				"SELECT o FROM AdvertiseInfo o where o.mac=? and o.source=? and o.appid=? order by o.createtime desc", AdvertiseInfo.class)
				.setParameter(1, mac).setParameter(2, source).setParameter(3, appId);
		return q.getResultList();
	}
	
}
