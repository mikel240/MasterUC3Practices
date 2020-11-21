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
        // Instanciar hazelcast Cliente y crear una cache
        ClientConfig config = new ClientConfig();
        ArrayList<String> ips = new ArrayList();
        ips.add("127.0.0.1");
        config.getNetworkConfig().setAddresses(ips);

        HazelcastInstance client = HazelcastClient.newHazelcastClient(config);

        // Crea mapa clave-valor distribuido
        Map<String, String> hazelMap = client.getMap("practice6");

        System.out.println(hazelMap.get("data"));
        hazelMap.put("data", "Dato guardado en caché");
        System.out.println(hazelMap.get("data"));

        client.shutdown();
    }
}
