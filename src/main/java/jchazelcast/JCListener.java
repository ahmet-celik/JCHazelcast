package jchazelcast;

import java.io.IOException;
import java.util.*;


public class JCListener implements  Runnable{
    private Set<EntryListener> listeners;
    private String mapName;
    private Thread listenThread;
    private JCConnection connection;
    private boolean stopListening;

    public JCListener(String name) throws IOException {
        this.connection =  JCConnection.getConnection();
        this.mapName=name;
        this.connection.connect();
        if(!this.connection.auth("AUTH_JCListener",JCHazelcast.getConfig().getUn(),JCHazelcast.getConfig().getPw()))
            throw new IOException("wrong pass or username");
        listeners = new HashSet<EntryListener>();
        stopListening = false;
        listenThread = new Thread(this);
        listenThread.start();

    }

    public int listenersSize(){
        return listeners.size();
    }

    public void stopListening(){
        stopListening =true;
    }

    public void addMapListener(EntryListener listener,boolean includeValue) throws IOException, InterruptedException {
        connection.sendOp("MADDLISTENER listener " + mapName + " " + includeValue + " " + false) ;
        listeners.add(listener);

    }

    public void removeMapListener(EntryListener listener) throws IOException {
        listeners.remove(listener);
        if(listeners.size()==0){
            connection.sendOp("MREMOVELISTENER listener " + mapName + " " + false) ;
        }
    }


    public void run()  {
        while(!stopListening){
            try {
                if( connection.ready()){
                    JCConnection.Response resp = readEventNResponse();
                    if(resp instanceof Event)
                        notifyEvent((Event) resp);
                    else
                        System.out.println("inthread response: "+resp);
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void notifyEvent(Event e){
        if(e.type.equals("UPDATED")){
                for(EntryListener each : listeners){
                    each.onUpdated(e);
                }
        }else if(e.type.equals("ADDED")){
            for(EntryListener each : listeners){
                each.onAdded(e);
            }
        }else if(e.type.equals("REMOVED")){
            for(EntryListener each : listeners){
                each.onRemoved(e);
            }
        }else if(e.type.equals("EVICTED")){
            for(EntryListener each : listeners){
                each.onEvicted(e);
            }
        }else {
            throw new IllegalArgumentException("Unknown event type!");
        }
    }

    public JCConnection.Response readEventNResponse() throws IOException, ClassNotFoundException {
        String responseLine = connection.readLine();
        System.out.println("DEBUG::inthread "+responseLine);
        String[] split = responseLine.split(" ");

        int count=0;
        List<String> values = new ArrayList<String>();
        if ( split[split.length - 1].startsWith("#")) {
             count = Integer.parseInt(split[split.length - 1].substring(1));
            if(count>0){
                String sizeLine = connection.readLine();
                System.out.println("before:"+sizeLine);
                String[] tokens = sizeLine.split(" ");
                System.out.println(sizeLine);
                for (int i = 0; i < count; i++) {
                    values.add((new String(connection.readData(Integer.parseInt(tokens[i])))));
                }
                connection.readData(2); //read CRLF

            }
        }
        if(split[0].equals("EVENT")){
            String eventType = split[4];
            boolean inc= (count <= 1);
            if(inc){
                if(eventType.equals("UPDATED")){
                    return new Event(split[4],split[3],inc,values.get(0),values.get(1),values.get(2));
                }else{
                    return new Event(split[4],split[3],inc,values.get(0),values.get(1));
                }
            }else{
                return new Event(split[4],split[3],inc,values.get(0));
            }
        }
        return new JCConnection.Response(responseLine,values);
    }




}
