package com.github.skillstree.aws;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.skillstree.aws.model.LambdaProxiedRequest;
import com.github.skillstree.aws.model.LambdaResponse;
import com.github.skillstree.core.model.UserSkill;
import com.github.skillstree.core.service.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSkillsHandler implements RequestHandler<LambdaProxiedRequest, LambdaResponse> {

    private static final Logger logger = LoggerFactory.getLogger(UserSkillsHandler.class);

    private static final Pattern ALLOWED_PATH_PATTERN = Pattern.compile("([a-zA-Z0-9|]*)(\\/skills)");

    private final PersistenceService persistenceService = new PersistenceService();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public LambdaResponse handleRequest(LambdaProxiedRequest event, Context context) {
        logger.info("Event: {}", event);

        String path = URLDecoder.decode(event.getPathParameters().get("proxy"), StandardCharsets.UTF_8);
        logger.info("path is '{}'", path);
        Matcher matcher = ALLOWED_PATH_PATTERN.matcher(path);
        if (!matcher.find()) {
            return createLambdaResponseWithCorsEnabled()
                    .setStatusCode(404)
                    .setBody("Unknown path");
        }

        String userId = matcher.group(1);
        logger.info("userId: '{}'", userId);

        if (event.getHttpMethod().equals("GET")) {
            return getUserSkills(userId);

        } else if (event.getHttpMethod().equals("PUT")) {
            String payloadJson = event.getBody();
            if (payloadJson == null || payloadJson.isEmpty()) {
                return createLambdaResponseWithCorsEnabled()
                        .setStatusCode(400)
                        .setBody("Request's payload is empty");
            }
            return updateUserSkills(userId, payloadJson);

        } else {
            return createLambdaResponseWithCorsEnabled()
                    .setStatusCode(404)
                    .setBody("Unknown method");
        }
    }

    private LambdaResponse getUserSkills(String userId) {
        List<UserSkill> userSkills = persistenceService.getUserSkills(userId);
        logger.info("skills obtained by user: {}", userSkills);

        try {
            LambdaResponse response = createLambdaResponseWithCorsEnabled()
                    .setBase64Encoded(false)
                    .setStatusCode(200)
                    .setBody(mapper.writeValueAsString(new UserSkillsContainer(userSkills)));
            logger.info("response: {}", response);

            return response;

        } catch (IOException e) {
            logger.error("Cannot create response", e);
            throw new RuntimeException("Cannot create json string from the response object", e);
        }
    }

    private static LambdaResponse createLambdaResponseWithCorsEnabled() {
        String allowedOrigins = System.getenv("ALLOWED_ORIGINS");
        return new LambdaResponse().addHeader(
                "Access-Control-Allow-Origin", Objects.requireNonNullElse(allowedOrigins, ""));
    }

    private LambdaResponse updateUserSkills(String userId, String payloadJson) {
        try {
            var skillsContainer = mapper.readValue(payloadJson, UserSkillsContainer.class);
            persistenceService.updateUserSkills(userId, skillsContainer.getUserSkills());
            return createLambdaResponseWithCorsEnabled().setStatusCode(200);

        } catch (JsonProcessingException e) {
            logger.error("Cannot read payload", e);
            return createLambdaResponseWithCorsEnabled()
                    .setStatusCode(400)
                    .setBody("Cannot read request's payload as json");
        }
    }

    public static class UserSkillsContainer {

        private List<UserSkill> userSkills;

        public UserSkillsContainer() {
        }

        public UserSkillsContainer(List<UserSkill> userSkills) {
            this.userSkills = userSkills;
        }

        public List<UserSkill> getUserSkills() {
            return userSkills;
        }

        public UserSkillsContainer setUserSkills(List<UserSkill> userSkills) {
            this.userSkills = userSkills;
            return this;
        }
    }
}
