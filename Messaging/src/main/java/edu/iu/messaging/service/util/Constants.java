package edu.iu.messaging.service.util;

/**
 * Created by Ajinkya on 2/2/17.
 */
public class Constants {
    public static final String EXCHANGE_NAME = "edm.test";
    public static final String QUEUE_NAME = "test.queue";
    public static final String EXCHANGE_TYPE_TOPIC = "topic";
    public static final String AMQP_URI = "amqp://airavata:airavata@gw56.iu.xsede.org:5672/messaging";
    public static final String CONSUMER_TAG = "default";
    public static final boolean IS_DURABLE_QUEUE = true;
    public static final int PREFETCH_COUT = 20;
    public static final String CUSTOMER_QUEUE = "customer.queue";
    public static final String ORDER_EXCHANGE_NAME = "exchange.order";
    public static final String CUSTOMER_EXCHANGE_NAME = "exchange.customer";
    public static final String ORDER_QUEUE = "order.queue";
    public static final String ORDER_ROUTING_KEY = "rk.order";
    public static final String CUSTOMER_ROUTING_KEY = "rk.customer";
}
