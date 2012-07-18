package jchazelcast;


public class Event  extends JCResponse {
    protected  String type;
    protected  String name;
    protected  String structure;
    protected  boolean includeValue;
    protected  Object[] values;

    public Event(String type, String name, String structure, boolean includeValue, Object... values) {
        super("EVENT");
        this.type = type;
        this.name = name;
        this.structure = structure;
        this.includeValue = includeValue;
        this.values = values;
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


}
