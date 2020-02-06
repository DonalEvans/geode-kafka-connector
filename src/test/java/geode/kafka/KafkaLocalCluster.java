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
package geode.kafka;

import java.io.IOException;
import java.util.Properties;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;

public class KafkaLocalCluster {

  KafkaServerStartable kafka;

  public KafkaLocalCluster(Properties kafkaProperties) throws IOException, InterruptedException {
    KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
    kafka = new KafkaServerStartable(kafkaConfig);
  }

  public void start() {
    try {
      kafka.startup();
      System.out.println("Kafka started up");
    } catch (Throwable t) {
      System.out.println(t);
    }
  }


  public void stop() {
    kafka.shutdown();
  }
}
