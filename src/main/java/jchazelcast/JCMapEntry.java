package jchazelcast;

public class JCMapEntry<K,V>  {
    private  K key;
    private  V value;
    private long cost;
    private long creationTime;
    private long expirationTime;
    private int hits;
    private long lastAccessTime;
    private long lastStoredTime;
    private long lastUpdateTime;
    private long version;
    private boolean valid;

    public JCMapEntry(K key, V value, long cost, long creationTime, long expirationTime, int hits, long lastAccessTime,
                      long lastStoredTime, long lastUpdateTime, long version, boolean valid) {
        this.key = key;
        this.value = value;
        this.cost = cost;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
        this.hits = hits;
        this.lastAccessTime = lastAccessTime;
        this.lastStoredTime = lastStoredTime;
        this.lastUpdateTime = lastUpdateTime;
        this.version = version;
        this.valid = valid;
    }

   
    public K getKey() {
        return key;  //To change body of implemented methods use File | Settings | File Templates.
    }

   
    public V getValue() {
        return value;
    }

   
    public long getCost() {
        return cost;
    }

   
    public long getCreationTime() {
        return creationTime;
    }

   
    public long getExpirationTime() {
        return expirationTime;
    }
   
    public int getHits() {
        return hits;
    }
   
    public long getLastAccessTime() {
        return lastAccessTime;
    }

   
    public long getLastStoredTime() {
        return lastStoredTime;
    }

   
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

   
    public long getVersion() {
        return version;
    }

   
    public boolean isValid() {
        return valid;
    }
}
