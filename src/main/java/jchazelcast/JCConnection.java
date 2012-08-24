package jchazelcast;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static jchazelcast.JCProtocol.CHARSET;
import static jchazelcast.JCProtocol.END_OF_LINE;


public class JCConnection {
    private JCSerial serial;
    private String un;
    private String pw;
    private String host ;
    private int port ;
    private Socket socket;
    private BufferedOutputStream outputStream;
    private byte[] buf;
    private int current,max;
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



     void connect()  {
        if(!isConnected()){
            try {
                socket = new Socket();
                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true);
                socket.setSoLinger(true, 0);
                socket.connect(new InetSocketAddress(host, port), timeout);
                socket.setSoTimeout(timeout);
                outputStream = new BufferedOutputStream(socket.getOutputStream());
                inputStream = socket.getInputStream();
                serial = new JCSerial();
                buf = new byte[JCProtocol.BUFFER_SIZE];
            } catch (IOException exception) {
                throw new JCException("ERROR while connecting...",exception);
            }
        }
    }

    void disconnect() {
        if(isConnected()){
            try {
                outputStream.close();
                inputStream.close();
                if (!socket.isClosed())
                    socket.close();
            } catch (IOException exception) {
                throw new JCException("ERROR while disconnecting...",exception);
            }
        }
    }

    //again from xetorthio/jedis project.
    boolean isConnected() {
        return socket != null && socket.isBound() && !socket.isClosed()  && socket.isConnected()
                && !socket.isInputShutdown() && !socket.isOutputShutdown();
    }

    boolean auth(String username,String pass)  {
            sendOp(JCProtocol.ID);
            sendOp("AUTH "  + username + " " + pass);
            return readResponse().responseLine.startsWith("OK");
    }

    void sendOp(String commandLine) {
        try {
            outputStream.write(commandLine.getBytes(CHARSET));
            outputStream.write(END_OF_LINE);
            outputStream.flush();
        } catch (IOException e) {
            throw new JCException("ERROR while sending single line command.",e);
        }

    }

    void sendOp(String commandLine,Object... objects)  {
        int len = objects.length;
        byte[][] data = new byte[len][];
        for(int i=0;i<len;i++){
           data[i]=serial.serialize(objects[i]);
        }
        sendOp(commandLine,data);
    }

    private void sendOp(String commandLine,byte[]... data) {
        try{
            outputStream.write(commandLine.getBytes(CHARSET));
            outputStream.write('#');
            outputStream.write((""+data.length).getBytes());
            outputStream.write(END_OF_LINE);
            StringBuilder sb = new StringBuilder();
            sb.append(data[0].length);
            for(int i=1;i<data.length;i++){
                sb.append(" "+data[i].length);
            }
            outputStream.write(sb.toString().getBytes());
            outputStream.write(END_OF_LINE);
            for(byte[] ba:data){
                outputStream.write(ba);
            }
            outputStream.write(END_OF_LINE);
            outputStream.flush();
        }catch (IOException e){
            throw  new  JCException(e);
        }
    }

    //following three private method is modified version of methods in github project: xetorthio/jedis
    private byte[] readData(int len)  {
        byte[] buffer = new byte[len];
        int remained =len;
        while(remained!=0){
            if (current == max)
                readToBuf();
            if (max == -1)
                return null;

            int length = Math.min(max - current, remained);
            System.arraycopy(buf, current, buffer, 0, length);
            current += length;
            remained-= length;
        }
        return buffer;
    }

    private String readLine() {

        byte first,second;
        StringBuilder line = new StringBuilder();
        for(;;) {
            if (current == max)
                readToBuf();
            if (max == -1)
                break;

            first = buf[current++];
            if (first == '\r') {
                if (current == max)
                    readToBuf();

                if (max == -1) {
                    line.append((char) first);
                    break;
                }
                second = buf[current++];
                if (second == '\n')
                    break;
                line.append((char) first).append((char) second);
            } else
                line.append((char) first);

        }
        return line.toString();
    }


    private void readToBuf()  {

        try {
            max = inputStream.read(buf);
        } catch (IOException e) {
            throw new JCException("ERROR while reading to buffer.",e);
        }
        current = 0;
    }



     JCResponse readResponse()  {
         String responseLine = readLine();
         String[] split = responseLine.split(" ");
         int count=0;
         List<Object> values = null;
         if (  split[split.length - 1].charAt(0)=='#') {
             values = new ArrayList<Object>();
             count = Integer.parseInt(split[split.length - 1].substring(1));
             if(count>0){
                 String[] tokens = readLine().split(" ");
//                System.out.println(sizeLine);
                 for (int i = 0; i < count; i++) {
                     values.add(serial.deserialize(readData(Integer.parseInt(tokens[i]))));
                 }
                 readData(2); //read CRLF
             }
         }
         return new JCResponse(responseLine,values);
     }



}

