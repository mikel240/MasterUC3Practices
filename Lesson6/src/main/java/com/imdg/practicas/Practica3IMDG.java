package com.imdg.practicas;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.util.ArrayList;
import java.util.Map;

public class Practica3IMDG {

    public static void main(String[] args) {
        ClientConfig config = new ClientConfig();
        ArrayList<String> ips = new ArrayList();
        ips.add("127.0.0.1");
        config.getNetworkConfig().setAddresses(ips);

        // Instanciar hazelcast cliente y crear una cache
        HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
        Map<String, String> hazelMap = client.getMap("practice6");

        // Lectura
        System.out.println("Valor antes de guardarlo en caché: " + hazelMap.get("data"));
        hazelMap.put("data", "Lorem ipsum");
        System.out.println("Valor después de guardarlo en caché: " + hazelMap.get("data"));

        client.shutdown();
    }
}
