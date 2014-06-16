package com.ruyicai.advert.service;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.consts.errorcode.QqErrorCode;
import com.ruyicai.advert.domain.QqUserInfo;
import com.ruyicai.advert.exception.QqException;
import com.ruyicai.advert.util.DateParseFormatUtil;
import com.ruyicai.advert.util.StringUtil;

@Service
public class QqService {

	private Logger logger = Logger.getLogger(QqService.class);
	
	@Autowired
	private CommonService commonService;
	
	/**
	 * 应用宝任务集市
	 * @param cmd
	 * @param openid
	 * @param appid
	 * @param ts
	 * @param version
	 * @param contractid
	 * @param step
	 * @param payitem
	 * @param billno
	 * @param pkey
	 * @param sig
	 */
	public void taskMarket(String cmd, String openid, String appid, String ts, 
			String version, String contractid, String step, String payitem, 
			String billno, String pkey, String sig, String ip) {
		logger.info("应用宝任务集市,cmd:"+cmd+",openid:"+openid+",appid:"+appid+",ts:"+ts+
				",version:"+version+",contractid:"+contractid+",step:"+step+",payitem:"+payitem+
				",billno:"+billno+",pkey:"+pkey+",sig:"+sig+",ip:"+ip);
		if (StringUtils.isBlank(cmd)||StringUtils.isBlank(openid)) {
			throw new QqException(QqErrorCode.paramError);
		}
		QqUserInfo qqUserInfo = QqUserInfo.findByMid(openid);
		if (qqUserInfo==null||StringUtils.isBlank(qqUserInfo.getUserno())) {
			throw new QqException(QqErrorCode.userNotEsist);
		}
		String userno = qqUserInfo.getUserno(); //用户编号
		JSONObject userinfoObject = commonService.getUserinfoByUserno(userno);
		if (userinfoObject==null) {
			throw new QqException(QqErrorCode.userNotEsist);
		}
		String channel = userinfoObject.getString("channel"); //用户渠道号
		if (StringUtils.equals("cmd", "Check")) { //查询用户是否完成该任务步骤(系统自动扫描时触发)
			checkTaskFinish(userinfoObject);
		} else if (StringUtils.equals("cmd", "Check_award")) { //查询用户是否完成步骤,若完成,则给用户发放步骤礼包
			if (StringUtils.equals(step, "3")) { //步骤3
				checkTaskFinish(userinfoObject);
				//todo:赠送彩金
				commonService.presentDividend(userno, "300", channel, "应用宝任务奖励");
			}
		}
	}
	
	/**
	 * 检查任务是否完成
	 * @param userno
	 */
	private void checkTaskFinish(JSONObject userinfoObject) {
		//注册时间
		String regTime = userinfoObject.getString("regtime");
		regTime = DateParseFormatUtil.formatDate(regTime, "yyyy-MM-dd");
		//当前日期
		String today = DateParseFormatUtil.getPreDayDate(0, "yyyy-MM-dd");
		if (!StringUtils.equals(regTime, today)) {
			throw new QqException(QqErrorCode.notFinish);
		}
		String mobileId = userinfoObject.getString("mobileid"); //绑定的手机号码
		String userName = userinfoObject.getString("userName"); //用户名
		String certId = userinfoObject.getString("certid"); //身份证
		String name = userinfoObject.getString("name"); //真实姓名
		if (StringUtil.isBlank(mobileId)||StringUtil.isBlank(userName)
				||StringUtil.isBlank(certId)||StringUtil.isBlank(name)) {
			throw new QqException(QqErrorCode.notFinish);
		}
	}
	
}
