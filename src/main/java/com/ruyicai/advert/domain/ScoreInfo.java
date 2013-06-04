package com.ruyicai.advert.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="scoreinfo", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class ScoreInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name = "aduid")
	private String aduid;
	
	@Column(name = "uid")
	private String uid;
	
	@Column(name = "aid")
	private String aid;
	
	@Column(name = "point")
	private BigDecimal point;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "sign")
	private String sign;
	
	@Column(name = "timestamp")
	private String timestamp;
	
	@Column(name = "createtime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createtime;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "updatetime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatetime;
	
	@Column(name = "userno")
	private String userno;
	
}
