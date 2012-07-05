package jchazelcast;


public class Event<K,V>  extends JCResponse {
    private String type;
    private String name;
    private String structure;
    private boolean includeValue;
    private K key;
    private V newvalue;
    private V oldvalue;

    public V getValue() {
        return newvalue;
    }

    public K getKey() {
        return key;
    }

    public String getEventType() {
        return type;
    }

    public String getListenedStructureName() {
        return name;
    }

    public String getStructureType(){
        return structure;
    }

    public Event(String type,String name,String structure, boolean iV,  K key) {
        super("EVENT");
        this.type = type;
        this.includeValue = iV;
        this.name = name;
        this.structure=structure;
        this.key = key;
    }

    public Event(String type, String name,String structure,boolean iV,  K key, V value) {
        super("EVENT");
        this.type = type;
        this.includeValue = iV;
        this.name = name;
        this.key = key;
        this.newvalue= value;
        this.structure=structure;
    }

    public Event(String type,String name,String structure, boolean iV,  K key, V newvalue, V oldvalue) {
        super("EVENT");
        this.type = type;
        this.includeValue = iV;
        this.name = name;
        this.key = key;
        this.newvalue = newvalue;
        this.oldvalue = oldvalue;
        this.structure=structure;
    }
}
