package edu.iu.customer.service.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import edu.iu.customer.service.model.OrderService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.iu.customer.service.adapter.JPAThriftAdapter;
import edu.iu.customer.service.dao.EntityDAO;
import edu.iu.customer.service.entity.Customer;
import edu.iu.customer.service.entity.Orders;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import edu.iu.customer.service.utils.ThriftConstants;

public class EntityDAOImpl implements EntityDAO {

	Logger logger = LogManager.getLogger(EntityDAOImpl.class);

	private OrderService.Client orderClient;

	public EntityDAOImpl() {
		TSocket tSocket = new TSocket(ThriftConstants.ORDER_SERVICE_THRIFT_IP, ThriftConstants.ORDER_SERVICE_THRIFT_PORT);
		orderClient = new OrderService.Client(new TBinaryProtocol(tSocket));
		try {
			tSocket.open();
		} catch ( Exception e){
			logger.info("Open Exception for tSocket : " + e.getMessage());
		}
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

	public void prepareEntity(Object entity, long deliveryTag) throws Exception {
		try{

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
			int res = orderClient.prepareCustomer(customer);
			if( res == -1){
				logger.info("DB phase 1 failure; closing connections now!");
				logger.info("DB phase 1 failure;RESULT:" + res);
				tx.rollback();
				return;
			}
			logger.info("DB phase 1 success; closing connections now!");

			if(orderClient.commitCustomer(customer) == -1){
				logger.info("DB phase 2 failure; closing connections now!");
				tx.rollback();
				return;
			}
			logger.info("DB phase 2 success; closing connections now!");

			// Committing transaction.
			tx.commit();
			logger.info("DB commit successful; closing connections now!");
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
}
