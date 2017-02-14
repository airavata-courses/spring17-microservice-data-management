package edu.iu.customer.service.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import edu.iu.customer.service.handler.CustomerServiceHandler;
import edu.iu.customer.service.model.CustomerService;
import edu.iu.customer.service.model.CustomerService.Iface;

public class CustomerServiceThriftServer {

	private static final Logger logger = LogManager.getLogger(CustomerServiceThriftServer.class);
	private static CustomerService.Processor<Iface> processor;
	private static CustomerServiceHandler handler;
	
	public static void simpleServer(CustomerService.Processor<Iface> processor) {
		try {
			logger.info("CustomerService SimpleServer Listening to port 9091");
			TServerTransport serverTransport = new TServerSocket(9091);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
			server.serve();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public static void main(String[] args) {
		try {
			handler = new CustomerServiceHandler();
			processor = new CustomerService.Processor<CustomerService.Iface>(handler);
			
			// create a thread for running the server
			Runnable simple = new Runnable() {
				@Override
				public void run() {
					simpleServer(processor);
				}
			};
			
			// start the server thread
			logger.info("Starting thrift server for CustomerService");
			new Thread(simple).start();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
}
