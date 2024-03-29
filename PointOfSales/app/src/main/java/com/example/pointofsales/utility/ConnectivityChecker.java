package com.example.pointofsales.utility;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Utility class to check if there's internet connection
 */
public class ConnectivityChecker extends AsyncTask<Void, Void, Boolean> {

    private static final String host = "8.8.8.8";
    private static final int port = 53;
    private static final int timeout = 1500;

    private ConnectivityCheckerInterface mConnectivityCheckerInterface;

    public ConnectivityChecker(ConnectivityCheckerInterface connectivityCheckerInterface) {
        mConnectivityCheckerInterface = connectivityCheckerInterface;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {

            // Ping the Google DNS to check if the internet connection is valid
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.close();

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        // Callback on result
        mConnectivityCheckerInterface.isInternetAvailable(aBoolean);
    }
}
