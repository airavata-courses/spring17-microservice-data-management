package edu.iu.common.client;

import java.util.List;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import edu.iu.customer.service.model.Customer;
import edu.iu.customer.service.model.CustomerService;
import edu.iu.order.service.model.OrderService;
import edu.iu.order.service.model.Orders;

public class CommonClient {

	private static final Logger logger = LogManager.getLogger(CommonClient.class);
	private static CustomerService.Client customerClient;
	private static OrderService.Client orderClient;
	private static final Random random = new Random();

	public static void createCustomer(CustomerService.Client customerClient, int customerId) throws TException {
		if (customerClient != null) {
			// generate random customerId
			//int customerId = 291380607;//random.nextInt(999999999);
			logger.info("Creating customer with ID: " + customerId);
			Customer customer = new Customer(customerId, "TestCustomer-" + customerId, 5000);
			customerClient.createCustomer(customer);
			logger.info("Customer creation request submitted!");
		}
	}

	public static void createOrder(OrderService.Client orderClient) throws TException {
		if (orderClient != null) {
			// get customer list and use first record
			logger.info("Getting customer list");
			List<edu.iu.order.service.model.Customer> customerList = orderClient.getCustomers();
			if (!customerList.isEmpty()) {
				// generate random orderId
				int orderId = random.nextInt(999999999);
				logger.info("Creating order with ID: " + orderId);
				edu.iu.order.service.model.Customer customer = customerList.get(0);
				Orders order = new Orders(orderId, 2500, "CREATED", customer);
				orderClient.createOrder(order);
				logger.info("Order creation request submitted!");
			} else {
				throw new TException("No Customers in DB; Cannot create Order");
			}
		}
	}

	public static void main(String[] args) {
		if(args.length != 1){
			System.out.println("Please provide any one of the services you want to invoke.\n  --customer-primary \n  --customer-random \n  --order");
			return;
		}
		if(!args[0].equalsIgnoreCase("order") && !args[0].equalsIgnoreCase("customer-primary") && !args[0].equalsIgnoreCase("customer-random")){
			System.out.println("Please provide valid service name.\n  --customer-primary \n  --customer-random \n  --order");
			return;
		}

		try {

			if(args[0].equalsIgnoreCase("customer-primary")){
				// create customer test
				//logger.info("Opening transport for Customer");
				TTransport transportCustomer = new TSocket("127.0.0.1", 9091);
				TProtocol protocolCustomer = new TBinaryProtocol(transportCustomer);
				customerClient = new CustomerService.Client(protocolCustomer);
				transportCustomer.open();
				createCustomer(customerClient, 291380607);
				transportCustomer.close();
				return;
			}

			if(args[0].equalsIgnoreCase("customer-random")){
				// create customer test
				//logger.info("Opening transport for Customer");
				TTransport transportCustomer = new TSocket("127.0.0.1", 9091);
				TProtocol protocolCustomer = new TBinaryProtocol(transportCustomer);
				customerClient = new CustomerService.Client(protocolCustomer);
				transportCustomer.open();
				createCustomer(customerClient, random.nextInt(999999999));
				transportCustomer.close();
				return;
			}

			if(args[0].equalsIgnoreCase("order")){
				TTransport transportOrder = new TSocket("127.0.0.1", 9090);
				TProtocol protocolOrder = new TBinaryProtocol(transportOrder);
				orderClient = new OrderService.Client(protocolOrder);
				transportOrder.open();
				// create order test
				logger.info("Opening transport for Order");
				createOrder(orderClient);
				transportOrder.close();
				return;
			}

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
}