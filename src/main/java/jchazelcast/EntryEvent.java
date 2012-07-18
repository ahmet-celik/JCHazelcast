package jchazelcast;


public class EntryEvent<K,V> extends Event {
    public EntryEvent(String type, String name, String structure, boolean includeValue, Object... values) {
        super(type, name, structure, includeValue, values);
    }

    public K getKey(){
        return (K) values[0];
    }

    public V getValue(){
        return includeValue ? (V) values[1] : null;
    }

    public V getOldValue(){
        return includeValue&&type.charAt(0)=='U' ? (V) values[2] : null;
    }
}
