package jchazelcast;

import java.io.*;
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
    private String un;
    private String pw;
    private String host ;
    private int port ;
    private Socket socket;
    private OutputStream outputStream;
    private  byte buf[];
    private int count, limit;
    private InputStream inputStream;
    private int timeout = JCProtocol.DEFAULT_TIMEOUT;

    public void setConfig (JCConfig cf){
            host = cf.getHost();
            port = cf.getPort();
            pw = cf.getPw();
            un= cf.getUn();
    }

    public JCConfig getConfig(){
        return new JCConfig(un,pw,host,port);
    }

    static JCConnection  getConnection(JCConfig cf){
        JCConnection jcConnection = new JCConnection();
        jcConnection.setConfig(cf);
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
                inputStream = socket.getInputStream();
                buf = new byte[JCProtocol.BUFFER_SIZE];
            } catch (IOException exception) {
                throw new IOException(exception);
            }
        }
    }

    void disconnect() throws IOException{
        if(isConnected()){
            try {
                outputStream.close();
                inputStream.close();
                if (!socket.isClosed())
                    socket.close();
            } catch (IOException exception) {
                throw new IOException(exception);
            }
        }
    }

    boolean isConnected() {
        return socket != null && socket.isBound() && !socket.isClosed()  && socket.isConnected()
                && !socket.isInputShutdown() && !socket.isOutputShutdown();
    }

    public boolean auth(String flag,String username,String pass) throws IOException, ClassNotFoundException {
        sendOp(JCProtocol.ID);
        sendOp("AUTH " + flag + " " + username + " " + pass);
        return readResponse().responseLine.equals("OK "+flag);
    }

    void sendOp(String commandLine) throws IOException{
        outputStream.write(commandLine.getBytes(CHARSET));
        outputStream.write(END_OF_LINE);
        outputStream.flush();
    }

    boolean ready() throws IOException {
        return inputStream.available()>0;
    }
    void sendOp(String commandLine,Object... objects) throws IOException {
        int len = objects.length;
        byte[][] data = new byte[len][];
        for(int i=0;i<len;i++){
           data[i]=JCSerial.serialize(objects[i]);
        }
        sendOp(commandLine,data);
    }

    void sendOp(String commandLine,byte[]... data) throws IOException{
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

    byte[] readData(int len) throws IOException {
        byte[] b = new byte[len];
        int remained =len;
        while(remained!=0){
            if (count == limit) {
                fill();
                if (limit == -1)
                    return null;
            }
            int length = Math.min(limit - count, len);
            System.arraycopy(buf, count, b, 0, length);
            count += length;
            remained-= length;
        }
        return b;
    }

     String readLine() throws IOException{
         int b;
         byte c;
         StringBuilder sb = new StringBuilder();
         while (true) {
             if (count == limit) {
                 fill();
             }
             if (limit == -1)
                 break;

             b = buf[count++];
             if (b == '\r') {
                 if (count == limit) {
                     fill();
                 }

                 if (limit == -1) {
                     sb.append((char) b);
                     break;
                 }
                 c = buf[count++];
                 if (c == '\n') {
                     break;
                 }
                 sb.append((char) b);
                 sb.append((char) c);
             } else {
                 sb.append((char) b);
             }
         }
         return sb.toString();
    }


    private void fill() throws IOException {
        limit = inputStream.read(buf);
        count = 0;
    }

    Response readResponse() throws IOException, ClassNotFoundException {
        List<Object> values = new ArrayList<Object>();
        String responseLine = readLine();
        String[] split = responseLine.split(" ");
        if ( split[split.length - 1].startsWith("#")) {
            int count = Integer.parseInt(split[split.length - 1].substring(1));
            if(count>0){
                String sizeLine = readLine();
                String[] tokens = sizeLine.split(" ");
                System.out.println(sizeLine);
                for (int i = 0; i < count; i++) {
                    values.add(JCSerial.deserialize(readData(Integer.parseInt(tokens[i]))));
                }
                readData(2); //read CRLF
            }
        }

        return new Response(responseLine,values);
    }

    static class Response {
        String responseLine;
        List<Object> data;
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

