package com.sample.manager;

import java.util.Arrays;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@EnableJms
@Configuration
@PropertySource("classpath:application.properties")
public class SampleBatchMQConfiguration {

    public static final String BEAN_MQ_CONNECTION_FACTORY = "batchJobInputMQConnectionFactory";
    public static final String BEAN_MQ_JMS_TEMPLATE = "batchJobInputJMSTemplate";

    @Value("${batchframework.activemq.broker-url}")
    private String brokerUrl;

    @Value("${batchframework.activemq.user}")
    private String user;

    @Value("${batchframework.activemq.password}")
    private String password;

    @Bean()
    public ActiveMQConnectionFactory batchJobInputMQConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(this.brokerUrl);
        connectionFactory.setUserName(user);
        connectionFactory.setPassword(password);
        connectionFactory.setTrustAllPackages(false);
        connectionFactory.setTrustedPackages(Arrays.asList("com.*"));
        return connectionFactory;
    }

    @Bean(BEAN_MQ_JMS_TEMPLATE)
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(batchJobInputMQConnectionFactory());
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            @Qualifier(BEAN_MQ_CONNECTION_FACTORY) ActiveMQConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        defaultJmsListenerContainerFactory.setConnectionFactory(connectionFactory);
        return defaultJmsListenerContainerFactory;
    }
}
