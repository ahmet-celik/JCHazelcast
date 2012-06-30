package jchazelcast;


public interface EntryListener<K,V> {
       public void onUpdated(Event<K, V> e);
       public void onAdded(Event<K, V> e);
       public void onRemoved(Event<K, V> e);
       public void onEvicted(Event<K, V> e);
}
