package jchazelcast;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 04.07.2012
 * Time: 12:34
 * To change this template use File | Settings | File Templates.
 */
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

        public String toString(){
            return responseLine+"\r\n"+data;
        }

        boolean isOK(){
            return responseLine.charAt(0)=='O' ;
        }

        boolean booleanResponse(){
            String[] res = responseLine.split(" ");
            if(res[0].charAt(0)=='O')
                return Boolean.valueOf(res[2]);
            else
                return false;
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
            return r[0].charAt(0)=='O'? Long.valueOf(r[2]) : 0;
        }



}
