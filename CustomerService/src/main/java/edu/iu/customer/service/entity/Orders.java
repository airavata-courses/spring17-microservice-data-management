package edu.iu.customer.service.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Orders {

	@Id
	Integer ID;
	
	@Column
	Integer orderAmount;
	
	@ManyToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn (name = "customer_id")
	Customer customer;
	
	@Column
	String status;

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public Integer getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(Integer orderAmount) {
		this.orderAmount = orderAmount;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Orders [ID=" + ID + ", orderAmount=" + orderAmount + ", customer=" + customer + ", status=" + status
				+ "]";
	}
}
