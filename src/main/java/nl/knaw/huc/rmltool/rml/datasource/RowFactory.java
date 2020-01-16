package nl.knaw.huc.rmltool.rml.datasource;

import nl.knaw.huc.rmltool.rml.ErrorHandler;
import nl.knaw.huc.rmltool.rml.Row;

import java.util.Map;

public interface RowFactory {
  JoinHandler getJoinHandler();

  Row makeRow(Map<String, String> values, ErrorHandler errorHandler);
}
