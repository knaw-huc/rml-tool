package nl.knaw.huc.rmltool.rml.rmldata;

import nl.knaw.huc.rmltool.rml.dto.Quad;
import nl.knaw.huc.rmltool.rml.dto.RdfUri;
import nl.knaw.huc.rmltool.rml.Row;

import java.util.stream.Stream;

public interface RrPredicateObjectMap {
  Stream<Quad> generateValue(RdfUri subject, Row row);
}
