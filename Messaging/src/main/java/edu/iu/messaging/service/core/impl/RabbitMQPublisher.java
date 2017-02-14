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
import edu.iu.messaging.service.util.MessageContext;
import edu.iu.messaging.service.core.Publisher;
import edu.iu.messaging.service.model.Message;
import edu.iu.messaging.service.util.RabbitMQProperties;
import edu.iu.messaging.service.util.ThriftUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import java.io.IOException;

public class RabbitMQPublisher implements Publisher {

    private static final Logger logger = LogManager.getLogger(RabbitMQPublisher.class);

    //private final Function<MessageContext, String> routingKeySupplier;
    private Connection connection;
    private final RabbitMQProperties properties;
    private Channel channel;
    private String routingKey;


    public RabbitMQPublisher(RabbitMQProperties properties, String routingKey) {
        this.properties = properties;
        this.routingKey = routingKey;
        connect();
    }

    private void connect(){
        try {
            logger.info("connect() -> Connecting to server");
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setUri(properties.getBrokerUrl());
            connectionFactory.setAutomaticRecoveryEnabled(properties.isAutoRecoveryEnable());
            connection = connectionFactory.newConnection();
            connection.addShutdownListener(new ShutdownListener() {
                public void shutdownCompleted(ShutdownSignalException cause) {
                }
            });

            channel = connection.createChannel();

            /*
            Not required for work queue implementation
             */
            //channel.basicQos(properties.getPrefetchCount());
            //channel.exchangeDeclare(properties.getExchangeName(), properties.getExchangeType(), true);

        } catch (Exception e) {
            logger.error("connect() -> Error connecting to server.", e);
        }


    }


    @Override
    public void publish(MessageContext messageContext) throws Exception{
        logger.info("publish() -> Publishing message. Message Id : " + messageContext.getMessageId());

        byte[] body = ThriftUtils.serializeThriftObject(messageContext.getEvent());
        Message message = new Message();
        message.setEvent(body);
        message.setMessageId(messageContext.getMessageId());

        byte[] messageBody = ThriftUtils.serializeThriftObject(message);

        send(messageBody, routingKey);
        logger.info("publish() -> Message Sent. Message Id : " + messageContext.getMessageId());

    }

    public void send(byte []message, String routingKey) throws Exception {
        try {
            channel.basicPublish(properties.getExchangeName(), routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        } catch (IOException e) {
            logger.error("send() -> Error sending message.", e);
        }
    }
}
