package jchazelcast;


import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class MapTest extends testAuth {
    String f = "flag";
    boolean a = false;

    @Test
    public void mapPut()  {
        JCMap m = clientPool.get(0).getMap("ahmet");
        Assert.assertNull(m.put(f, a, "key", "dat0"));
        Assert.assertEquals(m.put(f,a,"key","dat1"),"dat0");
    }

    @Test
    public void mapGetNTransientPut() throws InterruptedException {
        JCMap m = clientPool.get(0).getMap("ahmet");
        Assert.assertNull(m.put(f,a,"key","adat"));
        Assert.assertEquals(m.get(f,  "key"),"adat");
        Assert.assertTrue(m.putTransient(f,2000,a,"key2","data2"));
        Assert.assertEquals(m.get(f,"key2"),"data2");
        Thread.sleep(2000);
        Assert.assertNull(m.get(f,"key2"));
    }

    @Test
    public void mapSet(){
        JCMap m = clientPool.get(0).getMap("ahmet");
                mapPut();
        Assert.assertTrue(m.set(f, 0, a, "key", "xyz"));
        Assert.assertEquals(m.get(f,  "key"),"xyz");
    }

    private volatile CountDownLatch cdl;
    private volatile CountDownLatch cdl2;


    @Test
    public void mapLocks() throws InterruptedException {
        JCMap m =  clientPool.get(0).getMap("ahmet");
                mapPut();
        System.out.println(m.lockMap(f, 0));
        cdl = new CountDownLatch(1);
        putFAT("ahmet", "dat1");
        cdl.await();
        Assert.assertTrue(m.unlockMap(f, 0));

    }


    void putFAT(final String n,final String pr){
        (new Thread(new Runnable() {
            @Override
            public void run() {
                JCMap m = clientPool.get(1).getMap(n);
                Assert.assertEquals(m.put(f, a, "key", "ali"),pr);
                cdl.countDown();
            }
        })).start();
    }

    void lockFAT(final String n,final String key) {
        (new Thread(new Runnable() {
            @Override
            public void run()  {
                JCMap m = clientPool.get(1).getMap(n);
                Assert.assertTrue(m.lock(f, 0, key));
                cdl.countDown();
                try {
                    cdl2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                Assert.assertTrue(m.unlock(f, key));
                cdl.countDown();
            }
        })).start();
    }

    @Test
    public void tryPut() throws InterruptedException {
        JCMap m = clientPool.get(0).getMap("ahmet");
                mapPut();
        cdl = new CountDownLatch(1);
        cdl2 = new CountDownLatch(1);
        lockFAT("ahmet","key");
        cdl.await();
        Assert.assertTrue(m.isKeyLocked(f,"key"));
        Assert.assertFalse(m.tryPut(f, 2000, "key", "dat2"));
        cdl = new CountDownLatch(1);
        cdl2.countDown();
        cdl.await();
        Assert.assertFalse(m.isKeyLocked(f, "key"));
        Assert.assertTrue(m.tryPut(f,2000,"key","dat2"));
    }

    @Test
    public void putNUnlock(){
        JCMap m =  clientPool.get(0).getMap("ahmet");
        mapPut();
        Assert.assertTrue(m.lock(f, 0, "key"));
        Assert.assertTrue(m.putAndUnlock(f,a,"key","datx"));
        Assert.assertEquals(m.get(f,"key"),"datx");
    }




}
