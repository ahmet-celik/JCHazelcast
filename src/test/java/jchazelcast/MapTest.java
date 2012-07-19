package jchazelcast;


import junit.framework.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MapTest extends Base {
    String f = "flag";
    boolean a = false;

    @Test
    public void mapPutAll(){
        Map map = new HashMap();
        map.put("key","data");
        map.put("key2","data2");
        map.put("key3","data3");
        JCMap m = clientPool.get(0).getMap("ahmet");
        Assert.assertTrue(m.putAll(f,a, map));
    }

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

    void lockUnlockFAT(final String n,final String key) {
        (new Thread(new Runnable() {
            @Override
            public void run()  {
                JCMap m = clientPool.get(1).getMap(n);
                Assert.assertTrue(m.lock(f, 0, key));
                cdl.countDown();
                try {
                    cdl2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Assert.assertTrue(m.unlock(f, key));
                cdl.countDown();
            }
        })).start();
    }

    @Test
    public void tryPutRemove() throws InterruptedException {
        JCMap m = clientPool.get(0).getMap("ahmet");
                mapPut();
        cdl = new CountDownLatch(1);
        cdl2 = new CountDownLatch(1);
        lockUnlockFAT("ahmet", "key");
        cdl.await();
        Assert.assertTrue(m.isKeyLocked(f,"key"));
        Assert.assertFalse(m.tryPut(f, 2000, "key", "dat2"));
        cdl = new CountDownLatch(1);
        cdl2.countDown();
        cdl.await();
        Assert.assertFalse(m.isKeyLocked(f, "key"));
        Assert.assertTrue(m.tryPut(f,2000,"key","dat2"));
        Assert.assertTrue(m.tryRemove(f,0,"key").equals("dat2"));
    }

    @Test
    public void putNUnlock(){
        JCMap m =  clientPool.get(0).getMap("ahmet");
        mapPut();
        Assert.assertTrue(m.lock(f, 0, "key"));
        Assert.assertTrue(m.putAndUnlock(f,a,"key","datx"));
        Assert.assertEquals(m.get(f,"key"),"datx");
    }

    @Test
    public void tryLockNGet(){
        JCMap m =  clientPool.get(0).getMap("ahmet");
        mapPut();
        Assert.assertTrue(m.tryLockAndGet(f,0,"key").equals("dat1"));
    }

    @Test
    public void getAllKeySet(){
        JCMap m =  clientPool.get(0).getMap("ahmet");
        mapPut();
        m.put(f, a, "key2", "data2");
        Assert.assertEquals(m.remove(f,a,"key2"),"data2");
        Assert.assertTrue(m.getAll(f, m.keySet(f, "map")).contains("dat1"));
    }

    @Test
    public void entry(){
        JCMap m =  clientPool.get(0).getMap("ahmet");
        mapPut();
        JCMapEntry me = m.getEntry(f, "key");
        Assert.assertTrue(me.getKey().equals("key") && me.getValue().equals("dat1"));
    }

    void lockFAT(final String n,final String key) {
        (new Thread(new Runnable() {
            @Override
            public void run()  {
                JCMap m = clientPool.get(1).getMap(n);
                Assert.assertTrue(m.tryLock(f, 0, key));
                cdl.countDown();
            }
        })).start();
    }

    @Test
    public void forceUnlock() throws InterruptedException {
        JCMap m = clientPool.get(0).getMap("ahmet");
        mapPut();
        cdl = new CountDownLatch(1);
        lockFAT("ahmet", "key");
        cdl.await();
        Assert.assertTrue(m.forceUnlock(f, "key"));
        Assert.assertTrue(m.tryPut(f, 2000, "key", "dat2"));
    }

    @Test
    public void contains(){
        JCMap m = clientPool.get(0).getMap("ahmet");
        mapPut();
        Assert.assertNull(m.putIfAbsent(f, a, "keyX", "datX"));
        Assert.assertEquals(m.putIfAbsent(f,a,"key","dat2"),"dat1");
        Assert.assertEquals(m.get(f,"key"),"dat1");
        Assert.assertTrue(m.containsKey(f,"key"));
        Assert.assertTrue(m.containsValue(f,"dat1"));
    }

    @Test
    public void flushNEvict(){
        JCMap m = clientPool.get(0).getMap("ahmet");
        mapPut();
        Assert.assertTrue(m.evict(f,a,"key"));
        Assert.assertTrue(m.flush(f,a));

    }

    @Test
    public void conditionals(){
        JCMap m = clientPool.get(0).getMap("ahmet");
        mapPut();
        Assert.assertNull(m.replaceIfNotNull(f, a, "mis_key", "dat2"));
        Assert.assertEquals(m.replaceIfNotNull(f, a, "key", "dat2"),"dat1");
//        Assert.assertEquals(m.replaceIfSame(f,a,"key","dat1","dat3"),"dat2");
//        Assert.assertEquals(m.replaceIfSame(f,a,"key","dat2","dat3"),"dat2");
//        Assert.assertFalse(m.removeIfSame(f,a,"key","dat1"));
//        Assert.assertTrue(m.removeIfSame(f,a,"key","dat3"));
    }

    @Test
    public void listenerTest() throws InterruptedException {
        mapPutAll();
        JCMap<String,String> m =  clientPool.get(0).getMap("a");
        cdl = new CountDownLatch(1);
        (new Thread(new Runnable() {
            @Override
            public void run() {
                JCMap<String,String> mp =  clientPool.get(1).getMap("a");
                mp.addMapListener(new MyListener("ahmet"),true);
            }
        })).start();
        cdl.await();
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




}
