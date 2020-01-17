package nl.knaw.huc.rmltool.rml.datasource.excel;

import nl.knaw.huc.rmltool.rml.DataSource;
import nl.knaw.huc.rmltool.rml.datasource.jexl.JexlRowFactory;
import nl.knaw.huc.rmltool.rml.datasource.joinhandlers.HashMapBasedJoinHandler;
import nl.knaw.huc.rmltool.rml.rdfshim.RdfResource;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ExcelSheetDataSourceFactory {
  private static final String NS_RML = "http://semweb.mmlab.be/ns/rml#";
  private final Workbook workbook;

  public ExcelSheetDataSourceFactory(Workbook workbook) {

    this.workbook = workbook;
  }
  public Optional<DataSource> apply(RdfResource rdfResource) {
    for (RdfResource resource : rdfResource.out(NS_RML + "source")) {
      Set<RdfResource> rawCollection = resource.out("http://timbuctoo.huygens.knaw.nl/mapping#rawCollectionUri");
      Set<RdfResource> customFields = resource.out("http://timbuctoo.huygens.knaw.nl/mapping#customField");

      Map<String, String> expressions = new HashMap<>();
      for (RdfResource customField : customFields) {
        Set<RdfResource> fieldNameResource = customField.out("http://timbuctoo.huygens.knaw.nl/mapping#name");
        Set<RdfResource> fieldValueResource = customField.out("http://timbuctoo.huygens.knaw.nl/mapping#expression");
        fieldNameResource.iterator().next().asLiteral().ifPresent(fieldName -> {
          fieldValueResource.iterator().next().asLiteral().ifPresent(fieldValue -> {
            expressions.put(fieldName.getValue(), fieldValue.getValue());
          });
        });
      }


      if (rawCollection.size() == 1) {
        return rawCollection.iterator().next().asIri()
                            .map(collectionIri -> new ExcelSheetDataSource(
                                getSheet(collectionIri),
                                new JexlRowFactory(expressions, new HashMapBasedJoinHandler())
                            ));
      }
    }
    return Optional.empty();
  }

  private Sheet getSheet(String collectionIri) {
    return workbook.getSheet(collectionIri.substring(collectionIri.lastIndexOf("/") + 1));
  }


}
