package jchazelcast;

import com.hazelcast.core.Hazelcast;
import org.junit.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class testAuth {
    public final static int NUM =  2;
    public static ArrayList<JCHazelcast> clientPool=new ArrayList<JCHazelcast>();

    @BeforeClass
    public static void init()  {
        Hazelcast.getCluster();
    }

    @AfterClass
    public static void cleanup() {
        Hazelcast.shutdownAll();
    }



   @Before
    public void connect() {
        for(int i=0;i<NUM;i++)
            clientPool.add(new JCHazelcast());
    }

    @Test
    public  void latestTest()  {
        Map map = new HashMap();
        map.put("key","data");
        map.put("key2","data2");
        map.put("key3","data3");
        JCMap m = clientPool.get(0).getMap("ahmet");
        System.out.println("result***"+m.putAll("putall", false, map));
        System.out.println("result***" + m.put("put", false, "key3", "data4"));
//        clientPool.get(0).destroy("dest","ahmet","map");
        m=clientPool.get(0).getMap("ahmet");
//        System.out.println(clientPool.get(0).members("mem"));
//        System.out.println(clientPool.get(0).partitions("par"));
 //       System.out.println(clientPool.get(0).txnBegin("beg"));
        System.out.println(m.put("put", false, "key", "myobj"));
        System.out.println(m.putAll("all", false, map));
        System.out.println("not yet");
//        clientPool.get(0).txnCommit("com");


    }

    public static class MyListener extends JCMapListener{
        String n;
        MyListener(String name){
              n=name;
        }

        @Override
        public void entryUpdated(Event e)  {
            System.out.println(n+" listens(updated) "+e.getListenedStructureName()+" key: "+e.getKey()+" value: "+e.getValue());

        }

        @Override
        public void entryAdded(Event e) {
            System.out.println(n+" listens(added) "+e.getListenedStructureName()+" key: "+e.getKey()+" value: "+e.getValue());
        }

        @Override
        public void entryRemoved(Event e)  {
                this.removeMapListener(e);
        }

        @Override
        public void entryEvicted(Event e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    @Test
    public void perf() throws InterruptedException {
        JCMap map = clientPool.get(0).getMap("ahmet");
        map.put("flag",false, "1","istanbul");
        Thread.sleep(2000);
        final AtomicInteger c = new AtomicInteger(0);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int current = c.getAndSet(0);
                System.out.println("* "+current + " ops per second");
            }
        }, 1000, 1000);
        for (; ; ) {
            map.set("flag",0,false, "1","Stanbul");
            c.incrementAndGet();
        }
    }


    @Test
    public void perf2() throws InterruptedException {
        JCAtomicNumber a1 = clientPool.get(0).getAtomicNumber("1");
        a1.addAndGet("x",15);
        Thread.sleep(2000);
        final AtomicInteger c = new AtomicInteger(0);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int current = c.getAndSet(0);
                System.out.println("* "+current + " ops per second");
            }
        }, 1000, 1000);
        for (; ; ) {
            a1.compareAndSet("0", 15, 15);
            c.incrementAndGet();
        }
    }


    @Test
    public void binaryTest() throws UnsupportedEncodingException {
        byte[] b = JCSerial.serialize("ahmetç") ;
        char[] c =   (new String(b)).toCharArray();
        System.out.println(JCSerial.deserialize((new String(c)).getBytes("UTF-16")));
    }


    //seems OK.
    @Test
    public  void atomicTest(){
       JCAtomicNumber a1 = clientPool.get(0).getAtomicNumber("1");
        JCAtomicNumber a2 = clientPool.get(0).getAtomicNumber("2");

        System.out.println(a1.addAndGet("0", 15));
        System.out.println(a1.getAndAdd("1",5));
        System.out.println(a1.compareAndSet("2",20,0));
        System.out.println(a2.getAndAdd("7", 5));
        System.out.println(a2.compareAndSet("3",5,8));
        System.out.println(a2.getAndSet("4", 3));
        System.out.println(a2.addAndGet("5", 10));
    }

    //could not test error in server.
    @Test
   public void multiMapTest(){
        JCMultiMap<Integer,String> mm1= clientPool.get(0).getMultiMap("mm1");

        System.out.println(mm1.put("0",false,18,"ali"));
        System.out.println(mm1.put("1",false,18,"selen"));
        System.out.println(mm1.put("2",false,21,"ali"));
        System.out.println(mm1.valueCount("3",18));
        System.out.println(mm1.removeValue("4",false,18,"selen"));
        System.out.println(mm1.valueCount("5",18));
        System.out.println(mm1.removeKey("6",false,21));
        System.out.println(mm1.valueCount("7",21));
    }

    @Test
    public void idTest(){
        JCIDGenerator gen = clientPool.get(0).getIDGenerator("d");
        System.out.println(gen.newID("3"));
        System.out.println(gen.newID("3"));
        System.out.println(gen.newID("3"));
    }

    //does not work.
    @Test
    public void cdl(){
        JCCountDownLatch cdl = clientPool.get(0).getCountDownLatch("l");
        final JCCountDownLatch cdl2 = clientPool.get(1).getCountDownLatch("l");
        System.out.println("set "+cdl.setCount("3",2));
        System.out.println("get " + cdl.getCount("0"));
        (new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("in "+cdl2.await("fl",5000));
                System.out.println("Now! It is time to ...");
            }
        })).start();
        System.out.println("dec " + cdl.countDown("2"));
        System.out.println("get "+cdl.getCount("0"));
        System.out.println("dec "+cdl.countDown("2"));

    }


    //gives errors.
    @Test
    public void locks(){
        JCLock lk = clientPool.get(0).getLock("2");
        System.out.println(lk.lock("1"));
        System.out.println(lk.isLocked("2"));
        System.out.println(lk.unlock("3"));
        System.out.println(lk.isLocked("4"));
        System.out.println(lk.lock("5"));
        System.out.println(lk.forceUnlock("1"));
    }
//    public static class Listener implements EntryListener{
//        String name;
//        public Listener(String name){
//            this.name = name;
//        }
//        @Override
//        public void onUpdated(Event e) {
//            System.out.println(name+" listens(updated) "+e.name+" key: "+e.key+" value: "+e.newvalue);
//        }
//
//        @Override
//        public void onAdded(Event e) {
//            System.out.println(name+" listens(added) "+e.name+" key: "+e.key+" value: "+e.newvalue);
//        }
//
//        @Override
//        public void onRemoved(Event e) {
//            System.out.println(name+" listens(removed) "+e.name+" key: "+e.key+" value: "+e.newvalue);
//        }
//
//        @Override
//        public void onEvicted(Event e) {
//            System.out.println(name+" listens(evicted) "+e.name+" key: "+e.key+" value: "+e.newvalue);
//        }
//    }
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
