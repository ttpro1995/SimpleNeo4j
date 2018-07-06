package SimpleNeo4j;

import org.neo4j.driver.v1.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Hunger @since 22.10.13
 */
public class BoltCypherExecutor implements CypherExecutor {

    private final org.neo4j.driver.v1.Driver driver;

    public BoltCypherExecutor(String url) {
        this(url, null, null);
    }

    public BoltCypherExecutor(String url, String username, String password) {
        boolean hasPassword = password != null && !password.isEmpty();
        AuthToken token = hasPassword ? AuthTokens.basic(username, password) : AuthTokens.none();
        //
        driver = GraphDatabase.driver(url, token, Config.build().toConfig());

    }

    @Override
    public Iterator<Map<String, Map<String, Object>>> query(String query, Map<String, Object> params) {
        try (Session session = driver.session()) {
            List<Map<String, Map<String, Object>>> list = session.run(query, params)
                    .list( r -> r.asMap(BoltCypherExecutor::convert));
            return list.iterator();
        }
    }

    static Map<String, Object> convert(Value value) {
        switch (value.type().name()) {
            case "PATH":
//                return value.asList(BoltCypherExecutor::convert);
                return value.asMap();
            case "NODE":
            case "RELATIONSHIP":
                return value.asMap();
        }
        return new HashMap<>();
    }

}
