package jchazelcast;

import java.io.IOException;

public class JCMap  {
    private String name;
    private static JCConnection connection;
    private JCListener listenThread;



    public JCMap(String name) throws IOException {
        if(connection==null){
            connection = JCConnection.getConnection();
            connection.connect();
            if(!connection.auth("AUTH_JCMAP",JCHazelcast.getConfig().getUn(),JCHazelcast.getConfig().getPw()))
                throw new IOException("wrong pass or username");
        }
        this.name       = name;
    }



   public void put(String flag,boolean noreply,byte[] key,byte[] data) throws IOException {
        put(flag,  JCProtocol.DEFAULT_TIMETOLIVE, noreply, key, data);
    }

   public void put(String flag,long ttl,boolean noreply,byte[] key,byte[] data) throws IOException {
        connection.sendOp("MPUT " + flag + " " + name + " " + ttl + (noreply ? " noreply " : " "), key, data) ;
        System.out.println(connection.readResponse());
    }

   public void putTransient(String flag,long ttl,boolean noreply,byte[] key,byte[] data) throws IOException {
        connection.sendOp("MPUTTRANSIENT "+flag+" "+name+" "+ttl+  (noreply ? " noreply ":" "),key,data) ;
        System.out.println(connection.readResponse());
    }

   public void set(String flag,long ttl,boolean noreply,byte[] key,byte[] data) throws IOException{
        connection.sendOp("MSET "+flag+" "+name+" "+ttl+  (noreply ? " noreply ":" "),key,data) ;
        System.out.println(connection.readResponse());
    }

   public void tryPut(String flag,long timeout,byte[] key,byte[] data) throws IOException{
        connection.sendOp("MTRYPUT "+flag+" "+name+" "+timeout+ " ",key,data) ;
        System.out.println(connection.readResponse());
    }

   public void putAll(String flag,boolean noreply,byte[]... data) throws IOException{
        connection.sendOp("MPUTALL "+flag+" "+name+ (noreply ? " noreply ":" "),data) ;
        System.out.println(connection.readResponse());
    }

   public void putAndUnlock(String flag,boolean noreply,byte[] key,byte[] data) throws IOException{
        connection.sendOp("MPUTANDUNLOCK " + flag + " " + name  + (noreply ? " noreply " : " "), key, data) ;
        System.out.println(connection.readResponse());
    }

   public void tryLockAndGet(String flag,long timeout,byte[] key) throws IOException{
        connection.sendOp("MTRYLOCKANDGET "+flag+" "+name+" "+timeout+ " ",key) ;
        System.out.println(connection.readResponse());
    }

   public void get(String flag,byte[] key) throws IOException{
        connection.sendOp("MGET " + flag + " " + name + " " , key) ;
        System.out.println(connection.readResponse());
    }

   public void getAll(String flag,byte[]... keys) throws IOException{
        connection.sendOp("MGETALL " + flag + " " + name + " " , keys) ;
        System.out.println(connection.readResponse());
    }

   public void remove(String flag,boolean noreply,byte[] key) throws IOException{
        connection.sendOp("MREMOVE " + flag + " " + name + (noreply ? " noreply " : " ") , key) ;
        System.out.println(connection.readResponse());
    }

    //should return also #1 in response.
   public void getEntry(String flag,byte[] key) throws IOException{
        connection.sendOp("MGETENTRY " + flag + " " + name + " " , key) ;
        System.out.println(connection.readResponse());
    }

   public void keySet(String flag,String type) throws IOException{
        connection.sendOp("KEYSET " + flag + " " + type + " "+name  ) ;
        System.out.println(connection.readResponse());
    }

   public void lock(String flag,long timeout,byte[] key) throws IOException{
        connection.sendOp("MLOCK " + flag + " " + name + " "+timeout+" ",key  ) ;
        System.out.println(connection.readResponse());
    }

   public void unlock(String flag,byte[] key) throws IOException{
        connection.sendOp("MUNLOCK " + flag + " " + name + " ",key  ) ;
        System.out.println(connection.readResponse());
    }

   public void tryLock(String flag,long timeout,byte[] key) throws IOException{
        connection.sendOp("MTRYLOCK " + flag + " " + name + " "+timeout+" ",key  ) ;
        System.out.println(connection.readResponse());
    }

