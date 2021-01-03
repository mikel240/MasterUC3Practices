package com.cenebrera.uc3.tech.lesso10.marketDataProvider;

import com.cenebrera.uc3.tech.lesso10.acceptor.AcceptorRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;

import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.component.Instrument;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by alexvarela on 20/12/16.
 */
public class MarketDataSubscriberRunner {
    /**
     * a org.slf4j.Logger with the instance of this class given by org.slf4j.LoggerFactory
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(MarketDataSubscriberRunner.class);


    public static void main(String[] args) {
        //Read the info from a xml and populate the class
        URL url = AcceptorRunner.class.getClassLoader().getResource("initiator.cfg");

        // MarketDataProvider is your class that implements the Application interface
        Application application = new MarketDataSubscriber();

        SessionSettings settings = null;
        try {
            settings = new SessionSettings(new FileInputStream(new File(url.toURI())));
        } catch (ConfigError configError) {
            LOGGER.error("Config Error ", configError);
        } catch (FileNotFoundException e) {
            LOGGER.error("File [{}] nto found", url, e);
        } catch (URISyntaxException e) {
            LOGGER.error("Uri bad format  url [{}]", url, e);
        }

        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);
        DefaultMessageFactory messageFactory = new DefaultMessageFactory();

        Initiator initiator = null;
        try {
            initiator = new SocketInitiator
                    (application, storeFactory, settings, logFactory, messageFactory);
            initiator.start();
        } catch (ConfigError configError) {
            configError.printStackTrace();
        }
        LOGGER.debug("Start MarketDataRunnerSubscriber");

        // Create message
        MarketDataRequest marketDataRequest = new MarketDataRequest();
        marketDataRequest.set(new MDReqID("Test1"));
        marketDataRequest.set(new SubscriptionRequestType('1'));
        marketDataRequest.set(new MarketDepth(0));

        MarketDataRequest.NoMDEntryTypes entryTypes = new MarketDataRequest.NoMDEntryTypes();
        entryTypes.set(new MDEntryType('2'));

        Instrument instrument = new Instrument();
        instrument.set(new Symbol("BBVA"));

        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(instrument);

        marketDataRequest.addGroup(noRelatedSym);
        marketDataRequest.addGroup(entryTypes);

        // Wait to send
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Send message
        try {
            Session.sendToTarget(marketDataRequest, "Initiator", "Acceptor");
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }
}
