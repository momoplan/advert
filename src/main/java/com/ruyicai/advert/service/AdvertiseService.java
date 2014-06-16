package com.ruyicai.advert.service;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.center.ScoreWall;
import com.ruyicai.advert.consts.errorcode.DomobErrorCode;
import com.ruyicai.advert.consts.errorcode.MopanErrorCode;
import com.ruyicai.advert.consts.errorcode.RuanlieErrorCode;
import com.ruyicai.advert.consts.errorcode.WangyuErrorCode;
import com.ruyicai.advert.consts.errorcode.YijifenErrorCode;
import com.ruyicai.advert.controller.ResponseData;
import com.ruyicai.advert.exception.DomobException;
import com.ruyicai.advert.exception.MopanException;
import com.ruyicai.advert.exception.RuanlieException;
import com.ruyicai.advert.exception.WangyuException;
import com.ruyicai.advert.exception.YijifenException;

@Service
public class AdvertiseService {

	private Logger logger = Logger.getLogger(AdvertiseService.class);
	
	@Autowired
	private AdvertManager advertManager;
	
	/**
	 * 力美广告点击记录
	 * @param request
	 * @param mac
	 * @param appId
	 * @param source
	 * @return
	 */
	public ResponseData limeiReceive(String ip, String mac, String appId, String source) {
		logger.info("力美广告点击记录 start mac="+mac+";appId="+appId+";source="+source+";ip="+ip);
		//验证参数为空
		if (StringUtils.isBlank(mac)) {
			return new ResponseData(false, "参数为空");
		}
		ScoreWall scoreWall = advertManager.getScoreWall("limei");
		if (scoreWall==null) {
			logger.error("力美广告点击记录未对接,mac="+mac+",appId="+appId+",source="+source+",ip="+ip);
			return new ResponseData(false, "未对接");
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("ip", ip);
		param.put("mac", mac);
		param.put("appId", appId);
		param.put("source", source);
		Map<String, Object> resultMap = scoreWall.receiveAdvertise(param);
		return new ResponseData((Boolean)resultMap.get("success"), (String)resultMap.get("message"));
	}
	
	/**
	 * 点入广告通知
	 * @param request
	 * @param drkey
	 * @param source
	 * @return
	 */
	public ResponseData dianruReceive(String ip, String drkey, String source) {
		//验证参数
		if (StringUtils.isBlank(drkey) || drkey.length()<32) {
			return new ResponseData(false, "参数不合法");
		}
		ScoreWall scoreWall = advertManager.getScoreWall("diarnu");
		if (scoreWall==null) {
			logger.error("点入广告点击记录未对接,drkey="+drkey+",source="+source+",ip="+ip);
			return new ResponseData(false, "未对接");
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("ip", ip);
		param.put("drkey", drkey);
		param.put("source", source);
		Map<String, Object> resultMap = scoreWall.receiveAdvertise(param);
		return new ResponseData((Boolean)resultMap.get("success"), (String)resultMap.get("message"));
	}
	
	/**
	 * 多盟广告通知
	 * @param mac
	 * @param appId
	 * @param source
	 * @param returnFormat
	 * @return
	 */
	public void domobReceive(String ip, String appId, String mac, String idfa, String source) {
		logger.info("多盟广告点击记录 start appId="+appId+";mac="+mac+";idfa="+idfa+";source="+source+";ip="+ip);
		//验证参数为空
		if (StringUtils.isBlank(mac)&&StringUtils.isBlank(idfa)) {
			throw new DomobException(DomobErrorCode.paramException);
		}
		ScoreWall scoreWall = advertManager.getScoreWall("domob");
		if (scoreWall==null) {
			logger.error("多盟广告点击记录未对接,mac="+mac+",idfa="+idfa+",ip="+ip);
			throw new DomobException(DomobErrorCode.exception);
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("ip", ip);
		param.put("appId", appId);
		param.put("mac", mac);
		param.put("idfa", idfa);
		param.put("source", source);
		scoreWall.receiveAdvertise(param);
	}
	
	/**
	 * 米迪广告通知
	 * @param request
	 * @param mac
	 * @param appid
	 * @param source
	 * @return
	 */
	public ResponseData miidiReceive(String ip, String mac, String appId, String source) {
		//验证参数
		if (StringUtils.isBlank(mac)) {
			return new ResponseData(false, "参数为空");
		}
		ScoreWall scoreWall = advertManager.getScoreWall("miidi");
		if (scoreWall==null) {
			logger.error("米迪广告点击记录未对接,mac="+mac+",appId="+appId+",source="+source+",ip="+ip);
			return new ResponseData(false, "未对接");
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("ip", ip);
		param.put("mac", mac);
		param.put("appId", appId);
		param.put("source", source);
		Map<String, Object> resultMap = scoreWall.receiveAdvertise(param);
		return new ResponseData((Boolean)resultMap.get("success"), (String)resultMap.get("message"));
	}
	
	/**
	 * 软猎广告通知
	 * @param ip
	 * @param mac
	 * @param idfa
	 * @param appId
	 */
	public void ruanlieReceive(String ip, String mac, String idfa, String appId) {
		logger.info("软猎广告点击记录 start mac="+mac+";idfa="+idfa+";appId="+appId+";ip="+ip);
		//验证参数
		if (StringUtils.isBlank(mac)&&StringUtils.isBlank(idfa)) {
			throw new RuanlieException(RuanlieErrorCode.paramException);
		}
		ScoreWall scoreWall = advertManager.getScoreWall("ruanlie");
		if (scoreWall==null) {
			logger.error("软猎广告点击记录未对接,mac="+mac+",idfa="+idfa+",appId="+appId+",ip="+ip);
			throw new RuanlieException(RuanlieErrorCode.exception);
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("ip", ip);
		param.put("mac", mac);
		param.put("idfa", idfa);
		param.put("appId", appId);
		scoreWall.receiveAdvertise(param);
	}
	
	/**
	 * 磨盘广告通知
	 * @param ip
	 * @param appId
	 * @param mac
	 * @param idfa
	 */
	public void mopanReceive(String ip, String appId, String mac) {
		logger.info("磨盘广告点击记录 start mac="+mac+";appId="+appId+";ip="+ip);
		//验证参数
		if (StringUtils.isBlank(mac)) {
			throw new MopanException(MopanErrorCode.paramException);
		}
		ScoreWall scoreWall = advertManager.getScoreWall("mopan");
		if (scoreWall==null) {
			logger.error("磨盘广告点击记录未对接,mac="+mac+",appId="+appId+",ip="+ip);
			throw new MopanException(MopanErrorCode.exception);
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("ip", ip);
		param.put("mac", mac);
		param.put("appId", appId);
		scoreWall.receiveAdvertise(param);
	}
	
	/**
	 * 网域广告通知
	 * @param ip
	 * @param cid
	 * @param deviceid
	 */
	public void wangyuReceive(String ip, String cid, String deviceid) {
		logger.info("网域广告点击记录 start mac="+deviceid+";cid="+cid+";ip="+ip);
		//验证参数
		if (StringUtils.isBlank(deviceid)) {
			throw new WangyuException(WangyuErrorCode.paramException);
		}
		ScoreWall scoreWall = advertManager.getScoreWall("wangyu");
		if (scoreWall==null) {
			logger.error("网域广告点击记录未对接,mac="+deviceid+",cid="+cid+",ip="+ip);
			throw new WangyuException(WangyuErrorCode.exception);
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("ip", ip);
		param.put("cid", cid);
		param.put("deviceid", deviceid);
		scoreWall.receiveAdvertise(param);
	}
	
	/**
	 * 易积分广告通知
	 * @param ip
	 * @param cid
	 * @param deviceid
	 */
	public void yijifenReceive(String ip, String appId, String deviceid, String source, String idfa) {
		logger.info("易积分广告点击记录 start mac="+deviceid+";source="+source+";idfa="+idfa+";ip="+ip);
		//验证参数
		if (StringUtils.isBlank(deviceid)&&StringUtils.isBlank(idfa)) {
			throw new YijifenException(YijifenErrorCode.paramException);
		}
		ScoreWall scoreWall = advertManager.getScoreWall("yijifen");
		if (scoreWall==null) {
			logger.error("易积分广告点击记录未对接,mac="+deviceid+",idfa="+idfa+",ip="+ip);
			throw new YijifenException(YijifenErrorCode.exception);
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("ip", ip);
		param.put("appId", appId);
		param.put("deviceid", deviceid);
		param.put("source", source);
		param.put("idfa", idfa);
		scoreWall.receiveAdvertise(param);
	}
	
}
