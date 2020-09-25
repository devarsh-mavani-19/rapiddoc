package com.jayshreegopalapps.imagetopdf;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OfficeToPDFTask extends AsyncTask<Uri, Void, String> {
    AlertDialog dialog;
    Context context;
    OfficeToPDFTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new AlertDialog.Builder(context).setView(R.layout.layout_loading_dialog).setCancelable(false).create();
        dialog.show();
    }

    @Override
    protected String doInBackground(Uri... strings) {

        try {
            String authJson = "{" +
                    "\"public_key\":\"" + "project_public_68df87ff7ab9dbdb0b5520c79bd96305_8_Y-Uf95288c31b29a96ea5940b468a748171\"" +
                    "}";

            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES).build();
            String authUrl = "https://api.ilovepdf.com/v1/auth";
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), authJson);
            Request request = new Request.Builder().url(authUrl).post(body).build();
            Response authResp = client.newCall(request).execute();
            JSONObject token = new JSONObject(authResp.body().string());
            String tokenString = token.getString("token");

            String startUrl = "https://api.ilovepdf.com/v1/start/officepdf";
            Request request1 = new Request.Builder().url(startUrl).header("Cache-Control", "no-cache").header("Authorization", "Bearer " + tokenString).build();
            client.newCall(request1).execute();
            Response startResponse = client.newCall(request1).execute();
            JSONObject startJSON =new JSONObject(startResponse.body().string());
            String task = startJSON.getString("task");
            String server = startJSON.getString("server");

            String uploadUrl = "https://" + server + "/v1/upload";

//            File file = new File(strings[0]);
            FileInputStream fis = (FileInputStream) context.getContentResolver().openInputStream(strings[0]);
            byte[] bytes = new byte[(int) fis.getChannel().size()];
            fis.read(bytes);
            RequestBody uploadBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", strings[0].toString(),
                            RequestBody.create(MediaType.parse("application/pdf"),  bytes))
                    .addFormDataPart("task", task).addFormDataPart("file", "filename=" + strings[0])
                    .build();
            Request request2 = new Request.Builder().url(uploadUrl).post(uploadBody).header("Authorization" , "Bearer " + tokenString).header("cache-control", "no-cache").header("Content-Type", "multipart/form-data;").build();
            Response uploadResponse = client.newCall(request2).execute();
            JSONObject uploadResp = new JSONObject(uploadResponse.body().string());
            String returnFileName = uploadResp.getString("server_filename");

            String processurl = "https://"+ server +"/v1/process";
            RequestBody processBodu = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("tool", "officepdf")
                    .addFormDataPart("Postman-Token", "db647d2d-3db9-347d-fdd4-94ff46534630")
                    .addFormDataPart("files[0][server_filename]", returnFileName)
                    .addFormDataPart("files[0][filename]", strings[0].toString())
                    .addFormDataPart("output_filename", strings[0].toString())
                    .addFormDataPart("optimize", "true")
                    .addFormDataPart("task", task)
                    .build();
            Request processRequest = new Request.Builder().header("Content-Type", " multipart/form-data;").header("Cache-Control", "no-cache").header("Authorization", "Bearer " + tokenString).url(processurl).post(processBodu).build();
            Response processResp = client.newCall(processRequest).execute();
            JSONObject processJSON = new JSONObject(processResp.body().string());

            if(processJSON.getString("status").equals("TaskSuccess")) {
                String downloadUrl = "https://" + server + "/v1/download/" + task;

                Request downloadReq = new Request.Builder().header("Authorization", "Bearer " + tokenString).url(downloadUrl).build();
                Response downloadResp = client.newCall(downloadReq).execute();
                String sfile = Constants.OFFICETOPDF + System.currentTimeMillis() + ".pdf";
                File f = new File(sfile);
                System.out.println(sfile);
                if(!f.exists()) {
                    f.createNewFile();
                }
                byte[] op = downloadResp.body().bytes();
                FileOutputStream stream = new FileOutputStream(sfile);
                stream.write(op);
                stream.close();
                return sfile;
            }
            else{
                return null;
            }

           
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        if(aVoid==null) {
            Toast.makeText(context, "Failed to convert File to pdf", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "File Saved at " + aVoid, Toast.LENGTH_SHORT).show();
        }
    }
}
