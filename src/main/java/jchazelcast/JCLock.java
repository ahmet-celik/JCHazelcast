package jchazelcast;



public class JCLock extends JCStruct{
    protected JCLock(String name, JCConnection connection) {
        super(name, connection);
    }

    /**
     * Acquires the lock. If the lock is not available
     * then the current command becomes disabled for thread scheduling purposes
     * and lies dormant until the lock has been acquired.
     * @return true if op is OK
     */
    public boolean lock(){
        connection.sendOp("LOCK_LOCK "+name);
        return connection.readResponse().isOK();
    }

    /**
     * Releases the lock.
     * @return  true if op is OK
     */
    public boolean unlock(){
        connection.sendOp("LOCK_UNLOCK "+name);
        return connection.readResponse().isOK();
    }

    /**
     * Releases the lock regardless of the lock owner.
     * It always successfully unlocks, never blocks and returns immediately.
     * @return true if op is OK
     */
    public boolean forceUnlock(){
        connection.sendOp("LOCK_FORCE_UNLOCK "+name);
        return connection.readResponse().isOK();
    }

    /**
     * Returns true of the lock is already acquired by someone.
     * @return  true if it is locked
     */
    public boolean isLocked(){
        connection.sendOp("LOCK_IS_LOCKED "+name);
        return connection.readResponse().booleanResponse();
    }
}
