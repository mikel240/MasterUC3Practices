package com.imdg.practicas;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.imdg.pojos.Person;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PracticaIMDG2a {
    public static void main(String[] args) throws InterruptedException {
        // hazelcast config
        Config config = new Config();

        config.getNetworkConfig()
                .getJoin()
                .getTcpIpConfig()
                .addMember("localhost")
                .setEnabled(true);

        config.getNetworkConfig()
                .getJoin()
                .getMulticastConfig()
                .setEnabled(false);

        // Instanciar hazelcast
        HazelcastInstance hazelInstance =
                Hazelcast.newHazelcastInstance(config);

        // Countdown
        ICountDownLatch latch = hazelInstance.getCountDownLatch("countDownLatch");
        if (latch.getCount() == 0) {
            latch.trySetCount(3);
        }

        // Crea mapa clave-valor distribuido
        Map<String, Person> hazelMap = hazelInstance.getMap("people");

        // Nuevo objeto Person
        Person persona1 = new Person(
                "Lucas Valdivia",
                28051,
                "St. Camino Rural",
                "St. Camino Rural nº123"
        );

        // Anyade obj a hazelmap
        hazelMap.put("node1", persona1);

        // Sync nodes
        latch.countDown();
        latch.await(20, TimeUnit.SECONDS);

        // Leer datos
        System.out.println(hazelMap.get("node1").toString());
        Person node2Person = hazelMap.get("node2");
        Person node3Person = hazelMap.get("node3");

        if (node2Person != null) {
            System.out.println(hazelMap.get("node2").toString());
        }

        if (node3Person != null) {
            System.out.println(hazelMap.get("node3").toString());
        }
    }
}
