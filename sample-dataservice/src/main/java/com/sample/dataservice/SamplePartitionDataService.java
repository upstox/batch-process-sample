package com.sample.dataservice;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

import com.batch.process.common.BatchConstant;
import com.batch.process.dataservice.PartitionDataService;
import com.sample.common.SampleConstants;

@Configuration
public class SamplePartitionDataService implements PartitionDataService<String> {

	@Override
	public String getDataServiceName() {
		return SamplePartitionDataService.class.getSimpleName();
	}

	@Bean(SampleConstants.MANAGER_OUT_CHANNEL)
	public DirectChannel requestChannel() {
		return new DirectChannel();
	}

	@Bean(SampleConstants.MANAGER_OUT_FLOW)
	public IntegrationFlow outboundFlow(@Qualifier(SampleConstants.MANAGER_OUT_CHANNEL) DirectChannel requestChannel,
			@Qualifier(BatchConstant.BEAN_ACTIVE_MQ_CONNECTION) ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(requestChannel)
				.handle(Jms.outboundAdapter(connectionFactory).destination(SampleConstants.MANAGER_OUT_CHANNEL)).get();
	}

	@Bean(SampleConstants.MANAGER_IN_CHANNEL)
	public DirectChannel replyChannel() {
		return new DirectChannel();
	}

	@Bean(SampleConstants.MANAGER_IN_FLOW)
	public IntegrationFlow inboundFlow(@Qualifier(SampleConstants.MANAGER_IN_CHANNEL) DirectChannel repliesChannel,
			@Qualifier(BatchConstant.BEAN_ACTIVE_MQ_CONNECTION) ActiveMQConnectionFactory connectionFactory) {
		return IntegrationFlows.from(
				Jms.messageDrivenChannelAdapter(connectionFactory).destination(SampleConstants.MANAGER_IN_CHANNEL))
				.channel(repliesChannel).get();
	}

	@Override
	public String getWorkerstepName() {
		return SampleConstants.WORKER_STEP_NAME;
	}
}
