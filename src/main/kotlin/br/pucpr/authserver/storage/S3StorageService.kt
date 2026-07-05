package br.pucpr.authserver.storage

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream

@Component
@Profile("aws")
class S3StorageService : StorageService {
    private val s3: AmazonS3

    init {
        val endpoint = AwsClientBuilder.EndpointConfiguration("http://localhost:4566", Regions.US_EAST_1.name)
        s3 = AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(endpoint)
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials("test", "test")))
            .withPathStyleAccessEnabled(true)
            .build()

        try {
            // Create the bucket in LocalStack on startup if not present
            if (!s3.doesBucketExistV2(BUCKET)) {
                s3.createBucket(BUCKET)
            }
        } catch (e: Exception) {
            log.warn("Could not connect to S3/LocalStack on startup. S3StorageService might not be fully functional. Error: {}", e.message)
        }
    }

    override fun save(path: String, contentType: String, bytes: ByteArray): String {
        val meta = ObjectMetadata()
        meta.contentType = contentType
        meta.contentLength = bytes.size.toLong()

        ByteArrayInputStream(bytes).use {
            s3.putObject(PutObjectRequest(BUCKET, path, it, meta))
        }
        return urlFor(path)
    }

    override fun load(path: String): Resource? =
        try {
            val key = path.replace("--", "/")
            val s3Object = s3.getObject(BUCKET, key)
            InputStreamResource(s3Object.objectContent)
        } catch (e: Exception) {
            null
        }

    override fun urlFor(name: String): String =
        "$PREFIX/$BUCKET/$name"

    companion object {
        private const val BUCKET = "avatar-bucket"
        private const val PREFIX = "http://localhost:4566"
        private val log = LoggerFactory.getLogger(S3StorageService::class.java)
    }
}
