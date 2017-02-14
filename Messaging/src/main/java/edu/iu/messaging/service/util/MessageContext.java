package edu.iu.messaging.service.util;

/**
 * Created by Ajinkya on 2/2/17.
 */
import org.apache.thrift.TBase;
public class MessageContext {
    private final TBase event;
    private final String messageId;
    private long deliveryTag;

    public MessageContext(TBase event, String messageId){
        this.event = event;
        this.messageId = messageId;
    }

    public MessageContext(TBase event, String messageId, long deliveryTag){
        this.event = event;
        this.messageId = messageId;
        this.deliveryTag = deliveryTag;
    }

    public TBase getEvent() {
        return event;
    }

    public String getMessageId() {
        return messageId;
    }

    public long getDeliveryTag() {
        return deliveryTag;
    }
}
