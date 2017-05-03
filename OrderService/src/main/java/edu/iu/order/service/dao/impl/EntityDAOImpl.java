package edu.iu.order.service.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.iu.order.service.adapter.JPAThriftAdapter;
import edu.iu.order.service.dao.EntityDAO;
import edu.iu.order.service.entity.Customer;
import edu.iu.order.service.entity.Orders;

public class EntityDAOImpl implements EntityDAO {

	Logger logger = LogManager.getLogger(EntityDAOImpl.class);


	public EntityDAOImpl(){ }
	
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

	@Override
	public int prepareCustomer(edu.iu.order.service.model.Customer entity, long deliveryTag) throws Exception {
		int RESULT = -1;
		try {
			logger.info("Saving prepare Customer in database. Entity: " + entity);
			// Connection details loaded from persistence.xml to create EntityManagerFactory.
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-order");

			EntityManager em = emf.createEntityManager();

			// Creating a new transaction.
			EntityTransaction tx = em.getTransaction();

			tx.begin();

			// Only prepare the object.
			em.persist(JPAThriftAdapter.getCustomerJPAEntity(entity));

			logger.info("DB persist successful!");
			logger.info("Saving prepare Customer in database. Result: " + RESULT);

			tx.rollback();
			// Closing connection.
			em.close();
			emf.close();
			RESULT = 1;
		} catch (Exception ex) {
			logger.error("Error persisting entity in database. Error: " + ex.getMessage(), ex);
			throw ex;
		}
		return RESULT;
	}

	@Override
	public int commitCustomer(edu.iu.order.service.model.Customer entity, long deliveryTag) throws Exception {
		int RESULT = -1;
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-order");

		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			logger.info("Saving Commit in database. Entity: " + entity);
			tx.begin();

			// Persisting the entity object.
			em.persist(JPAThriftAdapter.getCustomerJPAEntity(entity));

			// Committing transaction.
			tx.commit();
			RESULT = 1;

			logger.info("DB commit successful; closing connections now!");

		} catch (Exception ex) {
			logger.error("Error persisting entity in database. Error: " + ex.getMessage(), ex);
			// Rollback
			tx.rollback();
			logger.error("Rollback --- done");
			//throw ex;
		} finally {
			em.close();
			emf.close();
		}
		return RESULT;
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
}
