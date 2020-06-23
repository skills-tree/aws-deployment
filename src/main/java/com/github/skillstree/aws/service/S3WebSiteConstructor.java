package com.github.skillstree.aws.service;

import com.github.skillstree.core.service.WebSiteConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class S3WebSiteConstructor implements WebSiteConstructor {

    private static final Logger logger = LoggerFactory.getLogger(S3WebSiteConstructor.class);

    @Override
    public void updateSkillsTree(String skillsTreeJson) {
        Region region = Region.US_EAST_1; // TODO: should be a parameter
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        String bucket = "seskillstree.ml"; // TODO: should be a parameter
        String key = "skillstree.js"; // TODO: should be a parameter

        PutObjectResponse response = s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                RequestBody.fromString("const skillsTreeJson = " + skillsTreeJson));

        logger.info("SkillsTree is updated in S3 with version " + response.versionId());
    }
}
