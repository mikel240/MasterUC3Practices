package com.cnebrera.uc3.tech.lesson8.jaxb;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * My Schema Output Resolver
 * --------------------------------------
 *
 * @author Francisco Manuel Benitez Chico
 * --------------------------------------
 */
public class MySchemaOutputResolver extends SchemaOutputResolver {
    /**
     * @param namespaceURI      with the namespace URI
     * @param suggestedFileName with the suggested file name
     * @return an instance of Result
     * @throws IOException with an occurred exception
     */
    @Override
    public Result createOutput(final String namespaceURI, final String suggestedFileName) throws IOException {
        // devuelve fichero con id unico asociado
        System.out.println(suggestedFileName);
        final File file = new File(suggestedFileName);
        final StreamResult result = new StreamResult(file);

        result.setSystemId(file.toURI().toURL().toString());
        return result;
    }
}
