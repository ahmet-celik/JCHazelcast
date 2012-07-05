package jchazelcast;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 04.07.2012
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public abstract class JCMapListener extends JCListener {
    public abstract void entryUpdated(Event e) throws IOException, ClassNotFoundException, InterruptedException;
    public abstract void entryAdded(Event e);
    public abstract void entryRemoved(Event e) throws IOException, ClassNotFoundException, InterruptedException;
    public abstract void entryEvicted(Event e);

    void notifyEvent(Event e) throws IOException, ClassNotFoundException, InterruptedException {
        if(e.getEventType().equals("UPDATED"))
            entryUpdated(e);
        else if(e.getEventType().equals("ADDED"))
            entryAdded(e);
        else if(e.getEventType().equals("REMOVED"))
            entryRemoved(e);
        else if(e.getEventType().equals("EVICTED"))
            entryEvicted(e);
        else
             throw new IllegalArgumentException("Unknown event type!");
    }


    void addMapListener(String mapName,boolean includeValue,JCConnection connection) throws IOException, InterruptedException, ClassNotFoundException {
        this.connection = connection;
        connection.sendOp("MADDLISTENER listener "  + mapName + " "+includeValue+ " false") ;
       if(connection.readResponse().responseLine.startsWith("OK"))
           startListening();

    }

    public void removeMapListener(Event e) throws IOException, InterruptedException, ClassNotFoundException {
        connection.sendOp("MREMOVELISTENER listener " + e.getListenedStructureName() + " " + false) ;
        if(connection.readResponse().responseLine.startsWith("OK"))
            stopListening();
    }


}
