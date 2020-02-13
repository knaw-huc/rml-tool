package nl.knaw.huc.rmltool.util;

import com.google.common.base.Charsets;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public class Rdf4jWriter implements Closeable {
  public static final Charset UTF_8 = Charsets.UTF_8;
  protected final RDFWriter rdfWriter;
  private final RDFFormat rdfFormat;
  protected final SimpleValueFactory valueFactory;
  protected final Writer writer;
  public static final String LANGSTRING = "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";
  public static final String STRING = "http://www.w3.org/2001/XMLSchema#string";

  public Rdf4jWriter(OutputStream outputStream, RDFFormat rdfFormat) {
    writer = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));
    rdfWriter = Rio.createWriter(rdfFormat, writer);
    this.rdfFormat = rdfFormat;
    valueFactory = SimpleValueFactory.getInstance();
    rdfWriter.startRDF();
  }

  private Resource makeResource(String input) {
    if (input.startsWith("_:")) {
      return valueFactory.createBNode(input.substring(2));
    } else {
      return valueFactory.createIRI(input);
    }
  }

  public void onRelation(String subject, String predicate, String object, String graph) {
    rdfWriter.handleStatement(
      valueFactory.createStatement(
        makeResource(subject),
        valueFactory.createIRI(predicate),
        makeResource(object),
        makeResource(graph)
      )
    );
  }

  public void onValue(String subject, String predicate, String value, String valueType, String graph) {
    rdfWriter.handleStatement(
      valueFactory.createStatement(
        makeResource(subject),
        valueFactory.createIRI(predicate),
        valueFactory.createLiteral(value, valueFactory.createIRI(valueType)),
        makeResource(graph)
      )
    );
  }

  public void onLanguageTaggedString(String subject, String predicate, String value, String language, String graph) {
    rdfWriter.handleStatement(
      valueFactory.createStatement(
        makeResource(subject),
        valueFactory.createIRI(predicate),
        valueFactory.createLiteral(value, language),
        makeResource(graph)
      )
    );
  }

  @Override
  public void close() {
    rdfWriter.endRDF();
  }

  private class LogStorageFailedException extends Exception {
  }
}
