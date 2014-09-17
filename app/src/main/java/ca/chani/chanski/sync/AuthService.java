package ca.chani.chanski.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by chani on 2014-03-03.
 * 99% boilerplate sync crud.
 */
public class AuthService extends Service {
    private Authenticator mAuthenticator;
    @Override
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
