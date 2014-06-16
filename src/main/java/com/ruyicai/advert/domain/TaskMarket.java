package com.ruyicai.advert.domain;

import java.math.BigDecimal;
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
@RooEntity(versionField="", table="taskmarket", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class TaskMarket {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name = "userno")
	private String userno;
	
	@Column(name = "openid")
	private String openid;
	
	@Column(name = "appid")
	private String appid;
	
	@Column(name = "ts")
	private String ts;
	
	@Column(name = "version")
	private String version;
	
	@Column(name = "contractid")
	private String contractid;
	
	@Column(name = "step")
	private String step;
	
	@Column(name = "payitem")
	private String payitem;
	
	@Column(name = "billno")
	private String billno;
	
	@Column(name = "pkey")
	private String pkey;
	
	@Column(name = "sig")
	private String sig;
	
	@Column(name = "amt")
	private BigDecimal amt;
	
	@Column(name = "createtime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	
	@Column(name = "updatetime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatetime;
	
	@Column(name = "state")
	private Integer state;
	
	public static TaskMarket findByOpenidContractid(String openid, String contractid) {
		TypedQuery<TaskMarket> q = entityManager().createQuery(
				"SELECT o FROM TaskMarket o where o.openid=? and o.contractid=? and o.state=?", TaskMarket.class);
				q.setParameter(1, openid).setParameter(2, contractid).setParameter(3, 1);
		List<TaskMarket> list = q.getResultList();
		if (list!=null&&list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	
	/*public static List<QqUserInfo> getListByMac(String mac) {
		TypedQuery<QqUserInfo> q = entityManager().createQuery(
				"SELECT o FROM UserInf o where o.mac=? order by o.createtime desc", QqUserInfo.class);
				q.setParameter(1, mac);
		return q.getResultList();
	}
	
	public static List<QqUserInfo> getListByMacPlatform(String mac, String platform) {
		TypedQuery<QqUserInfo> q = entityManager().createQuery(
				"SELECT o FROM UserInf o where o.mac=? and o.platfrom=? order by o.createtime desc", QqUserInfo.class);
				q.setParameter(1, mac).setParameter(2, platform);
		return q.getResultList();
	}*/
	
}
