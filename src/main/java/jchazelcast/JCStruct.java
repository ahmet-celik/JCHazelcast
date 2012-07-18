package jchazelcast;



public class JCStruct {
    protected String name;
    protected JCConnection connection;

    protected JCStruct(String name, JCConnection connection) {
        this.name = name;
        this.connection = connection;
    }

    static enum Type {
        MAP,SET,LIST,MULTIMAP,TOPIC,ATOMICNUMBER,IDGENERATOR,COUNTDOWNLATCH,LOCK,QUEUE
    }
}
