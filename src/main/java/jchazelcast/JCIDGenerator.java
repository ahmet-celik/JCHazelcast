package jchazelcast;


public class JCIDGenerator extends JCStruct{
    JCIDGenerator(String name, JCConnection connection) {
        super(name, connection);
    }

    /**
     *  Generates a new cluster wide unique id.
     * @return the new generated id as long.
     */
    public long newID(){
         connection.sendOp("NEWID "+name);
         return connection.readResponse().longResponse();
    }
}
