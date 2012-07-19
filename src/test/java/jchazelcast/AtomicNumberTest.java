package jchazelcast;


import junit.framework.Assert;
import org.junit.Test;

public class AtomicNumberTest extends Base {
    String f = "flag";

    //seems OK.
    @Test
    public  void atomicTest(){
        JCAtomicNumber a1 = clientPool.get(0).getAtomicNumber("1");
        JCAtomicNumber a2 = clientPool.get(0).getAtomicNumber("2");
        Assert.assertEquals(a1.addAndGet(f, 15),15);
        Assert.assertEquals(a1.getAndAdd(f,5),15);
        Assert.assertEquals(a1.compareAndSet(f,20,3),true);
        Assert.assertEquals(a2.getAndAdd(f, 5),0);
        Assert.assertEquals(a2.compareAndSet(f,8,8),false);
        Assert.assertEquals(a2.getAndSet(f, 3),5);
        Assert.assertEquals(a2.addAndGet(f, 10),13);
    }
}
