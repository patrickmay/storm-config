#!/bin/bash

${HOME}/packages/kafka_2.11-0.8.2.1/bin/kafka-topics.sh --create \
       --zookeeper docker-machine \
       --replication-factor 1 \
       --partitions 3 \
       --topic test.echo

