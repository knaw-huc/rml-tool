package nl.knaw.huc.rmltool.rml;

import java.util.List;

public interface Row {
  List<String> getJoinValue(String key);

  String getRawValue(String key);

  void handleLinkError(String childField, String parentCollection, String parentField);
}
