package jchazelcast;


import java.util.Collection;




public class JCResponse {
        String responseLine;
        Collection<Object> data;
        JCResponse(String responseLine){
            this.responseLine=responseLine;
            this.data = null;
        }
        JCResponse(String responseLine,Collection<Object> data){
            this.responseLine=responseLine;
            this.data = data;
        }

    private JCResponse() {}

    public String toString(){
            return responseLine+"\r\n"+data;
        }

        boolean isOK(){
            return responseLine.charAt(0)=='O' ;
        }

        boolean booleanResponse(){
            String[] res = responseLine.split(" ");
            if(res[0].charAt(0)=='O')
                return Boolean.valueOf(res[1]);
            else
                return false;
        }

        boolean isEvent(){
            return responseLine.charAt(0)=='E' && responseLine.charAt(1)=='V';
        }

        Object singleValueResponse(){
            int len = responseLine.length();
            if(responseLine.charAt(0)=='O'&&responseLine.charAt(len-2)=='#'&&responseLine.charAt(len-1)=='1')
                return  data.toArray()[0];
            else
                return null;
        }

        Collection<Object> collectionResponse(){
            if(responseLine.charAt(0)=='O')
                return data;
            else
                return null;
        }

        long longResponse(){
            String[] r =responseLine.split(" ");
            return r[0].charAt(0)=='O'? Long.valueOf(r[1]) : 0;
        }

        Event toEvent(boolean isMapEvent){
            String[] r = responseLine.split(" ");
            return isMapEvent ? new EntryEvent(r[3],r[2],r[1],data.size()>1,data.toArray()) : new ItemEvent(r[3],r[2],r[1],data.size()>1,data.toArray());
        }
}
