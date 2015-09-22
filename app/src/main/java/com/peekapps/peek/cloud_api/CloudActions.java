package com.peekapps.peek.cloud_api;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Slav on 16/09/2015.
 */
public class CloudActions {

    private Context context;
    private static Storage storageService;
    private static final String APPLICATION_NAME = "Peek/0.1";
    private static final String STORAGE_SCOPE = "https://www.googleapis.com/auth/devstorage.read_write";

    private static Plus plus;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String SERVICE_ACCOUNT_EMAIL =
            "25301496576-eoqnv5tp0kq532hkcrcu2hnaj7pb2232@developer.gserviceaccount.com";
    /**
     * Returns an authenticated Storage object used to make service calls to Cloud Storage.
     */
    private static Storage getService(Context context) throws IOException, GeneralSecurityException {
        if (null == storageService) {
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            // Build service account credential.
            String emailAddress = "25301496576-m81utq0m3rql99i4rofsetmt8l8ui176@developer.gserviceaccount.com";
            //Get private key from raw assets
            PrivateKey serviceAccountPrivateKey = SecurityUtils.loadPrivateKeyFromKeyStore
                    (SecurityUtils.getPkcs12KeyStore(),
                            context.getResources().getAssets().open("Peek-33708f8bc75c.p12"),
                            "notasecret", "privatekey", "notasecret");
            //Generate the credential for OAuth2 service account
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountId(emailAddress)
                    .setServiceAccountPrivateKey(serviceAccountPrivateKey)
                    .setServiceAccountScopes(Collections.singleton(STORAGE_SCOPE))
                    .build();

            // set up global Plus instance
            plus = new Plus.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();
            storageService = new Storage.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();

            // Depending on the environment that provides the default credentials (e.g. Compute Engine,
            // App Engine), the credentials may require us to specify the scopes we need explicitly.
            // Check for this case, and inject the Bigquery scope if required.
            if (credential.createScopedRequired()) {
                credential = credential.createScoped(StorageScopes.all());
            }

        }
        return storageService;
    }


    /**
     * Uploads data to an object in a bucket.
     *
     * @param name the name of the destination object.
     * @param contentType the MIME type of the data.
     * @param stream the data - for instance, you can use a FileInputStream to upload a file.
     * @param bucketName the name of the bucket to create the object in.
     */
    public static void uploadStream(Context context,
            String name, String contentType, InputStream stream, String bucketName)
            throws IOException, GeneralSecurityException {
        InputStreamContent contentStream = new InputStreamContent(contentType, stream);
        StorageObject objectMetadata = new StorageObject()
                // Set the destination object name
                .setName(name)
                        // Set the access control list to publicly read-only
                .setAcl(Arrays.asList(
                        new ObjectAccessControl().setEntity("allUsers").setRole("READER")));

        // Do the insert
        Storage client = getService(context);
        Storage.Objects.Insert insertRequest = client.objects().insert(
                bucketName, objectMetadata, contentStream);

        insertRequest.execute();
    }
}
