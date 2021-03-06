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
@RooEntity(versionField="", table="tbl_userinf", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class UserInf {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name = "imei")
	private String imei;
	
	@Column(name = "imsi")
	private String imsi;
	
	@Column(name = "mobilenum")
	private String mobilenum;
	
	@Column(name = "createtime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	
	@Column(name = "lastnetconnecttime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastnetconnecttime;
	
	@Column(name = "softwareversion")
	private String softwareversion;
	
	@Column(name = "platfrom")
	private String platfrom;
	
	@Column(name = "channel")
	private String channel;
	
	@Column(name = "machine")
	private String machine;
	
	@Column(name = "isemular")
	private String isemular;
	
	@Column(name = "regtime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date regtime;
	
	@Column(name = "userno")
	private String userno;
	
	@Column(name = "phoneSIM")
	private String phoneSIM;
	
	@Column(name = "mac")
	private String mac;
	
	public static List<UserInf> getListByMacPlatform(String mac, String platform) {
		TypedQuery<UserInf> q = entityManager().createQuery(
				"SELECT o FROM UserInf o where o.mac=? and o.platfrom=? order by o.createtime desc", UserInf.class);
				q.setParameter(1, mac).setParameter(2, platform);
		return q.getResultList();
	}
	
}
