package nl.knaw.huc.rmltool.rml;

import java.util.stream.Stream;

public interface DataSource {
  Stream<Row> getRows(ErrorHandler errorHandler);

  void willBeJoinedOn(String fieldName, String referenceJoinValue, String uri, String outputFieldName);
}
