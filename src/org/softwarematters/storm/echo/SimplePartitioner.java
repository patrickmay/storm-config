/**
 * SimplePartitioner is a simple Kafka partitioner.
 *
 * @author Patrick May (patrick@softwarematters.org)
 * @author &copy; 2015 Patrick May.  All rights reserved.
 * @version 1
 */

package org.softwarematters.storm.echo;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimplePartitioner implements Partitioner
{
  private static Logger logger_
    = LoggerFactory.getLogger(SimplePartitioner.class);
  
  public SimplePartitioner (VerifiableProperties props)
    {
    }
 
  public int partition(Object key, int partitions)
    {
    int partition = 0;
    String stringKey = (String) key;
    int offset = stringKey.lastIndexOf('.');
    if (offset > 0)
      partition = Integer.parseInt( stringKey.substring(offset + 1))
                  % partitions;

    return partition;
    }
}  // end SimplePartitioner
