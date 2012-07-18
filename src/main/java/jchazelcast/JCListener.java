package jchazelcast;


public  class  JCListener {
    protected boolean stopListening;
    protected JCConnection connection;

    //do not close connection,user should close explicitly
    protected void stopListening()  {
        stopListening =true;
    }

}
