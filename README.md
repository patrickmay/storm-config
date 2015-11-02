## Overview

This is an example of how to pass configuration parameters into a Storm topology.  It builds on [the simplest possible Storm topology that integrates with Kafka](https://github.com/patrickmay/kafka-storm).

As with the simple Echo topology, a producer based on [the Kafka examples](https://cwiki.apache.org/confluence/display/KAFKA/0.8.0+Producer+Example) is included.

## Configuration

Configuration files are stored in the ```config``` directory.  The two included are ```docker.properties``` and ```aws.properties```.

## Docker

This example is configured to run in Docker on OS X with an ```/etc/hosts``` entry for ```docker-machine``` (see [this blog post](https://softwarematters.wordpress.com/2015/09/27/docker-machine/) for more details on configuring this environment).  If it is running on a different configuration, the files ```bin/deploy-topology.sh``` and ```docker-config.yml``` must be modified to specify the correct Nimbus host and Kafka hosts, respectively.

## Build

The example depends on SLF4J, Zookeeper, Kafka, and Storm.  The directories for those packages are specified in ```build.properties```.  These must be set correctly for the build to succeed.

To build the jar files the first time, run ```ant build```.  To clean and build, run ```ant rebuild```.  This will create jar files in the ```lib``` directory.

## Run

If Docker is being used to run the example, the first step is to start the Docker containers running Zookeeper, Kafka, and Storm.  When Kafka and Storm are available, create the topic, deploy the topology then feed messages to Kafka:

1. ```docker-compose up &``` (if using Docker)
2. ```bin/create-topic.sh```
3. ```bin/deploy-topology-on-docker.sh```
4. ```bin/feed-kafka.sh```

To run with the faux AWS configuration file, replace step 3 with ```bin/deploy-topology-on-aws.sh```.

The configuration details are used by the topology to configure the Kafka spout.  The bolt in this topology writes a couple of the properties to the log file.  To view them, run ```docker ps -a``` to find the ID of the image running the Storm supervisor node, then:

```
docker exec -it <image-id> bash
cd /tmp/storm/logs
grep EchoBolt worker*.log | grep Configuration
```
