package nl.knaw.huc.rmltool.rml.rmldata.termmaps;

import nl.knaw.huc.rmltool.rml.Row;
import nl.knaw.huc.rmltool.rml.dto.QuadPart;

import java.util.Optional;

public interface RrTermMap {
  Optional<QuadPart> generateValue(Row input);

}
