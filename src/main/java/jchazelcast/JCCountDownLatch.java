package jchazelcast;


public class JCCountDownLatch extends JCStruct{
    protected JCCountDownLatch(String name, JCConnection connection) {
        super(name, connection);
    }

    /**
     * Causes the current command to wait until the latch has counted down to zero
     * or the specified waiting time elapses
     * @param time in milliseconds
     * @return true if op is OK
     */
    public boolean await(long time){
        connection.sendOp("CDLAWAIT "+name+" "+time);
        return connection.readResponse().isOK();
    }

    /**
     * Returns the current count.
     * @return count
     */
    public int getCount(){
        connection.sendOp("CDLGETCOUNT "+name);
        return (int) connection.readResponse().longResponse();
    }

    /**
     * Sets the count to the given value if the current count is zero.
     * The calling cluster member becomes the owner of the countdown
     *  and is responsible for staying connected to the cluster until the count reaches zero.
     * @param count
     * @return true if the new count was set   @TODO donot return boolean
     */
    public boolean setCount(int count){
        connection.sendOp("CDLSETCOUNT "+name+" "+count);
         return connection.readResponse().isOK();
    }

    /**
     * Decrements the count of the latch, releasing all waiting threads if the count reaches zero.
     * @return  true if op is OK
     */
    public boolean countDown(){
        connection.sendOp("CDLCOUNTDOWN "+name);
        return connection.readResponse().isOK();
    }
}
