package jchazelcast;



public class JCList<V> extends JCStruct{

    JCList(String name, JCConnection connection) {
        super(name, connection);
    }

    /**
     * Appends the specified element to the end of this list.
     * @param noreply
     * @param item
     * @return true if op is OK.
     */
    public boolean add(boolean noreply,V item){
        connection.sendOp("LADD "+name+(noreply?" noreply ":" "), item);
        return !noreply && connection.readResponse().isOK();
    }
}
