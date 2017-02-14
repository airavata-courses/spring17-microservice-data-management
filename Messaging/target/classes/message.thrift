namespace java edu.iu.messaging.service.model

typedef i32 integer

struct Message {
    1: required binary event;
    2: required string messageId;
}
