/*
 * Copyright (c) 2008-2012, Hazel Bilisim Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jchazelcast;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleMapTest {

    public static int THREAD_COUNT = 1;
    public static int ENTRY_COUNT = 10 * 1000;
    public static int VALUE_SIZE = 1000;
    public static final int STATS_SECONDS = 3;
    public static int GET_PERCENTAGE = 0;
    public static int PUT_PERCENTAGE = 100;
    final static Stats stats = new Stats();

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            for (String arg : args) {
                arg = arg.trim();
                if (arg.startsWith("t")) {
                    THREAD_COUNT = Integer.parseInt(arg.substring(1));
                } else if (arg.startsWith("c")) {
                    ENTRY_COUNT = Integer.parseInt(arg.substring(1));
                } else if (arg.startsWith("v")) {
                    VALUE_SIZE = Integer.parseInt(arg.substring(1));
                } else if (arg.startsWith("g")) {
                    GET_PERCENTAGE = Integer.parseInt(arg.substring(1));
                } else if (arg.startsWith("p")) {
                    PUT_PERCENTAGE = Integer.parseInt(arg.substring(1));
                }
            }
        } else {
            System.out.println("Help: sh test.sh t200 v130 p10 g85 ");
            System.out.println("    // means 200 threads, value-size 130 bytes, 10% put, 85% get");
            System.out.println("");
        }
        System.out.println("Starting Test with ");
        System.out.println("      Thread Count: " + THREAD_COUNT);
        System.out.println("       Entry Count: " + ENTRY_COUNT);
        System.out.println("        Value Size: " + VALUE_SIZE);
        System.out.println("    Get Percentage: " + GET_PERCENTAGE);
        System.out.println("    Put Percentage: " + PUT_PERCENTAGE);
        System.out.println(" Remove Percentage: " + (100 - (PUT_PERCENTAGE + GET_PERCENTAGE)));
        ExecutorService es = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            es.submit(new Runnable() {
                private final JCMap map =  (new JCHazelcast()).getMap("default");
                public void run() {
                    while (true) {
                        int key = (int) (Math.random() * ENTRY_COUNT);
                        int operation = ((int) (Math.random() * 100));
                        if (operation < GET_PERCENTAGE) {
                            map.get (String.valueOf(key));
                            stats.gets.incrementAndGet();
                        } else if (operation < GET_PERCENTAGE + PUT_PERCENTAGE) {
                            map.put(false,String.valueOf(key), new byte[VALUE_SIZE]);
                            stats.puts.incrementAndGet();
                        } else {
                            map.remove(false,String.valueOf(key));
                            stats.removes.incrementAndGet();
                        }
                    }
                }
            });
        }
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(STATS_SECONDS * 1000);
                        Stats statsNow = stats.getAndReset();
                        System.out.println(statsNow);
                        System.out.println("Operations per Second : " + statsNow.total() / STATS_SECONDS);
                    } catch (InterruptedException ignored) {
                        return;
                    }
                }
            }
        });
    }

    public static class Stats {
        public AtomicLong gets = new AtomicLong();
        public AtomicLong puts = new AtomicLong();
        public AtomicLong removes = new AtomicLong();

        public Stats getAndReset() {
            long getsNow = gets.getAndSet(0);
            long putsNow = puts.getAndSet(0);
            long removesNow = removes.getAndSet(0);
            Stats newOne = new Stats();
            newOne.gets.set(getsNow);
            newOne.puts.set(putsNow);
            newOne.removes.set(removesNow);
            return newOne;
        }

        public long total() {
            return gets.get() + puts.get() + removes.get();
        }

        public String toString() {
            return "total= " + total() + ", gets:" + gets.get() + ", puts:" + puts.get() + ", removes:" + removes.get();
        }
    }
}