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



    public void mapPutAll(String name){
        Map map = new HashMap();
        map.put("key","data");
        map.put("key2","data2");
        map.put("key3","data3");
        JCMap m = clientPool.get(0).getMap(name);
        System.out.println(name+"*** "+m.putAll("putall", false, map));
    }


    @Test
    public void listenerTest() throws InterruptedException {
       mapPutAll("a");
        JCMap<String,String> m =  clientPool.get(0).getMap("a");
        (new Thread(new Runnable() {
            @Override
            public void run() {
                 JCMap<String,String> mp =  clientPool.get(1).getMap("a");
                  mp.addMapListener(new MyListener("ahmet"),true);
            }
        })).start();
        Thread.sleep(2000);
        m.put("0",false,"key","data0");
        m.put("0",false,"key4","dataX");
        m.remove("0",false,"key");
        m.evict("0",false,"key2");


    }

    public static class MyListener extends JCMapListener{
        String n;
        MyListener(String name){
              n=name;
        }

        @Override
        public void entryUpdated(EntryEvent e)  {
            System.out.println(n+" listens(updated) "+e.getListenedStructureName()+" key: "+e.getKey()+" value: "+e.getValue());

        }

        @Override
        public void entryAdded(EntryEvent e) {
            System.out.println(n+" listens(added) "+e.getListenedStructureName()+" key: "+e.getKey()+" value: "+e.getValue());
            this.removeMapListener(e);

        }

        @Override
        public void entryRemoved(EntryEvent e)  {
            System.out.println(n+" listens(removed) "+e.getListenedStructureName()+" key: "+e.getKey()+" value: "+e.getValue());

        }

        @Override
        public void entryEvicted(EntryEvent e) {
            System.out.println(n+" listens(evict) "+e.getListenedStructureName()+" key: "+e.getKey()+" value: "+e.getValue());

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
        System.out.println(a1.compareAndSet("2",20,3));
        System.out.println(a2.getAndAdd("7", 5));
        System.out.println(a2.compareAndSet("3",5,8));
        System.out.println(a2.getAndSet("4", 3));
        System.out.println(a2.addAndGet("5", 10));
    }


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
//        (new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("in "+cdl2.await("fl",5000));
//                System.out.println("Now! It is time to ...");
//            }
//        })).start();
//        System.out.println("dec " + cdl.countDown("2"));
//        System.out.println("get "+cdl.getCount("0"));
//        System.out.println("dec "+cdl.countDown("2"));

    }

    @Test
    public void ladd(){
        JCList<Integer> l = clientPool.get(0).getList("l");
        System.out.println(l.add("d",false,4));
    }

    @Test
    public void sadd(){
        JCSet<Integer> s = clientPool.get(0).getSet("2");
        s.add("s",false,45);
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

    @Test
    public void queue() throws InterruptedException {
        JCQueue q = clientPool.get(0).getQueue("qu");
        System.out.println(q.offer("1",false,0,34));  //ok
        System.out.println(q.offer("1",false,0,38));  //ok

        // System.out.println(q.peek("2"));              //ok
       // System.out.println(q.poll("3", 0));
        System.out.println(q.offer("1",false,0,42));  //ok

        System.out.println(q.size("4"));
        System.out.println(q.remove("4.5", false, 38));
        System.out.println(q.remainingCapacity("5"));
        System.out.println(q.entries("6"));
    }

    public static class MyItm extends ItemListener{

        @Override
        public void itemAdded(ItemEvent e) {
            System.out.println("Added to "+e.getListenedStructureName()+" item: "+e.getItem());
        }

        @Override
        public void itemRemoved(ItemEvent e) {
            System.out.println("Removed from "+e.getListenedStructureName()+" item: "+e.getItem());
            removeListener(e);
        }
    }
}
