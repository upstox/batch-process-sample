package com.sample.trade.worker;

import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.MDC;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;

import com.batch.process.common.BatchConstant;
import com.batch.process.worker.service.AbstractWorkerConfiguration;
import com.sample.common.Constant;
import com.sample.common.TradeConstant;
import com.sample.common.util.DataFileLocationUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableIntegration
@ComponentScan({ "com.sample.*", "com.batch.*" })
@Configuration
public class TradeWorker extends AbstractWorkerConfiguration {

	private final ActiveMQConnectionFactory connectionFactory;
	private final DeliveryMarkingProcessor deliveryMarkingProcessor;

	protected TradeWorker(RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory,
			@Qualifier(BatchConstant.BEAN_ACTIVE_MQ_CONNECTION) ActiveMQConnectionFactory connectionFactory,
			DeliveryMarkingProcessor deliveryMarkingProcessor) {
		super(workerStepBuilderFactory);
		this.connectionFactory = connectionFactory;
		this.deliveryMarkingProcessor = deliveryMarkingProcessor;
	}

	@Override
	public String getJobType() {
		return TradeConstant.JOB_TYPE;
	}

	@Override
	public String getFileType() {
		return TradeConstant.FILE_TYPE;
	}

	@Override
	public String workerStepName() {
		return TradeConstant.WORKER_STEP_NAME;
	}

	@Bean(TradeConstant.WORKER_IN_CHANNEL)
	@Override
	public DirectChannel getInputChannel() {
		return new DirectChannel();
	}

	@Bean(TradeConstant.WORKER_OUT_CHANNEL)
	@Override
	public DirectChannel getOutputChannel() {
		return new DirectChannel();
	}

	@Bean(TradeConstant.WORKER_IN_FLOW)
	@Override
	public IntegrationFlow getInFlow(@Qualifier(TradeConstant.WORKER_IN_CHANNEL) DirectChannel inputChannel) {
		return IntegrationFlows
				.from(Jms.messageDrivenChannelAdapter(connectionFactory).destination(TradeConstant.WORKER_IN_CHANNEL))
				.channel(inputChannel).get();
	}

	@Bean(TradeConstant.WORKER_OUT_FLOW)
	@Override
	public IntegrationFlow getOutFlow(@Qualifier(TradeConstant.WORKER_OUT_CHANNEL) DirectChannel outputChannel) {
		return IntegrationFlows.from(outputChannel)
				.handle(Jms.outboundAdapter(connectionFactory).destination(TradeConstant.WORKER_OUT_CHANNEL)).get();
	}

	@Bean(TradeConstant.WORKER_STEP_NAME)
	public Step targetWorkerBean(@Qualifier(TradeConstant.WORKER_IN_CHANNEL) DirectChannel inputChannel,
			@Qualifier(TradeConstant.WORKER_OUT_CHANNEL) DirectChannel outputChannel) {
		return this.workerStepBuilderFactory.get(TradeConstant.WORKER_STEP_NAME).inputChannel(inputChannel)
				.outputChannel(outputChannel).listener(getWorkerStepListener()).tasklet(tasklet(null)).build();
	}

	@Bean("tradeWorkerTasklet")
	@StepScope
	@Override
	public Tasklet tasklet(@Value("#{stepExecutionContext['file']}") String partition) {
		return (contribution, chunkContext) -> {
			log.info("Worker invoked for partition : {}", partition);
			try {
				StepExecution stepExecution = contribution.getStepExecution();
				JobParameters jobParameters = stepExecution.getJobParameters();
				Map<String, Object> paramMap = new HashMap<>();
				jobParameters.getParameters().forEach((k, v) -> paramMap.put(k, v.toString()));
				MDC.put(Constant.JOB_ID, String.valueOf(stepExecution.getJobExecutionId()));
				ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();

				// Update the contextMap with info to be propagated to next step
				Map<String, String> contextMap = new HashMap<>();
				executionContext.entrySet().forEach(e -> {
					String key = e.getKey();
					contextMap.put(key, e.getValue().toString());
					log.info("Step Context map parameter : key: {}, value: {}", key, contextMap.get(key));
				});

				String jobId = stepExecution.getJobExecutionId().toString();
				Map<String, Object> metaInfoMap = new HashMap<>();
				metaInfoMap.put(Constant.JOB_ID, jobId);
				metaInfoMap.put(Constant.PARTITION, partition);

				metaInfoMap.put(TradeConstant.CONTEXT_KEY_FOLDER_TRADE_CODE_DATA, DataFileLocationUtil
						.getDataFile(partition, jobId, TradeConstant.CONTEXT_KEY_FOLDER_TRADE_CODE_DATA));

				metaInfoMap.put(TradeConstant.CONTEXT_KEY_FOLDER_TRADE_DATA, DataFileLocationUtil.getDataFile(partition,
						jobId, TradeConstant.CONTEXT_KEY_FOLDER_TRADE_DATA));

				deliveryMarkingProcessor.process(metaInfoMap, paramMap, contextMap);
			} catch (Exception e) {
				log.error("Exception occurred in worker ", e);
				throw e;
			} finally {
				MDC.remove(Constant.JOB_ID);
			}
			return RepeatStatus.FINISHED;
		};
	}

}
