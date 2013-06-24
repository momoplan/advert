package com.ruyicai.advert.jms;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RoutesConfiguration {
	
	private Logger logger = LoggerFactory.getLogger(RoutesConfiguration.class);

	@Resource(name="camelContext")
	private CamelContext camelContext;
	
	@PostConstruct
	public void init() {
		try {
			logger.info("init advert camel routes");
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					deadLetterChannel("jms:queue:dead").maximumRedeliveries(-1)
					.redeliveryDelay(3000);
					from("jms:queue:VirtualTopicConsumers.advert.notifyThirdParty?concurrentConsumers=5").to(
							"bean:notifyThirdPartyListener?method=notify").routeId("请求第三方的通知");
				}
			});
		} catch (Exception e) {
			logger.error("advert camel context start failed", e.getMessage());
			e.printStackTrace();
		}
	}
}
