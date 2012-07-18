package jchazelcast;


import java.util.Collection;

public class JCQueue<Item> extends JCStruct {
    protected JCQueue(String name, JCConnection connection) {
        super(name, connection);
    }

    public boolean offer(String flag,boolean noreply,long timeout,Item item){
        connection.sendOp("QOFFER "+flag+" "+name+" "+timeout+(noreply?" noreply ":" "),item);
        return !noreply && connection.readResponse().booleanResponse();
    }

    public boolean put(String flag,boolean noreply,Item item){
        connection.sendOp("QPUT "+flag+" "+name+(noreply?" noreply ":" "),item);
        return !noreply && connection.readResponse().isOK();
    }

    public Item poll(String flag,long timeout){
        connection.sendOp("QPOLL "+flag+" "+name+" "+timeout);
        return (Item) connection.readResponse().singleValueResponse();
    }

    public Item take(String flag){
        connection.sendOp("QTAKE "+flag+" "+name);
        return (Item) connection.readResponse().singleValueResponse();
    }

    public int size(String flag){
        connection.sendOp("QSIZE "+flag+" "+name);
        return (int) connection.readResponse().longResponse();
    }

    public Item peek(String flag){
        connection.sendOp("QPEEK "+flag+" "+name);
        return (Item) connection.readResponse().singleValueResponse();
    }

    public boolean remove(String flag,boolean noreply,Item item){
        connection.sendOp("QREMOVE "+flag+" "+name+(noreply?" noreply ":" "),item);
        return connection.readResponse().booleanResponse();
    }

    public int remainingCapacity(String flag){
        connection.sendOp("QREMCAPACITY "+flag+" "+name);
        return (int) connection.readResponse().longResponse();
    }

    public Collection<Item> entries(String flag){
        connection.sendOp("QENTRIES "+flag+" "+name);
        return (Collection<Item>) connection.readResponse().collectionResponse();
    }

    public void addListener(ItemListener listener ,boolean includeValue){
         connection.sendOp("QADDLISTENER listener "+name+" "+includeValue);
         listener.addItemListener(connection,"QREMOVELISTENER");
    }
}
