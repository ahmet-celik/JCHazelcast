package jchazelcast;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 09.07.2012
 * Time: 10:53
 * To change this template use File | Settings | File Templates.
 */
public class JCException extends RuntimeException {
       public JCException(String message){
           super(message);
       }
        public JCException(Throwable throwable){
            super(throwable);
        }
        public JCException(String message,Throwable throwable){
            super(message,throwable);
        }
}
