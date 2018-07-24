/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.thaithien.SimpleNeo4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

import org.neo4j.cypher.internal.v3_4.functions.E;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.helpers.collection.Iterators;

/**
 *
 *
 */
public class Neo4jManager {

    private static Driver driver;

    private static CypherExecutor cypher;

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private static final JsonParser JSON_PARSER = new JsonParser();

    private static boolean isInitialize = false;

    private static String driverUrl;
    private static String cypherUri;

    /**
     * init neo4j connection with ip, port, userID, password
     * can re-init to change IP, port, user, password
     * @param ip
     * @param port
     * @param user
     * @param password
     */
    public static void initialDriver(String ip, String port, String user, String password) {
        String url = String.format("bolt://%s:%s", ip, port);
        driverUrl = url;
        String uri = String.format("bolt://%s:%s@%s", user, password, ip);
        cypherUri = uri;
        driver = GraphDatabase.driver(url, AuthTokens.basic(user, password));
        cypher = createCypherExecutor(uri);
        isInitialize = true;
    }

    public static String getDriverUrl() {
        return driverUrl;
    }

    public static void setDriverUrl(String driverUrl) {
        Neo4jManager.driverUrl = driverUrl;
    }

    public static String getCypherUri() {
        return cypherUri;
    }

    public static void setCypherUri(String cypherUri) {
        Neo4jManager.cypherUri = cypherUri;
    }

    private static CypherExecutor createCypherExecutor(String uri) {
        try {
            String auth = new URL(uri.replace("bolt", "http")).getUserInfo();
            if (auth != null) {
                String[] parts = auth.split(":");
                return new BoltCypherExecutor(uri, parts[0], parts[1]);
            }
            return new BoltCypherExecutor(uri);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Neo4j-ServerURL " + uri);
        }
    }

    public static Stream<Map<String, String>> query(String query)throws Exception {
        if (isInitialize == false) {
            throw new Exception("Neo4jManager must be initialized. Call initialDriver to initialize.");
        }

        Optional<List<Map<String, String>>> queryResult;

        queryResult = Optional.of(Iterators.asCollection(cypher.query(query, new HashMap<>())).stream()
                .map(mapObject -> {
                    Map<String, String> result = new HashMap<>();
                    mapObject.forEach((entity, value) -> {
                        value.forEach((k, v) -> {
                            result.put(entity + "." + k, v.toString());
                        });
                    });
                    return result;
                })
                .collect(toList())
        );

        return queryResult.get().stream();
    }
}
