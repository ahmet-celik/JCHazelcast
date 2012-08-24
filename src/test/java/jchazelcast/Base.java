package jchazelcast;

import com.hazelcast.core.Hazelcast;
import org.junit.*;


import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Base {
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
    public void perf() throws InterruptedException {
        JCMap map = clientPool.get(0).getMap("ahmet");
        map.put(false, "1","istanbul");
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
            map.set(0,false, "1","Stanbul");
            c.incrementAndGet();
        }
    }


    @Test
    public void perf2() throws InterruptedException {
        JCAtomicNumber a1 = clientPool.get(0).getAtomicNumber("1");
        a1.addAndGet(15);
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
            a1.compareAndSet( 15, 15);
            c.incrementAndGet();
        }
    }


    @Test
    public void binaryTest() throws UnsupportedEncodingException {
        JCSerial s = new JCSerial();
        byte[] b = s.serialize("ahmetç") ;
        char[] c =   (new String(b)).toCharArray();
        System.out.println(s.deserialize((new String(c)).getBytes("UTF-16")));
    }





    @Test
   public void multiMapTest(){
        JCMultiMap<Integer,String> mm1= clientPool.get(0).getMultiMap("mm1");

        System.out.println(mm1.put(false,18,"ali"));
        System.out.println(mm1.put(false,18,"selen"));
        System.out.println(mm1.put(false,21,"ali"));
//        System.out.println(mm1.valueCount(18));
//        System.out.println(mm1.removeValue(false,18,"selen"));
//        System.out.println(mm1.valueCount(18));
//        System.out.println(mm1.removeKey(false,21));
//        System.out.println(mm1.valueCount(21));
    }

    @Test
    public void idTest(){
        JCIDGenerator gen = clientPool.get(0).getIDGenerator("d");
        System.out.println(gen.newID());
        System.out.println(gen.newID());
        System.out.println(gen.newID());
    }

    //does not work.
    @Test
    public void cdl(){
        JCCountDownLatch cdl = clientPool.get(0).getCountDownLatch("l");
        final JCCountDownLatch cdl2 = clientPool.get(1).getCountDownLatch("l");
        System.out.println("set "+cdl.setCount(2));
        System.out.println("get " + cdl.getCount());
        (new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("in "+cdl2.await(5000));
                System.out.println("Now! It is time to ...");
            }
        })).start();
        System.out.println("dec " + cdl.countDown());
        System.out.println("get "+cdl.getCount());
        System.out.println("dec "+cdl.countDown());

    }

    @Test
    public void ladd(){
        JCList<Integer> l = clientPool.get(0).getList("l");
        System.out.println(l.add(false,4));
    }

    @Test
    public void sadd(){
        JCSet<Integer> s = clientPool.get(0).getSet("2");
        System.out.println(s.add(false, 45));
    }


    @Test
    public void itemListenTest(){

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
