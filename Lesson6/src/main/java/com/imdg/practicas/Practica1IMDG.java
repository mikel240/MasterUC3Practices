package com.imdg.practicas;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.imdg.pojos.Person;

import java.util.Map;
import java.util.Queue;

public class Practica1IMDG {

    public static void main(String[] args) {
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

        // Crea mapa clave-valor distribuido
        Map<String, IMap<String,String>> hazelMap = hazelInstance.getMap("practice6");
    }
}
