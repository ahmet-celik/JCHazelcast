package jchazelcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static jchazelcast.JCProtocol.CHARSET;
import static jchazelcast.JCProtocol.END_OF_LINE;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 20.06.2012
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class JCConnection {
    private String host ;
    private int port ;
    private Socket socket;
    private OutputStream outputStream;
    private BufferedReader  inputStream;
    private int timeout = JCProtocol.DEFAULT_TIMEOUT;


    public void setConfig (JCConfig cf){
            host = cf.getHost();
            port = cf.getPort();
    }

    public static JCConnection  getConnection(){
        JCConnection jcConnection = new JCConnection();
        jcConnection.setConfig(JCHazelcast.getConfig());
        return jcConnection;
    }


    public void connect() throws IOException {
        if(!isConnected()){
            try {
                socket = new Socket();
                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true);
                socket.setSoLinger(true, 0);
                socket.connect(new InetSocketAddress(host, port), timeout);
                socket.setSoTimeout(timeout);
                outputStream = socket.getOutputStream();
                inputStream = new  BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException exception) {
                throw new IOException(exception);
            }
        }
    }

    public void disconnect() throws IOException{
        if(isConnected()){
            try {
                inputStream.close();
                outputStream.close();
                if (!socket.isClosed())
                    socket.close();
            } catch (IOException exception) {
                throw new IOException(exception);
            }
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isBound() && !socket.isClosed()  && socket.isConnected()
                && !socket.isInputShutdown() && !socket.isOutputShutdown();
    }

    public boolean auth(String flag,String username,String pass) throws IOException {
        sendOp(JCProtocol.ID);
        sendOp("AUTH " + flag + " " + username + " " + pass);
        return readResponse().responseLine.equals("OK "+flag);
    }

    protected void sendOp(String commandLine) throws IOException{
        outputStream.write(commandLine.getBytes(CHARSET));
        outputStream.write(END_OF_LINE);
        outputStream.flush();
    }
    protected boolean ready() throws IOException {
        return inputStream.ready();
    }
    protected void sendOp(String commandLine,byte[]... data) throws IOException{
        outputStream.write(commandLine.getBytes(CHARSET));
        outputStream.write('#');
        outputStream.write((""+data.length).getBytes());
        outputStream.write(END_OF_LINE);
        StringBuilder sb = new StringBuilder();
        for(byte[] ba:data){
            sb.append(""+ba.length+" ");
        }
        outputStream.write(sb.toString().getBytes());
        outputStream.write(END_OF_LINE);
        for(byte[] ba:data){
            outputStream.write(ba);
        }
        outputStream.write(END_OF_LINE);
        outputStream.flush();
    }

    protected  char[] readData(int size) throws IOException {
        char[] aux = new char[size];
        inputStream.read(aux);
        return aux;
    }

    protected  String readLine() throws IOException{
        //while(!inputStream.ready());
        return inputStream.readLine() ;
    }


    protected Response readResponse() throws IOException {
        List<String> values = new ArrayList<String>();
        String responseLine = readLine();
        String[] split = responseLine.split(" ");
        if ( split[split.length - 1].startsWith("#")) {
            int count = Integer.parseInt(split[split.length - 1].substring(1));
            if(count>0){
                String sizeLine = readLine();
                String[] tokens = sizeLine.split(" ");
                System.out.println(sizeLine);
                for (int i = 0; i < count; i++) {
                    values.add(new String(readData(Integer.parseInt(tokens[i]))));
                }
                readData(2); //read CRLF
            }
        }

        return new Response(responseLine,values);
    }

    public static class Response {
        String responseLine;
        List<String> data;
        public Response(String responseLine){
            this.responseLine=responseLine;
            this.data = null;
        }
        public Response(String responseLine,List data){
            this.responseLine=responseLine;
            this.data = data;
        }

        public String toString(){
            return responseLine+"\r\n"+data;
        }

    }

}

