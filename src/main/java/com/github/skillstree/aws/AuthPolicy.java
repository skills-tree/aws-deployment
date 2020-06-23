package com.github.skillstree.aws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthPolicy {

    private static final String VERSION = "Version";

    private static final String STATEMENT = "Statement";

    private static final String EFFECT = "Effect";

    private static final String ACTION = "Action";

    private static final String RESOURCE = "Resource";

    private String version = "2012-10-17";

    private List<Statement> statement;

    public AuthPolicy() {
    }

    public AuthPolicy(List<Statement> statement) {
        this.statement = statement;
    }

    public String getVersion() {
        return version;
    }

    public AuthPolicy setVersion(String version) {
        this.version = version;
        return this;
    }

    public List<Statement> getStatement() {
        return statement;
    }

    public AuthPolicy setStatement(List<Statement> statement) {
        this.statement = statement;
        return this;
    }

    /**
     * IAM Policies use capitalized field names, but Lambda by default will serialize object members using camel case.
     * @return IAM Policy with correct capitalization
     */
    public Map<String, Object> toLambdaAuthorizationResponse() {
        return toLambdaAuthorizationResponse("*");
    }

    public Map<String, Object> toLambdaAuthorizationResponse(String principal) {
        Map<String, Object> policyDocument = new HashMap<>();
        policyDocument.put(VERSION, this.getVersion());

        var statement = new ArrayList<Map<String, Object>>();
        this.getStatement().forEach(s -> {
            var st = new HashMap<String, Object>();
            st.put(ACTION, s.getAction());
            st.put(RESOURCE, s.getResource());
            st.put(EFFECT, s.getEffect());
            statement.add(st);
        });

        policyDocument.put(STATEMENT, statement);

        Map<String, Object> response = new HashMap<>();
        response.put("principalId", principal);
        response.put("policyDocument", policyDocument);

        return response;
    }

    public static class Statement {

        private String action;

        private List<String> resource = new ArrayList<>();

        private Effect effect;

        public String getAction() {
            return action;
        }

        public Statement setAction(String action) {
            this.action = action;
            return this;
        }

        public List<String> getResource() {
            return resource;
        }

        public Statement setResource(List<String> resource) {
            this.resource = resource;
            return this;
        }

        public Statement addResource(String res) {
            resource.add(res);
            return this;
        }

        public Effect getEffect() {
            return effect;
        }

        public Statement setEffect(Effect effect) {
            this.effect = effect;
            return this;
        }

        public enum Effect {
            ALLOW,
            DENY
        }
    }

}
