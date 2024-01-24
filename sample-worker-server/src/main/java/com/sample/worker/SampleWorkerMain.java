package com.sample.worker;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Archetype
 */
@EnableBatchProcessing
@EnableBatchIntegration
@ComponentScan({ "com.sample.*", "com.batch.*" })
@SpringBootApplication
@Slf4j
public class SampleWorkerMain {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(SampleWorkerMain.class, args);
		ConfigurableEnvironment env = run.getEnvironment();
		log.info("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));
		final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
		StreamSupport.stream(sources.spliterator(), false).filter(ps -> ps instanceof EnumerablePropertySource)
				.map(ps -> ((EnumerablePropertySource) ps).getPropertyNames()).flatMap(Arrays::stream).distinct()
				.filter(prop -> !(prop.contains("credentials") || prop.contains("password")))
				.forEach(prop -> log.info("{}: {}", prop, env.getProperty(prop)));
		log.info("===========================================");
	}

}
