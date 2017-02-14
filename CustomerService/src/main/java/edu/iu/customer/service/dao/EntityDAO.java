package edu.iu.customer.service.dao;

import java.util.List;

import edu.iu.customer.service.entity.Customer;
import edu.iu.customer.service.entity.Orders;

public interface EntityDAO {

	public void saveEntity(Object entity, long deliveryTag) throws Exception;
	
	public void createCustomer(edu.iu.customer.service.model.Customer customer) throws Exception;
	
	public List<Customer> getCustomers() throws Exception;
	
	public List<Orders> getOrdersForCustomer(Integer customerId) throws Exception;
}
