package edu.iu.order.service.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import edu.iu.order.service.handler.OrderServiceHandler;
import edu.iu.order.service.model.OrderService;
import edu.iu.order.service.model.OrderService.Iface;

public class OrderServiceThriftServer {
	
	private static final Logger logger = LogManager.getLogger(OrderServiceThriftServer.class);
	private static OrderServiceHandler handler;
	private static OrderService.Processor<Iface> processor;
	
	public static void simpleServer(OrderService.Processor<Iface> processor) {
		try {
			logger.info("OrderService SimpleServer Listening to port 9090");
			TServerTransport serverTransport = new TServerSocket(9090); 
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
			server.serve();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public static void main(String[] args) {
		try {
			handler = new OrderServiceHandler();
			processor = new OrderService.Processor<OrderService.Iface>(handler);
			
			// create a thread for running the server
			Runnable simple = new Runnable() {
				@Override
				public void run() {
					simpleServer(processor);
				}
			};
			
			// start the server thread
			logger.info("Starting thrift server for OrderService");
			new Thread(simple).start();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

}
