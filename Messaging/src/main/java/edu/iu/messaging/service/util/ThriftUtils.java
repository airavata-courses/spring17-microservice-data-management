package edu.iu.messaging.service.util;

import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

/**
 * Created by Ajinkya on 2/2/17.
 */
public class ThriftUtils {

    public static byte[] serializeThriftObject(TBase object) throws TException {
        return new TSerializer().serialize(object);
    }

    public static void createThriftFromBytes(byte[] bytes, TBase object) throws TException {
        new TDeserializer().deserialize(object, bytes);
    }
}
