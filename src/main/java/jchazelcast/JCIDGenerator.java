package jchazelcast;


public class JCIDGenerator extends JCStruct{
    JCIDGenerator(String name, JCConnection connection) {
        super(name, connection);
    }

    /**
     *  Generates a new cluster wide unique id.
     * @param flag
     * @return the new generated id as long.
     */
    public long newID(String flag){
         connection.sendOp("NEWID "+flag+" "+name);
         return connection.readResponse().longResponse();
    }
}
