package jchazelcast;


public class JCAtomicNumber extends JCStruct{
    JCAtomicNumber(String name, JCConnection connection) {
        super(name, connection);
    }

    /**
     *  Atomically adds the given value to the current value.
     * @param delta
     * @return  Updated value
     */
    public long addAndGet(int delta){
        connection.sendOp("ADDANDGET "+name+" "+delta);
        return connection.readResponse().longResponse();
    }

    /**
     * Atomically sets the given value.
     * @param newvalue
     * @return Old value.
     */
    public long getAndSet(long newvalue) {
        connection.sendOp("GETANDSET "+name+" "+newvalue);
        return connection.readResponse().longResponse();
    }

    /**
     * Atomically sets the value to the given updated value only if the current value equals to the expected value.
     * @param newvalue
     * @param expected
     * @return true if successful; or false if the actual value was not equal to the expected value
     */
    public boolean compareAndSet(long expected,long newvalue){
        connection.sendOp("COMPAREANDSET "+name+" "+newvalue+" "+ expected);
        return connection.readResponse().booleanResponse();
    }

    /**
     *  Atomically adds the given value to the current value.
     * @param delta
     * @return Old value.
     */
    public long getAndAdd(int delta){
        connection.sendOp("GETANDADD "+name+" "+delta);
        return connection.readResponse().longResponse();
    }
}
