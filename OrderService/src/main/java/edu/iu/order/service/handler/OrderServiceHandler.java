package edu.iu.order.service.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import edu.iu.order.service.adapter.JPAThriftAdapter;
import edu.iu.order.service.dao.EntityDAO;
import edu.iu.order.service.dao.impl.EntityDAOImpl;
import edu.iu.order.service.model.Customer;
import edu.iu.order.service.model.OperationFailedException;
import edu.iu.order.service.model.OrderService;
import edu.iu.order.service.model.Orders;

public class OrderServiceHandler implements OrderService.Iface{

	private static final EntityDAO DAO = new EntityDAOImpl();
	private static final Logger logger = LogManager.getLogger(OrderServiceHandler.class);

	@Override
	public List<Orders> getOrdersForCustomer(String customerId) throws OperationFailedException, TException {
		List<Orders> orders = new ArrayList<>();
		logger.info("Getting orders for customer id: " + customerId);
		try {
			// get orders list from db
			List<edu.iu.order.service.entity.Orders> ordersList =
					DAO.getOrdersForCustomer(Integer.parseInt(customerId));
			logger.info("Fetched orders from DB, JPA list: " + ordersList);

			// convert from JPA to Thrift
			for (edu.iu.order.service.entity.Orders order : ordersList) {
				orders.add(JPAThriftAdapter.getOrdersThriftDM(order));
			}
		} catch (Exception ex) {
			logger.error("getOrders | exception: " + ex.getMessage(), ex);
			throw new OperationFailedException("Get Orders operation failed: " + ex.getMessage());
		}
		logger.info("Returning list (thrift converted");
		return orders;
	}

	@Override
	public void createOrder(Orders order) throws OperationFailedException, TException {
		try {
			// save order in db
			if (order != null) {
				logger.info("Creating order entry in DB: " + order);
				DAO.createOrder(order);
			} else {
				throw new Exception ("Order object null");
			}
		} catch (Exception ex) {
			logger.error("createOrder | exception: " + ex.getMessage(), ex);
			throw new OperationFailedException("Create order operation failed: " + ex.getMessage());
		}
	}

	@Override
	public List<Customer> getCustomers() throws OperationFailedException, TException {
		List<Customer> customers = new ArrayList<>();
		try {
			// get orders list from db
			List<edu.iu.order.service.entity.Customer> customerList = DAO.getCustomers();
			logger.info("Fetched customers from DB, JPA list: " + customerList);

			// convert from JPA to Thrift
			for (edu.iu.order.service.entity.Customer customer : customerList) {
				customers.add(JPAThriftAdapter.getCustomerThriftDM(customer));
			}
		} catch (Exception ex) {
			logger.error("getOrders | exception: " + ex.getMessage(), ex);
			throw new OperationFailedException("Get Orders operation failed: " + ex.getMessage());
		}
		logger.info("Returning list (thrift converted");
		return customers;
	}
}