package jchazelcast;


public class JCConfig {
    private String un;
    private String pw;
    private String host;
    private int port;

    public JCConfig(String un, String pw, String host, int port) {
        this.un = un;
        this.pw = pw;
        this.host = host;
        this.port = port;
    }

    public JCConfig(){
        un=   JCProtocol.DEFAULT_UN ;
        pw  = JCProtocol.DEFAULT_PW;
        host= JCProtocol.DEFAULT_HOST;
        port=  JCProtocol.DEFAULT_PORT;
    }

    public String getUn() {
        return un;
    }

    public String getPw() {
        return pw;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
