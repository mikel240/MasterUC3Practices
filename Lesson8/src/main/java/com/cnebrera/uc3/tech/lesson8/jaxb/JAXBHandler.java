package com.cnebrera.uc3.tech.lesson8.jaxb;

import com.cnebrera.uc3.tech.lesson8.util.Constants;
import com.cnebrera.uc3.tech.lesson8.xjc.StudentLessons;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * JAXB Handler
 * --------------------------------------
 *
 * @author Francisco Manuel Benitez Chico
 * --------------------------------------
 */
public class JAXBHandler {
    /**
     * Attribute - JAXB Context
     */
    private final JAXBContext jaxbContext;

    /**
     * Initialize the JAXB Context
     *
     * @throws JAXBException with an occurred exception
     */
    public JAXBHandler() throws JAXBException {
        // Intanciamos JAXBContext
        this.jaxbContext = JAXBContext.newInstance(StudentLessons.class);
    }

    /**
     * @param file with the file
     * @return a new instance of StudentLessons with the filled values from the XML
     * @throws JAXBException with an occurred exception
     * @throws SAXException  with an occurred exception
     */
    public StudentLessons convertToObject(final File file) throws JAXBException, SAXException {
        // Creacion de unmarshaller (conversor XML -> JAVA)
        final Unmarshaller jaxbUnmarshaller = this.jaxbContext.createUnmarshaller();

        // Set validator
        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schema = sf.newSchema(new File(Constants.XSD_FILE_INPUT));
        jaxbUnmarshaller.setSchema(schema);

        // Convertir XML a java object
        return (StudentLessons) jaxbUnmarshaller.unmarshal(file);
    }

    /**
     * @param studentLessons with the instance of StudentLessons
     * @return a string with the XML content
     * @throws JAXBException with an occurred exception
     */
    public String convertToXml(final StudentLessons studentLessons) throws JAXBException {
        // instancia de marshaller (java object -> XML)
        final Marshaller jaxbMarshaller = this.jaxbContext.createMarshaller();

        // Salida legible
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        // creacion de objeto outputstream
        final OutputStream outputStream = new ByteArrayOutputStream();

        // conversor
        jaxbMarshaller.marshal(studentLessons, outputStream);

        // output xml
        return outputStream.toString();
    }

    /**
     * @param namespaceUri      with the namespace URI
     * @param suggestedFileName with the suggested file name
     * @return the generated schema
     * @throws IOException with an occurred exception
     */
    public void generateSchema(final String namespaceUri, final String suggestedFileName) throws IOException {
        // Instancia de SchemaOutputResolver
        final SchemaOutputResolver schemaOutputResolver = new MySchemaOutputResolver();

        // Generamos esquema
        this.jaxbContext.generateSchema(schemaOutputResolver);

        // Generar esquema en un fichero
        schemaOutputResolver.createOutput(namespaceUri, suggestedFileName);
    }
}
