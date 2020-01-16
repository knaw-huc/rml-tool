package nl.knaw.huc.rmltool.rml.rmldata.builders;

import nl.knaw.huc.rmltool.rml.rmldata.RrTriplesMap;
import nl.knaw.huc.rmltool.rml.rmldata.termmaps.RrRefObjectMap;
import nl.knaw.huc.rmltool.rml.rmldata.RrPredicateObjectMapOfReferencingObjectMap;
import nl.knaw.huc.rmltool.rml.rmldata.termmaps.RrJoinCondition;

import java.util.function.Function;

public class ReferencingObjectMapBuilder {

  private String parentTriplesMapUri;
  private RrJoinCondition rrJoinCondition;

  ReferencingObjectMapBuilder() {
  }

  public ReferencingObjectMapBuilder withParentTriplesMap(String rrTriplesMapUri) {
    this.parentTriplesMapUri = rrTriplesMapUri;
    return this;
  }

  public ReferencingObjectMapBuilder withJoinCondition(String child, String parent) {
    this.rrJoinCondition = new RrJoinCondition(child, parent);
    return this;
  }

  public void build(TermMapBuilder predicateMap,
                    Function<String, PromisedTriplesMap> getTriplesMap, RrTriplesMap ownTriplesMap) {
    getTriplesMap.apply(parentTriplesMapUri).onTriplesMapReceived((referencingTriplesMap) -> {
      ownTriplesMap.addPredicateObjectMap(
        new RrPredicateObjectMapOfReferencingObjectMap(
          predicateMap.build(),
          false,
          new RrRefObjectMap(referencingTriplesMap, rrJoinCondition, ownTriplesMap.getDataSource())
        )
      );
    });
  }

  String getReferencedMap() {
    return parentTriplesMapUri;
  }

}
