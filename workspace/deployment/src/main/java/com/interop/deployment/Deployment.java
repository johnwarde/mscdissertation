package com.interop.deployment;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;

public class Deployment {
	public static void main(String[] args) {

		String accesskeyid = "";
		String secretkey = "";
		String bucket = "testbucket.net";

		// get a context with amazon that offers the portable BlobStore API
		BlobStoreContext context = ContextBuilder.newBuilder("aws-s3")
				.credentials(accesskeyid, secretkey)
				.buildView(BlobStoreContext.class);

		// create a container in the default location
		BlobStore blobStore = context.getBlobStore();
		blobStore.createContainerInLocation(null, bucket);

		// add blob
		// Blob blob = blobStore.newBlob("jcloudstest.jpg");
		BlobBuilder blobBuilder = blobStore.blobBuilder("jcloudstest.jpg");
		Blob blob = blobBuilder.build();
		blob.setPayload("C/test/JamesBrown.jpg");
		blobStore.putBlob(bucket, blob);

	}
}

