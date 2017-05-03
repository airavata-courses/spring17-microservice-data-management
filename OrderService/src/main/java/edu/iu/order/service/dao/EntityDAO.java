package edu.iu.order.service.dao;

import java.util.List;

import edu.iu.order.service.entity.Customer;
import edu.iu.order.service.entity.Orders;

public interface EntityDAO {

	public void saveEntity(Object entity, long deliveryTag) throws Exception;
	
	public List<Customer> getCustomers() throws Exception;
	
	public List<Orders> getOrdersForCustomer(Integer customerId) throws Exception;
	
	public void createOrder(edu.iu.order.service.model.Orders order) throws Exception;

	public int prepareCustomer(edu.iu.order.service.model.Customer entity, long deliveryTag) throws Exception;

	public int commitCustomer(edu.iu.order.service.model.Customer entity, long deliveryTag) throws Exception;
}
