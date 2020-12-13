package com.cnebrera.uc3.tech.lesson9.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * This class represents the reference of the market
 */


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "referenceData", namespace = "http:/tech.uc3.cnebrera.com/lesson9")
public class ReferenceData {

    @XmlAttribute(name = "marketId")
    @JsonProperty(value = "market_id")
    /** a int that identifies the market */
    private int marketId;

    @XmlAttribute(name = "algorithmIdentifier")
    @JsonProperty(value = "algorithm_identifier")
    /** a {@link java.lang.String} with the algorithm identifier */
    private String algorithmIdentifier;

    @XmlElement(name = "instrument", namespace = "http:/tech.uc3.cnebrera.com/lesson9")
    @JsonProperty(value = "list_of_instruments")
    /** a List with the Instruments information **/
    private List<Instrument> listOfInstruments;


    public int getMarketId() {
        return marketId;
    }

    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }

    public String getAlgorithmIdentifier() {
        return algorithmIdentifier;
    }

    public void setAlgorithmIdentifier(String algorithmIdentifier) {
        this.algorithmIdentifier = algorithmIdentifier;
    }

    public List<Instrument> getListOfInstruments() {
        return listOfInstruments;
    }

    public void setListOfInstruments(List<Instrument> listOfInstruments) {
        this.listOfInstruments = listOfInstruments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReferenceData that = (ReferenceData) o;

        if (marketId != that.marketId) {
            return false;
        }
        if (algorithmIdentifier != null ? !algorithmIdentifier.equals(that.algorithmIdentifier) : that.algorithmIdentifier != null) {
            return false;
        }
        return listOfInstruments != null ? listOfInstruments.equals(that.listOfInstruments) : that.listOfInstruments == null;

    }

    @Override
    public int hashCode() {
        int result = marketId;
        result = 31 * result + (algorithmIdentifier != null ? algorithmIdentifier.hashCode() : 0);
        result = 31 * result + (listOfInstruments != null ? listOfInstruments.hashCode() : 0);
        return result;
    }
}
