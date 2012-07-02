package jchazelcast;

import com.hazelcast.core.Hazelcast;
import org.junit.*;

import java.io.IOException;

public class testAuth {

    public static JCHazelcast client;

    @BeforeClass
    public static void init() throws Exception {
        Hazelcast.getCluster();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        Hazelcast.shutdownAll();
    }



    @Before
    public void connect() throws Exception {
        client = new JCHazelcast();
    }

    @Test
    public  void latestTest() throws IOException, InterruptedException {
        Listener ali = new Listener("ali");
        Listener veli = new Listener("veli");
        JCMap map = JCHazelcast.getMap("ahmet");
        //JCMap map2 = JCHazelcast.getMap("mehmet");
        map.put("0",false,"mykey".getBytes(),"data1".getBytes());
        map.addListener(ali,true);
        map.addListener(veli,true);
        map.put("1",false,"yourkey".getBytes(),"rand".getBytes());
        map.put("2",false,"mykey".getBytes(),"data2".getBytes());
        map.removeListener(ali);
        map.remove("3",false,"yourkey".getBytes());



    }

    public static class Listener implements EntryListener{
        String name;
        public Listener(String name){
            this.name = name;
        }
        @Override
        public void onUpdated(Event e) {
            System.out.println(name+" listens(updated) "+e.name+" key: "+e.key+" value: "+e.newvalue);
        }

        @Override
        public void onAdded(Event e) {
            System.out.println(name+" listens(added) "+e.name+" key: "+e.key+" value: "+e.newvalue);
        }

        @Override
        public void onRemoved(Event e) {
            System.out.println(name+" listens(removed) "+e.name+" key: "+e.key+" value: "+e.newvalue);
        }

        @Override
        public void onEvicted(Event e) {
            System.out.println(name+" listens(evicted) "+e.name+" key: "+e.key+" value: "+e.newvalue);
        }
    }
//        client.mapPut("0", "default", false, "2".getBytes(), "mel".getBytes());
//        client.mapSet("1","default",0,false,"2".getBytes(),"sel".getBytes());
//        client.mapPutTransient("1","default", 100,false,"1".getBytes(),"fav".getBytes());
//        client.mapTryPut("2","default",100,"1".getBytes(),"çelik".getBytes("UTF-8"));
//        client.mapPut("2","default",false,"2".getBytes(),"gel".getBytes());
//        client.mapPutAll("3","default",false,"0".getBytes(),"emin".getBytes(),"1".getBytes(),"enes".getBytes(),"2".getBytes(),"mehmet".getBytes(),"3".getBytes(),"ahmetç".getBytes("UTF-8"));
//        client.mapTryLockAndGet("2","default",100,"2".getBytes());
//        client.mapPutAndUnlock("2","default",false,"2".getBytes(),"emin".getBytes());
//        client.mapRemove("4","default",false,"2".getBytes());
//        client.mapGet("4","default","1".getBytes());
//        client.mapGetEntry("5","default","1".getBytes());
//        client.keySet("6","map","default");
//        client.mapLock("7","default",100,"2".getBytes());
//        client.mapUnlock("8","default","2".getBytes());
        //client.mapGetAll("5","default","0".getBytes(),"2".getBytes(),"1".getBytes());
//    }

//    @Test
//    public void   genericTest() throws Exception{
//        client.auth("auth", "dev", "dev-pass");

        // client.multimapPut("0","default",false,"2".getBytes(),"sel".getBytes());
//        client.multimapRemove("1","default",false,"2".getBytes(),"gel".getBytes());
//        client.topicPublish("0","default","Please, close PC!".getBytes());
//        client.atomicAddAndGet("0","nu",43);
//        client.atomicCompareAndSet("1","nu",34,43);
//        client.atomicGetAndAdd("2","nu",16);
//        client.atomicGetAndSet("3","nu",161);
//        client.atomicAddAndGet("3","nu",161);
//        client.cdlAWait("0","cdl",2000000);
//
//        client.cdlSetCount("2","cdl",3000);
//        client.cdlGetCount("1","cdl");
//        client.cdlCountDown("3","cdl");

//        client.lockLock("0","mej");
//        putFromAnotherThread();
//        client.lockIsLocked("1","lck");
//        client.lockLock("2","lck");
//        client.lockIsLocked("3","lck");
//        client.lockUnlock("4","lck");
        //client.multimapValueCount("2","default","2".getBytes());
        //client.mapLock("1","default",2000,"2".getBytes());
        //client.mapGet("2","default","3".getBytes());
        //client.mapPutIfAbsent("3","default",100,false,"3".getBytes(),"dede".getBytes());
        // client.mapRemoveIfSame("3","default",false,"3".getBytes(),"mel".getBytes());
        //client.mapContainsValue("4","default","mel".getBytes());
        // client.mapIsKeyLocked("check","default","2".getBytes());
//        client.mapPut("1","put",false,"2".getBytes(), "donk".getBytes());
//        client.mapPut("5","de",false,"2".getBytes(),"funk".getBytes());
//
//        client.mapAddListener("put");
//        client.mapAddListener("de");
//        client.mapPut("2","put",false,"2".getBytes(),"dank".getBytes());//, "hulk".getBytes());
//        client.mapPut("6","de",false,"2".getBytes(),"sunk".getBytes());
//        client.mapRemoveListener("put");
//        client.mapPut("9","put",false,"2".getBytes(),"chunk".getBytes());//, "hulk".getBytes());

        //System.out.println(client.readResponse());
//    }

//    private void putFromAnotherThread() throws Exception {
//        Thread t = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    JCHazelcast client = new JCHazelcast("localhost");
//                    client.connect();
//                    client.auth("auth","dev","dev-pass");
//                    client.lockForceUnlock("thread", "lock");
//                    client.disconnect();
//                } catch (IOException e) {
//                }
//            }
//        });
//        t.start();
//        long start = System.currentTimeMillis();
//        t.join(2000);
//        long interval = System.currentTimeMillis() - start;
//        System.out.println(interval + ":: " + 2000);
//        if(2000 != 0 && interval > 2000-100){
//            throw new RuntimeException("Timeouted");
//        }
//    }
}
