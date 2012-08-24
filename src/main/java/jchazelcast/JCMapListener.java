package jchazelcast;


public abstract class JCMapListener extends JCListener {
    public abstract void entryUpdated(EntryEvent e) ;
    public abstract void entryAdded(EntryEvent e);
    public abstract void entryRemoved(EntryEvent e) ;
    public abstract void entryEvicted(EntryEvent e);

    protected void listenEntries()  {
        while(!stopListening){
            JCResponse resp = connection.readResponse();
            if(resp.isEvent())
                notifyEvent((EntryEvent) resp.toEvent(true));
            else{
                System.out.println(resp.responseLine);
            }
        }

    }

    protected void notifyEvent(EntryEvent e) {
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
        connection.sendOp("MADDLISTENER "  + mapName + " "+includeValue+ " false") ;
       if(connection.readResponse().responseLine.startsWith("OK")){
           listenEntries();
       } else
           throw new JCException("Could not start listening!");
    }

    /**
     * Removes  this listener for its map.
     * @param e
     */
    public void removeMapListener(EntryEvent e)  {
        connection.sendOp("MREMOVELISTENER " + e.getListenedStructureName() + " " + false) ;
        if(connection.readResponse().responseLine.startsWith("OK"))
            stopListening();
    }


}
