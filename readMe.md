# Sample Database

Create a database with the script provided below -

	CREATE DATABASE `medium_demo`;
	USE `medium_demo`
	CREATE TABLE `medium_demo`.`trade` (
	  `id` int NOT NULL AUTO_INCREMENT,
	  `code` varchar(6) NOT NULL,
	  `scrip_name` varchar(20) NOT NULL,
	  `bs` varchar(1) NOT NULL,
	  `qty` int NOT NULL,
	  PRIMARY KEY (`id`),
	  KEY `idx_trade_code` (`code`)
	) 

Import sample trade data made available in the repository

	./mysql --local-infile=1 -u root -p 
	Enter password: 
	Welcome to the MySQL monitor.  Commands end with ; or \g.
	Your MySQL connection id is 3854
	Server version: 8.0.23 MySQL Community Server - GPL
	mysql> LOAD DATA LOCAL  INFILE '/Users/developer/medium-blog/batch-process-sample/sample-manager-server/trades.csv' INTO TABLE medium_demo.trade  FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n';
	Query OK, 10000000 rows affected (1 min 25.67 sec)
	Records: 10000000  Deleted: 0  Skipped: 0  Warnings: 0
	

# Spring Batch Database

Create the Spring batch database using the script in **batch-process-sample/batch-ddl.sql**

# Configuration Changes

Change application.properties of manager and worker Spring Applications and worker Spring Applications

* batch-process-sample/sample-manager-server/src/main/resources/application.properties

	batch-run-data.datasource.username=root	
	batch-run-data.datasource.password=root
	# Below 2 properties come into play when profile is not dev. Ignored in dev profile
	batch.storage.regions[0]=ap-south-1
	batch.storage.buckets[0]=demo
	batch.storage.saveLocations[0]=/Users/developer/Projects/blog  — Change this to an existing folder path

* batch-process-sample/sample-worker-server/src/main/resources/application.properties

	batch-run-data.datasource.username=root	
	batch-run-data.datasource.password=root
	# Below 2 properties come into play when profile is not dev. Ignored in dev profile
	batch.storage.regions[0]=ap-south-1
	batch.storage.buckets[0]=demo
	batch.storage.saveLocations[0]=/Users/developer/Projects/blog  — Change this to an existing folder path

# ActiveMQ
Install ActiveMQ and start it locally

# Build
	
	cd batch-process
	mvn clean install -Pspring.profiles.active=dev

	cd batch-process-sample
	mvn clean install -Pspring.profiles.active=dev

# Run

	#To launch Manager
	java -jar -Dspring.profiles.active=dev batch-process-sample/sample-manager-server/target/sample-manager-server-0.0.1-SNAPSHOT.jar
	
	#To launch Worker
	java -jar -Dspring.profiles.active=dev batch-process-sample/sample-worker-server/target/sample-worker-server-0.0.1-SNAPSHOT.jar
	
	#To execute the job
	Use the curl made available at - batch-process-sample/sample-manager-server/sample-curl.txt
	
	curl --location --request POST 'localhost:60000/process' \
	--header 'Content-Type: application/json' \
	--data-raw '{
	    "jobType":"process-trade"
	}'

# Note
If launching from IDE pass **-Dspring.profiles.active=dev** to both manager and worker applications as environment values