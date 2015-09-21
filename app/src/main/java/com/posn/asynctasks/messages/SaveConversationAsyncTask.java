package com.posn.asynctasks.messages;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.posn.datatypes.ConversationMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SaveConversationAsyncTask extends AsyncTask<String, String, String>
   {
      private ProgressDialog pDialog;

      private Context context;
      private String filePath;

      private Map<String, ArrayList<ConversationMessage>>  conversationList;


      public SaveConversationAsyncTask(Context context, String filePath, Map<String, ArrayList<ConversationMessage>> conversationData)
         {
            super();
            this.context = context;
            this.filePath = filePath;

            this.conversationList = conversationData;
         }


      // Before starting background thread Show Progress Dialog
      @Override
      protected void onPreExecute()
         {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Saving Conversation...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
         }


      // Checking login in background
      protected String doInBackground(String... params)
         {
            System.out.println("SAVING CONVERSATION!!!");

            JSONArray messagesList = new JSONArray();

            try
               {
                  for (Map.Entry<String, ArrayList<ConversationMessage>> entry : conversationList.entrySet())
                     {
                        ArrayList<ConversationMessage> conversation = entry.getValue();

                        for (int i = 0; i < conversation.size(); i++)
                           {
                              ConversationMessage message = conversation.get(i);
                              messagesList.put(message.createJOSNObject());
                           }
                     }

                  JSONObject object = new JSONObject();
                  object.put("conversations", messagesList);

                  String jsonStr = object.toString();

                  FileWriter fw = new FileWriter(filePath);
                  BufferedWriter bw = new BufferedWriter(fw);
                  bw.write(jsonStr);
                  bw.close();

               }
            catch (JSONException | IOException e)
               {
                  e.printStackTrace();
               }

            return null;
         }


      // After completing background task Dismiss the progress dialog
      protected void onPostExecute(String file_url)
         {
            // dismiss the dialog once done
            pDialog.dismiss();
         }
   }