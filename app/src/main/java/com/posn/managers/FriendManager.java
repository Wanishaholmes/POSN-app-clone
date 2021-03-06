package com.posn.managers;

import android.os.Parcel;
import android.os.Parcelable;

import com.posn.constants.Constants;
import com.posn.datatypes.ApplicationFile;
import com.posn.datatypes.Friend;
import com.posn.datatypes.RequestedFriend;
import com.posn.exceptions.POSNCryptoException;
import com.posn.utility.SymmetricKeyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>This class creates a list of all current and requested/pending friends. The class uses an arraylist to hold the non-accepted friends and a hashmap for the accepted friends.
 *    Methods are included to read and write the data to and from a file.</p>
 * <p>Implements the methods defined by the ApplicationFile interface</p>
 * <p>Implements parcelable to easily pass this class between activities</p>
 **/
public class FriendManager implements Parcelable, ApplicationFile
   {
      public HashMap<String, Friend> currentFriends = new HashMap<>();
      public ArrayList<RequestedFriend> friendRequests = new ArrayList<>();


      public FriendManager()
         {

         }

      public Friend addNewAcceptedFriend(RequestedFriend friend, int status) throws POSNCryptoException
         {
            String symmetricKey = SymmetricKeyHelper.createRandomKey();
            Friend newFriend = new Friend(friend, symmetricKey, status);

            // remove request friend from friend request list and add to current friends list
            friendRequests.remove(friend);
            currentFriends.put(newFriend.ID, newFriend);

            return newFriend;
         }

      public void addNewPendingFriend(RequestedFriend friend)
         {
            friendRequests.add(friend);
         }

      public Friend getFriend(String friendID)
         {
            return currentFriends.get(friendID);
         }

      public void updateFriend(Friend friend)
         {
            currentFriends.put(friend.ID, friend);
         }


      @Override
      public void parseApplicationFileContents(String fileContents) throws JSONException
         {
            // load friends list into JSON object
            JSONObject data = new JSONObject(fileContents);

            // get array of friends
            JSONArray friendsList = data.getJSONArray("friends");

            // loop through array and parse individual friends
            for (int n = 0; n < friendsList.length(); n++)
               {
                  // parse the friend
                  Friend friend = new Friend();
                  friend.parseJSONObject(friendsList.getJSONObject(n));
                  currentFriends.put(friend.ID, friend);
               }

            JSONArray requestedFriendsList = data.getJSONArray("requests");
            for (int n = 0; n < requestedFriendsList.length(); n++)
               {
                  RequestedFriend friend = new RequestedFriend();
                  friend.parseJSONObject(requestedFriendsList.getJSONObject(n));
                  friendRequests.add(friend);
               }
         }

      @Override
      public String getDirectoryPath()
         {
            return Constants.applicationDataFilePath;
         }

      @Override
      public String getFileName()
         {
            return Constants.friendListFile;
         }

      @Override
      public String createApplicationFileContents() throws JSONException
         {
            Friend friend;
            RequestedFriend requestedFriend;

            JSONObject object = new JSONObject();
            JSONArray friendsList = new JSONArray();


            // add all of the friends in the current friends list into the JSON array
            for (Map.Entry<String, Friend> entry : currentFriends.entrySet())
               {
                  friend = entry.getValue();
                  friendsList.put(friend.createJSONObject());
               }
            object.put("friends", friendsList);


            // add all of the friends in the friend request list into the JSON array
            JSONArray requestedFriendsList = new JSONArray();

            for (int i = 0; i < friendRequests.size(); i++)
               {
                  requestedFriend = friendRequests.get(i);
                  requestedFriendsList.put(requestedFriend.createJSONObject());
               }
            object.put("requests", requestedFriendsList);

            // create new JSON object and put the JSON array into it

            return object.toString();
         }


      // Parcelling part
      public FriendManager(Parcel in)
         {
            // get requested friends
            friendRequests = in.readArrayList(RequestedFriend.class.getClassLoader());

            //initialize your map before
            int size = in.readInt();
            for (int i = 0; i < size; i++)
               {
                  String key = in.readString();
                  Friend value = in.readParcelable(Friend.class.getClassLoader());
                  currentFriends.put(key, value);
               }
         }


      @Override
      public void writeToParcel(Parcel dest, int flags)
         {
            dest.writeList(friendRequests);

            dest.writeInt(currentFriends.size());
            for (Map.Entry<String, Friend> entry : currentFriends.entrySet())
               {
                  dest.writeString(entry.getKey());
                  dest.writeParcelable(entry.getValue(), flags);
               }
         }

      public static final Parcelable.Creator<FriendManager> CREATOR = new Parcelable.Creator<FriendManager>()
         {
            public FriendManager createFromParcel(Parcel in)
               {
                  return new FriendManager(in);
               }

            public FriendManager[] newArray(int size)
               {
                  return new FriendManager[size];
               }
         };

      @Override
      public int describeContents()
         {
            return 0;
         }
   }
