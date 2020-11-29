package com.imdg.listeners;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapListener;
import com.imdg.pojos.MarketOrder;

import java.io.Serializable;

/**
 * Created by Sobremesa on 31/10/2016.
 */
public class VolumeListener
        implements EntryAddedListener<String, MarketOrder>,
        EntryUpdatedListener<String, MarketOrder>, Serializable {

    private String instrumentoAControlar;
    private int volumenAcumulado = 0;

    public VolumeListener(String instrument) {
        this.instrumentoAControlar = instrument;
    }

    /**
     * Escuchar entradas que se añaden y sumarlo al volumen/imprimir alerta si llegamos a 30000
     *
     * @param entryEvent
     */
    @Override
    public void entryAdded(EntryEvent<String, MarketOrder> entryEvent) {
        MarketOrder newMarketOrder = entryEvent.getValue();

        if (!isInstrumentControled(newMarketOrder.getInstrument())) {
            return;
        }

        this.volumenAcumulado += newMarketOrder.getVolume();

        if (this.volumenAcumulado > 30000) {
            System.out.println("Alerta: Instrumento " + this.instrumentoAControlar + "-> Volumen superado");
            this.volumenAcumulado = 0;
        }
    }

    /**
     * Escuchar entradas que se añaden, restar valor antiguo y
     * sumar el nuevo al volumen/imprimir alerta si llegamos a 30000
     *
     * @param entryEvent
     */
    @Override
    public void entryUpdated(EntryEvent<String, MarketOrder> entryEvent) {
        MarketOrder oldMarketOrder = entryEvent.getOldValue();
        MarketOrder newMarketOrder = entryEvent.getValue();

        if (!isInstrumentControled(newMarketOrder.getInstrument())) {
            return;
        }

        this.volumenAcumulado =
                (this.volumenAcumulado - oldMarketOrder.getVolume()) + newMarketOrder.getVolume();

        if (this.volumenAcumulado > 30000) {
            System.out.println("Alerta: Instrumento " + this.instrumentoAControlar + "-> Volumen superado");
            this.volumenAcumulado = 0;
        }
    }

    private boolean isInstrumentControled(String marketOrderInstrument) {
        return marketOrderInstrument.equals(this.instrumentoAControlar);
    }
}
