/**
 * EchoTopology is a simple Storm topology.
 *
 * @author Patrick May (patrick@softwarematters.org)
 * @author &copy; 2015 Patrick May.  All rights reserved.
 * @version 1
 */

package org.softwarematters.storm.echo;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import java.io.InputStream;
import java.io.IOException;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoTopology
{
  private static final String PROPERTIES_PROPERTY = "properties.file";
  private static final String ZOOKEEPER_PROPERTY = "zookeeper.servers";
  private static final String KAFKA_TOPIC_PROPERTY = "kafka.topic";

  private static Logger logger_
    = LoggerFactory.getLogger(EchoTopology.class);

  private TopologyBuilder builder_;
  private Config topologyConfig_;

  private Config loadProperties()
    {
    Config config = new Config();

    Map options = Utils.readCommandLineOpts();
    String propertiesFile = (String)options.get(PROPERTIES_PROPERTY);
    InputStream inputProps = EchoTopology.class
                             .getClassLoader()
                             .getResourceAsStream(propertiesFile);
    Properties props = new Properties();
    
    try
      {
      props.load(inputProps);

      for (String name : props.stringPropertyNames())
        config.put(name,props.getProperty(name));
      }
    catch (IOException ioe)
      {
      logger_.warn("Unable to load properties file.");
      }

    return config;
    }
  
  
  /**
   * The full constructor for the EchoTopology class.
   *
   */
  public EchoTopology()
    {
    topologyConfig_ = loadProperties();

    String zookeeper = (String)topologyConfig_.get(ZOOKEEPER_PROPERTY);
    String kafkaTopic = (String)topologyConfig_.get(KAFKA_TOPIC_PROPERTY);
    SpoutConfig spoutConfig
      = new SpoutConfig(new ZkHosts(zookeeper,"/brokers"),
                        kafkaTopic,
                        "/" + kafkaTopic,
                        UUID.randomUUID().toString());
    spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
    spoutConfig.forceFromStart = true;
    
    KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
    EchoBolt echoBolt = new EchoBolt();
    
    //Topology definition
    builder_ = new TopologyBuilder();
    builder_.setSpout("kafka-spout",kafkaSpout,1);
    builder_.setBolt("echo-bolt",echoBolt,3)
            .shuffleGrouping("kafka-spout");
    }


  /**
   * Run the topology.
   */
  private void submit()
    {
    try
      {
      StormSubmitter.submitTopology("Echo",
                                    topologyConfig_,
                                    builder_.createTopology());
      }
    catch (InvalidTopologyException ite)
      {
      }
    catch (AlreadyAliveException aae)
      {
      }
    }


  /**
   * The main harness for the EchoTopology class.
   *
   * @param args The command line arguments passed in.
   */
  public static void main(String args[])
    {
    EchoTopology topology = new EchoTopology();
    topology.submit();
    }
}  // end EchoTopology
