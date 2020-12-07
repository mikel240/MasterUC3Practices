package com.cnebrera.uc3.tech.lesson8;

import com.cnebrera.uc3.tech.lesson8.util.Constants;
import com.cnebrera.uc3.tech.lesson8.velocity.VelocityHandler;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * Launcher class - XSD Generator from JAXB Class
 * --------------------------------------
 *
 * @author Francisco Manuel Benitez Chico
 * --------------------------------------
 */
public class VelocityGeneratorLauncher extends AbstractXMLLauncher {
    /**
     * @throws IOException   with an occurred exception
     * @throws JAXBException with an occurred exception
     */
    protected void generateClassfromVelocity() throws IOException, JAXBException {
        // Instancia VelocityHandler
        final VelocityHandler velocityHandler = new VelocityHandler();

        // Config generaciona auto. de codigo
        velocityHandler.generateClassFromVelocityTemplate(Constants.VELOCITY_CLASS_GENERATED_PACKAGE,
                Constants.VELOCITY_CLASS_GENERATED_CLASSNAME);
    }

    /**
     * @param args with the input arguments
     * @throws IOException   with an occurred exception
     * @throws JAXBException with an occurred exception
     */
    public static void main(final String[] args) throws IOException, JAXBException {
        final VelocityGeneratorLauncher velocityGeneratorLauncher = new VelocityGeneratorLauncher();

        velocityGeneratorLauncher.generateClassfromVelocity();
    }
}
