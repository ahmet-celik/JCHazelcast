package jchazelcast;


public abstract class  JCListener {
    protected boolean stopListening;
    protected JCConnection connection;

    protected void startListening()  {
        while(!stopListening){
                    JCResponse resp = connection.readResponse();
                    if(resp instanceof Event)
                        notifyEvent((Event) resp);
                    else{
                        System.out.println(resp.responseLine);
                    }
        }

    }

   protected void stopListening()  {
        stopListening =true;
    }



    protected abstract void notifyEvent(Event e) ;



//    static JCResponse readEventNResponse() throws IOException, ClassNotFoundException {
//        String responseLine = connection.readLine();
////        System.out.println("DEBUG::inthread "+responseLine);
//        String[] split = responseLine.split(" ");
//
//        int count=0;
//        List<Object> values = new ArrayList<Object>();
//        if ( split[split.length - 1].startsWith("#")) {
//             count = Integer.parseInt(split[split.length - 1].substring(1));
//            if(count>0){
//                String sizeLine = connection.readLine();
//                String[] tokens = sizeLine.split(" ");
////                System.out.println(sizeLine);
//                for (int i = 0; i < count; i++) {
//                    values.add(JCSerial.deserialize(connection.readData(Integer.parseInt(tokens[i]))));
//                }
//                connection.readData(2); //read CRLF
//
//            }
//        }
//        if(split[0].equals("EVENT")){
//            String eventType = split[4];
//            boolean inc= (count > 1);
//            if(inc){
//                if(eventType.equals("UPDATED")){
//                    return new Event(split[4],split[3],split[2],inc,values.get(0),values.get(1),values.get(2));
//                }else{
//                    return new Event(split[4],split[3],split[2],inc,values.get(0),values.get(1));
//                }
//            }else{
//                return new Event(split[4],split[3],split[2],inc,values.get(0));
//            }
//        }
//        return new JCResponse(responseLine,values);
//    }




}
