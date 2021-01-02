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
        HazelcastInstance hazelInstance = Hazelcast.newHazelcastInstance(config);
        // Crea mapa clave-valor distribuido
        Map<String, Person> hazelMap = hazelInstance.getMap("people");

        // Countdown
        ICountDownLatch latch = hazelInstance.getCountDownLatch("countDownLatch");
        latch.trySetCount(3); // 3 miembros

        // Anyade objeto persona a hazelmap
        hazelMap.put("node1", new Person(
                "Lucas Valdivia",
                28051,
                "St. Camino Rural",
                "St. Camino Rural nº123"
        ));

        // Sync nodes
        latch.countDown(); // miembros - 1
        latch.await(20, TimeUnit.SECONDS);

        // Leer datos
        System.out.println(hazelMap.get("node1"));
        System.out.println(hazelMap.get("node2"));
        System.out.println(hazelMap.get("node3"));

        latch.destroy();
    }
}
