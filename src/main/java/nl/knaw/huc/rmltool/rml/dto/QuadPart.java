package nl.knaw.huc.rmltool.rml.dto;

import java.util.Optional;

public interface QuadPart {
  String getContent();

  Optional<String> getUri();

  Optional<String> getLiteral();

  Optional<String> getLiteralType();

  Optional<String> getLiteralLanguage();
}
