/**
 * Mule S3 Cloud Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.s3.simpleapi;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.StorageClass;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.Validate;

public class SimpleAmazonS3AmazonDevKitImpl implements SimpleAmazonS3
{
    AmazonS3 s3;

    public SimpleAmazonS3AmazonDevKitImpl(@NotNull AmazonS3 s3)
    {
        Validate.notNull(s3);
        this.s3 = s3;
    }

    // 1.1
    public List<Bucket> listBuckets() throws AmazonClientException, AmazonServiceException
    {
        return s3.listBuckets();
    }

    // 2.1
    public Bucket createBucket(@NotNull String bucketName, String region, CannedAccessControlList acl)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        CreateBucketRequest request = new CreateBucketRequest(bucketName, region);
        request.setCannedAcl(acl);
        return s3.createBucket(request);
    }

    // 2.2
    public void deleteBucket(@NotNull String bucketName) throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        s3.deleteBucket(bucketName);
    }

    public void deleteBucketAndObjects(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        ObjectListing objects = s3.listObjects(bucketName);
        for (S3ObjectSummary summary : objects.getObjectSummaries())
        {
            s3.deleteObject(bucketName, summary.getKey());
        }
        deleteBucket(bucketName);
    }

    // 2.3
    public ObjectListing listObjects(@NotNull String bucketName, @NotNull String prefix)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        Validate.notNull(prefix);
        return s3.listObjects(bucketName, prefix);
    }

    // 3.1.1
    public void deleteBucketPolicy(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        s3.deleteBucketPolicy(bucketName);
    }

    // 3.1.2
    public BucketPolicy getBucketPolicy(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        return s3.getBucketPolicy(bucketName);
    }

    // 3.1.3
    public void setBucketPolicy(@NotNull String bucketName, @NotNull String policyText)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        Validate.notNull(policyText);
        s3.setBucketPolicy(bucketName, policyText);
    }

    // 3.2.1
    public void deleteBucketWebsiteConfiguration(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        s3.deleteBucketWebsiteConfiguration(bucketName);
    }

    // 3.2.2
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        return s3.getBucketWebsiteConfiguration(bucketName);
    }

    // 3.2.3
    public void setBucketWebsiteConfiguration(@NotNull String bucketName,
                                              @NotNull BucketWebsiteConfiguration configuration)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        Validate.notNull(configuration);
        s3.setBucketWebsiteConfiguration(bucketName, configuration);
    }

    // TODO provide md5 and content length
    // 4.1
    public String createObject(@NotNull S3ObjectId objectId,
                               @NotNull InputStream input,
                               @NotNull ObjectMetadata metadata,
                               CannedAccessControlList acl,
                               StorageClass storageClass)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(input);
        Validate.notNull(metadata);
        PutObjectRequest request = new PutObjectRequest(objectId.getBucketName(), objectId.getKey(), input,
            metadata);
        request.setCannedAcl(acl);
        if (storageClass != null)
        {
            request.setStorageClass(storageClass);
        }
        return s3.putObject(request).getVersionId();
    }

    // 4.2
    public void deleteObject(@NotNull S3ObjectId objectId)
        throws AmazonClientException, AmazonServiceException
    {
        if (objectId.isVersioned())
        {
            s3.deleteVersion(objectId.getBucketName(), objectId.getKey(), objectId.getVersionId());
        }
        else
        {
            s3.deleteObject(objectId.getBucketName(), objectId.getKey());
        }
    }

    // 4.4
    public String copyObject(@NotNull S3ObjectId source,
                             @NotNull S3ObjectId destination,
                             CannedAccessControlList acl,
                             StorageClass storageClass) throws AmazonClientException, AmazonServiceException
    {
        CopyObjectRequest request = new CopyObjectRequest(source.getBucketName(), source.getKey(),
            source.getVersionId(), destination.getBucketName(), destination.getKey());
        request.setCannedAccessControlList(acl);
        if (storageClass != null)
        {
            request.setStorageClass(storageClass);
        }
        return s3.copyObject(request).getVersionId();
    }

    // 4.5
    public URI createPresignedUri(@NotNull S3ObjectId objectId, Date expiration, HttpMethod method)
        throws AmazonClientException, URISyntaxException
    {
        return s3.generatePresignedUrl(objectId.getBucketName(), objectId.getKey(), expiration, method)
            .toURI();
    }

    // 4.6
    // TODO this method should return the new version, but Amazon does not answer it
    public void setObjectStorageClass(@NotNull S3ObjectId objectId, StorageClass newStorageClass)
        throws AmazonClientException, AmazonServiceException
    {
        s3.changeObjectStorageClass(objectId.getBucketName(), objectId.getKey(), newStorageClass);
    }

    // TODO 3. conditional get
    // 4.3
    public InputStream getObjectContent(@NotNull S3ObjectId objectId)
        throws AmazonClientException, AmazonServiceException
    {
        return s3.getObject(
            new GetObjectRequest(objectId.getBucketName(), objectId.getKey(), objectId.getVersionId()))
            .getObjectContent();
    }

    public ObjectMetadata getObjectMetadata(@NotNull S3ObjectId objectId)
        throws AmazonClientException, AmazonServiceException
    {
        return s3.getObjectMetadata(new GetObjectMetadataRequest(objectId.getBucketName(), objectId.getKey(),
            objectId.getVersionId()));
    }

    public S3Object getObject(@NotNull S3ObjectId objectId)
    {
        return s3.getObject(new GetObjectRequest(objectId.getBucketName(), objectId.getKey(),
            objectId.getVersionId()));
    }

    
    
}