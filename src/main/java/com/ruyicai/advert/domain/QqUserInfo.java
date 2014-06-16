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
@RooEntity(versionField="", table="qquserinfo", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class QqUserInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name = "mid")
	private String mid;
	
	@Column(name = "userno")
	private String userno;
	
	@Column(name = "createtime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	
	@Column(name = "logintime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date logintime;
	
	public static QqUserInfo findByMid(String mId) {
		TypedQuery<QqUserInfo> q = entityManager().createQuery(
				"SELECT o FROM QqUserInfo o where o.mid=? order by o.logintime desc", QqUserInfo.class);
				q.setParameter(1, mId);
		List<QqUserInfo> list = q.getResultList();
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
