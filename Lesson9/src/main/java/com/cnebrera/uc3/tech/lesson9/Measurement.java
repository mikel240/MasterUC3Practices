package com.cnebrera.uc3.tech.lesson9;

import com.cnebrera.uc3.tech.lesson9.jaxb.JaxbSerializer;
import com.cnebrera.uc3.tech.lesson9.json.JsonSerializer;
import com.cnebrera.uc3.tech.lesson9.kryo.KryoSerializer;
import com.cnebrera.uc3.tech.lesson9.model.ReferenceData;
import com.cnebrera.uc3.tech.lesson9.proto.Lesson9;
import com.cnebrera.uc3.tech.lesson9.proto.ProtoSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Main Class that measure the performance
 */
public class Measurement {
    private static long NUM_ITERATIONS = 5;
    /**
     * a org.slf4j.Logger with the instance of this class given by org.slf4j.LoggerFactory
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(Measurement.class);

    private final static JaxbSerializer jaxbSerializer = new JaxbSerializer();
    private final static JsonSerializer jsonSerializer = new JsonSerializer();
    private final static KryoSerializer kryoSerializer = new KryoSerializer();
    private final static ProtoSerializer protoSerializer = new ProtoSerializer();

    public static void main(String[] args) throws URISyntaxException, IOException {


        //Read the info from a xml and populate the class
        URL url = Measurement.class.getClassLoader().getResource("Example.xml");
        URL urlJson = Measurement.class.getClassLoader().getResource("Example.json");

        String str = new String(Files.readAllBytes(Paths.get(url.toURI())));
        String json = new String(Files.readAllBytes(Paths.get(urlJson.toURI())));

        ReferenceData referenceData = jaxbSerializer.deserialize(str);

        LOGGER.debug("[Practica 1] Size of referenceData instrument list {}", referenceData.getListOfInstruments().size());
        LOGGER.debug("[Practica 1] Algorithm identifier{}", referenceData.getAlgorithmIdentifier());
        LOGGER.debug("[Practica 1] Algorithm marketId{}", referenceData.getMarketId());

        LOGGER.debug("[Practica 2] Json Serializer [{}] ", referenceData.equals(jsonSerializer.deserialize(json)));

        // Set the parameters in the builder using the values read in referenceData from JSON to ensure both have the same contents
        Lesson9.ReferenceData.Builder referenceDataBuilder = Lesson9.ReferenceData.newBuilder();
        Lesson9.Instrument.Builder instrumentBuilder = Lesson9.Instrument.newBuilder();

        referenceData.getListOfInstruments().forEach(instrument -> {
            instrumentBuilder.setInstrumentId(instrument.getInstrumentId());
            instrumentBuilder.setSymbol(instrument.getSymbol());
            referenceDataBuilder.addInstrument(instrumentBuilder);

        });

        //Test Proto
        Lesson9.ReferenceData referenceDataProto = referenceDataBuilder.build();
        LOGGER.debug("[Practica 3] Proto Serializer [{}] ", referenceDataProto.equals(protoSerializer.deserialize(protoSerializer.serialize(referenceDataProto))));

        //Test Kryo
        LOGGER.debug("[Practica 4] Kryo Serializer [{}] ", referenceData.equals(kryoSerializer.deserialize(kryoSerializer.serialize(referenceData))));

        //Warmup
        testPerformanceSerialization(referenceData, referenceDataProto, false);
        testPerformanceDeSerialization(str, jsonSerializer.serialize(referenceData), kryoSerializer.serialize(referenceData), referenceDataProto.toByteArray(), false);
        testPerformanceSerializationAndDeserialization(referenceData, referenceDataProto, false);

        //Tests
        testPerformanceSerialization(referenceData, referenceDataProto, true);
        testPerformanceDeSerialization(str, jsonSerializer.serialize(referenceData), kryoSerializer.serialize(referenceData), referenceDataProto.toByteArray(), true);
        testPerformanceSerializationAndDeserialization(referenceData, referenceDataProto, true);
    }


    private static void testPerformanceSerialization(ReferenceData referenceData,
                                                     Lesson9.ReferenceData referenceDataProto,
                                                     boolean printMeasurements) {
        //JAXB serialization
        String jaxbSerialize = "";
        long jaxbSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            jaxbSerialize = jaxbSerializer.serialize(referenceData);
        }
        long jaxbSerializationFin = System.nanoTime();
        long meanJaxb = (jaxbSerializationFin - jaxbSerializationIni) / NUM_ITERATIONS;

        //Json serialization
        String jsonSerialize = "";
        long jsonSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            jsonSerialize = jsonSerializer.serialize(referenceData);
        }
        long jsonSerializationFin = System.nanoTime();
        long meanJson = (jsonSerializationFin - jsonSerializationIni) / NUM_ITERATIONS;


        //Protocol Buffers serialization
        byte[] protoSerialize = new byte[0];
        long protoSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            protoSerialize = protoSerializer.serialize(referenceDataProto);
        }
        long protoSerializationFin = System.nanoTime();
        long meanProto = (protoSerializationFin - protoSerializationIni) / NUM_ITERATIONS;

        //Kryo serialization
        byte[] kryoSerialize = new byte[0];
        long kryoSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            kryoSerialize = kryoSerializer.serialize(referenceData);
        }
        long kryoSerializationFin = System.nanoTime();
        long meanKryo = (kryoSerializationFin - kryoSerializationIni) / NUM_ITERATIONS;

        // Output
        // Output
        if (!printMeasurements) {
            return;
        }

        System.out.println("SERIALIZATION SIZE");
        System.out.println("Jaxb " + jaxbSerialize.getBytes().length);
        System.out.println("jackson " + jsonSerialize.getBytes().length);
        System.out.println("proto " + protoSerialize.length);
        System.out.println("Kryo " + kryoSerialize.length);

        System.out.println("TIMES");
        System.out.println("###########Serialize###########");
        System.out.println("Jaxb " + meanJaxb);
        System.out.println("jackson " + meanJson);
        System.out.println("proto " + meanProto);
        System.out.println("Kryo " + meanKryo);
    }

    private static void testPerformanceDeSerialization(String jaxbSerialize,
                                                       String jsonSerlize,
                                                       byte[] kryoSerialize,
                                                       byte[] protoSerialize,
                                                       boolean printMeasurements) {
        //JAXB serialization
        long jaxbSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            jaxbSerializer.deserialize(jaxbSerialize);
        }
        long jaxbSerializationFin = System.nanoTime();
        long meanJaxb = (jaxbSerializationFin - jaxbSerializationIni) / NUM_ITERATIONS;

        //Json serialization
        long jsonSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            jsonSerializer.deserialize(jsonSerlize);
        }
        long jsonSerializationFin = System.nanoTime();
        long meanJson = (jsonSerializationFin - jsonSerializationIni) / NUM_ITERATIONS;

        //Protocol Buffers serialization
        long protoSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            protoSerializer.deserialize(protoSerialize);
        }
        long protoSerializationFin = System.nanoTime();
        long meanProto = (protoSerializationFin - protoSerializationIni) / NUM_ITERATIONS;

        //Kryo serialization
        long kryoSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            kryoSerializer.deserialize(kryoSerialize);
        }
        long kryoSerializationFin = System.nanoTime();
        long meanKryo = (kryoSerializationFin - kryoSerializationIni) / NUM_ITERATIONS;

        // Output
        if (!printMeasurements) {
            return;
        }
        System.out.println("###########Deserialize###########");
        System.out.println("Jaxb " + meanJaxb);
        System.out.println("jackson " + meanJson);
        System.out.println("proto " + meanProto);
        System.out.println("Kryo " + meanKryo);
    }

    private static void testPerformanceSerializationAndDeserialization(ReferenceData referenceData,
                                                                       Lesson9.ReferenceData referenceDataProto,
                                                                       boolean printMeasurements) {
        //JAXB serialization
        long jaxbSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            String jaxbSerialize = jaxbSerializer.serialize(referenceData);
            jaxbSerializer.deserialize(jaxbSerialize);
        }
        long jaxbSerializationFin = System.nanoTime();
        long meanJaxb = (jaxbSerializationFin - jaxbSerializationIni) / NUM_ITERATIONS;

        //Json serialization
        long jsonSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            String jsonSerialize = jsonSerializer.serialize(referenceData);
            jsonSerializer.deserialize(jsonSerialize);
        }
        long jsonSerializationFin = System.nanoTime();
        long meanJson = (jsonSerializationFin - jsonSerializationIni) / NUM_ITERATIONS;

        //Protocol Buffers serialization
        long protoSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            byte[] protoSerialize = protoSerializer.serialize(referenceDataProto);
            protoSerializer.deserialize(protoSerialize);
        }
        long protoSerializationFin = System.nanoTime();
        long meanProto = (protoSerializationFin - protoSerializationIni) / NUM_ITERATIONS;

        //Kryo serialization
        long kryoSerializationIni = System.nanoTime();
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            byte[] kryoSerialize = kryoSerializer.serialize(referenceData);
            kryoSerializer.deserialize(kryoSerialize);
        }
        long kryoSerializationFin = System.nanoTime();
        long meanKryo = (kryoSerializationFin - kryoSerializationIni) / NUM_ITERATIONS;

        // Output
        if (!printMeasurements) {
            return;
        }
        System.out.println("###########Serialize/Deserialize###########");
        System.out.println("Jaxb " + meanJaxb);
        System.out.println("jackson " + meanJson);
        System.out.println("proto " + meanProto);
        System.out.println("Kryo " + meanKryo);
    }
}

