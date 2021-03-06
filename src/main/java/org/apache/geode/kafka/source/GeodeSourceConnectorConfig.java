/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.kafka.source;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;

import org.apache.geode.kafka.GeodeConnectorConfig;

public class GeodeSourceConnectorConfig extends GeodeConnectorConfig {

  public static final ConfigDef SOURCE_CONFIG_DEF = configurables();

  // Geode Configuration
  public static final String DURABLE_CLIENT_ID_PREFIX = "durable-client-id-prefix";
  public static final String DEFAULT_DURABLE_CLIENT_ID = "";
  public static final String DURABLE_CLIENT_TIME_OUT = "durable-client-timeout";
  public static final String DEFAULT_DURABLE_CLIENT_TIMEOUT = "60000";

  public static final String CQ_PREFIX = "cq-prefix";
  public static final String DEFAULT_CQ_PREFIX = "cqForGeodeKafka";

  /**
   * Used as a key for source partitions
   */
  public static final String REGION_PARTITION = "regionPartition";
  public static final String REGION_TO_TOPIC_BINDINGS = "region-to-topics";
  public static final String DEFAULT_REGION_TO_TOPIC_BINDING = "[gkcRegion:gkcTopic]";
  public static final String CQS_TO_REGISTER = "cqsToRegister"; // used internally so that only 1
                                                                // task will register a cq

  public static final String BATCH_SIZE = "geode-connector-batch-size";
  public static final String DEFAULT_BATCH_SIZE = "100";

  public static final String QUEUE_SIZE = "geode-connector-queue-size";
  public static final String DEFAULT_QUEUE_SIZE = "10000";

  public static final String LOAD_ENTIRE_REGION = "load-entire-region";
  public static final String DEFAULT_LOAD_ENTIRE_REGION = "false";

  private final String durableClientId;
  private final String durableClientTimeout;
  private final String cqPrefix;
  private final boolean loadEntireRegion;
  private final int batchSize;
  private final int queueSize;

  private Map<String, List<String>> regionToTopics;
  private Collection<String> cqsToRegister;

  public GeodeSourceConnectorConfig(Map<String, String> connectorProperties) {
    super(SOURCE_CONFIG_DEF, connectorProperties);
    cqsToRegister = parseRegionToTopics(getString(CQS_TO_REGISTER)).keySet();
    regionToTopics = parseRegionToTopics(getString(REGION_TO_TOPIC_BINDINGS));
    String durableClientIdPrefix = getString(DURABLE_CLIENT_ID_PREFIX);
    if (isDurable(durableClientIdPrefix)) {
      durableClientId = durableClientIdPrefix + taskId;
    } else {
      durableClientId = "";
    }
    durableClientTimeout = getString(DURABLE_CLIENT_TIME_OUT);
    cqPrefix = getString(CQ_PREFIX);
    loadEntireRegion = getBoolean(LOAD_ENTIRE_REGION);
    batchSize = getInt(BATCH_SIZE);
    queueSize = getInt(QUEUE_SIZE);
  }

  protected static ConfigDef configurables() {
    ConfigDef configDef = GeodeConnectorConfig.configurables();
    configDef.define(CQS_TO_REGISTER, ConfigDef.Type.STRING, "", ConfigDef.Importance.HIGH,
        "Internally created and used parameter, for signalling a task to register cqs");
    configDef.define(REGION_TO_TOPIC_BINDINGS, ConfigDef.Type.STRING,
        DEFAULT_REGION_TO_TOPIC_BINDING, ConfigDef.Importance.HIGH,
        "A comma separated list of \"one region to many topics\" mappings.  Each mapping is surrounded by brackets.  For example \"[regionName:topicName], \"[anotherRegion: topicName, anotherTopic]\"");
    configDef.define(DURABLE_CLIENT_ID_PREFIX, ConfigDef.Type.STRING, DEFAULT_DURABLE_CLIENT_ID,
        ConfigDef.Importance.LOW,
        "Prefix string for tasks to append to when registering as a durable client.  If empty string, will not register as a durable client");
    configDef.define(DURABLE_CLIENT_TIME_OUT, ConfigDef.Type.STRING, DEFAULT_DURABLE_CLIENT_TIMEOUT,
        ConfigDef.Importance.LOW,
        "How long in milliseconds to persist values in Geode's durable queue before the queue is invalidated");
    configDef.define(CQ_PREFIX, ConfigDef.Type.STRING, DEFAULT_CQ_PREFIX, ConfigDef.Importance.LOW,
        "Prefix string to identify Connector cq's on a Geode server");
    configDef.define(BATCH_SIZE, ConfigDef.Type.INT, DEFAULT_BATCH_SIZE,
        ConfigDef.Importance.MEDIUM, "Maximum number of records to return on each poll");
    configDef.define(QUEUE_SIZE, ConfigDef.Type.INT, DEFAULT_QUEUE_SIZE,
        ConfigDef.Importance.MEDIUM,
        "Maximum number of entries in the connector queue before backing up all Geode cq listeners sharing the task queue ");
    configDef.define(LOAD_ENTIRE_REGION, ConfigDef.Type.BOOLEAN, DEFAULT_LOAD_ENTIRE_REGION,
        ConfigDef.Importance.MEDIUM,
        "Determines if we should queue up all entries that currently exist in a region.  This allows us to copy existing region data.  Will be replayed whenever a task needs to re-register a cq");
    return configDef;
  }

  public boolean isDurable() {
    return isDurable(durableClientId);
  }

  /**
   * @param durableClientId or prefix can be passed in. Either both will be "" or both will have a
   *        value
   */
  boolean isDurable(String durableClientId) {
    return !durableClientId.equals("");
  }

  public int getTaskId() {
    return taskId;
  }

  public String getDurableClientId() {
    return durableClientId;
  }

  public String getDurableClientTimeout() {
    return durableClientTimeout;
  }

  public String getCqPrefix() {
    return cqPrefix;
  }

  public boolean getLoadEntireRegion() {
    return loadEntireRegion;
  }

  public Map<String, List<String>> getRegionToTopics() {
    return regionToTopics;
  }

  public Collection<String> getCqsToRegister() {
    return cqsToRegister;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public int getQueueSize() {
    return queueSize;
  }
}
