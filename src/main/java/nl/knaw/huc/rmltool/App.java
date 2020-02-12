package nl.knaw.huc.rmltool;

import nl.knaw.huc.rmltool.rml.LoggingErrorHandler;
import nl.knaw.huc.rmltool.rml.datasource.excel.ExcelSheetDataSourceFactory;
import nl.knaw.huc.rmltool.rml.dto.QuadPart;
import nl.knaw.huc.rmltool.rml.dto.RdfValue;
import nl.knaw.huc.rmltool.rml.jena.JenaBasedReader;
import nl.knaw.huc.rmltool.rml.rmldata.RmlMappingDocument;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

public class App {
  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.err.println("Expected 2 arguments '{path_to_excel_file}' and '{path_to_mapping_file}'");
    }

    final XSSFWorkbook workbook = new XSSFWorkbook(new File(args[0]));


    final FileInputStream rmlDocument = new FileInputStream(new File(args[1]));
    final Model model = ModelFactory.createDefaultModel().read(rmlDocument, null, "JSON-LD");

    final RmlMappingDocument rmlMappingDocument = new JenaBasedReader().fromRdf(
        model,
        new ExcelSheetDataSourceFactory(workbook)::apply
    );

    try (PrintWriter printWriter = new PrintWriter("output.nt", "UTF-8")) {
      rmlMappingDocument.execute(new LoggingErrorHandler()).forEach(quad -> {
        printWriter.println(String.format(
            "<%s> <%s> %s .",
            quad.getSubject().getContent(),
            quad.getPredicate().getContent(),
            formatObject(quad.getObject())
        ));
      });
    }
  }

  private static String formatObject(QuadPart object) {
    if (object instanceof RdfValue) {
      return String.format("\"%s\"^^<%s>",object.getContent().replaceAll("\"", "\\\\\""),
          ((RdfValue) object).getLiteralType().get());
    }

    return "<" + object.getContent() + ">";
  }
}
