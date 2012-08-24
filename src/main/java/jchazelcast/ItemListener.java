package jchazelcast;


public abstract class ItemListener extends JCListener {
    private String removeCommand;

    public abstract void itemAdded(ItemEvent e);
    public abstract void itemRemoved(ItemEvent e);


    protected void listenItems()  {
        while(!stopListening){
            JCResponse resp = connection.readResponse();
            if(resp.isEvent())
                notifyEvent((ItemEvent)resp.toEvent(false));
            else{
                System.out.println(resp.responseLine);
            }
        }

    }

    protected void notifyEvent(ItemEvent e) {
        if(e.getEventType().equals("ADDED"))
            itemAdded(e);
        else if(e.getEventType().equals("REMOVED"))
            itemRemoved(e);
        else
            throw new JCException("Unknown event type!");
    }

    void addItemListener(JCConnection connection,String command) {
        this.connection = connection;
        this.removeCommand = command;
        if(connection.readResponse().responseLine.startsWith("OK")){
            listenItems();
        } else
            throw new JCException("Could not start listening!");
    }

    public void removeListener(ItemEvent e)  {
        connection.sendOp(removeCommand+" " + e.getListenedStructureName() + " " + false) ;
        if(connection.readResponse().responseLine.startsWith("OK")){
            stopListening();
            System.out.println("STOPPED LISTENING: "+e.getListenedStructureName()+" name: "+e.name);
        }
    }
}
