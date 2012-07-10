package jchazelcast;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 09.07.2012
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class JCStruct {
    protected String name;
    protected JCConnection connection;

    protected JCStruct(String name, JCConnection connection) {
        this.name = name;
        this.connection = connection;
    }

    static enum Type {
        MAP,SET,LIST,MULTIMAP,TOPIC,ATOMICNUMBER,IDGENERATOR,COUNTDOWNLATCH,LOCK
    }
}
