package SimpleNeo4j;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Michael Hunger @since 22.10.13
 */
public interface CypherExecutor {
    Iterator<Map<String, Map<String, Object>>> query(String statement, Map<String, Object> params);
}
