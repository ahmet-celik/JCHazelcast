package jchazelcast;


import java.util.*;

/**
 * Creates a client with type of Object.
 * If you want different type of key or object, create new client.
 */
public class JCHazelcast{
    private  List<Map<String,JCStruct>> structHandlers;
    private  JCConnection connection;


    public JCHazelcast(JCConfig cf)  {
        init(cf);
    }

    public JCHazelcast()  {
        init(new JCConfig());
    }

    public void restruct(){
        init(new JCConfig());
    }

    public void restruct(JCConfig cf){
        init(cf);
    }

    /**
     * Destroys only client. Do not touch map,sets,lists... that are created here.
     * You should use destroy command. To use again this client, you should call
     */
    public void destruct()  {
        connection.disconnect();
        structHandlers.clear();
    }

    void init(JCConfig jcConfig)  {
            connection = JCConnection.getConnection(jcConfig);
            connection.connect();
            if(!connection.auth(connection.getConfig().getUn(),connection.getConfig().getPw()))
                  throw new JCException("Wrong password or username");
            int structCount = JCStruct.Type.values().length;
            structHandlers =  new ArrayList<Map<String, JCStruct>>(structCount) ;
            int i=0;
            while(i++<structCount)
               structHandlers.add(new HashMap<String, JCStruct>()) ;

    }

    /**
     * Returns map from cluster with this name.
     * @param name
     * @return JCMap
     */
    public <K,V> JCMap<K,V> getMap(String name) {
        return (JCMap<K,V>) getStruct(JCStruct.Type.MAP,name);
    }

    /**
     * Returns set from cluster with this name.
     * @param name
     * @return JCSet
     */
    public <V> JCSet<V> getSet(String name) {
        return (JCSet<V>) getStruct(JCStruct.Type.SET,name);
    }

    /**
     * Returns list from cluster with this name.
     * @param name
     * @return JCList
     */
    public <V> JCList<V> getList(String name) {
        return (JCList<V>) getStruct(JCStruct.Type.LIST,name);
    }

    /**
     * Returns MultiMap from cluster with this name.
     * @param name
     * @return JCMultiMap
     */
    public <K,V> JCMultiMap<K,V> getMultiMap(String name){
        return (JCMultiMap<K,V>) getStruct(JCStruct.Type.MULTIMAP,name);

    }

    /**
     * Returns AtomicNumber from cluster with this name
     * @param name
     * @return JCAtomicNumber
     */
    public JCAtomicNumber getAtomicNumber(String name){
        return  (JCAtomicNumber) getStruct(JCStruct.Type.ATOMICNUMBER,name);
    }

    /**
     * Returns IDGenerator from cluster with this name
     * @param name
     * @return
     */
    public JCIDGenerator getIDGenerator(String name){
        return (JCIDGenerator) getStruct(JCStruct.Type.IDGENERATOR,name);
    }

    /**
     * Return CountDownLatch from cluster with this name
     * @param name
     * @return
     */
    public JCCountDownLatch getCountDownLatch(String name){
        return (JCCountDownLatch) getStruct(JCStruct.Type.COUNTDOWNLATCH,name);
    }

    /**
     * Return Lock from cluster with this name
     * @param name
     * @return
     */
    public JCLock getLock(String name){
        return (JCLock) getStruct(JCStruct.Type.LOCK,name);
    }

    /**
     * Return Queue from cluster with this name
     * @param name
     * @return
     */
    public JCQueue getQueue(String name){
        return (JCQueue) getStruct(JCStruct.Type.QUEUE,name);
    }

    private JCStruct getStruct(JCStruct.Type type,String name){
           JCStruct struct = structHandlers.get(type.ordinal()).get(name);
           if(struct==null){
               switch(type){
                   case MAP: struct = new JCMap(name,connection); break;
                   case SET: struct = new JCSet(name,connection); break;
                   case LIST: struct = new JCList(name,connection); break;
                   case MULTIMAP: struct = new JCMultiMap(name,connection); break;
                   //case TOPIC: struct = new JCTopic(name,connection); break;
                   case ATOMICNUMBER: struct = new JCAtomicNumber(name,connection); break;
                   case IDGENERATOR: struct = new JCIDGenerator(name,connection); break;
                   case COUNTDOWNLATCH: struct = new JCCountDownLatch(name,connection); break;
                   case LOCK: struct = new JCLock(name,connection); break;
                   case QUEUE: struct = new JCQueue(name,connection); break;
               }
               structHandlers.get(type.ordinal()).put(name,struct);
               return struct;
           }else
               return struct;
    }

