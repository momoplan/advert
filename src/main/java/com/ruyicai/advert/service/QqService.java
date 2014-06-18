package com.ruyicai.advert.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.consts.errorcode.QqErrorCode;
import com.ruyicai.advert.domain.QqTaskProgress;
import com.ruyicai.advert.domain.QqUserInfo;
import com.ruyicai.advert.domain.TaskMarket;
import com.ruyicai.advert.exception.QqException;
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
		if (StringUtils.isBlank(cmd)&&StringUtils.isBlank(openid)&&StringUtils.isBlank(appid)
				&&StringUtils.isBlank(ts)&&StringUtils.isBlank(version)&&StringUtils.isBlank(contractid)
				&&StringUtils.isBlank(step)&&StringUtils.isBlank(payitem)&&StringUtils.isBlank(billno)
				&&StringUtils.isBlank(pkey)&&StringUtils.isBlank(sig)) {
			return;
		}
		if (StringUtils.isBlank(cmd)||StringUtils.isBlank(openid)) {
			throw new QqException(QqErrorCode.paramError);
		}
		//验证签名
		verifySign(cmd, openid, appid, ts, version, contractid, step, payitem, billno, pkey, sig);
		//查询userno
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
				String award = "100"; //1元彩金
				//发放奖励
				taskAward(award, openid, appid, ts, version, contractid, step, payitem, billno, 
						pkey, sig, userno, channel);
			} else {
				throw new QqException(QqErrorCode.paramError);
			}
		} else if (StringUtils.equals(cmd, "Check")) { //查询用户是否完成该任务步骤(系统自动扫描时触发)
			if (StringUtils.equals(step, "2")) { //步骤2
				checkTask(userno, 2);
			} else if (StringUtils.equals(step, "3")) { //步骤3
				checkTask(userno, 1);
			} else {
				throw new QqException(QqErrorCode.paramError);
			}
		} else if (StringUtils.equals(cmd, "Check_award")) { //查询用户是否完成步骤,若完成,则给用户发放步骤礼包
			String award = "";
			if (StringUtils.equals(step, "2")) { //步骤2
				award = "200"; //2元彩金
				checkTask(userno, 2);
			} else if (StringUtils.equals(step, "3")) { //步骤3
				award = "500"; //5元彩金
				checkTask(userno, 1);
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
	
	@SuppressWarnings("unchecked")
	private void verifySign(String cmd, String openid, String appid, String ts, String version, 
			String contractid, String step, String payitem, String billno, String pkey, String sig) {
		try {
			String appkey = "ee7e8cdedcd8aab1faaf3300c20f8cff";
			//验证pkey
			StringBuilder pbuilder = new StringBuilder();
			pbuilder.append(openid).append(appkey).append(ts);
			if (!StringUtils.equals(Tools.md5(pbuilder.toString()), pkey)) {
				throw new QqException(QqErrorCode.pkeyError);
			}
			//验证sig
			Map<String, String> map = new HashMap<String, String>();
			map.put("cmd", cmd);
			map.put("openid", openid);
			map.put("appid", appid);
			map.put("ts", ts);
			map.put("version", version);
			map.put("contractid", contractid);
			map.put("step", step);
			map.put("payitem", payitem);
			map.put("billno", billno);
			map.put("pkey", pkey);
			map = Tools.sortMapByKey(map, false);
			StringBuilder sbuilder = new StringBuilder();
			for(Map.Entry<String, String> entry: map.entrySet()) {
				sbuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
			}
			String httpMethod = "GET";
			String uri = "/advert/qq/taskMarket";
			String param = StringUtils.stripEnd(sbuilder.toString(), "&");
			String str = httpMethod+"&"+URLEncoder.encode(uri, "UTF-8")+"&"+URLEncoder.encode(param, "UTF-8");
			String hmac = Tools.hmac(str, appkey+"&");
			if (!StringUtils.equals(hmac, sig)) {
				throw new QqException(QqErrorCode.sigError);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new QqException(QqErrorCode.exception);
		}
	}

	/**
	 * 检查任务是否完成
	 * @param userno
	 * @param step
	 */
	private void checkTask(String userno, Integer type) {
		QqTaskProgress taskProgress = QqTaskProgress.findByUsernoType(userno, type);
		if (taskProgress==null) {
			throw new QqException(QqErrorCode.notFinish);
		}
		if (type==1) { //购彩金额累积达到100元以上
			BigDecimal amt = taskProgress.getAmt();
			if (amt==null||amt.compareTo(new BigDecimal("10000"))==-1) {
				throw new QqException(QqErrorCode.notFinish);
			}
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
	
	/*@SuppressWarnings("unchecked")
	public static void main(String[] args) throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		Map<String, String> map = new HashMap<String, String>();
		map.put("cmd", "award");
		map.put("openid", "05ad35af3ae00b0043818973acd32207ae5b046a");
		map.put("appid", "100737758");
		map.put("ts", "1402986653");
		map.put("version", "V3M");
		map.put("contractid", "100737758T3M20140617121145");
		map.put("step", "1");
		map.put("payitem", "123");
		map.put("billno", "3e5078421962d8f1956d955128776cd8");
		map.put("pkey", "d3381ed62cf6b1c9d185abac96ead130");
		map = Tools.sortMapByKey(map, false);
		for(Map.Entry<String, String> entry: map.entrySet()) {
			builder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
		}
		String httpMethod = "GET";
		String uri = "/advert/qq/taskMarket";
		String param = StringUtils.stripEnd(builder.toString(), "&");
		String str = httpMethod+"&"+URLEncoder.encode(uri, "UTF-8")+"&"+URLEncoder.encode(param, "UTF-8");
		System.out.println(str);
		System.out.println(Tools.hmac(str, "ee7e8cdedcd8aab1faaf3300c20f8cff&"));
	}*/
	
}
