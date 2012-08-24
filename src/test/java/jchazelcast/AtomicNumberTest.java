package jchazelcast;


import junit.framework.Assert;
import org.junit.Test;

public class AtomicNumberTest extends Base {

    //seems OK.
    @Test
    public  void atomicTest(){
        JCAtomicNumber a1 = clientPool.get(0).getAtomicNumber("1");
        JCAtomicNumber a2 = clientPool.get(0).getAtomicNumber("2");
        Assert.assertEquals(a1.addAndGet( 15),15);
        Assert.assertEquals(a1.getAndAdd(5),15);
        Assert.assertEquals(a1.compareAndSet(20,3),true);
        Assert.assertEquals(a2.getAndAdd( 5),0);
        Assert.assertEquals(a2.compareAndSet(8,8),false);
        Assert.assertEquals(a2.getAndSet( 3),5);
        Assert.assertEquals(a2.addAndGet( 10),13);
    }
}
