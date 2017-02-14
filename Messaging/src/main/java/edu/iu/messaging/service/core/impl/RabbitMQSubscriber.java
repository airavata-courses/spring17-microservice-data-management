package edu.iu.messaging.service.core.impl;/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

import com.rabbitmq.client.*;
import edu.iu.messaging.service.core.Subscriber;
import edu.iu.messaging.service.util.Constants;
import edu.iu.messaging.service.util.RabbitMQProperties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;


public class RabbitMQSubscriber implements Subscriber {

    private static final Logger logger = LogManager.getLogger(RabbitMQSubscriber.class);

    private Connection connection;
    private Channel channel;
    private Map<String, QueueDetail> queueDetailMap = new HashMap<>();
    private RabbitMQProperties properties;

    public RabbitMQSubscriber(RabbitMQProperties properties){
        this.properties = properties;
        createConnection();
    }

    private void createConnection() {
        try {
            logger.info("createConnection() -> Connecting to server");
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setUri(properties.getBrokerUrl());
            connectionFactory.setAutomaticRecoveryEnabled(properties.isAutoRecoveryEnable());
            connection = connectionFactory.newConnection();
            addShutdownListener();
            channel = connection.createChannel();

            /*
            Not required for work queue implementation
             */
            //channel.basicQos(Constants.PREFETCH_COUT);
            //channel.exchangeDeclare(properties.getExchangeName(), properties.getExchangeType(), true);

        } catch (Exception e) {
            logger.error("createConnection() -> Error connecting to server.", e);
        }
    }

    @Override
    public String listen(BiFunction<Connection, Channel, Consumer> supplier,
                         String queueName,
                         List<String> routingKeys){

        try {
            if (!channel.isOpen()) {
                channel = connection.createChannel();

                /*
                Not required for work queue implementation
                 */
                //channel.exchangeDeclare(properties.getExchangeName(), properties.getExchangeType(), true);
            }
            if (queueName == null) {
                queueName = channel.queueDeclare().getQueue();
            } else {
                channel.queueDeclare(queueName,
                                     true, // durable
                                     false, // exclusive
                                     false, // autoDelete
                                     null);// arguments
            }
            final String id = getId(routingKeys, queueName);
            if (queueDetailMap.containsKey(id)) {
                throw new IllegalStateException("This subscriber is already defined for this Subscriber, " +
                        "cannot define the same subscriber twice");
            }
            // bind all the routing keys
            for (String key : routingKeys) {
                channel.queueBind(queueName, properties.getExchangeName(), key);
            }

            channel.basicConsume(queueName,
                    properties.isAutoAck(),
                    properties.getConsumerTag(),
                    supplier.apply(connection, channel));

            queueDetailMap.put(id, new QueueDetail(queueName, routingKeys));
            return id;
        } catch (IOException e) {
            logger.error("listen() -> Error listening to queue. Queue Name : " + queueName, e);
        }
        return "-1";
    }

    @Override
    public void stopListen(String id){
        QueueDetail details = queueDetailMap.get(id);
        if (details != null) {
            try {
                for (String key : details.getRoutingKeys()) {
                    channel.queueUnbind(details.getQueueName(), properties.getExchangeName(), key);
                }
                //channel.queueDelete(details.getQueueName(), true, true);
            } catch (IOException e) {
                logger.error("stopListen() -> Error listening to queue. Id : " + id, e);
            }
        }
    }

    @Override
    public void sendAck(long deliveryTag) throws IOException {

    logger.info("sendAck() -> Sending ack. Delivery Tag : " + deliveryTag);

        if (channel.isOpen()){
            channel.basicAck(deliveryTag,false);
        }else {
            channel = connection.createChannel();
            channel.basicQos(properties.getPrefetchCount());
            channel.basicAck(deliveryTag, false);
        }
    }

    private void addShutdownListener() {
        connection.addShutdownListener(new ShutdownListener() {
            public void shutdownCompleted(ShutdownSignalException cause) {
            }
        });
    }


    private String getId(List<String> routingKeys, String queueName) {
        String id = "";
        for (String key : routingKeys) {
            id = id + "_" + key;
        }
        return id + "_" + queueName;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException ignore) {
            }
        }
    }


    private class QueueDetail {
        String queueName;
        List<String> routingKeys;

        private QueueDetail(String queueName, List<String> routingKeys) {
            this.queueName = queueName;
            this.routingKeys = routingKeys;
        }

        public String getQueueName() {
            return queueName;
        }

        List<String> getRoutingKeys() {
            return routingKeys;
        }
    }
}
