package com.github.skillstree.aws;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.github.skillstree.core.SkillsUpdater;
import com.github.skillstree.aws.service.S3WebSiteConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillsUpdateHandler implements RequestHandler<Map<String, Object>, String> {

    private static final Logger logger = LoggerFactory.getLogger(SkillsUpdateHandler.class);

    private static final String REF_PREFIX = "refs/heads/";

    private final SkillsUpdater skillsUpdater = new SkillsUpdater(new S3WebSiteConstructor());

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        logger.info("Event: {}", event);
        logger.info("Context: {}", context);

        String ref = (String) event.get("ref");
        Optional<String> branch = branchCommitsWerePushedTo(ref);
        if (branch.isEmpty() || !branch.get().equals("aws")) {
            logger.warn("Branch into which commits were pushed is not equal to expected [" + "aws" + "]");
            return null;
        }

        try {
            skillsUpdater.pullPreviouslyPersistedSkills();
            skillsUpdater.retrieveSkillsTrees();
            skillsUpdater.updateSkills();
        } catch (Exception e) {
            logger.error("Cannot process the request to update skills", e);
        }

        return "finished";
    }

    private static Optional<String> branchCommitsWerePushedTo(String ref) {
        if (ref.startsWith(REF_PREFIX)) {
            return Optional.of(ref.substring(REF_PREFIX.length()));
        } else {
            return Optional.empty();
        }
    }

    public static void main(String[] args) throws GitAPIException, IOException {
        SkillsUpdater skillsUpdater = new SkillsUpdater(new S3WebSiteConstructor());
        skillsUpdater.pullPreviouslyPersistedSkills();
        skillsUpdater.retrieveSkillsTrees();
        skillsUpdater.updateSkills();
    }
}
