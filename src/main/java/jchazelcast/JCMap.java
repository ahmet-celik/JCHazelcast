package jchazelcast;

import com.hazelcast.core.MapEntry;

import java.io.IOException;
import java.util.*;

public class JCMap  {
    private String name;
    private JCConnection connection;



    JCMap(String name,JCConnection connection) throws IOException {
        this.connection=connection;
        this.name       = name;
    }

   public Object put(String flag,boolean noreply,Object key,Object data) throws IOException, ClassNotFoundException {
        return put(flag, JCProtocol.DEFAULT_TIMETOLIVE, noreply, key, data);
    }

   public Object put(String flag,long ttl,boolean noreply,Object key,Object data) throws IOException, ClassNotFoundException {
       connection.sendOp("MPUT " + flag + " " + name + " " + ttl + (noreply ? " noreply " : " "), key, data) ;
       if(!noreply){
           JCResponse res= connection.readResponse();
           if(res.responseLine.startsWith("OK"))
               return res.data.get(0);
           else
               return null;
       }else
           return null;

    }

   public boolean putTransient(String flag,long ttl,boolean noreply,Object key,Object data) throws IOException, ClassNotFoundException  {
        connection.sendOp("MPUTTRANSIENT "+flag+" "+name+" "+ttl+  (noreply ? " noreply ":" "),key,data) ;
       return noreply ?false: connection.readResponse().responseLine.equals("OK "+flag) ;

    }

   public boolean set(String flag,long ttl,boolean noreply,Object key,Object data) throws IOException, ClassNotFoundException {
        connection.sendOp("MSET "+flag+" "+name+" "+ttl+  (noreply ? " noreply ":" "),key,data) ;
       return noreply ?false: connection.readResponse().responseLine.equals("OK "+flag) ;
   }

   public boolean tryPut(String flag,long timeout,Object key,Object data) throws IOException, ClassNotFoundException {
       connection.sendOp("MTRYPUT "+flag+" "+name+" "+timeout+ " ",key,data) ;
       String[] res =connection.readResponse().responseLine.split(" ");
       if(res[0].equals("OK"))
            return Boolean.valueOf(res[2]);
       else
           return false;
    }

    public boolean putAll(String flag,boolean noreply,Map map) throws IOException, ClassNotFoundException {
        Object[] data = new Object[map.size()*2];
        int i=0;
        for(Object k:map.keySet()) {
            data[i++]=k;
            data[i++]=map.get(k);
        }
        return putAll(flag,noreply,data);
    }

   private boolean putAll(String flag,boolean noreply,Object... data) throws IOException, ClassNotFoundException {
        connection.sendOp("MPUTALL "+flag+" "+name+ (noreply ? " noreply ":" "),data) ;
       return noreply ?false: connection.readResponse().responseLine.equals("OK "+flag) ;
   }

   public boolean putAndUnlock(String flag,boolean noreply,Object key,Object data) throws IOException, ClassNotFoundException {
        connection.sendOp("MPUTANDUNLOCK " + flag + " " + name  + (noreply ? " noreply " : " "), key, data) ;
       return noreply ?false: connection.readResponse().responseLine.equals("OK "+flag) ;
   }

   public Object tryLockAndGet(String flag,long timeout,Object key) throws IOException, ClassNotFoundException {
        connection.sendOp("MTRYLOCKANDGET "+flag+" "+name+" "+timeout+ " ",key) ;
        JCResponse res =   connection.readResponse();
        if(res.responseLine.startsWith("OK"))
            return res.data.get(0);
        else
           return null;
    }

   public Object get(String flag,Object key) throws IOException, ClassNotFoundException {
        connection.sendOp("MGET " + flag + " " + name + " " , key) ;
       JCResponse res =   connection.readResponse();
       if(res.responseLine.startsWith("OK"))
           return res.data.get(0);
       else
           return null;
    }

    public Collection<Object> getAll(String flag,Collection keys) throws IOException, ClassNotFoundException {
        return getAll(flag,keys.toArray());
    }

