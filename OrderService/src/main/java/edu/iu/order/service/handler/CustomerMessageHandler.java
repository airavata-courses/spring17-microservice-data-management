package edu.iu.order.service.handler;

import edu.iu.messaging.service.util.MessageContext;
import edu.iu.messaging.service.core.MessageHandler;
import edu.iu.messaging.service.util.ThriftUtils;
import edu.iu.order.service.adapter.JPAThriftAdapter;
import edu.iu.order.service.dao.impl.EntityDAOImpl;
import edu.iu.order.service.model.Customer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.thrift.TBase;


/**
 * Created by Ajinkya on 2/6/17.
 */

public class CustomerMessageHandler implements MessageHandler {

    private static final Logger logger = LogManager.getLogger(CustomerMessageHandler.class);

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

            Customer customer = new Customer();
            ThriftUtils.createThriftFromBytes(bytes, customer);

            logger.info("onMessage() -> Received object. Customer : " + customer);
            new EntityDAOImpl().saveEntity(JPAThriftAdapter.getCustomerJPAEntity(customer), message.getDeliveryTag());
            //System.out.println(customer);

        } catch (Exception e) {
            logger.error("onMessage() -> Error handling message. Message Id : " + message.getMessageId(), e);
        }
    }
}

