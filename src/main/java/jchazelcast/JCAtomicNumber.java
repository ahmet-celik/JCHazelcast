package jchazelcast;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 09.07.2012
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class JCAtomicNumber extends JCStruct{
    JCAtomicNumber(String name, JCConnection connection) {
        super(name, connection);
    }

    /**
     *  Atomically adds the given value to the current value.
     * @param flag
     * @param delta
     * @return  Updated value
     */
    public long addAndGet(String flag,int delta){
        connection.sendOp("ADDANDGET "+flag+" "+name+" "+delta);
        return connection.readResponse().longResponse();
    }

    /**
     * Atomically sets the given value.
     * @param flag
     * @param newvalue
     * @return Old value.
     */
    public long getAndSet(String flag,long newvalue) {
        connection.sendOp("GETANDSET "+flag+" "+name+" "+newvalue);
        return connection.readResponse().longResponse();
    }

    /**
     * Atomically sets the value to the given updated value only if the current value equals to the expected value.
     * @param flag
     * @param newvalue
     * @param expected
     * @return true if successful; or false if the actual value was not equal to the expected value
     */
    public boolean compareAndSet(String flag,long expected,long newvalue){
        connection.sendOp("COMPAREANDSET "+flag+" "+name+" "+newvalue+" "+ expected);
        return connection.readResponse().booleanResponse();
    }

    /**
     *  Atomically adds the given value to the current value.
     * @param flag
     * @param delta
     * @return Old value.
     */
    public long getAndAdd(String flag,int delta){
        connection.sendOp("GETANDADD "+flag+" "+name+" "+delta);
        return connection.readResponse().longResponse();
    }
}
