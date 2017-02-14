/**
 * Created by Ajinkya on 2/2/17.
 */
import edu.iu.messaging.service.util.MessageContext;
import edu.iu.messaging.service.core.MessageHandler;
import edu.iu.messaging.service.core.MessagingFactory;
import edu.iu.messaging.service.core.Publisher;
import edu.iu.messaging.service.core.Subscriber;
import edu.iu.messaging.service.model.Customer;
import edu.iu.messaging.service.model.Orders;
import edu.iu.messaging.service.util.ThriftUtils;
import edu.iu.messaging.service.util.Type;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Stub {
    public Stub() throws Exception{

        Publisher publisher = MessagingFactory.getPublisher(Type.CUSTOMER);
        List<String> routingKeys = new ArrayList<>();
        routingKeys.add("rk.cutomer");
        Subscriber subscriber = MessagingFactory.getSubscriber(new OrderMessageHandler(), routingKeys, Type.CUSTOMER);
        Customer cus = new Customer();
        cus.setCustomerName("customer2");
        cus.setCreditLimit(234);
        Orders orders = new Orders();
        orders.setOrderAmount(200);
        orders.setCustomer(cus);
        orders.setStatus("pending");
        MessageContext mctx = new MessageContext(orders, "order-1");
        publisher.publish(mctx);
    }

    /**
     * @param args
     * @throws SQLException
     * @throws IOException
     */
    public static void main(String[] args) throws Exception{
        new Stub();
    }

    private class OrderMessageHandler implements MessageHandler {
        /**
         * This method only handle MessageType.PROCESS type messages.
         * @param message
         */
        @Override
        public void onMessage(MessageContext message) {

            try {
                Customer customer = new Customer();
                TBase event = message.getEvent();
                byte[] bytes = ThriftUtils.serializeThriftObject(event);
                Orders order = new Orders();
                ThriftUtils.createThriftFromBytes(bytes, order);
                System.out.println(order);

            } catch (TException e) {
               e.printStackTrace();
            }
        }
    }
}