package jchazelcast;

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
        List<Object> data;
        public JCResponse(String responseLine){
            this.responseLine=responseLine;
            this.data = null;
        }
        public JCResponse(String responseLine,List data){
            this.responseLine=responseLine;
            this.data = data;
        }

        public String toString(){
            return responseLine+"\r\n"+data;
        }

}
