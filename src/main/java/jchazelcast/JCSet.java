package jchazelcast;

public class JCSet<V> extends JCStruct {

    JCSet(String name, JCConnection connection) {
        super(name, connection);
    }

    /**
     * Adds the specified element to this set if it is not already present
     * @param noreply
     * @param item
     * @return true if item is added to set. If it is present then false is returned.
     */
    public boolean add(boolean noreply,V item){
        connection.sendOp("SADD "+ name+(noreply?" noreply ":" "),item);
        return !noreply && connection.readResponse().booleanResponse();
    }
}
