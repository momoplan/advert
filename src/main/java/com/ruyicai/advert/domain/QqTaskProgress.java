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
@RooEntity(versionField="", table="qqtaskprogress", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class QqTaskProgress {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name = "userno")
	private String userno;
	
	@Column(name = "type")
	private Integer type; //1:投注;2:合买
	
	@Column(name = "amt")
	private BigDecimal amt;
	
	@Column(name = "createtime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	
	@Column(name = "updatetime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatetime;
	
	public static QqTaskProgress findByUsernoType(String userno, Integer type) {
		TypedQuery<QqTaskProgress> q = entityManager().createQuery(
				"SELECT o FROM QqTaskProgress o where o.userno=? and o.type=?", QqTaskProgress.class);
		q.setParameter(1, userno).setParameter(2, type);
		List<QqTaskProgress> list = q.getResultList();
		if (list!=null&&list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	
}
