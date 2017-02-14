package edu.iu.customer.service.adapter;

import edu.iu.customer.service.entity.Customer;
import edu.iu.customer.service.entity.Orders;

public class JPAThriftAdapter {

	public static Customer getCustomerJPAEntity(edu.iu.customer.service.model.Customer customerTDM) {
		Customer customerJPE = null;
		if (customerTDM != null) {
			customerJPE = new Customer();
			customerJPE.setID(customerTDM.getId());
			customerJPE.setCustomerName(customerTDM.getCustomerName());
			customerJPE.setCreditLimit(customerTDM.getCreditLimit());
		}
		return customerJPE;
	}
	
	public static Orders getOrdersJPAEntity(edu.iu.customer.service.model.Orders orderTDM) {
		Orders orderJPE = null;
		if (orderTDM != null) {
			orderJPE = new Orders();
			orderJPE.setID(orderTDM.getId());
			orderJPE.setCustomer(getCustomerJPAEntity(orderTDM.getCustomer()));
			orderJPE.setOrderAmount(orderTDM.getOrderAmount());
			orderJPE.setStatus(orderTDM.getStatus());
		}
		return orderJPE;
	}
	
	public static edu.iu.customer.service.model.Customer getCustomerThriftDM(Customer customerJPE) {
		edu.iu.customer.service.model.Customer customerTDM = null;
		if (customerJPE != null) {
			customerTDM = new edu.iu.customer.service.model.Customer();
			customerTDM.setId(customerJPE.getID());
			customerTDM.setCustomerName(customerJPE.getCustomerName());
			customerTDM.setCreditLimit(customerJPE.getCreditLimit());
		}
		return customerTDM;
	}
	
	public static edu.iu.customer.service.model.Orders getOrdersThriftDM(Orders orderJPE) {
		edu.iu.customer.service.model.Orders orderTDM = null;
		if (orderJPE != null) {
			orderTDM = new edu.iu.customer.service.model.Orders();
			orderTDM.setId(orderJPE.getID());
			orderTDM.setOrderAmount(orderJPE.getOrderAmount());
			orderTDM.setCustomer(getCustomerThriftDM(orderJPE.getCustomer()));
			orderTDM.setStatus(orderJPE.getStatus());
		}
		return orderTDM;
	}
}