   public void isKeyLocked(String flag,byte[] key) throws IOException{
        connection.sendOp("MISKEYLOCKED " + flag + " " + name + " ",key  ) ;
        System.out.println(connection.readResponse());
    }

   public void lockMap(String flag,long timeout) throws IOException{
        connection.sendOp("MLOCKMAP " + flag + " "+name+" "+timeout ) ;
        System.out.println(connection.readResponse());
    }

   public void unlockMap(String flag,long timeout) throws IOException{
        connection.sendOp("MUNLOCKMAP " + flag + " "+name+" "+timeout ) ;
        System.out.println(connection.readResponse());
    }

   public void forceUnlock(String flag,byte[] key) throws IOException{
        connection.sendOp("MFORCEUNLOCK " + flag + " " + name + " ",key  ) ;
        System.out.println(connection.readResponse());
    }

   public void containsKey(String flag,byte[] key) throws IOException{
        connection.sendOp("MCONTAINSKEY " + flag + " " + name + " map ",key  ) ;
        System.out.println(connection.readResponse());
    }

   public void containsValue(String flag,byte[] value) throws IOException{
        connection.sendOp("MCONTAINSVALUE " + flag + " " + name + " map ",value  ) ;
        System.out.println(connection.readResponse());
    }

   public void putIfAbsent(String flag,boolean noreply,byte[] key,byte[] data) throws IOException {
        putIfAbsent(flag, JCProtocol.DEFAULT_TIMETOLIVE, noreply, key, data);
    }

   public void putIfAbsent(String flag,long ttl,boolean noreply,byte[] key,byte[] data) throws IOException {
        connection.sendOp("MPUTIFABSENT " + flag + " " + name + " " + ttl + (noreply ? " noreply " : " "), key, data) ;
        System.out.println(connection.readResponse());
    }

   public void removeIfSame(String flag,boolean noreply,byte[] key,byte[] data) throws IOException {
        connection.sendOp("MREMOVEIFSAME " + flag + " " + name +  (noreply ? " noreply " : " "), key, data) ;
        System.out.println(connection.readResponse());
    }

   public void replaceIfNotNull(String flag,boolean noreply,byte[] key,byte[] data) throws IOException {
        connection.sendOp("MREPLACEIFNOTNULL " + flag + " " + name +  (noreply ? " noreply " : " "), key, data) ;
        System.out.println(connection.readResponse());
    }

   public void replaceIfSame(String flag,boolean noreply,byte[] key,byte[] old_data,byte[] new_data) throws IOException {
        connection.sendOp("MREPLACEIFSAME " + flag + " " + name +  (noreply ? " noreply " : " "), key, old_data,new_data) ;
        System.out.println(connection.readResponse());
    }

   public void flush(String flag,boolean noreply) throws IOException{
        connection.sendOp("MFLUSH " + flag + " "+name+ (noreply ? " noreply " : " ") ) ;
        System.out.println(connection.readResponse());
    }

   public void evict(String flag,boolean noreply,byte[] key) throws IOException{
        connection.sendOp("MEVICT " + flag + " " + name + (noreply ? " noreply " : " "),key  ) ;
        System.out.println(connection.readResponse());
    }

    public void addListener(EntryListener listener,boolean inc) throws IOException, InterruptedException {
//        connection.sendOp("MADDLISTENER " + flag + " " + name + " "+inc_value+(noreply ? " noreply " : " "),key  ) ;
//        System.out.println(connection.readResponse());
        if(listenThread==null) {
            listenThread = new JCListener(name);
        }
        listenThread.addMapListener(listener,inc);
    }

    public void removeListener(EntryListener listener) throws  IOException{
        listenThread.removeMapListener(listener);
        if(listenThread.listenersSize()==0){
            listenThread.stopListening();
        }
    }

//    private void mapAddListener(String flag,boolean inc_value,boolean noreply) throws IOException{
//        connection.sendOp("MADDLISTENER " + flag + " " + name + " "+inc_value+(noreply ? " noreply " : " ") ) ;
//        System.out.println(connection.readResponse());
//    }


}
