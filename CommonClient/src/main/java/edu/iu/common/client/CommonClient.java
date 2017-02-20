package edu.iu.common.client;

import java.util.ArrayList;
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

	public static void createCustomer(CustomerService.Client customerClient) throws TException {
		if (customerClient != null) {
			// generate random customerId
			int customerId = random.nextInt(999999999);
			logger.info("Creating customer with ID: " + customerId);
			Customer customer = new Customer(customerId, "TestCustomer-" + customerId, 5000);
			customerClient.createCustomer(customer);
			logger.info("Customer creation request submitted!");
		}
	}

	public static List<Customer> getCustomers(CustomerService.Client customerClient) throws TException {
		List<Customer> customerList = new ArrayList<>();
		if (customerClient != null) {
			logger.info("Getting customer list from database.");
			customerList = customerClient.getCustomers();
		}
		return customerList;
	}

	public static void createOrder(OrderService.Client orderClient, CustomerService.Client customerClient) throws TException {
		if (orderClient != null) {
			// get customer list and use first record
			if (customerClient != null) {
				logger.info("Getting customer list");
				List<Customer> customerList = customerClient.getCustomers();
				if (!customerList.isEmpty()) {
					// generate random orderId
					int orderId = random.nextInt(999999999);
					logger.info("Creating order with ID: " + orderId);
					edu.iu.order.service.model.Customer customer = new edu.iu.order.service.model.Customer(
							customerList.get(0).getId(),
							customerList.get(0).getCustomerName(),
							customerList.get(0).getCreditLimit());
					Orders order = new Orders(orderId, 2500, "CREATED", customer);
					orderClient.createOrder(order);
					logger.info("Order creation request submitted!");
				} else {
					throw new TException("No Customers in DB; Cannot create Order");
				}
			}
		}
	}

	public static List<Orders> getOrdersForCustomer(OrderService.Client orderClient, String customerId) throws TException {
		List<Orders> orders = new ArrayList<>();
		if (orderClient != null) {
			logger.info("Getting order list from database");
			orders = orderClient.getOrdersForCustomer(customerId);
		}
		return orders;
	}

	public static void main(String[] args) {
		if(args.length != 1){
			System.out.println("Please provide any one of the services you want to invoke.\n  --customer \n  --order");
			return;
		}
		if(args.length  > 1){
			System.out.println("Please provide any one of the services you want to invoke.\n  --customer \n  --order");
			return;
		}
		if(!args[0].equalsIgnoreCase("order") && !args[0].equalsIgnoreCase("customer")){
			System.out.println("Please provide valid service name.\n  --customer \n  --order");
			return;
		}
		TTransport transportOrder = new TSocket("127.0.0.1", 9090);
		TTransport transportCustomer = new TSocket("127.0.0.1", 9091);

		try {
			logger.info("Opening transport for Customer & Order");
			//transportOrder.open();
			transportCustomer.open();

			TProtocol protocolCustomer = new TBinaryProtocol(transportCustomer);
			TProtocol protocolOrder = new TBinaryProtocol(transportOrder);

			customerClient = new CustomerService.Client(protocolCustomer);
			orderClient = new OrderService.Client(protocolOrder);


			if(args[0].equalsIgnoreCase("customer")){
				// create customer test
				createCustomer(customerClient);
				return;
			}

			if(args[0].equalsIgnoreCase("order")){
				// create order test
				createOrder(orderClient, customerClient);
				return;
			}

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			logger.info("Closing customer and order transports");
			transportOrder.close();
			transportCustomer.close();
		}
	}
}