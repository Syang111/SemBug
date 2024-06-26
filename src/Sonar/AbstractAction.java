package Sonar;

import Sonar.common.query.Query;

public interface AbstractAction<G> {

    Query<?> getQuery(G globalState) throws Exception;

    
    default boolean canBeRetried() {
        return true;
    }

}
