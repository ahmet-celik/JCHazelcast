package jchazelcast;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 09.07.2012
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
public class JCMultiMap<K,V> extends JCStruct{
    JCMultiMap(String name, JCConnection connection) {
        super(name, connection);
    }

    public boolean put(String flag,boolean noreply,K key,V value){
        connection.sendOp("MMPUT "+flag+" "+name+(noreply?" noreply ":" "),key,value);
        return !noreply && connection.readResponse().booleanResponse();
    }

    public boolean removeKey(String flag,boolean noreply,K key){
        connection.sendOp("MMREMOVE "+flag+" "+name+(noreply?" noreply ":" "),key);
        return !noreply && connection.readResponse().booleanResponse();
    }

    public boolean removePair(String flag,boolean noreply,K key,V value){
        connection.sendOp("MMREMOVE "+flag+" "+name+(noreply?" noreply ":" "),key,value);
        return !noreply && connection.readResponse().booleanResponse();
    }
}
