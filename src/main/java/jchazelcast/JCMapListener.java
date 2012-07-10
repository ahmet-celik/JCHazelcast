package jchazelcast;


public abstract class JCMapListener extends JCListener {
    public abstract void entryUpdated(Event e) ;
    public abstract void entryAdded(Event e);
    public abstract void entryRemoved(Event e) ;
    public abstract void entryEvicted(Event e);

    protected void notifyEvent(Event e) {
        if(e.getEventType().equals("UPDATED"))
            entryUpdated(e);
        else if(e.getEventType().equals("ADDED"))
            entryAdded(e);
        else if(e.getEventType().equals("REMOVED"))
            entryRemoved(e);
        else if(e.getEventType().equals("EVICTED"))
            entryEvicted(e);
        else
             throw new JCException("Unknown event type!");
    }


    void addMapListener(String mapName,boolean includeValue,JCConnection connection) {
        this.connection = connection;
        connection.sendOp("MADDLISTENER listener "  + mapName + " "+includeValue+ " false") ;
       if(connection.readResponse().responseLine.startsWith("OK")){
           startListening();
       } else
           throw new JCException("Could not start listening!");
    }

    public void removeMapListener(Event e)  {
        connection.sendOp("MREMOVELISTENER listener " + e.getListenedStructureName() + " " + false) ;
        if(connection.readResponse().responseLine.startsWith("OK"))
            stopListening();
    }


}
