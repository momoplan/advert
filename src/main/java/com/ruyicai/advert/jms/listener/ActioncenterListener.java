package com.ruyicai.advert.jms.listener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.advert.consts.AdvertiseSource;
import com.ruyicai.advert.consts.Platform;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.domain.QqTaskProgress;
import com.ruyicai.advert.domain.TregisterInfo;
import com.ruyicai.advert.domain.UserInf;
import com.ruyicai.advert.util.AdvertiseUtil;
import com.ruyicai.advert.util.StringUtil;

/**
 * 用户充值或购彩监听
 * @author Administrator
 *
 */
@Service
public class ActioncenterListener {

	private Logger logger = Logger.getLogger(ActioncenterListener.class);
	
	@Autowired
	private AdvertiseUtil advertiseUtil;
	
	public void process(@Header("TTRANSACTIONID") String ttransactionid,
			@Header("LADDERPRESENTFLAG") Long ladderpresentflag,
			@Header("USERNO") String userno, 
			@Header("TYPE") Integer type,
			@Header("BUSINESSID") String businessId,
			@Header("BUSINESSTYPE") Integer businessType, 
			@Header("AMT") Long amt,
			@Header("BANKID") String bankid) {
		try {
			logger.info("充值或购彩活动监听 start USERNO:"+userno+";TYPE:"+type+";BUSINESSTYPE:"+businessType+";AMT:"+amt
					+ ";TTRANSACTIONID=" + ttransactionid + ";BUSINESSID=" + businessId + ";LADDERPRESENTFLAG=" + ladderpresentflag
					+ ";BANKID=" + bankid);
			long startMillis = System.currentTimeMillis();
			if (type==null) {
				return;
			}
			/*if (type==2) { //1:充值;2:购彩
				updateByType(userno, 1, amt);
				if (businessType==null) {
					return;
				}
				if (businessType==3) { //1:订单投注或追号购买;3:合买投注
					updateByType(userno, 2, amt);
				}
			}*/
			if (type == 1) // 暂时只对用户充值行为进行处理
			{
				if (StringUtils.isBlank(userno))
					return;
				
				List<TregisterInfo> tregisterInfoList = TregisterInfo.findByUserno(userno);
				if (tregisterInfoList == null || tregisterInfoList.size() == 0)
					return;
				
				TregisterInfo tregisterInfo = tregisterInfoList.get(0);
				if (!StringUtils.equals(tregisterInfo.getPlatform(), Platform.iPhone.value()))
					return;
				
				List<UserInf> list = UserInf.getListByMacPlatform(tregisterInfo.getMac(), Platform.iPhone.value());
				if (list == null || list.size() == 0)
					return;
				
				UserInf userInf = list.get(0);
				String source = AdvertiseSource.getSourceByCoopId(userInf.getChannel());
				if (StringUtil.isEmpty(source))//通过其他渠道激活
					return;
				
				// 按首次充值算
				if (!StringUtils.equals(source, AdvertiseSource.mopan.value())
						&& !StringUtils.equals(source, AdvertiseSource.yijifen.value()))
					return;
				
				//通知第三方积分墙
				logger.info("充值成功通知第三方积分墙 userno=" + userno + ";ttransactionid=" + ttransactionid + ";mac=" + tregisterInfo.getMac());
				AdvertiseInfo advertiseInfo = advertiseUtil.getValidAdvertiseInfo(tregisterInfo.getMac(), source);
				advertiseUtil.notifyThirdParty(advertiseInfo);
			}
			else
			{
				return;
			}
			long endMillis = System.currentTimeMillis();
			logger.info("充值或购彩活动监听  end,用时:"+(endMillis-startMillis)+" userno:"+userno+";type:"+type+
					";businessType:"+businessType+";amt:"+amt+ ";ttransactionid=" + ttransactionid);
		} catch (Exception e) {
			logger.error("充值或购彩活动监听异常,userno:"+userno, e);
		}
	}
	
	private void updateByType(String userno, Integer type, long amt) {
		try {
			QqTaskProgress taskProgress = QqTaskProgress.findByUsernoType(userno, type);
			if (taskProgress==null) {
				taskProgress = new QqTaskProgress();
				taskProgress.setUserno(userno);
				taskProgress.setType(type);
				taskProgress.setAmt(new BigDecimal(amt));
				taskProgress.setCreatetime(new Date());
				taskProgress.persist();
			} else {
				taskProgress.setAmt(taskProgress.getAmt().add(new BigDecimal(amt)));
				taskProgress.setUpdatetime(new Date());
				taskProgress.merge();
			}
		} catch (Exception e) {
			logger.error("活动监听-updateByType发生异常,userno:"+userno+";type:"+type, e);
		}
	}
	
}
