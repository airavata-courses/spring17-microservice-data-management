package edu.iu.messaging.service.util;

/**
 * Created by Ajinkya on 2/4/17.
 */
public class RabbitMQProperties {
    private String brokerUrl;
    private EXCHANGE_TYPE exchangeType;
    private String exchangeName;
    private int prefetchCount;
    private boolean durable;
    private String queueName;
    private String consumerTag = "default";
    private boolean autoRecoveryEnable;
    private boolean autoAck;

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public RabbitMQProperties setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
        return this;
    }

    public boolean isDurable() {
        return durable;
    }

    public RabbitMQProperties setDurable(boolean durable) {
        this.durable = durable;
        return this;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public RabbitMQProperties setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
        return this;
    }

    public int getPrefetchCount() {
        return prefetchCount;
    }

    public RabbitMQProperties setPrefetchCount(int prefetchCount) {
        this.prefetchCount = prefetchCount;
        return this;
    }

    public String getQueueName() {
        return queueName;
    }

    public RabbitMQProperties setQueueName(String queueName) {
        this.queueName = queueName;
        return this;
    }

    public String getConsumerTag() {
        return consumerTag;
    }

    public RabbitMQProperties setConsumerTag(String consumerTag) {
        this.consumerTag = consumerTag;
        return this;
    }

    public boolean isAutoRecoveryEnable() {
        return autoRecoveryEnable;
    }

    public RabbitMQProperties setAutoRecoveryEnable(boolean autoRecoveryEnable) {
        this.autoRecoveryEnable = autoRecoveryEnable;
        return this;
    }

    public String getExchangeType() {
        return exchangeType.type;
    }

    public RabbitMQProperties setExchangeType(EXCHANGE_TYPE exchangeType) {
        this.exchangeType = exchangeType;
        return this;
    }

    public boolean isAutoAck() {
        return autoAck;
    }

    public RabbitMQProperties setAutoAck(boolean autoAck) {
        this.autoAck = autoAck;
        return this;
    }


    public enum EXCHANGE_TYPE{
        TOPIC("topic"),
        FANOUT("fanout");

        private String type;

        EXCHANGE_TYPE(String type) {
            this.type = type;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RabbitMQProperties{");
        sb.append("brokerUrl='").append(brokerUrl).append('\'');
        sb.append(", exchangeType=").append(exchangeType);
        sb.append(", exchangeName='").append(exchangeName).append('\'');
        sb.append(", prefetchCount=").append(prefetchCount);
        sb.append(", durable=").append(durable);
        sb.append(", queueName='").append(queueName).append('\'');
        sb.append(", consumerTag='").append(consumerTag).append('\'');
        sb.append(", autoRecoveryEnable=").append(autoRecoveryEnable);
        sb.append(", autoAck=").append(autoAck);
        sb.append('}');
        return sb.toString();
    }
}
