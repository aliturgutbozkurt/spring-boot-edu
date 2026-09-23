package com.springbootedu.hazelcast.exercise1;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

/**
 * Given: reads the same key many times from a near-cached map and from a map without a near cache.
 */
public final class ReadTimer {

    public record Result(long nearCachedNanosPerRead, long remoteNanosPerRead) {

        @Override
        public String toString() {
            return "near cache: %,d ns/read · member: %,d ns/read · %.0fx faster".formatted(
                    nearCachedNanosPerRead, remoteNanosPerRead,
                    (double) remoteNanosPerRead / Math.max(1, nearCachedNanosPerRead));
        }
    }

    private ReadTimer() {
    }

    public static Result compare(HazelcastInstance client, String nearCachedMap, String plainMap, String key, int reads) {
        IMap<String, Object> cached = client.getMap(nearCachedMap);
        IMap<String, Object> plain = client.getMap(plainMap);
        plain.put(key, "value");
        return new Result(nanosPerRead(cached, key, reads), nanosPerRead(plain, key, reads));
    }

    private static long nanosPerRead(IMap<String, Object> map, String key, int reads) {
        map.get(key);                                                   // warm up: fills the near cache, if any
        long start = System.nanoTime();
        for (int i = 0; i < reads; i++) {
            map.get(key);
        }
        return (System.nanoTime() - start) / reads;
    }
}
