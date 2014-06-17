package com.ruyicai.advert.service;

import java.math.BigDecimal;
import java.util.Date;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.consts.errorcode.QqErrorCode;
import com.ruyicai.advert.domain.QqUserInfo;
import com.ruyicai.advert.domain.TaskMarket;
import com.ruyicai.advert.exception.QqException;
import com.ruyicai.advert.util.DateParseFormatUtil;
import com.ruyicai.advert.util.StringUtil;
import com.ruyicai.advert.util.Tools;

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
		if (StringUtils.equals(cmd, "Award")) { //给用户发放步骤1礼包
			if (StringUtils.equals(step, "1")) {
				String award = "100";
				//发放奖励
				taskAward(award, openid, appid, ts, version, contractid, step, payitem, billno, 
						pkey, sig, userno, channel);
			} else {
				throw new QqException(QqErrorCode.paramError);
			}
		} else if (StringUtils.equals(cmd, "Check")) { //查询用户是否完成该任务步骤(系统自动扫描时触发)
			if (StringUtils.equals(step, "2")) { //步骤2
				checkTaskStep2(userinfoObject);
			} else if (StringUtils.equals(step, "3")) { //步骤3
				checkTaskStep3(userinfoObject);
			} else {
				throw new QqException(QqErrorCode.paramError);
			}
		} else if (StringUtils.equals(cmd, "Check_award")) { //查询用户是否完成步骤,若完成,则给用户发放步骤礼包
			String award = "";
			if (StringUtils.equals(step, "2")) { //步骤2
				award = "100";
				checkTaskStep2(userinfoObject);
			} else if (StringUtils.equals(step, "3")) { //步骤3
				award = "100";
				checkTaskStep3(userinfoObject);
			} else {
				throw new QqException(QqErrorCode.paramError);
			}
			//发放奖励
			taskAward(award, openid, appid, ts, version, contractid, step, payitem, billno, 
					pkey, sig, userno, channel);
		} else {
			throw new QqException(QqErrorCode.paramError);
		}
	}

	private void taskAward(String award, String openid, String appid, String ts, String version, String contractid, 
			String step, String payitem, String billno, String pkey, String sig, String userno, String channel) {
		//查询奖励是否已发放过
		TaskMarket taskMarket1 = TaskMarket.findByOpenidContractid(openid, contractid);
		if (taskMarket1!=null) {
			throw new QqException(QqErrorCode.awardHasGive);
		}
		//保存记录
		TaskMarket taskMarket2 = saveTaskMarket(userno, openid, appid, ts, version, contractid, step, 
				payitem, billno, pkey, sig);
		//赠送彩金
		if (StringUtils.isBlank(award)) {
			throw new QqException(QqErrorCode.paramError);
		}
		String presentResult = commonService.presentDividend(userno, award, channel, "应用宝任务奖励");
		if (!StringUtils.equals(presentResult, "0")) {
			throw new QqException(QqErrorCode.awardGiveFail);
		}
		taskMarket2.setAmt(new BigDecimal(award));
		taskMarket2.setUpdatetime(new Date());
		taskMarket2.setState(1);
		taskMarket2.merge();
	}
	
	private void checkTaskStep2(JSONObject userinfoObject) {
		/*//注册时间
		String regTime = userinfoObject.getString("regtime");
		regTime = DateParseFormatUtil.formatDate(regTime, "yyyy-MM-dd");
		//当前日期
		String today = DateParseFormatUtil.getPreDayDate(0, "yyyy-MM-dd");
		if (!StringUtils.equals(regTime, today)) {
			throw new QqException(QqErrorCode.notFinish);
		}*/
	}
	
	/**
	 * 检查任务是否完成
	 * @param userno
	 */
	private void checkTaskStep3(JSONObject userinfoObject) {
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
	
	private TaskMarket saveTaskMarket(String userno, String openid, String appid, String ts, String version, 
			String contractid, String step, String payitem, String billno, String pkey, String sig) {
		TaskMarket taskMarket = new TaskMarket();
		taskMarket.setUserno(userno);
		taskMarket.setOpenid(openid);
		taskMarket.setAppid(appid);
		taskMarket.setTs(ts);
		taskMarket.setVersion(version);
		taskMarket.setContractid(contractid);
		taskMarket.setStep(step);
		taskMarket.setPayitem(payitem);
		taskMarket.setBillno(billno);
		taskMarket.setPkey(pkey);
		taskMarket.setSig(sig);
		taskMarket.setCreatetime(new Date());
		taskMarket.setState(0);
		return taskMarket;
	}
	
	public static void main(String[] args) {
		/*String str = "aaaccc1399541144";
		System.out.println(Tools.md5(str));*/
		
		String str = "GET&%2Fv3%2Fuser%2Fget_info&appid%3D123456%26format%3Djson%26openid%3D11111111111111111%26openkey%3D2222222222222222%26pf%3Dqzone%26userip%3D112.90.139.30";
		System.out.println(Tools.hmac(str, "228bf094169a40a3bd188ba37ebe8723&"));
	}
	
}
