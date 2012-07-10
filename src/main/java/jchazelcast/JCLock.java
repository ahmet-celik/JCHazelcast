package jchazelcast;



public class JCLock extends JCStruct{
    protected JCLock(String name, JCConnection connection) {
        super(name, connection);
    }

    /**
     * Acquires the lock. If the lock is not available
     * then the current command becomes disabled for thread scheduling purposes
     * and lies dormant until the lock has been acquired.
     * @param flag
     * @return true if op is OK
     */
    public boolean lock(String flag){
        connection.sendOp("LOCK_LOCK "+flag+" "+name);
        return connection.readResponse().isOK();
    }

    /**
     * Releases the lock.
     * @param flag
     * @return  true if op is OK
     */
    public boolean unlock(String flag){
        connection.sendOp("LOCK_UNLOCK "+flag+" "+name);
        return connection.readResponse().isOK();
    }

    /**
     * Releases the lock regardless of the lock owner.
     * It always successfully unlocks, never blocks and returns immediately.
     * @param flag
     * @return true if op is OK
     */
    public boolean forceUnlock(String flag){
        connection.sendOp("LOCK_FORCE_UNLOCK "+flag+" "+name);
        return connection.readResponse().isOK();
    }

    /**
     * Returns true of the lock is already acquired by someone.
     * @param flag
     * @return  true if it is locked
     */
    public boolean isLocked(String flag){
        connection.sendOp("LOCK_IS_LOCKED "+flag+" "+name);
        return connection.readResponse().booleanResponse();
    }
}
