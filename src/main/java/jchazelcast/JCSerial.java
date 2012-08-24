package jchazelcast;

import com.hazelcast.nio.Serializer;


public class JCSerial {
    private Serializer s= new Serializer();


    public  byte[] serialize(Object obj) {
//        try {
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            ObjectOutputStream os = new ObjectOutputStream(out);
//            os.writeObject(obj);
//            return out.toByteArray();
//        } catch (IOException e) {
//            throw new JCException("IOException in the serialization of object.",e);
//        }
        return s.toByteArray(obj);
    }
    public  Object deserialize(byte[] data) {
//        try {
//            ByteArrayInputStream in = new ByteArrayInputStream(data);
//            ObjectInputStream is = new ObjectInputStream(in);
//            return is.readObject();
//        }catch (IOException e){
//            throw new JCException("IOException in the serialization of object.",e);
//
//        } catch (ClassNotFoundException e) {
//            throw new JCException("ClassNotFoundException in the serialization of object.",e);
//
//        }
        return s.toObject(data);
    }


}