   private Collection<Object> getAll(String flag,Object... keys) throws IOException, ClassNotFoundException {
        connection.sendOp("MGETALL " + flag + " " + name + " " , keys) ;
        JCResponse res =   connection.readResponse();
        if(res.responseLine.startsWith("OK"))
           return res.data;
        else
           return null;
    }

   public Object remove(String flag,boolean noreply,Object key) throws IOException, ClassNotFoundException {
        connection.sendOp("MREMOVE " + flag + " " + name + (noreply ? " noreply " : " ") , key) ;
       JCResponse res =   connection.readResponse();
       if(!noreply){
           if(res.responseLine.startsWith("OK"))
               return res.data.get(0);
           else
               return null;
       }else
           return null;
    }

    //should return also #1 in response.
   public MapEntry getEntry(String flag,Object key) throws IOException, ClassNotFoundException {
       connection.sendOp("MGETENTRY " + flag + " " + name + " " , key) ;
       JCResponse res =   connection.readResponse();
       String[] split = res.responseLine.split(" ");
       if(split[0].equals("OK") && split.length==10)
          return new JCMapEntry(key,res.data.get(0),Long.valueOf(split[1]),Long.valueOf(split[2]),Long.valueOf(split[3]),Integer.valueOf(split[4]),Long.valueOf(split[5]),Long.valueOf(split[6]),Long.valueOf(split[7]),Long.valueOf(split[8]),Boolean.valueOf(split[9]));
       else
        return null;
    }

   public Collection<Object> keySet(String flag,String type) throws IOException, ClassNotFoundException {
        connection.sendOp("KEYSET " + flag + " " + type + " "+name  ) ;
       JCResponse res =   connection.readResponse();
       if(res.responseLine.startsWith("OK"))
           return res.data;
       else
           return null;
    }

   public boolean lock(String flag,long timeout,Object key) throws IOException, ClassNotFoundException {
        connection.sendOp("MLOCK " + flag + " " + name + " "+timeout+" ",key  ) ;
        return connection.readResponse().responseLine.equals("OK "+flag);
    }

   public boolean unlock(String flag,Object key) throws IOException, ClassNotFoundException {
        connection.sendOp("MUNLOCK " + flag + " " + name + " ",key  ) ;
        return connection.readResponse().responseLine.equals("OK "+flag);
    }

   public boolean tryLock(String flag,long timeout,Object key) throws IOException, ClassNotFoundException {
        connection.sendOp("MTRYLOCK " + flag + " " + name + " "+timeout+" ",key  ) ;
        String[] res =connection.readResponse().responseLine.split(" ");
        if(res[0].equals("OK"))
           return Boolean.valueOf(res[2]);
        else
           return false;
    }

   public boolean isKeyLocked(String flag,Object key) throws IOException, ClassNotFoundException {
        connection.sendOp("MISKEYLOCKED " + flag + " " + name + " ",key  ) ;
        String[] res =connection.readResponse().responseLine.split(" ");
        if(res[0].equals("OK"))
           return Boolean.valueOf(res[2]);
        else
           return false;
    }

   public boolean lockMap(String flag,long timeout) throws IOException, ClassNotFoundException {
        connection.sendOp("MLOCKMAP " + flag + " "+name+" "+timeout ) ;
       String[] res =connection.readResponse().responseLine.split(" ");
       if(res[0].equals("OK"))
           return Boolean.valueOf(res[2]);
       else
           return false;
    }

   public boolean unlockMap(String flag,long timeout) throws IOException, ClassNotFoundException {
        connection.sendOp("MUNLOCKMAP " + flag + " "+name+" "+timeout ) ;
        return connection.readResponse().responseLine.equals("OK "+flag);
    }

   public boolean forceUnlock(String flag,Object key) throws IOException, ClassNotFoundException {
        connection.sendOp("MFORCEUNLOCK " + flag + " " + name + " ",key  ) ;
        return connection.readResponse().responseLine.equals("OK "+flag);
    }

   public boolean containsKey(String flag,Object key) throws IOException, ClassNotFoundException {
        connection.sendOp("MCONTAINSKEY " + flag + " " + name + " map ",key  ) ;
       String[] res =connection.readResponse().responseLine.split(" ");
       if(res[0].equals("OK"))
           return Boolean.valueOf(res[2]);
       else
           return false;
    }

