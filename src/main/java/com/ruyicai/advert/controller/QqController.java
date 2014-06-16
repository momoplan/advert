package com.ruyicai.advert.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruyicai.advert.consts.QqErrorCode;
import com.ruyicai.advert.controller.resp.QqResponseData;
import com.ruyicai.advert.exception.QqException;
import com.ruyicai.advert.service.QqService;

@RequestMapping("/qq")
@Controller
public class QqController {

	private Logger logger = Logger.getLogger(QqController.class);
	
	@Autowired
	private QqService qqService;
	
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
	 * @return
	 */
	@RequestMapping(value = "/taskMarket")
	public @ResponseBody 
	QqResponseData taskMarket(@RequestParam("cmd") String cmd, @RequestParam("openid") String openid, 
			@RequestParam("appid") String appid, @RequestParam("ts") String ts, @RequestParam("version") String version,
			@RequestParam("contractid") String contractid, @RequestParam("step") String step,
			@RequestParam("payitem") String payitem, @RequestParam("billno") String billno,
			@RequestParam("pkey") String pkey, @RequestParam("sig") String sig) {
		QqErrorCode result = QqErrorCode.success;
		try {
			qqService.taskMarket(cmd, openid, appid, ts, version, contractid, step, payitem, billno, pkey, sig);
		} catch (QqException e) {
			result = e.getErrorCode();
			logger.info("应用宝任务集市处理内部错误,code:"+result.value+",memo:"+result.memo+",cmd:"+cmd+",openid:"+openid);
		} catch (Exception e) {
			result = QqErrorCode.exception;
			logger.error("应用宝任务集市处理发生异常,cmd:"+cmd+",openid:"+openid, e);
		}
		QqResponseData rd = new QqResponseData(result.value, result.memo, "");
		return rd;
	}
	
}
