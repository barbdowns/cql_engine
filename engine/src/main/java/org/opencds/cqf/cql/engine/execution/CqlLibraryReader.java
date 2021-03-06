package org.opencds.cqf.cql.engine.execution;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.cqframework.cql.elm.execution.Library;
import org.opencds.cqf.cql.engine.elm.execution.ObjectFactoryEx;
import org.opencds.cqf.cql.engine.exception.CqlException;

public class CqlLibraryReader {

    private static JAXBContext context;
    private static Unmarshaller unmarshaller;

    // Performance enhancement additions ~ start
    public synchronized static Unmarshaller getUnmarshaller() throws JAXBException {
        if (context == null)
        {
            context = JAXBContext.newInstance(ObjectFactoryEx.class);
        }

        if (unmarshaller == null) {
            unmarshaller = context.createUnmarshaller();
        }

        return unmarshaller;
    }

    public static Library read(Unmarshaller u, File file) throws IOException, JAXBException {
        return read(u, toSource(file));
    }

    public static Library read(Unmarshaller u, URL url) throws IOException, JAXBException {
        return read(u, toSource(url));
    }

    public static Library read(Unmarshaller u, URI uri) throws IOException, JAXBException {
        return read(u, toSource(uri));
    }

    public static Library read(Unmarshaller u, String string) throws IOException, JAXBException {
        return read(u, toSource(string));
    }

    public static Library read(Unmarshaller u, InputStream inputStream) throws IOException, JAXBException {
        return read(u, toSource(inputStream));
    }

    public static Library read(Unmarshaller u, Reader reader) throws IOException, JAXBException {
        return read(u, toSource(reader));
    }

    @SuppressWarnings("unchecked")
    public synchronized static Library read(Unmarshaller u, Source source) throws JAXBException {
        Object result = u.unmarshal(source);
        return ((JAXBElement<Library>)result).getValue();
    }
    // Performance enhancement additions ~ end

    public static Library read(File file) throws IOException, JAXBException {
        return read(toSource(file));
    }

    public static Library read(URL url) throws IOException, JAXBException {
        return read(toSource(url));
    }

    public static Library read(URI uri) throws IOException, JAXBException {
        return read(toSource(uri));
    }

    public static Library read(String string) throws IOException, JAXBException {
        return read(toSource(string));
    }

    public static Library read(InputStream inputStream) throws IOException, JAXBException {
        return read(toSource(inputStream));
    }

    public static Library read(Reader reader) throws IOException, JAXBException {
        return read(toSource(reader));
    }

    public synchronized static Library read(Source source) throws JAXBException {
        return read(getUnmarshaller(), source);
    }

    /**
     * Creates {@link Source} from various XML representation.
     */
    private static Source toSource(Object xml) throws IOException {
        if (xml == null)
            throw new CqlException("no XML is given");

        if (xml instanceof String) {
            try {
                xml = new URI((String)xml);
            } catch (URISyntaxException e) {
                xml = new File((String)xml);
            }
        }

        if (xml instanceof File) {
            return new StreamSource((File)xml);
        }

        if (xml instanceof URI) {
            xml = ((URI)xml).toURL();
        }

        if (xml instanceof URL) {
            return new StreamSource(((URL)xml).toExternalForm());
        }

        if (xml instanceof InputStream) {
            return new StreamSource((InputStream)xml);
        }

        if (xml instanceof Reader) {
            return new StreamSource((Reader)xml);
        }

        if (xml instanceof Source) {
            return (Source)xml;
        }

        throw new CqlException(String.format("Could not determine access path for input of type %s.", xml.getClass()));
    }
}
