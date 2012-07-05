package jchazelcast;

import com.hazelcast.core.MapEntry;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 05.07.2012
 * Time: 09:42
 * To change this template use File | Settings | File Templates.
 */
public class JCMapEntry<K,V> implements MapEntry<K,V> {
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

    public JCMapEntry(K key, V value, long cost, long creationTime, long expirationTime, int hits, long lastAccessTime, long lastStoredTime, long lastUpdateTime, long version, boolean valid) {
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

    @Override
    public K getKey() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public long getCost() {
        return cost;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public long getExpirationTime() {
        return expirationTime;
    }
    @Override
    public int getHits() {
        return hits;
    }
    @Override
    public long getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public long getLastStoredTime() {
        return lastStoredTime;
    }

    @Override
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public V setValue(V value) {
        this.value = value;
        return value;
    }
}
