package jchazelcast;

import java.util.*;

public class JCMap<K,V> extends JCStruct {

    public JCMap(String name, JCConnection connection) {
        super(name, connection);
    }

    /**
     * Associates the specified value with the specified key in the map.
     * If the map previously contained a mapping for this key,
     * the old value is replaced by the specified value. The operation will return the old value.
     * @param flag
     * @param noreply
     * @param key
     * @param value
     * @return Old value of key if exists
     */
   public V put(String flag,boolean noreply,K key,V value){
        return put(flag, JCProtocol.DEFAULT_TIMETOLIVE, noreply, key, value);
   }

    /**
     * Associates the specified value with the specified key in the map.
     * If the map previously contained a mapping for this key,
     * the old value is replaced by the specified value. The operation will return the old value.
     * ttl is optional parameter in milliseconds. If set, the entry will be evicted after ttl milliseconds.
     * @param flag
     * @param ttl  In milliseconds
     * @param noreply
     * @param key
     * @param value
     * @return Old value of key if exists
     */
   public V put(String flag,long ttl,boolean noreply,K key,V value)  {
       connection.sendOp("MPUT " + flag + " " + name + " " + ttl + (noreply ? " noreply " : " "), key, value) ;
       return noreply?null: (V) connection.readResponse().singleValueResponse();
    }

    /**
     * Associates the specified value with the specified key in the map.
     * If the map previously contained a mapping for this key,
     * the old value is replaced by the specified value.
     * This command is exactly the same as MPUT, the only difference is that it
     * want trigger Map Store if defined and will not return the old value.
     * @param flag
     * @param ttl in milliseconds
     * @param noreply
     * @param key
     * @param value
     * @return true if op is OK
     */
   public boolean putTransient(String flag,long ttl,boolean noreply,K key,V value)   {
        connection.sendOp("MPUTTRANSIENT "+flag+" "+name+" "+ttl+  (noreply ? " noreply ":" "),key,value) ;
       return !noreply && connection.readResponse().isOK();

    }

    /**
     * Puts an entry into this map with a given ttl (time to live) value.
     * Entry will expire and get evicted after the ttl.
     * If ttl is 0, then the entry lives forever.
     * Similar to MPUT command except that set doesn't return the old value which is more efficient.
     * @param flag
     * @param ttl in milliseconds
     * @param noreply
     * @param key
     * @param value
     * @return true if op is OK
     */
   public boolean set(String flag,long ttl,boolean noreply,K key,V value)  {
        connection.sendOp("MSET "+flag+" "+name+" "+ttl+  (noreply ? " noreply ":" "),key,value) ;
       return !noreply && connection.readResponse().isOK();
   }

