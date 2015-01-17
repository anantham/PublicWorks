package com.vishnu.aditya.sanitationreview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class EmployeeSender extends AsyncTask<String, Void, Boolean> {

    String BASE_URL = "http://5313ee71.ngrok.com/admin-panel-sanitation/checkDatabase.php/?key=";
    private Context context;
    ProgressDialog prog;

    public EmployeeSender(Context loginContext) {
        this.context = loginContext;
    }

    @Override
    protected void onPreExecute() {
        prog = new ProgressDialog(context);
        prog.setMessage("Verifying with server");
        prog.show();
    }

    @Override
    protected Boolean doInBackground(String... empID) {

        // Ensure URL is of proper form
        try {
            URL url = new URL(BASE_URL+empID[0]);
            Log.i("the empId",empID[0]);

            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();

            String encoding = connection.getContentEncoding();
            if (encoding == null) encoding = "UTF-8";

            ByteArrayOutputStream outputByteByByte = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int len;
            try {
                // Read the inputStream using the buffer
                while ((len = inputStream.read(buffer)) != -1) {
                    // write what you get to the outputByteByByte variable
                    outputByteByByte.write(buffer, 0, len);
                }

                String serverResponse = new String(outputByteByByte.toByteArray(), encoding);
                Log.i("the server response",serverResponse);

                if (serverResponse.equals("1")) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }

            } catch (IOException e) {
                Log.i("IOException", "buffer to outputByteByByte");
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            Log.i("MalformedURLException", "URL not in proper format");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("IOException", "connection with server");
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void onPostExecute(Boolean result){
        super.onPostExecute(result);
        prog.dismiss();

        if(result == Boolean.TRUE){
            Intent postLoginIntent = new Intent();
            postLoginIntent.setClass(context,LocationFixer.class);
            context.startActivity(postLoginIntent);
        }
        else{
            Toast.makeText(context,"Sorry no volunteer corresponding to this ID exists",Toast.LENGTH_SHORT).show();
        }
    }

}