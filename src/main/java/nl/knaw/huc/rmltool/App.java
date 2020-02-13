package nl.knaw.huc.rmltool;

import nl.knaw.huc.rmltool.rml.LoggingErrorHandler;
import nl.knaw.huc.rmltool.rml.datasource.excel.ExcelSheetDataSourceFactory;
import nl.knaw.huc.rmltool.rml.dto.RdfBlankNode;
import nl.knaw.huc.rmltool.rml.dto.RdfLanguageTaggedString;
import nl.knaw.huc.rmltool.rml.dto.RdfUri;
import nl.knaw.huc.rmltool.rml.dto.RdfValue;
import nl.knaw.huc.rmltool.rml.jena.JenaBasedReader;
import nl.knaw.huc.rmltool.rml.rmldata.RmlMappingDocument;
import nl.knaw.huc.rmltool.util.Rdf4jWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class App {
  public static void main(String[] args) throws Exception {
    if (args.length < 3) {
      System.err.println("Expected 3 arguments '{path_to_excel_file}' and '{path_to_mapping_file}' and '{graph_uri}'");
      System.exit(1);
    }

    final XSSFWorkbook workbook = new XSSFWorkbook(new File(args[0]));

    final FileInputStream rmlDocument = new FileInputStream(new File(args[1]));
    final Model model = ModelFactory.createDefaultModel().read(rmlDocument, null, "JSON-LD");

    final String graphUri = args[2];

    final RmlMappingDocument rmlMappingDocument = new JenaBasedReader().fromRdf(
        model,
        new ExcelSheetDataSourceFactory(workbook)::apply
    );

    OutputStream outputStream = new FileOutputStream("output.nq");
    try (Rdf4jWriter rdfWriter = new Rdf4jWriter(outputStream, RDFFormat.NQUADS )) {
      rmlMappingDocument.execute(new LoggingErrorHandler()).forEach(quad -> {
        if (quad.getObject() instanceof RdfUri || quad.getObject() instanceof RdfBlankNode) {
          rdfWriter.onRelation(quad.getSubject().getContent(), quad.getPredicate().getContent(),
              quad.getObject().getContent(), graphUri);
        }
        if (quad.getObject() instanceof RdfValue) {
          rdfWriter.onValue(quad.getSubject().getContent(), quad.getPredicate().getContent(),
              quad.getObject().getContent(), Rdf4jWriter.STRING,graphUri);
        }
        if(quad.getObject() instanceof RdfLanguageTaggedString) {
          rdfWriter.onLanguageTaggedString(quad.getSubject().getContent(), quad.getPredicate().getContent(),
              quad.getObject().getContent(), Rdf4jWriter.LANGSTRING,graphUri);
        }
      });
    }
  }

}
