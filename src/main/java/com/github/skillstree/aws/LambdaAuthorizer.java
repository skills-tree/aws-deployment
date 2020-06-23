package com.github.skillstree.aws;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.github.skillstree.aws.model.LambdaProxiedRequest;
import com.github.skillstree.core.service.Authorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LambdaAuthorizer implements RequestHandler<LambdaProxiedRequest, Map<String, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(LambdaAuthorizer.class);

    private final Authorizer authorizer = new Authorizer();

    @Override
    public Map<String, Object> handleRequest(LambdaProxiedRequest event, Context context) {
        logger.info("Event: {}", event);

        final String methodArn = event.getMethodArn();
        logger.info("Method ARN: {}", methodArn);

        /*
        String[] arnParts = methodArn.split(":");
        String region = arnParts[3];
        String awsAccountId = arnParts[4];
        String[] apiGatewayArnParts = arnParts[5].split("/");
        String restApiId = apiGatewayArnParts[0];
        String stage = apiGatewayArnParts[1];
        String httpMethod = apiGatewayArnParts[2];
        String resource = ""; // root resource
        if (apiGatewayArnParts.length == 4) {
            resource = apiGatewayArnParts[3];
        }
        */

        final String path = URLDecoder.decode(event.getPath(), StandardCharsets.UTF_8);
        final String jwtToken = getAuthorizationHeader(event);

        AuthPolicy.Statement policyStatement = new AuthPolicy.Statement()
                .setAction("execute-api:Invoke")
                .addResource(methodArn);

        if (jwtToken != null && authorizer.hasAccess(path, jwtToken)) {
            return new AuthPolicy(List.of(policyStatement.setEffect(AuthPolicy.Statement.Effect.ALLOW)))
                    .toLambdaAuthorizationResponse();
        } else {
            return new AuthPolicy(List.of(policyStatement.setEffect(AuthPolicy.Statement.Effect.DENY)))
                    .toLambdaAuthorizationResponse();
        }
    }

    private String getAuthorizationHeader(LambdaProxiedRequest event) {
        String header = event.getHeaders().getOrDefault(
                "Authorization", event.getHeaders().get("authorization"));

        if (header != null) {
            header = header.replace("Bearer ", "").trim();
        }
        return header;
    }
}
