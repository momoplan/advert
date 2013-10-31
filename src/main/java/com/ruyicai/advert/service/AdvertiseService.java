package com.ruyicai.advert.service;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.controller.ResponseData;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.util.PropertiesUtil;
import com.ruyicai.advert.util.StringUtil;
import com.ruyicai.advert.util.VerifyUtil;

@Service
public class AdvertiseService {

	private Logger logger = Logger.getLogger(AdvertiseService.class);
	
	@Autowired
	private PropertiesUtil propertiesUtil;
	
	/**
	 * 力美广告通知
	 * @param request
	 * @param mac
	 * @param appId
	 * @param source
	 * @return
	 */
	public ResponseData limeiNotify(HttpServletRequest request, String mac, String appId, String source) {
		long startTimeMillis = System.currentTimeMillis();
		String ip = request.getHeader("X-Forwarded-For");
		logger.info("力美广告点击记录 start mac="+mac+";appId="+appId+";source="+source+";ip="+ip);
		//验证ip
		boolean verfyIp = VerifyUtil.verfyIp(ip, propertiesUtil.getLimei_ip());
		if (!verfyIp) {
			logger.error("力美广告点击记录,ip不合法 mac="+mac+";appId="+appId+";source="+source+";ip="+ip);
			return new ResponseData(false, "ip不合法");
		}
		//验证参数为空
		if (StringUtils.isBlank(mac)) {
			return new ResponseData(false, "参数为空");
		}
		//转换mac
		mac = transferMac(mac);
		//验证是否已激活
		boolean verifyActivate = verifyActivate(mac);
		if (!verifyActivate) {
			logger.error("力美广告点击记录,已被激活 mac="+mac);
			return new ResponseData(false, "已被激活");
		}
		List<AdvertiseInfo> list = AdvertiseInfo.getListByMacSourceAppid(mac, source, appId);
		if (list==null||list.size()==0) {
			//保存记录
			saveAdvertiseInfoByAppid(mac, appId, source);
			long endTimeMillis = System.currentTimeMillis();
			logger.info("力美广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",mac="+mac);
			return new ResponseData(true, "通知成功");
		} else {
			long endTimeMillis = System.currentTimeMillis();
			logger.info("力美广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",mac="+mac);
			return new ResponseData(false, "重复记录");
		}
	}
	
	/**
	 * 点入广告通知
	 * @param request
	 * @param drkey
	 * @param source
	 * @return
	 */
	public ResponseData dianruNotify(HttpServletRequest request, String drkey, String source) {
		long startTimeMillis = System.currentTimeMillis();
		String ip = request.getHeader("X-Forwarded-For");
		logger.info("点入广告点击记录 start drkey="+drkey+";source="+source+";ip="+ip);
		//验证ip
		boolean verfyIp = VerifyUtil.verfyIp(ip, propertiesUtil.getDianru_ip());
		if (!verfyIp) {
			logger.error("点入广告点击记录,ip不合法 drkey="+drkey+";source="+source+";ip="+ip);
			return new ResponseData(false, "ip不合法");
		}
		//验证参数
		if (StringUtils.isBlank(drkey) || drkey.length()<32) {
			return new ResponseData(false, "参数错误");
		}
		String mac = StringUtils.substring(drkey, 32); //mac地址
		//验证是否已激活
		boolean verifyActivate = verifyActivate(mac);
		if (!verifyActivate) {
			logger.error("点入广告点击记录,已被激活 drkey="+drkey);
			return new ResponseData(false, "已被激活");
		}
		List<AdvertiseInfo> list = AdvertiseInfo.getListByMacSourceDrkey(mac, source, drkey);
		if (list==null||list.size()==0) {
			//保存记录
			saveAdvertiseInfoByDrkey(mac, drkey, source);
			long endTimeMillis = System.currentTimeMillis();
			logger.info("点入广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",drkey="+drkey);
			return new ResponseData(true, "通知成功");
		} else {
			long endTimeMillis = System.currentTimeMillis();
			logger.info("点入广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",drkey="+drkey);
			return new ResponseData(false, "重复记录");
		}
	}
	
	/**
	 * 多盟广告通知
	 * @param mac
	 * @param appId
	 * @param source
	 * @param returnFormat
	 * @return
	 */
	/*public ResponseData domobNotify(String mac, String appId, String source, String returnFormat) {
		logger.info("多盟广告点击记录 start mac="+mac+";appId="+appId+";source="+source+";returnFormat="+returnFormat);
		List<AdvertiseInfo> list = AdvertiseInfo.getListByMacSourceAppid(mac, source, appId);
		if (list==null||list.size()==0) {
			//保存记录
			saveAdvertiseInfoByAppid(mac, appId, source);
			return new ResponseData(true, "通知成功");
		} else {
			return new ResponseData(false, "重复记录");
		}
	}*/
	
	/**
	 * 米迪广告通知
	 * @param request
	 * @param mac
	 * @param appid
	 * @param source
	 * @return
	 */
	public ResponseData miidiNotify(HttpServletRequest request, String mac, String appid, String source) {
		return new ResponseData(true, "通知成功");
	}
	
	private String transferMac(String mac) {
		if (StringUtils.indexOf(mac, ":")>-1 || StringUtils.indexOf(mac, "-")>-1) {
			return mac;
		} else {
			if (mac.length()==12) { //mac(不带":")
				mac = StringUtils.join(StringUtil.getStringArrayFromString(mac, 2), ":");
			}
		}
		return mac;
	}
	
	/**
	 * 验证是否已经激活
	 * @param mac
	 * @return true:未激活;false:已激活
	 */
	private boolean verifyActivate(String mac) {
		try {
			List<AdvertiseInfo> list = AdvertiseInfo.getListByMac(mac);
			if (list!=null && list.size()>0) {
				for (AdvertiseInfo advertiseInfo : list) {
					if (StringUtils.equals(advertiseInfo.getState(), "0")) { //已激活
						return false;
					}
				}
			}
		} catch (Exception e) {
			logger.info("广告通知-验证是否已经激活,mac="+mac+",发生异常:", e);
		}
		return true;
	}
	
	/**
	 * 根据appId保存广告记录
	 * @param mac
	 * @param appId
	 * @param source
	 */
	private void saveAdvertiseInfoByAppid(String mac, String appId, String source) {
		AdvertiseInfo advertiseInfo = new AdvertiseInfo();
		advertiseInfo.setMac(mac);
		advertiseInfo.setAppid(appId);
		advertiseInfo.setSource(source);
		advertiseInfo.setCreatetime(new Date());
		advertiseInfo.setUpdatetime(new Date());
		advertiseInfo.setState("1");
		advertiseInfo.persist();
	}
	
	/**
	 * 根据drkey保存广告记录
	 * @param mac
	 * @param drkey
	 * @param source
	 */
	private void saveAdvertiseInfoByDrkey(String mac, String drkey, String source) {
		AdvertiseInfo advertiseInfo = new AdvertiseInfo();
		advertiseInfo.setMac(mac);
		advertiseInfo.setDrkey(drkey);
		advertiseInfo.setSource(source);
		advertiseInfo.setCreatetime(new Date());
		advertiseInfo.setUpdatetime(new Date());
		advertiseInfo.setState("1");
		advertiseInfo.persist();
	}
	
	/*public static void main(String[] args) {
		String string = transferMac("abc123");
		System.out.println(string);
	}*/
	
}
