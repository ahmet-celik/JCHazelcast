package jchazelcast;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;


public class QueueTest extends Base {
    boolean a = false;

    @Test
    public void queue()  {
        JCQueue q = clientPool.get(0).getQueue("qu");
        Assert.assertTrue(q.offer( a, 0, 34));  //ok
        Assert.assertTrue(q.offer( a, 0, 38));  //ok

        Assert.assertEquals(q.peek(),34);              //ok
//        System.out.println(q.poll( 0));
        Assert.assertTrue(q.offer(a,0,42));  //ok

        Assert.assertEquals(q.size(),3);
        Assert.assertTrue(q.remove(a, 38));
        Assert.assertEquals(q.remainingCapacity(),Integer.MAX_VALUE-2);
        Assert.assertTrue(q.entries().containsAll(Arrays.asList(42, 34)));
        Assert.assertFalse(q.entries().containsAll(Arrays.asList(42, 34,38)));

    }
}
