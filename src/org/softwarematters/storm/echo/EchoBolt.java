/**
 * EchoBolt is a simple Storm bolt implementation.
 *
 * @author Patrick May (patrick@softwarematters.org)
 * @author &copy; 2015 Patrick May.  All rights reserved.
 * @version 1
 */

package org.softwarematters.storm.echo;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoBolt extends BaseRichBolt
{
  private static Logger logger_ = LoggerFactory.getLogger(EchoBolt.class);

  private OutputCollector collector_;
  
  /**
   * The full constructor for EchoBolt.
   */
  public EchoBolt()
    {
    super();
    logger_.info("EchoBolt constructed.");
    }


  /**
   * Called on initialization.
   */
  public void prepare(Map stormConfig,
                      TopologyContext context,
                      OutputCollector collector)
    {
    collector_ = collector;

    logger_.info("Configuration name = "
                 + (String)stormConfig.get("config.name"));
    logger_.info("Configuration type = "
                 + (String)stormConfig.get("config.type"));
    
    logger_.info("EchoBolt prepared.");
    }


  /**
   * Process an input tuple.
   */
  public void execute(Tuple input)
    {
    logger_.info(input.getFields().toString());
    logger_.info(input.getStringByField(input.getFields().get(0)));
    collector_.ack(input);
    }


  /**
   * Inherited from IComponent
   */
  public void declareOutputFields(OutputFieldsDeclarer declarer)
    {
    }
}  // end EchoBolt
