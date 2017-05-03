namespace java edu.iu.customer.service.model

typedef i32 integer

struct Customer {
	1: required integer id,
	2: required string customerName,
	3: required integer creditLimit
}

struct Orders {
	1: required integer id,
	2: required integer orderAmount,
	3: required string status,
	4: required Customer customer
}

exception OperationFailedException {
	1: string message
}

service CustomerService {
	list<Customer> getCustomers() throws (1: OperationFailedException ex);
	void createCustomer(1: Customer customer) throws (1: OperationFailedException ex);
	integer prepareOrder(1: Orders order) throws (1: OperationFailedException ex);
	integer commitOrder(1: Orders order) throws (1: OperationFailedException ex);
}