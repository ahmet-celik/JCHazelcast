package jchazelcast;

import java.io.IOException;
import java.util.*;


public class JCHazelcast {
    private  Map<String,JCMap> mapHandlers;
    private  JCConnection connection;


    public JCHazelcast(JCConfig cf) throws IOException, ClassNotFoundException {
        init(cf);
    }

    public JCHazelcast() throws IOException, ClassNotFoundException {
        init(new JCConfig());
    }

    public void init(JCConfig jcConfig) throws IOException, ClassNotFoundException {
            connection = JCConnection.getConnection(jcConfig);
            connection.connect();
            if(!connection.auth("AUTH_HZC",connection.getConfig().getUn(),connection.getConfig().getPw()))
                  throw new IOException("wrong pass or username");
            mapHandlers = new HashMap<String, JCMap>();
    }

    public JCMap getMap(String name) throws IOException {
        JCMap map = mapHandlers.get(name);
        if(map==null){
            map = new JCMap(name,connection);
            mapHandlers.put(name,map);
            return map;
        }else
            return map;

    }


    //    private void  sendOp(String commandLine,Map data) throws IOException {
//        outputStream.write(commandLine.getBytes(CHARSET));
//        outputStream.write(NUMBER_SIGN);
//        outputStream.write(("" + data.size()).getBytes());
//        outputStream.write(END_OF_LINE);
//        StringBuilder sb = new StringBuilder();
//        for(Object key:data.keySet()){
//            sb.append(""+key.length()+" ");
//        }
//        outputStream.write(sb.toString().getBytes());
//        outputStream.write(END_OF_LINE);
//        for(Object key:data.keySet(){
//            outputStream.write(s.getBytes());
//        }
//        outputStream.write(END_OF_LINE);
//        outputStream.flush();
//    }



//    private static final class ByteArrayWrapper {
//        private final char[] data;
//
//        public ByteArrayWrapper(byte[] data) {
//            if (data == null) {
//                throw new NullPointerException();
//            }
//            this.data = data;
//        }
//
//        public boolean equals(Object other) {
//            return !(other instanceof ByteArrayWrapper) ? false : Arrays.equals(data, ((ByteArrayWrapper) other).data);
//        }
//
//        public int hashCode() {
//            return Arrays.hashCode(data);
//        }
//
//        public String toString(){
//            return new String(data);
//        }
//    }

//
//
//    public void destroy(String flag,String name,String type) throws IOException {
//        sendOp("DESTROY " + flag + " "+type+" "+name ) ;
//        System.out.println(readResponse());
//    }
//
//    public void txnBegin(String flag) throws IOException {
//        sendOp("TRXBEGIN " + flag ) ;
//        System.out.println(readResponse());
//    }
//
//    public void txnCommit(String flag) throws IOException {
//        sendOp("TRXCOMMIT " + flag ) ;
//        System.out.println(readResponse());
//    }
//
//    public void txnRollback(String flag) throws IOException {
//        sendOp("TRXROLLBACK " + flag ) ;
//        System.out.println(readResponse());
//    }
//
//    public void instances(String flag) throws IOException {
//        sendOp("INSTANCES " + flag ) ;
//        System.out.println(readResponse());
//    }
//
//    public void members(String flag) throws IOException {
//        sendOp("MEMBERS " + flag ) ;
//        System.out.println(readResponse());
//    }
//
//    public void clusterTime(String flag) throws IOException {
//        sendOp("CLUSTERTIME " + flag ) ;
//        System.out.println(readResponse());
//    }
//
//    public void ping(String flag) throws IOException {
//        sendOp("PING " + flag ) ;
//        System.out.println(readResponse());
//    }
//    public void partitions(String flag,byte[] key) throws IOException {
//        sendOp("PARTITONS " + flag +" ",key) ;
//        System.out.println(readResponse());
//    }
//
//    //MAP COMMANDS
//
//
//    public void setAdd(String flag,String name,boolean noreply,byte[] data) throws IOException {
//        sendOp("SADD " + flag + " " + name +  (noreply ? " noreply " : " "),  data) ;
//        System.out.println(readResponse());
//    }
//
//    public void listAdd(String flag,String name,boolean noreply,byte[] data) throws IOException {
//        sendOp("LADD " + flag + " " + name +  (noreply ? " noreply " : " "),  data) ;
//        System.out.println(readResponse());
//    }
//
//    public void multimapPut(String flag,String name,boolean noreply,byte[] key,byte[] data) throws IOException {
//        sendOp("MMPUT " + flag + " " + name +  (noreply ? " noreply " : " "), key, data) ;
//        System.out.println(readResponse());
//    }
//
//    public void multimapRemove(String flag,String name,boolean noreply,byte[] key,byte[] data) throws IOException {
//        sendOp("MMREMOVE " + flag + " " + name + (noreply ? " noreply " : " "), key, data) ;
//        System.out.println(readResponse());
//    }
//
//    public void multimapRemove(String flag,String name,boolean noreply,byte[] key) throws IOException {
//        sendOp("MMREMOVE " + flag + " " + name + (noreply ? " noreply " : " "), key) ;
//        System.out.println(readResponse());
//    }
//
//    public void multimapValueCount(String flag,String name,byte[] key) throws IOException {
//        sendOp("MMREMOVE " + flag + " " + name + " ", key) ;
//        System.out.println(readResponse());
//    }
//
//    public void topicPublish(String flag,String name,byte[] message) throws IOException{
//        sendOp("TPUBLISH " + flag + " "+name+  " ",message ) ;
//       System.out.println(readResponse());
//    }
//
//    public void atomicAddAndGet(String flag,String name,long delta) throws IOException{
//        sendOp("ADDANDGET " + flag + " "+name+ " "+delta ) ;
//        System.out.println(readResponse());
//    }
//
//    public void atomicGetAndSet(String flag,String name,long new_value) throws IOException{
//        sendOp("GETANDSET " + flag + " "+name+ " "+new_value ) ;
//        System.out.println(readResponse());
//    }
//
//    public void atomicCompareAndSet(String flag,String name,long update,long expect) throws IOException{
//        sendOp("COMPAREANDSET " + flag + " "+name+ " "+update+" "+expect ) ;
//        System.out.println(readResponse());
//    }
//
//    public void atomicGetAndAdd(String flag,String name,long delta) throws IOException{
//        sendOp("GETANDADD " + flag + " "+name+ " "+delta ) ;
//        System.out.println(readResponse());
//    }
//
//    public void generateNewID(String flag,String name) throws IOException {
//        sendOp("NEWID " + flag + " "+name ) ;
//        System.out.println(readResponse());
//    }
//
//    public void cdlAWait(String flag,String name,long time) throws IOException {
//        sendOp("CDLAWAIT " + flag + " "+name+" "+time ) ;
//        System.out.println(readResponse());
//    }
//
//    public void cdlGetCount(String flag,String name) throws IOException {
//        sendOp("CDLGETCOUNT " + flag + " "+name ) ;
//        System.out.println(readResponse());
//    }
//
//    public void cdlSetCount(String flag,String name,long count) throws IOException {
//        sendOp("CDLSETCOUNT " + flag + " "+name+" "+count ) ;
//        System.out.println(readResponse());
//    }
//
//    public void cdlCountDown(String flag,String name) throws IOException {
//        sendOp("CDLCOUNTDOWN " + flag + " "+name ) ;
//        System.out.println(readResponse());
//    }
//
//    public void lockLock(String flag,String name) throws IOException {
//        sendOp("LOCK_LOCK " + flag + " "+name ) ;
//        System.out.println(readResponse());
//    }
//
//    public void lockUnlock(String flag,String name) throws IOException {
//        sendOp("LOCK_UNLOCK " + flag + " "+name ) ;
//        System.out.println(readResponse());
//    }
//
//    public void lockForceUnlock(String flag,String name) throws IOException {
//        sendOp("LOCK_FORCE_UNLOCK " + flag + " "+name ) ;
//        System.out.println(readResponse());
//    }
//
//    public void lockIsLocked(String flag,String name) throws IOException {
//        sendOp("LOCK_IS_LOCKED " + flag + " "+name ) ;
//        System.out.println(readResponse());
//    }
//
//
//












}
