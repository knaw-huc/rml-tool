package nl.knaw.huc.rmltool.rml.jena;

import nl.knaw.huc.rmltool.rml.rdfshim.RdfLiteral;
import org.apache.jena.rdf.model.Literal;

public class JenaLiteral implements RdfLiteral {

  private Literal literal;

  JenaLiteral(Literal literal) {
    this.literal = literal;
  }

  @Override
  public String getValue() {
    return this.literal.getLexicalForm();
  }

  @Override
  public String getLanguage() {
    return this.literal.getLanguage();
  }

  @Override
  public String getTypeUri() {
    return this.literal.getDatatypeURI();
  }
}
