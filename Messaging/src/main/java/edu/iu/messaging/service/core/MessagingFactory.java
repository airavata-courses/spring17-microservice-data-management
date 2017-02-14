/*
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
package edu.iu.messaging.service.core;

import edu.iu.messaging.service.core.impl.CustomerConsumer;
import edu.iu.messaging.service.core.impl.OrderConsumer;
import edu.iu.messaging.service.core.impl.RabbitMQPublisher;
import edu.iu.messaging.service.core.impl.RabbitMQSubscriber;
import edu.iu.messaging.service.util.Constants;
import edu.iu.messaging.service.util.RabbitMQProperties;
import edu.iu.messaging.service.util.Type;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class MessagingFactory {

    private static final Logger logger = LogManager.getLogger(MessagingFactory.class);

    public static Publisher getPublisher(Type type) {

        logger.info("getPublisher() -> Creating Publisher. Type : " + type);

        RabbitMQProperties rProperties = getProperties();
        Publisher publisher = null;
        switch (type) {
            case ORDER:
                publisher = getOrderPublisher(rProperties);
                break;
            case CUSTOMER:
                publisher = getCustomerPublisher(rProperties);
                break;
        }

        logger.info("getPublisher() -> Publisher created. Type : " + type);
        return publisher;
    }

    public static Subscriber getSubscriber(final MessageHandler messageHandler, List<String> routingKeys, Type type) {

        logger.info("getSubscriber() -> Creating subscriber. Routing keys : " + routingKeys.toString() + ", Type : " + type);

        RabbitMQProperties rProperties = getProperties();
        Subscriber subscriber = null;
        switch (type) {
            case ORDER:
                subscriber = getOrderSubscriber(rProperties);
                subscriber.listen(((connection, channel) -> new OrderConsumer(messageHandler, connection, channel)),
                        rProperties.getQueueName(),
                        routingKeys);
                break;
            case CUSTOMER:
                subscriber = getCustomerSubscriber(rProperties);
                subscriber.listen(((connection, channel) -> new CustomerConsumer(messageHandler, connection, channel)),
                        rProperties.getQueueName(),
                        routingKeys);
                break;
        }

        logger.debug("getSubscriber() -> Subscriber created. Routing keys : " + routingKeys.toString() + ", Type : " + type);

        return subscriber;
    }

    public static Publisher getCustomerPublisher(RabbitMQProperties rProperties){
        rProperties.setExchangeName(Constants.CUSTOMER_EXCHANGE_NAME);
        logger.info("getCustomerPublisher() -> Fetching customer publisher. Routing Props : " + rProperties.toString());
        return new RabbitMQPublisher(rProperties, Constants.CUSTOMER_ROUTING_KEY);
    }

    public static Publisher getOrderPublisher(RabbitMQProperties rProperties){
        rProperties.setExchangeName(Constants.ORDER_EXCHANGE_NAME);
        logger.info("getOrderPublisher() -> Fetching order publisher. Routing Props : " + rProperties.toString());
        return new RabbitMQPublisher(rProperties, Constants.ORDER_ROUTING_KEY);
    }

    private static Subscriber getCustomerSubscriber(RabbitMQProperties rProperties){
        rProperties.setExchangeName(Constants.CUSTOMER_EXCHANGE_NAME)
                .setQueueName(Constants.CUSTOMER_QUEUE)
                .setAutoAck(false);
        logger.info("getCustomerSubscriber() -> Fetching customer subscriber. Routing Props : " + rProperties.toString());
        return new RabbitMQSubscriber(rProperties);

    }

    private static Subscriber getOrderSubscriber(RabbitMQProperties rProperties){
        rProperties.setExchangeName(Constants.ORDER_EXCHANGE_NAME)
                .setQueueName(Constants.ORDER_QUEUE)
                .setAutoAck(false);
        logger.info("getOrderSubscriber() -> Fetching order subscriber. Routing Props : " + rProperties.toString());
        return new RabbitMQSubscriber(rProperties);

    }

    private static RabbitMQProperties getProperties() {
        return new RabbitMQProperties()
                .setBrokerUrl(Constants.AMQP_URI)
                .setDurable(Constants.IS_DURABLE_QUEUE)
                .setPrefetchCount(Constants.PREFETCH_COUT)
                .setAutoRecoveryEnable(true)
                .setConsumerTag("default")
                .setExchangeType(RabbitMQProperties.EXCHANGE_TYPE.TOPIC);
    }

}
