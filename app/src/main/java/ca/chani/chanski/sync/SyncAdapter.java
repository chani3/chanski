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
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
            return;
        }

        JSONObject postData = new JSONObject();
        postData.put("data", dataArray);
        Log.e(TAG, "json: " + postData.toString());

        httpClient = new SyncHttpClient();
        httpClient.addHeader("Authorization", token);

        String url = "https://chani.ca/TODO";
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
}