    /**
     * Tries to put the given key, value into this map within specified timeout value.
     * If this method returns false,
     * it means that the caller thread couldn't acquire the lock for the key within timeout duration,
     * thus put operation is not successful.
     * @param flag
     * @param timeout in milliseconds
     * @param key
     * @param value
     * @return true if op is OK
     */
   public boolean tryPut(String flag,long timeout,K key,V value)  {
       connection.sendOp("MTRYPUT "+flag+" "+name+" "+timeout+ " ",key,value) ;
       return connection.readResponse().booleanResponse();
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * The effect of this call is equivalent to that of calling
     * MPUT k v on this map once for each mapping from key k to value v in the specified map but MPUTALL will be much
     * faster as it combines all the keys that belong to certain member and performs bulk operation per member.
     * @param flag
     * @param noreply
     * @param map  Map to copy its key-value pairs
     * @return  true if op is OK
     */
    public boolean putAll(String flag,boolean noreply,Map<K,V> map)  {
        Object[] data = new Object[map.size()*2];
        int i=0;
        for(K k:map.keySet()) {
            data[i++]=k;
            data[i++]=map.get(k);
        }
        return putAll(flag,noreply,data);
    }

   private boolean putAll(String flag,boolean noreply,Object... value)  {
        connection.sendOp("MPUTALL "+flag+" "+name+ (noreply ? " noreply ":" "),value) ;
       return !noreply && connection.readResponse().isOK();
   }

    /**
     * Puts the key and value into this map and unlocks the key if the calling thread owns the lock.
     * @param flag
     * @param noreply
     * @param key
     * @param value
     * @return true if op is OK
     */
   public boolean putAndUnlock(String flag,boolean noreply,K key,V value)  {
        connection.sendOp("MPUTANDUNLOCK " + flag + " " + name  + (noreply ? " noreply " : " "), key, value) ;
       return !noreply && connection.readResponse().isOK();
   }

    /**
     * Tries to acquire the lock for the specified key and returns the value of the key if lock is required in time.
     * If the lock is not available then the current thread becomes disabled for thread scheduling purposes
     * and lies dormant until one of two things happens:
     * - The lock is acquired by the current thread.
     * - The specified waiting time elapses.
     * @param flag
     * @param timeout  in milliseconds
     * @param key
     * @return value of given key in map
     */
   public V tryLockAndGet(String flag,long timeout,K key)  {
        connection.sendOp("MTRYLOCKANDGET "+flag+" "+name+" "+timeout+ " ",key) ;
        return (V)  connection.readResponse().singleValueResponse();
    }

    /**
     * Returns the value to which this map maps the specified key.
     * Returns null if the map contains no mapping for this key.
     * @param flag
     * @param key
     * @return value of given key in map
     */
   public V get(String flag,K key)  {
        connection.sendOp("MGET " + flag + " " + name + " " , key) ;
        return (V)  connection.readResponse().singleValueResponse();
    }

    /**
     * Returns the entries for the given keys.
     * @param flag
     * @param keys
     * @return Collection of values
     */
    public Collection<V> getAll(String flag,Collection<K> keys)  {
        return getAll(flag,(K[]) keys.toArray());
    }

   private Collection<V> getAll(String flag,K... keys)  {
        connection.sendOp("MGETALL " + flag + " " + name + " " , keys) ;
        return (Collection<V>)  connection.readResponse().collectionResponse();
    }

    /**
     * Removes the mapping for this key from this map if it is present.
     * @param flag
     * @param noreply
     * @param key
     * @return value of given key in map
     */
   public V remove(String flag,boolean noreply,K key)  {
        connection.sendOp("MREMOVE " + flag + " " + name + (noreply ? " noreply " : " ") , key) ;
       return noreply?null: (V) connection.readResponse().singleValueResponse();
   }

    /**
     * Tries to remove the entry with the given key from this map within specified timeout value.
     * If the key is already locked by another thread and/or member,
     * then this operation will wait timeout amount for acquiring the lock.
     * @param flag
     * @param timeout in milliseconds
     * @param key
     * @return value of given key in map
     */
   public V tryRemove(String flag,long timeout,K key)  {
       connection.sendOp("MTRYREMOVE "+flag+" "+name+" "+timeout,key);
       return (V) connection.readResponse().singleValueResponse();
   }

    /**
     * Returns the entry statistics and value for a given key.
     * If value is null then just OK will be returned.
     * The time values are long representation of java Date values.
     * @param flag
     * @param key
     * @return JCMapEntry<K,V> object
     */
   public JCMapEntry<K,V> getEntry(String flag,K key)  {
       connection.sendOp("MGETENTRY " + flag + " " + name + " " , key) ;
       JCResponse res =   connection.readResponse();
       String[] split = res.responseLine.split(" ");
       if(split[0].equals("OK") && split.length==12)
          return new JCMapEntry(key,res.data.toArray()[0],Long.valueOf(split[2]),Long.valueOf(split[3]),Long.valueOf(split[4]),Integer.valueOf(split[5]),Long.valueOf(split[6]),Long.valueOf(split[7]),Long.valueOf(split[8]),Long.valueOf(split[9]),Boolean.valueOf(split[10]));
       else
        return null;
    }

    /**
     * Returns keys of the map.
     * @param flag
     * @param type
     * @return Collection of keys
     */
   public Collection<K> keySet(String flag,String type)  {
        connection.sendOp("KEYSET " + flag + " " + type + " "+name  ) ;
       return (Collection<K>) connection.readResponse().collectionResponse();
    }

    /**
     * Acquires the lock for the specified key.
     * If the lock is not available then the call is scheduled and waits until the lock has
     * been acquired. Scope of the lock is this map only.
     * Acquired lock is only for the key in this map. Locks are re-entrant so if
     * the key is locked N times then it should be unlocked N times before another thread can acquire it.
     * @param flag
     * @param timeout in milliseconds
     * @param key
     * @return true if op is OK
     */
   public boolean lock(String flag,long timeout,K key)  {
        connection.sendOp("MLOCK " + flag + " " + name + " "+timeout+" ",key  ) ;
        return connection.readResponse().isOK();
    }

    /**
     * Releases the lock for the specified key. It never blocks and returns immediately.
     * If the current thread is the holder of this lock then the hold count is decremented.
     * If the hold count is now zero then the lock is released.
     * If the current thread is not the holder of this lock then {@link IllegalMonitorStateException} is thrown.
     * @param flag
     * @param key
     * @return true if op is OK
     */
   public boolean unlock(String flag,K key)  {
        connection.sendOp("MUNLOCK " + flag + " " + name + " ",key  ) ;
        return connection.readResponse().isOK();
    }

    /**
     * Tries to acquire the lock for the specified key.
     * If the lock is not available then the current thread becomes disabled for
     * thread scheduling purposes and lies dormant until one of two things happens:
     * -The lock is acquired by the current thread.
     * -The specified waiting time elapses.
     * @param flag
     * @param timeout in milliseconds. 0 means do not wait at all.
     * @param key
     * @return true if lock has been acquired
     */
   public boolean tryLock(String flag,long timeout,K key)  {
        connection.sendOp("MTRYLOCK " + flag + " " + name + " "+timeout+" ",key  ) ;
        return connection.readResponse().booleanResponse();
    }

    /**
     * Checks the lock for the specified key. If the lock is acquired then returns true, else false.
     * @param flag
     * @param key
     * @return true if key is locked.
     */
   public boolean isKeyLocked(String flag,K key)  {
        connection.sendOp("MISKEYLOCKED " + flag + " " + name + " ",key  ) ;
        return connection.readResponse().booleanResponse();
   }

    /**
     * Tries to acquire the lock for the entire map.
     * The thread that locks the map can do all the operations but other threads in the
     * cluster cannot operate on the map.
     * If the lock is not available then the operation waits until one of two things happens:
     * -The lock is acquired by the current thread.
     * -The specified waiting time elapses.
     * @param flag
     * @param timeout in milliseconds
     * @return true if map is locked
     */
   public boolean lockMap(String flag,long timeout)  {
        connection.sendOp("MLOCKMAP " + flag + " "+name+" "+timeout ) ;
        return connection.readResponse().booleanResponse();
   }

    /**
     * Unlocks the map. It never blocks and returns immediately.
     * @param flag
     * @param timeout in milliseconds
     * @return true if op is OK
     */
   public boolean unlockMap(String flag,long timeout)  {
        connection.sendOp("MUNLOCKMAP " + flag + " "+name+" "+timeout ) ;
        return connection.readResponse().isOK();
    }

    /**
     * Releases the lock for the specified key regardless of the lock owner.
     * It always successfully unlocks the key, never blocks and returns immediately.
     * @param flag
     * @param key
     * @return true if op is OK
     */
   public boolean forceUnlock(String flag,K key)  {
        connection.sendOp("MFORCEUNLOCK " + flag + " " + name + " ",key  ) ;
        return connection.readResponse().isOK();
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     * @param flag
     * @param key
     * @return true if key exist in map
     */
   public boolean containsKey(String flag,K key)  {
        connection.sendOp("MCONTAINSKEY " + flag + " " + name + " map ",key  ) ;
        return connection.readResponse().booleanResponse();
   }

    /**
     * Returns true if this map maps one or more keys to the specified value.
     * @param flag
     * @param value
     * @return true if value exist in map
     */
   public boolean containsValue(String flag,V value)  {
        connection.sendOp("MCONTAINSVALUE " + flag + " " + name + " map ",value  ) ;
       return connection.readResponse().booleanResponse();
   }

    /**
     * If the specified key is not already associated with a value, associate it with the given value.
     * @param flag
     * @param noreply
     * @param key
     * @param value
     * @return Old value if exists
     */
   public V putIfAbsent(String flag,boolean noreply,K key,V value)   {
        return putIfAbsent(flag, JCProtocol.DEFAULT_TIMETOLIVE, noreply, key, value);
    }

    /**
     * If the specified key is not already associated with a value, associate it with the given value.
     * The entry will be evicted after ttl milliseconds.
     * @param flag
     * @param ttl in milliseconds
     * @param noreply
     * @param key
     * @param value
     * @return  Old value if exist
     */
   public V putIfAbsent(String flag,long ttl,boolean noreply,K key,V value)   {
        connection.sendOp("MPUTIFABSENT " + flag + " " + name + " " + ttl + (noreply ? " noreply " : " "), key, value) ;
       return noreply?null: (V) connection.readResponse().singleValueResponse();
   }

    /**
     * Removes the entry for a key only if currently mapped to a given value.
     * @param flag
     * @param noreply
     * @param key
     * @param value
     * @return true if it is removed
     */
   public boolean removeIfSame(String flag,boolean noreply,K key,V value)   {
        connection.sendOp("MREMOVEIFSAME " + flag + " " + name +  (noreply ? " noreply " : " "), key, value) ;
       return !noreply && connection.readResponse().booleanResponse();

   }

    /**
     * Replaces the entry for a key only if currently mapped to some value.
     * @param flag
     * @param noreply
     * @param key
     * @param data
     * @return Old value mapped to key
     */
   public V replaceIfNotNull(String flag,boolean noreply,K key,V data)   {
        connection.sendOp("MREPLACEIFNOTNULL " + flag + " " + name +  (noreply ? " noreply " : " "), key, data) ;
       return noreply?null: (V) connection.readResponse().singleValueResponse();
   }

    /**
     * Replaces the entry for a key only if currently mapped to a given value. This is equivalent to:
     * <code>
     * if (map.containsKey(key) && map.get(key).equals(oldValue)) {
     *      map.put(key, newValue);
     *      return true;
     *  } else
     *      return false;
     *
     * </code>
     * @param flag
     * @param noreply
     * @param key
     * @param old_data
     * @param new_data
     * @return true if it is replaced
     */
   public boolean replaceIfSame(String flag,boolean noreply,K key,V old_data,V new_data)   {
        connection.sendOp("MREPLACEIFSAME " + flag + " " + name +  (noreply ? " noreply " : " "), key, old_data,new_data) ;
       return !noreply && connection.readResponse().booleanResponse();

   }

    /**
     * If this map has a MapStore and write-delay-seconds is bigger than 0 (write-behind)
     * then this method flushes all the local dirty entries by calling MapStore.storeAll()
     * @param flag
     * @param noreply
     * @return true if op is OK
     */
   public boolean flush(String flag,boolean noreply)  {
        connection.sendOp("MFLUSH " + flag + " "+name+ (noreply ? " noreply " : " ") ) ;
        return !noreply && connection.readResponse().isOK();
    }

    /**
     * Evicts the specified key from this map.
     * If MapStore defined for this map, then the entry is not deleted from the underlying
     * MapStore, evict only removes the entry from the memory.
     * @param flag
     * @param noreply
     * @param key
     * @return true if it is evicted.
     */
   public boolean evict(String flag,boolean noreply,K key)  {
        connection.sendOp("MEVICT " + flag + " " + name + (noreply ? " noreply " : " "),key  ) ;
      return !noreply && connection.readResponse().booleanResponse();
    }

    /**
     * Adds an entry listener for this map, starts listening for events immediately.
     * In order to stop listening, you should call removeMapListener(Event e) method of listener in the
     * methods of events, or you may close client.
     * Listener will get notified for all map add/remove/update/evict events.
     * @param listener
     * @param inc if it is true, value will be returned with events.
     */
    public void addMapListener(JCMapListener listener,boolean inc)  {
          listener.addMapListener(this.name,inc,this.connection);
    }



//    private void mapAddListener(String flag,boolean inc_value,boolean noreply)  {
//        connection.sendOp("MADDLISTENER " + flag + " " + name + " "+inc_value+(noreply ? " noreply " : " ") ) ;
//        return connection.readResponse().responseLine.startsWith("OK");
//    }


}
