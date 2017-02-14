
package edu.iu.customer.service.handler;

import edu.iu.customer.service.adapter.JPAThriftAdapter;
import edu.iu.customer.service.dao.impl.EntityDAOImpl;
import edu.iu.customer.service.model.Orders;
import edu.iu.messaging.service.util.MessageContext;
import edu.iu.messaging.service.core.MessageHandler;
import edu.iu.messaging.service.util.ThriftUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.thrift.TBase;

public class OrderMessageHandler implements MessageHandler {

    private static final Logger logger = LogManager.getLogger(OrderMessageHandler.class);


    /*
     * This method only handle MessageType.PROCESS type messages.
     * @param message
     */

    @Override
    public void onMessage(MessageContext message) {

        try {

            logger.info("onMessage() -> New message received. Message Id : " + message.getMessageId());

            TBase event = message.getEvent();
            byte[] bytes = ThriftUtils.serializeThriftObject(event);

            Orders order = new Orders();
            ThriftUtils.createThriftFromBytes(bytes, order);

            logger.info("onMessage() -> Received object. Order : " + order);
            new EntityDAOImpl().saveEntity(JPAThriftAdapter.getOrdersJPAEntity(order), message.getDeliveryTag());
            //System.out.println(order);

        } catch (Exception e) {
            logger.error("onMessage() -> Error handling message. Message Id : " + message.getMessageId(), e);
        }
    }
}

