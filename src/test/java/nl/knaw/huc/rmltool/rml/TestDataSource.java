package nl.knaw.huc.rmltool.rml;

import com.google.common.collect.Maps;
import nl.knaw.huc.rmltool.rml.datasource.JoinHandler;
import nl.knaw.huc.rmltool.rml.datasource.joinhandlers.HashMapBasedJoinHandler;
import nl.knaw.huc.rmltool.util.StreamIterator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TestDataSource implements DataSource {
  private final Iterable<Map<String, String>> data;
  private final ErrorHandler errorHandler;
  private final JoinHandler joinHandler = new HashMapBasedJoinHandler();

  TestDataSource(Iterable<Map<String, String>> data) {
    this(data, null);
  }

  TestDataSource(Iterable<Map<String, String>> data, ErrorHandler errorHandler) {
    this.data = data;
    this.errorHandler = errorHandler;
  }

  @Override
  public Stream<Row> getRows(ErrorHandler defaultErrorHandler) {
    return StreamIterator.stream(data.iterator())
                         .map(values -> {
        Map<String, String> mutableValues = Maps.newHashMap(values);
        Map<String, List<String>> joinValues = joinHandler.resolveReferences(mutableValues);

        Set<String> unMappedKeys = mutableValues.keySet().stream()
                                                .filter(key -> mutableValues.get(key) == null || mutableValues.get(key).isEmpty())
                                                .collect(Collectors.toSet());

        for (String key : unMappedKeys) {
          mutableValues.remove(key);
        }
        return (Row) new TestRow(mutableValues, joinValues, errorHandler == null ? defaultErrorHandler : errorHandler);
      });
  }

  @Override
  public void willBeJoinedOn(String fieldName, String referenceJoinValue, String uri, String outputFieldName) {
    joinHandler.willBeJoinedOn(fieldName, referenceJoinValue, uri, outputFieldName);
  }

}
