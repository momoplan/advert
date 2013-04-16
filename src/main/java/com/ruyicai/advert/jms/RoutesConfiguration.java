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

	@Resource(name="advertCamelContext")
	private CamelContext advertCamelContext;
	
	@PostConstruct
	public void init() throws Exception{
		logger.info("init advertCamel routes");
		advertCamelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				deadLetterChannel("jms:queue:dead").maximumRedeliveries(-1)
				.redeliveryDelay(3000);
				from("jmsAdvert:queue:VirtualTopicConsumers.advert.notifyThirdParty").to("bean:notifyThirdPartyListener?method=notify").routeId("请求第三方的通知");
			}
		});
	}
}
