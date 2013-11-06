package com.ruyicai.advert.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruyicai.advert.service.ScoreService;

/**
 * 积分墙积分Controller
 * @author Administrator
 *
 */
@RequestMapping("/score")
@Controller
public class ScoreController {

	private Logger logger = Logger.getLogger(ScoreController.class);
	
	@Autowired
	private ScoreService scoreService;
	
	/**
	 * 力美积分墙加积分通知
	 * @param aduid
	 * @param uid
	 * @param aid
	 * @param point
	 * @param source
	 * @param sign
	 * @param timestamp
	 * @return
	 */
	@RequestMapping(value = "/limeiNotify", method = RequestMethod.GET)
	public @ResponseBody ResponseDataScore limeiNotify(HttpServletRequest request, @RequestParam("aduid") String aduid, 
			@RequestParam("uid") String uid, @RequestParam("aid") String aid, @RequestParam("point") String point, 
			@RequestParam("source") String source, @RequestParam("sign") String sign, @RequestParam("timestamp") String timestamp,
			@RequestParam("idfa") String idfa) {
		try {
			long startmillis = System.currentTimeMillis();
			String ip = request.getHeader("X-Forwarded-For");
			ResponseDataScore response = scoreService.limeiAddScore(ip, aduid, uid, aid, point, source, sign, timestamp, idfa);
			long endmillis = System.currentTimeMillis();
			logger.info("力美积分墙加积分通知,用时:"+(endmillis-startmillis)+";aid="+aid+";sign="+sign);
			return response;
		} catch (Exception e) {
			logger.error("力美积分墙加积分通知发生异常", e);
			return new ResponseDataScore("500", "赠送失败");
		}
	}
	
}
