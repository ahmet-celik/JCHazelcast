package jchazelcast;

import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;


public class LockTest extends Base{


    //gives errors.
    @Test
    public void locks() throws InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(1);
        final JCLock lk = clientPool.get(0).getLock("lk");
        Assert.assertTrue(lk.lock());
        Assert.assertTrue(lk.isLocked());
        Assert.assertTrue(lk.unlock());
        Assert.assertFalse(lk.isLocked());
        (new Thread(new Runnable() {
            @Override
            public void run() {
                Assert.assertTrue(lk.lock());
                cdl.countDown();
            }
        })).start();
        cdl.await();
        System.out.println(lk.forceUnlock());
    }
}
