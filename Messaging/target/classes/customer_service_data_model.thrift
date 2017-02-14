namespace java edu.iu.messaging.service.model

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
