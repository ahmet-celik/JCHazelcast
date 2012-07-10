package jchazelcast;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 10.07.2012
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
public class JCCountDownLatch extends JCStruct{
    protected JCCountDownLatch(String name, JCConnection connection) {
        super(name, connection);
    }

    /**
     * Causes the current command to wait until the latch has counted down to zero
     * or the specified waiting time elapses
     * @param flag
     * @param time in milliseconds
     * @return true if op is OK
     */
    public boolean await(String flag,long time){
        connection.sendOp("CDLAWAIT "+flag+" "+name+" "+time);
        return connection.readResponse().isOK();
    }

    /**
     * Returns the current count.
     * @param flag
     * @return count
     */
    public int getCount(String flag){
        connection.sendOp("CDLGETCOUNT "+flag+" "+name);
        return (int) connection.readResponse().longResponse();
    }

    /**
     * Sets the count to the given value if the current count is zero.
     * The calling cluster member becomes the owner of the countdown
     *  and is responsible for staying connected to the cluster until the count reaches zero.
     * @param flag
     * @param count
     * @return true if the new count was set   @TODO donot return boolean
     */
    public boolean setCount(String flag,int count){
        connection.sendOp("CDLSETCOUNT "+flag+" "+name+" "+count);
         return connection.readResponse().isOK();
    }

    /**
     * Decrements the count of the latch, releasing all waiting threads if the count reaches zero.
     * @param flag
     * @return  true if op is OK
     */
    public boolean countDown(String flag){
        connection.sendOp("CDLCOUNTDOWN "+flag+" "+name);
        return connection.readResponse().isOK();
    }
}
