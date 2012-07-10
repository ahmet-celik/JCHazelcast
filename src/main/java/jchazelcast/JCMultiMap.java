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

    /**
     * Stores a key-value pair in the multimap.
     * @param flag
     * @param noreply
     * @param key
     * @param value
     * @return  true if size of the multimap is increased.
     */
    public boolean put(String flag,boolean noreply,K key,V value){
        connection.sendOp("MMPUT "+flag+" "+name+(noreply?" noreply ":" "),key,value);
        return !noreply && connection.readResponse().booleanResponse();
    }

    /**
     * Remove all the values associated to the given key
     * @param flag
     * @param noreply
     * @param key
     * @return  true if size of the multimap is changed after remove operation.
     */
    public boolean removeKey(String flag,boolean noreply,K key){
        connection.sendOp("MMREMOVE "+flag+" "+name+(noreply?" noreply ":" "),key);
        return !noreply && connection.readResponse().booleanResponse();
    }

    /**
     * Remove only the value associated to the given key
     * @param flag
     * @param noreply
     * @param key
     * @param value
     * @return  true if size of the multimap is changed after remove operation.
     */
    public boolean removeValue(String flag,boolean noreply,K key,V value){
        connection.sendOp("MMREMOVE "+flag+" "+name+(noreply?" noreply ":" "),key,value);
        return !noreply && connection.readResponse().booleanResponse();
    }

    /**
     *  Return number of values associated with the key.
     * @param flag
     * @param key
     * @return  number of values.
     */
    public int valueCount(String flag,K key){
        connection.sendOp("MMVALUECOUNT "+flag+" "+name+" ",key);
        return (int) connection.readResponse().longResponse();

    }
}
