package com.sample.worker;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
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
import com.sample.common.SampleConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableIntegration
@ComponentScan({ "com.sample.*", "com.batch.*" })
@Configuration
public class SampleWorker extends AbstractWorkerConfiguration {

    private final ActiveMQConnectionFactory connectionFactory;

    protected SampleWorker(RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory,
            @Qualifier(BatchConstant.BEAN_ACTIVE_MQ_CONNECTION) ActiveMQConnectionFactory connectionFactory) {
        super(workerStepBuilderFactory);
        this.connectionFactory = connectionFactory;
    }

    @Override
    public String getJobType() {
        return SampleConstants.JOB_TYPE;
    }

    @Override
    public String getFileType() {
        return SampleConstants.FILE_TYPE;
    }

    @Override
    public String workerStepName() {
        return SampleConstants.WORKER_STEP_NAME;
    }

    @Bean(SampleConstants.WORKER_IN_CHANNEL)
    @Override
    public DirectChannel getInputChannel() {
        return new DirectChannel();
    }

    @Bean(SampleConstants.WORKER_OUT_CHANNEL)
    @Override
    public DirectChannel getOutputChannel() {
        return new DirectChannel();
    }

    @Bean(SampleConstants.WORKER_IN_FLOW)
    @Override
    public IntegrationFlow getInFlow(@Qualifier(SampleConstants.WORKER_IN_CHANNEL) DirectChannel inputChannel) {
        return IntegrationFlows.from(Jms.messageDrivenChannelAdapter(connectionFactory)
                .destination(SampleConstants.WORKER_IN_CHANNEL))
                .channel(inputChannel)
                .get();
    }

    @Bean(SampleConstants.WORKER_OUT_FLOW)
    @Override
    public IntegrationFlow getOutFlow(@Qualifier(SampleConstants.WORKER_OUT_CHANNEL) DirectChannel outputChannel) {
        return IntegrationFlows.from(outputChannel)
                .handle(Jms.outboundAdapter(connectionFactory)
                        .destination(SampleConstants.WORKER_OUT_CHANNEL))
                .get();
    }

    @Bean(SampleConstants.WORKER_STEP_NAME)
    public Step targetWorkerBean(@Qualifier(SampleConstants.WORKER_IN_CHANNEL) DirectChannel inputChannel,
            @Qualifier(SampleConstants.WORKER_OUT_CHANNEL) DirectChannel outputChannel) {
        return this.workerStepBuilderFactory.get(SampleConstants.WORKER_STEP_NAME)
                .inputChannel(inputChannel)
                .outputChannel(outputChannel)
                .listener(getWorkerStepListener())
                .tasklet(tasklet(null))
                .build();
    }

    @Bean("sampleWorkerTasklet")
    @StepScope
    @Override
    public Tasklet tasklet(@Value("#{stepExecutionContext['file']}") String partition) {
        return (contribution, chunkContext) -> {
            log.info("Worker invoked for partition : {}", partition);
            return RepeatStatus.FINISHED;
        };
    }

}
