/**
 * KafkaProducer is a simple Kafka producer.
 *
 * @author Patrick May (patrick@softwarematters.org)
 * @author &copy; 2015 Patrick May.  All rights reserved.
 * @version 1
 */

package org.softwarematters.storm.echo;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaProducer
{
  private static final String MESSAGE_TOPIC = "test.echo";

  private static Logger logger_ = LoggerFactory.getLogger(KafkaProducer.class);
  private static Random generator_ = new Random();

  private Producer<String,String> producer_ = null;
  private long messageCount_ = 0;
  private int millisecondDelay_ = 0;


  /**
   * The full constructor for the KafkaProducer class.
   *
   * @param messageCount
   *        The number of messages to send.
   * @param millisecondDelay
   *        The amount of time to delay between sending each message.
   */
  public KafkaProducer(long messageCount,int millisecondDelay)
    {
    messageCount_ = messageCount;
    millisecondDelay_ = millisecondDelay;
    
    Properties props = new Properties();
    props.put("metadata.broker.list",
              "docker-machine:9092,docker-machine:9093,docker-machine:9094");
    props.put("serializer.class","kafka.serializer.StringEncoder");
    props.put("partitioner.class",
              "org.softwarematters.storm.echo.SimplePartitioner");
    props.put("request.required.acks","1");
 
    ProducerConfig config = new ProducerConfig(props);
    producer_ = new Producer<String,String>(config);
    }


  /**
   * Create a new random message.
   *
   * @return A random message.
   */
  private KeyedMessage<String,String> message()
    {
    long runtime = new Date().getTime();
    String ip = "192.168.2." + generator_.nextInt(255);
    
    return new KeyedMessage<String,String>(MESSAGE_TOPIC,
                                           ip,
                                           runtime + ",www.example.com," + ip);
    }


  /**
   * Send a message to Kafka.
   *
   * @param message
   *        The message to send.
   */
  private void send(KeyedMessage<String,String> message)
    {
    producer_.send(message);
    }
  

  /**
   * Send the specified number of messages.
   */
  private void run()
    {
    logger_.info("Running...");

    for (long count = 0;count < messageCount_;count++)
      {
      send(message());
      if (millisecondDelay_ > 0)
        {
        try { Thread.sleep(millisecondDelay_); }
        catch (Exception ignore) { }
        }
      }

    logger_.info("Done.");

    producer_.close();
    }


  /**
   * The main harness for the KafkaProducer class.
   *
   * @param args The command line arguments passed in.
   */
  public static void main(String args[])
    {
    if (args.length == 2)
      {
      long messageCount = Long.parseLong(args[0]);
      int messageDelay = Integer.parseInt(args[1]);
      KafkaProducer producer = new KafkaProducer(messageCount,messageDelay);
      producer.run();
      }
    else
      System.out.println(
        "Usage:  java KafkaProducer <message-count> <message-delay>");
    }
}  // end KafkaProducer
