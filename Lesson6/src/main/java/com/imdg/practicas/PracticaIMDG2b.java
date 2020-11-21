package com.imdg.practicas;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.imdg.pojos.Person;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PracticaIMDG2b {
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
        Person persona2 = new Person(
                "Fernando Fernandez",
                28052,
                "St. Goya",
                "St. Goya nº321"
        );

        // Anyade obj a hazelmap
        hazelMap.put("node2", persona2);

        // Sync nodes
        latch.countDown();
        latch.await(20, TimeUnit.SECONDS);

        // Leer datos
        System.out.println(hazelMap.get("node2").toString());
        Person node1Person = hazelMap.get("node1");
        Person node3Person = hazelMap.get("node3");

        if (node1Person != null){
            System.out.println(hazelMap.get("node1").toString());
        }

        if (node3Person != null){
            System.out.println(hazelMap.get("node3").toString());
        }
    }
}
