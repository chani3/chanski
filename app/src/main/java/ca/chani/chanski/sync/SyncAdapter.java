package ca.chani.chanski.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import ca.chani.chanski.R;

/**
 * Created by chani on 2014-03-03.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SyncAdapter";
    //note: loopj is a bit overkill for my simple http use, but, httpurlconnection is just too ugly.
    SyncHttpClient httpClient;

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.e(TAG, "onPerformSync " + account.toString());
        AccountManager manager = AccountManager.get(getContext());
        try {
            String token = manager.blockingGetAuthToken(account, "", true);
            Log.e(TAG, "token: " + token);
            syncData(token, provider, syncResult);

        } catch (OperationCanceledException e) {
            Log.e(TAG, "OperationCanceledException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            e.printStackTrace();
            syncResult.stats.numIoExceptions++;
        } catch (AuthenticatorException e) {
            //TODO: ask the user for the token?
            Log.e(TAG, "AuthenticatorException");
        } catch (JSONException e) {
            syncResult.stats.numParseExceptions++;
            e.printStackTrace();
        } finally {
            httpClient = null;
        }
    }

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
        if (httpClient != null) {
            httpClient.cancelRequests(getContext(), true);
        }
    }

    private void syncData(String token, ContentProviderClient provider, final SyncResult syncResult) throws JSONException, UnsupportedEncodingException {
        JSONArray dataArray = buildJson(provider);
        final int updates = dataArray.length();

        Log.e(TAG, String.format("%d things changed", updates));
        if (updates <= 0) {
            //return;
        }

        JSONObject postData = new JSONObject();
        postData.put("data", dataArray);
        Log.e(TAG, "json: " + postData.toString());

        httpClient = new SyncHttpClient(); //note: don't bother specifying port here, it's lost when we set up the cert
        //TODO maybe we should keep the httpclient around to reuse this? or at least the ssl factory?
        try {
            addSslCert(httpClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpClient.addHeader("Authorization", token);

        String url = "https://chani.ca:3000/";
        StringEntity entity = new StringEntity(postData.toString());
        String contentType = "application/json";
        httpClient.post(getContext(), url, entity, contentType, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Log.e(TAG, "success!");
                Log.e(TAG, responseBody);
                syncResult.stats.numUpdates += updates;
                markItemsSynced();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                Log.e(TAG, "failure: " + error.toString());
                error.printStackTrace();
                Log.e(TAG, Integer.toString(statusCode));
                if (statusCode == 401) {
                    syncResult.stats.numAuthExceptions++;
                } else {
                    syncResult.stats.numIoExceptions++;
                }
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "onFinish");
                httpClient = null;
            }
        });
    }

    private void markItemsSynced() {
        //TODO... update the database, or..?
    }

    private JSONArray buildJson(ContentProviderClient provider) throws JSONException {
        JSONArray itemsArray = new JSONArray();

        Object[] items = null; //TODO get un-synced stuff from provider
        if (items == null) {
            return itemsArray;
        }
        for (Object item : items) {
            JSONObject jsonObject = buildItemJson(item);
            itemsArray.put(jsonObject);
        }
        return itemsArray;
    }

    private JSONObject buildItemJson(Object item) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("foo", 0);
        //TODO serialize the item

        return jsonObject;
    }

    /**
     * this function is 90% example code.
     * it's a hacky merger of https://developer.android.com/training/articles/security-ssl.html and
     * https://github.com/loopj/android-async-http/blob/master/sample/src/main/java/com/loopj/android/http/sample/CustomCASample.java
     * with my own shortcuts when I noticed I can just pass the keystore to apache's SSLSocketFactory instead of javax's.
     * I really hope this works properly.
      */
    private void addSslCert(SyncHttpClient httpClient) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {

        // Load CAs from an InputStream
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = getContext().getResources().openRawResource(R.raw.cert);
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            Log.d(TAG, "ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        SSLSocketFactory factory = new SSLSocketFactory(keyStore);
        httpClient.setSSLSocketFactory(factory);
    }
}
