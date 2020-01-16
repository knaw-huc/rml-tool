package nl.knaw.huc.rmltool.rml.rmldata;


import nl.knaw.huc.rmltool.rml.Row;
import nl.knaw.huc.rmltool.rml.dto.RdfUri;
import nl.knaw.huc.rmltool.rml.rmldata.termmaps.RrTermMap;

import java.util.Optional;

public class RrSubjectMap {
  private final RrTermMap termMap;

  public RrSubjectMap(RrTermMap termMap) {
    this.termMap = termMap;
  }

  public Optional<RdfUri> generateValue(Row row) {
    return termMap.generateValue(row).map(x -> (RdfUri) x);
  }

  @Override
  public String toString() {
    return String.format("    SubjectMap: \n%s",
      this.termMap
    );
  }

}
