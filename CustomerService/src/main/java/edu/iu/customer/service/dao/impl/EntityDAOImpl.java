package edu.iu.customer.service.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.iu.customer.service.adapter.JPAThriftAdapter;
import edu.iu.customer.service.dao.EntityDAO;
import edu.iu.customer.service.entity.Customer;
import edu.iu.customer.service.entity.Orders;
import edu.iu.customer.service.handler.OrderMessageHandler;
import edu.iu.messaging.service.util.MessageContext;
import edu.iu.messaging.service.core.MessagingFactory;
import edu.iu.messaging.service.core.Publisher;
import edu.iu.messaging.service.core.Subscriber;
import edu.iu.messaging.service.util.Constants;
import edu.iu.messaging.service.util.Type;

public class EntityDAOImpl implements EntityDAO {

	Logger logger = LogManager.getLogger(EntityDAOImpl.class);
	private Publisher customerPublisher;
	private Subscriber orderSubscriber;
	
	public EntityDAOImpl() {
		customerPublisher = MessagingFactory.getPublisher(Type.CUSTOMER);
		orderSubscriber = MessagingFactory.getSubscriber(new OrderMessageHandler(), getRoutingKeys(), Type.ORDER);
	}
	
	public List<String> getRoutingKeys(){
		return new ArrayList<String>(){/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{add(Constants.ORDER_ROUTING_KEY);}};
	}
	
	@Override
	public void saveEntity(Object entity, long deliveryTag) throws Exception {
		try {
			logger.info("Saving entity in database. Entity: " + entity);
			// Connection details loaded from persistence.xml to create EntityManagerFactory.
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-customer");

			EntityManager em = emf.createEntityManager();

			// Creating a new transaction.
			EntityTransaction tx = em.getTransaction();

			tx.begin();

			// Persisting the entity object.
			em.merge(entity);

			logger.info("saveEntity() -> Sending ack. Delivery Tag : " + deliveryTag);
			orderSubscriber.sendAck(deliveryTag);

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
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-customer");

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
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-customer");

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
	public void createCustomer(edu.iu.customer.service.model.Customer customer) throws Exception {
		// Connection details loaded from persistence.xml to create EntityManagerFactory.
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-customer");
		EntityManager em = emf.createEntityManager();
		
		// Creating a new transaction.
		EntityTransaction tx = em.getTransaction();
		try {
			// Persisting the customer object.
			logger.info("Saving customer in database. Customer: " + customer);
			tx.begin();
			em.persist(JPAThriftAdapter.getCustomerJPAEntity(customer));
			
			// Publish message
			logger.info("Publishing new customer to outside world: " + customer);
			MessageContext mctx = new MessageContext(customer, customer.getCustomerName());
			customerPublisher.publish(mctx);

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
////		EntityDAO dao = new EntityDAOImpl();
//		
//		// create customer
//		Customer customer = new Customer();
//		customer.setID(0000001);
//		customer.setCustomerName("Customer-B");
//		customer.setCreditLimit(5000);
//		
//		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-customer");;
//		EntityManager em = emf.createEntityManager();
//		// persist in db
//		EntityTransaction tx = em.getTransaction();
//		
//		try {
//			tx.begin();
//			em.merge(customer);
//			System.out.println("Submitted merge req, but not commited tx.");
//			tx.commit();
////			dao.saveEntity(customer);
////			System.out.println("Customer record saved!");
//			
//			// get list of customers
////			List<Customer> customers = dao.getCustomers();
////			System.out.println("* Customers JPA List: " + customers);
////			System.out.println("* Printing Customers Thrift List: " + (customers.isEmpty() ? "empty" : ""));
////			for (Customer customer : customers) {
////				System.out.println("\t" + JPAThriftAdapter.getCustomerThriftDM(customer));
////			}
//		} catch (Exception ex) {
//			System.err.println("Exception occured: " + ex);
//			System.out.println("Rolling-back tx.");
//			tx.rollback();
//		} finally {
//			em.close();
//			emf.close();
//		}
//	}
}