    /**
     * Destroys this instance cluster-wide. Clears and releases all resources for this instance.
     * @param name Name of data structure.
     * @param type Type of data structure.
     */
    public boolean destroy(String name,String type) {
         connection.sendOp("DESTROY " + type + " " + name) ;
         String[] r =  connection.readResponse().responseLine.split(" ");
        return r[0].equals("OK") ? removeStruct(JCStruct.Type.valueOf(type), name) : false;
    }

    private boolean removeStruct(JCStruct.Type type,String name){
          return structHandlers.get(type.ordinal()).remove(name)!=null;
    }

    /**
     * Starts the transaction. All subsequent commands will be queued until commit or rollback is sent.
     * @return true if op is OK.
     */
    public boolean txnBegin() {
        connection.sendOp("TRXBEGIN ") ;
        return connection.readResponse().responseLine.equals("OK ") ;
    }

    /**
     * Commits the transaction.
     * @return true if op is OK.
     */
    public boolean txnCommit() {
        connection.sendOp("TRXCOMMIT ") ;

        return connection.readResponse().responseLine.equals("OK ") ;
    }

    /**
     * Rollbacks the transaction.
     * @return  true if op is OK.
     */
    public boolean txnRollback() {
        connection.sendOp("TRXROLLBACK " ) ;

        return connection.readResponse().responseLine.equals("OK ") ;
    }

    /**
     * Returns all queue, map, set, list, topic, lock, multimap instances created by Hazelcast.
     * Instance of is defined with String type and String instance name.
     * @return  Collection of Instance objects.
     */
    public Collection<Instance> instances()  {
        connection.sendOp("INSTANCES ") ;
        String[] r =  connection.readResponse().responseLine.split(" ");
        if(r[0].equals("OK")){
                List<Instance> list = new ArrayList<Instance>();
                for(int i = 1;i<r.length;i++){
                     list.add(new Instance(r[i++],r[i++]));
                }
                return list;
        }
        return null;
    }

    public static class Instance{
        private String type;
        private String name;

        public Instance(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Instance{" +
                    "type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    /**
     * Collection of Strings that are name of current members in the cluster.
     * Every member in the cluster has the same member list in the same order.
     * First member is the oldest member.
     * @return  Collection of names.
     */
    public Collection<String> members()  {
        connection.sendOp("MEMBERS " ) ;
        String[] r = connection.readResponse().responseLine.split(" ");
        if(r[0].equals("OK")){
            Collection<String> c = new ArrayList<String>();
            for(int i = 1;i<r.length;i++){
                c.add(r[i++]);
            }
            return c;
        }
        return null;
    }

    /**
     * Returns the cluster-wide time.
     * Cluster tries to keep a cluster-wide time which is might be different than the member's own
     * system time. Cluster-wide time is -almost- the same on all members of the cluster.
     * @return  Time
     */
    public long clusterTime()  {
        connection.sendOp("CLUSTERTIME ") ;
        String[] r =  connection.readResponse().responseLine.split(" ");
        if(r[0].equals("OK")){
            return Long.valueOf(r[1]);
        }
        return -1;
    }

    /**
     * Pings the cluster. Will return OK.
     * @return
     */
    public boolean ping()  {
        connection.sendOp("PING ") ;
        return connection.readResponse().responseLine.equals("OK ") ;
    }

    /**
     * Return all partitions as a pair of partition id and member address that owns the partition.
     * Partition is class that defines a partition with its ID and address.
     * @return  Collection of Partition objects.
     */
    public Collection<Partition> partitions() {
        connection.sendOp("PARTITIONS " ) ;
        String[] r = connection.readResponse().responseLine.split(" ");
        if(r[0].equals("OK")){
            Collection<Partition> c = new ArrayList<Partition>();
            for(int i = 1;i<r.length;i++){
                c.add(new Partition(Long.valueOf(r[i++]),r[i++]));
            }
            return c;
        }
        return null;
    }

    /**
     * Return only the partition that the key falls
     * @param key
     * @return Partition object.
     */
    public <K> Partition partitionOf(K key){
        connection.sendOp("PARTITIONS " ,key) ;
        String[] r = connection.readResponse().responseLine.split(" ");
        if(r[0].equals("OK"))
            return new Partition(Long.valueOf(r[1]),r[2]);
        else
            return null;

    }

    public static class Partition{
        private long ID;
        private String address;

        public Partition(long ID, String address) {
            this.ID = ID;
            this.address = address;
        }

        public long getID() {
            return ID;
        }

        public String getAddress() {
            return address;
        }

        @Override
        public String toString() {
            return "Partition{" +
                    "ID=" + ID +
                    ", address='" + address + '\'' +
                    '}';
        }
    }
}
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