   public boolean containsValue(String flag,Object value) throws IOException, ClassNotFoundException {
        connection.sendOp("MCONTAINSVALUE " + flag + " " + name + " map ",value  ) ;
       String[] res =connection.readResponse().responseLine.split(" ");
       if(res[0].equals("OK"))
           return Boolean.valueOf(res[2]);
       else
           return false;
    }

   public Object putIfAbsent(String flag,boolean noreply,Object key,Object data) throws IOException, ClassNotFoundException  {
        return putIfAbsent(flag, JCProtocol.DEFAULT_TIMETOLIVE, noreply, key, data);
    }

   public Object putIfAbsent(String flag,long ttl,boolean noreply,Object key,Object data) throws IOException, ClassNotFoundException  {
        connection.sendOp("MPUTIFABSENT " + flag + " " + name + " " + ttl + (noreply ? " noreply " : " "), key, data) ;
        if(!noreply){
           JCResponse res= connection.readResponse();
           if(res.responseLine.startsWith("OK"))
               return res.data.get(0);
           else
               return null;
        }else
           return null;
    }

   public boolean removeIfSame(String flag,boolean noreply,Object key,Object data) throws IOException, ClassNotFoundException  {
        connection.sendOp("MREMOVEIFSAME " + flag + " " + name +  (noreply ? " noreply " : " "), key, data) ;
       if(!noreply){
            String[] res =connection.readResponse().responseLine.split(" ");
            if(res[0].equals("OK"))
               return Boolean.valueOf(res[2]);
            else
               return false;
       }else
           return false;
    }

   public Object replaceIfNotNull(String flag,boolean noreply,Object key,Object data) throws IOException, ClassNotFoundException  {
        connection.sendOp("MREPLACEIFNOTNULL " + flag + " " + name +  (noreply ? " noreply " : " "), key, data) ;
       if(!noreply){
           JCResponse res= connection.readResponse();
           if(res.responseLine.startsWith("OK"))
               return res.data.get(0);
           else
               return null;
       }else
           return null;
    }

   public boolean replaceIfSame(String flag,boolean noreply,Object key,Object old_data,Object new_data) throws IOException, ClassNotFoundException  {
        connection.sendOp("MREPLACEIFSAME " + flag + " " + name +  (noreply ? " noreply " : " "), key, old_data,new_data) ;
       if(!noreply){
           String[] res =connection.readResponse().responseLine.split(" ");
           if(res[0].equals("OK"))
               return Boolean.valueOf(res[2]);
           else
               return false;
       }else
           return false;
    }

   public boolean flush(String flag,boolean noreply) throws IOException, ClassNotFoundException {
        connection.sendOp("MFLUSH " + flag + " "+name+ (noreply ? " noreply " : " ") ) ;
        return noreply ?false: connection.readResponse().responseLine.equals("OK "+flag) ;
    }

   public boolean evict(String flag,boolean noreply,Object key) throws IOException, ClassNotFoundException {
        connection.sendOp("MEVICT " + flag + " " + name + (noreply ? " noreply " : " "),key  ) ;
       if(!noreply){
           String[] res =connection.readResponse().responseLine.split(" ");
           if(res[0].equals("OK"))
               return Boolean.valueOf(res[2]);
           else
               return false;
       }else
           return false;
    }

    public void addMapListenerAndStartListen(JCMapListener listener,boolean inc) throws IOException, ClassNotFoundException , InterruptedException {
//        connection.sendOp("MADDLISTENER " + flag + " " + name + " "+inc_value+(noreply ? " noreply " : " "),key  ) ;
//        return connection.readResponse().responseLine.equals("OK "+flag);
          listener.addMapListener(this.name,inc,this.connection);

    }



//    private void mapAddListener(String flag,boolean inc_value,boolean noreply) throws IOException, ClassNotFoundException {
//        connection.sendOp("MADDLISTENER " + flag + " " + name + " "+inc_value+(noreply ? " noreply " : " ") ) ;
//        return connection.readResponse().responseLine.startsWith("OK");
//    }


}
