package me.izstas.rfs.client.rfs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import me.izstas.rfs.model.Metadata;
import me.izstas.rfs.model.Version;

/**
 * A helper class for executing RFS API calls.
 */
public final class Rfs {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    private final CloseableHttpClient client;
    private final HttpClientContext context;
    private final URI uri;

    /**
     * Constructs this class for executing RFS API calls, authenticating anonymously.
     * @param uri RFS API base URI
     */
    public Rfs(URI uri) {
        this.uri = uri;

        client = HttpClients.createDefault();
        context = HttpClientContext.create();
    }

    /**
     * Constructs this class for executing RFS API calls, authenticating with specified username and password.
     * @param uri RFS API base URI
     * @param username the username for authentication
     * @param password the password for authentication
     */
    public Rfs(URI uri, String username, String password) {
        this.uri = uri;

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(URIUtils.extractHost(uri)), new UsernamePasswordCredentials(username, password));

        client = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        AuthCache authCache = new BasicAuthCache();
        authCache.put(URIUtils.extractHost(uri), new BasicScheme());

        context = HttpClientContext.create();
        context.setAuthCache(authCache);
    }

    /**
     * Releases all resources allocated by this object.
     */
    public void shutdown() {
        try {
            client.close();
        }
        catch (IOException e) {
            throw new AssertionError(e); // Should never happen, HttpClient catches IOException inside
        }

        executor.shutdownNow();
    }


    /**
     * Performs GET / (get version information) API call.
     * This call is executed asynchronously.
     */
    public ListenableFuture<Version> getVersion() {
        final HttpGet request = new HttpGet(uri);
        request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        return executor.submit(new Callable<Version>() {
            @Override
            public Version call() throws Exception {
                try (CloseableHttpResponse response = client.execute(request, context)) {
                    return checkAndParseResponse(response, Version.class);
                }
            }
        });
    }

    /**
     * Performs GET /metadata/@path (get file metadata) API call.
     * This call is executed asynchronously.
     */
    public ListenableFuture<Metadata> getMetadata(String path) {
        final HttpGet request = new HttpGet(uri.resolve("metadata/").resolve(fixPath(path)));
        request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        return executor.submit(new Callable<Metadata>() {
            @Override
            public Metadata call() throws Exception {
                try (CloseableHttpResponse response = client.execute(request, context)) {
                    return checkAndParseResponse(response, Metadata.class);
                }
            }
        });
    }


    /**
     * Performs POST /metadata/@path (apply file metadata) API call.
     * This call is executed asynchronously.
     */
    public ListenableFuture<Void> applyMetadata(String path, Metadata metadata) {
        final HttpPost request = new HttpPost(uri.resolve("metadata/").resolve(fixPath(path)));
        request.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        try {
            request.setEntity(new ByteArrayEntity(mapper.writeValueAsBytes(metadata)));
        }
        catch (JsonProcessingException e) {
            throw new AssertionError(e); // Should not really be happening
        }

        return executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (CloseableHttpResponse response = client.execute(request, context)) {
                    checkResponse(response);
                    return null;
                }
            }
        });
    }

    /**
     * Performs GET /content/@path (get file content) API call.
     * This call is executed <strong>synchronously</strong>.
     */
    public void getContent(String path, final OutputStream output) throws IOException {
        final HttpGet request = new HttpGet(uri.resolve("content/").resolve(fixPath(path)));
        request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_OCTET_STREAM.getMimeType());

        try (CloseableHttpResponse response = client.execute(request, context)) {
            checkResponse(response);
            response.getEntity().writeTo(output);
        }
    }


    private String fixPath(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private void checkResponse(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
            throw new RfsAuthenticationException();
        }

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
            throw new RfsAccessException();
        }
    }

    private <T> T checkAndParseResponse(HttpResponse response, Class<T> clazz) throws IOException {
        checkResponse(response);

        try {
            return mapper.readValue(response.getEntity().getContent(), clazz);
        }
        catch (JsonProcessingException e) {
            throw new RfsResponseException(e);
        }
    }


    /**
     * Performs early pre-initialization of some classes used by Rfs objects.
     * While it is not necessary to call this method, it seems to be a good idea
     * because ObjectMapper and HttpClient take a noticeable amount of time to initialize.
     */
    public static void preInitialize() {
        try {
            HttpClients.createDefault().close();
        }
        catch (IOException e) {
            // Ignore
        }
    }
}
