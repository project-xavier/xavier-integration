package org.jboss.xavier.integrations.storage;

import javax.inject.Inject;
import javax.inject.Named;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StorageService {
    private static Logger logger = LoggerFactory.getLogger(StorageService.class);

    @Inject
    @Named("s3client")
    private AmazonS3 storageClient;

    @Value("${S3_BUCKET}")
    private String bucket;

    public int getStorageObjectsSize() {
        int s3Size = storageClient.listObjectsV2(new ListObjectsV2Request().withBucketName(bucket)).getKeyCount();
        logger.info("S3 Objects : " + s3Size);
        return s3Size;
    }
}