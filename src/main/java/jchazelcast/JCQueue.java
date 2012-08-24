package jchazelcast;


import java.util.Collection;

public class JCQueue<Item> extends JCStruct {
    protected JCQueue(String name, JCConnection connection) {
        super(name, connection);
    }

    public boolean offer(boolean noreply,long timeout,Item item){
        connection.sendOp("QOFFER "+name+" "+timeout+(noreply?" noreply ":" "),item);
        return !noreply && connection.readResponse().booleanResponse();
    }

    public boolean put(boolean noreply,Item item){
        connection.sendOp("QPUT "+name+(noreply?" noreply ":" "),item);
        return !noreply && connection.readResponse().isOK();
    }

    public Item poll(long timeout){
        connection.sendOp("QPOLL "+name+" "+timeout);
        return (Item) connection.readResponse().singleValueResponse();
    }

    public Item take(){
        connection.sendOp("QTAKE "+name);
        return (Item) connection.readResponse().singleValueResponse();
    }

    public int size(){
        connection.sendOp("QSIZE "+name);
        return (int) connection.readResponse().longResponse();
    }

    public Item peek(){
        connection.sendOp("QPEEK "+name);
        return (Item) connection.readResponse().singleValueResponse();
    }

    public boolean remove(boolean noreply,Item item){
        connection.sendOp("QREMOVE "+name+(noreply?" noreply ":" "),item);
        return connection.readResponse().booleanResponse();
    }

    public int remainingCapacity(){
        connection.sendOp("QREMCAPACITY "+name);
        return (int) connection.readResponse().longResponse();
    }

    public Collection<Item> entries(){
        connection.sendOp("QENTRIES "+name);
        return (Collection<Item>) connection.readResponse().collectionResponse();
    }

    public void addListener(ItemListener listener ,boolean includeValue){
         connection.sendOp("QADDLISTENER "+name+" "+includeValue);
         listener.addItemListener(connection,"QREMOVELISTENER");
    }
}
