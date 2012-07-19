package jchazelcast;

import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;


public class LockTest extends Base{
    String f = "flag";

    //gives errors.
    @Test
    public void locks() throws InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(1);
        final JCLock lk = clientPool.get(0).getLock("lk");
        Assert.assertTrue(lk.lock(f));
        Assert.assertTrue(lk.isLocked(f));
        Assert.assertTrue(lk.unlock(f));
        Assert.assertFalse(lk.isLocked(f));
        (new Thread(new Runnable() {
            @Override
            public void run() {
                Assert.assertTrue(lk.lock(f));
                cdl.countDown();
            }
        })).start();
        cdl.await();
        System.out.println(lk.forceUnlock(f));
    }
}
