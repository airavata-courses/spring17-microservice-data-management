package edu.iu.order.service.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import edu.iu.order.service.handler.CustomerMessageHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.iu.messaging.service.util.MessageContext;
import edu.iu.messaging.service.core.MessagingFactory;
import edu.iu.messaging.service.core.Publisher;
import edu.iu.messaging.service.core.Subscriber;
import edu.iu.messaging.service.util.Constants;
import edu.iu.messaging.service.util.Type;
import edu.iu.order.service.adapter.JPAThriftAdapter;
import edu.iu.order.service.dao.EntityDAO;
import edu.iu.order.service.entity.Customer;
import edu.iu.order.service.entity.Orders;

public class EntityDAOImpl implements EntityDAO {

	Logger logger = LogManager.getLogger(EntityDAOImpl.class);
	
	private Publisher orderPublisher;
	@SuppressWarnings("unused")
	private Subscriber customerSubscriber;

	public EntityDAOImpl(){
		orderPublisher = MessagingFactory.getPublisher(Type.ORDER);
		customerSubscriber = MessagingFactory.getSubscriber(new CustomerMessageHandler(), getRoutingKeys(), Type.CUSTOMER);
	}

	public List<String> getRoutingKeys(){
		return new ArrayList<String>(){/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{add(Constants.CUSTOMER_ROUTING_KEY);}};
	}
	
	@Override
	public void saveEntity(Object entity, long deliveryTag) throws Exception {
		try {
			logger.info("Saving entity in database. Entity: " + entity);
			// Connection details loaded from persistence.xml to create EntityManagerFactory.
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-order");

			EntityManager em = emf.createEntityManager();

			// Creating a new transaction.
			EntityTransaction tx = em.getTransaction();

			tx.begin();

			// Persisting the entity object.
			em.merge(entity);

			logger.info("saveEntity() -> Sending ack. Delivery Tag : " + deliveryTag);
			customerSubscriber.sendAck(deliveryTag);

			// Committing transaction.
			tx.commit();


			logger.info("DB persist successful; closing connections now!");

			// Closing connection.
			em.close();
			emf.close();
		} catch (Exception ex) {
			logger.error("Error persisting entity in database. Error: " + ex.getMessage(), ex);
			throw ex;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Customer> getCustomers() throws Exception {
		List<Customer> customers = null;
		
		try {
			// Connection details loaded from persistence.xml to create EntityManagerFactory.
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-order");

			EntityManager em = emf.createEntityManager();

			// Creating a new transaction.
			EntityTransaction tx = em.getTransaction();

			tx.begin();

			Query query = em.createQuery("SELECT c FROM Customer c");
			customers = query.getResultList();

			// Committing transaction.
			tx.commit();

			// Closing connection.
			em.close();
			emf.close();
		} catch (Exception ex) {
			logger.error("Error getting customers from database. Error: " + ex.getMessage(), ex);
			throw ex;
		}
		return customers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Orders> getOrdersForCustomer(Integer customerId) throws Exception {
		List<Orders> orders = null;
		try {
			// Connection details loaded from persistence.xml to create EntityManagerFactory.
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-order");

			EntityManager em = emf.createEntityManager();

			// Creating a new transaction.
			EntityTransaction tx = em.getTransaction();

			tx.begin();

			Query query = em.createQuery("SELECT o FROM Order o WHERE o.customer.ID=" + customerId);
			orders = query.getResultList();

			// Committing transaction.
			tx.commit();

			// Closing connection.
			em.close();
			emf.close();
		} catch (Exception ex) {
			logger.error("Error getting orders from database. Error: " + ex.getMessage(), ex);
			throw ex;
		}
		return orders;
	}
	
	@Override
	public void createOrder(edu.iu.order.service.model.Orders order) throws Exception {
		// Connection details loaded from persistence.xml to create EntityManagerFactory.
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-order");
		EntityManager em = emf.createEntityManager();

		// Creating a new transaction.
		EntityTransaction tx = em.getTransaction();
		try {
			// Persisting the order object.
			logger.info("Saving order in database. Order: " + order);
			tx.begin();
			em.merge(JPAThriftAdapter.getOrdersJPAEntity(order));
			
			// Publish message
			logger.info("Publishing new order to outside world: " + order);
			MessageContext mctx = new MessageContext(order, order.getCustomer().getCustomerName());
			orderPublisher.publish(mctx);
			
			// Committing transaction.
			tx.commit();
			logger.info("DB persist successful; closing connections now!");
		} catch (Exception ex) {
			logger.error("Error persisting entity in database, Rolling back. Error: " + ex.getMessage(), ex);
			tx.rollback();
			throw ex;
		} finally {
			// Closing connection.
			em.close();
			emf.close();
		}
	}
	
//	public static void main(String[] args) {
//		EntityDAO dao = new EntityDAOImpl();
//		
//		// create customer
//		Customer customer = new Customer();
//		customer.setCustomerName("Customer-A");
//		customer.setCreditLimit(5000);
//		
//		try {
//			// persist in db
//			dao.saveEntity(customer);
//			System.out.println("Customer record saved!");
//			
//			// get list of customers
//			List<Customer> customers = dao.getCustomers();
//			System.out.println("* Customers JPA List: " + customers);
//			System.out.println("* Printing Customers Thrift List: " + (customers.isEmpty() ? "empty" : ""));
//			for (Customer cust : customers) {
//				System.out.println("\t " + JPAThriftAdapter.getCustomerThriftDM(cust));
//			}
//		} catch (Exception ex) {
//			System.err.println("Exception occured: " + ex);
//		}
//	}
}
