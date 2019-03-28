package com.diveboard.dataaccess;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.SearchSpot;
import com.diveboard.model.SpotsDbUpdater;
import com.diveboard.util.Callback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchSpotOfflineRepository implements SearchSpotRepository {
    private final int userId;
    private ApplicationController context;

    public SearchSpotOfflineRepository(ApplicationController context) {
        this.context = context;
        userId = context.getModel().getUser().getId();
    }

    @Override
    public void search(String term, LatLng position, LatLngBounds bounds, Callback<List<SearchSpot>> callback, Callback<String> errorCallback) {
        ArrayList<SearchSpot> result = new ArrayList<>();
        if (errorCallback == null) {
            errorCallback = new Callback.Empty<>();
        }
        if (callback == null) {
            errorCallback = new Callback.Empty<>();
        }
        if (position != null) {
            position = new LatLng(position.latitude, position.longitude - Math.floor((position.longitude + 180.0) / 360.0) * 360.0);
        }
        SpotsDbUpdater updater = new SpotsDbUpdater(context);
        File spotsFile = updater.getSpotsFile();

        if (!spotsFile.exists()) {
            errorCallback.execute(context.getResources().getString(R.string.no_db));
            return;
        }
        SQLiteDatabase mDataBase = SQLiteDatabase.openDatabase(spotsFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
        if (mDataBase == null) {
            errorCallback.execute(context.getResources().getString(R.string.db_generic_error));
        }

        String whereClause = "(spots.private_user_id IS NULL OR spots.private_user_id = " + userId + ")";
        if (term != null) {
            String[] terms = term.split(" ");
            String matchString = "";
            for (int i = 0; i < terms.length; i++) {
                if (i != 0)
                    matchString += " ";
                String s = terms[i].trim();
                if (s.length() == 0) {
                    continue;
                }
                matchString += s + "*";
            }
            whereClause += " AND spots_fts.name MATCH '" + matchString + "'";
        }
        if (bounds != null) {
            if (bounds.southwest.longitude >= 0 && bounds.northeast.longitude < 0) {
                //TODO: надо вкурить
                whereClause += " AND (spots.lng BETWEEN " + bounds.southwest.longitude + " AND 180 AND SPOTS.lng BETWEEN 0 AND " + bounds.northeast.longitude + " AND spots.lat BETWEEN " + bounds.southwest.latitude + " AND " + bounds.northeast.latitude + ")";
            }
            whereClause += " AND (spots.lng BETWEEN " + bounds.southwest.longitude + " AND " + bounds.northeast.longitude + " AND spots.lat BETWEEN " + bounds.southwest.latitude + " AND " + bounds.northeast.latitude + ")";
        }
        if (position != null) {
            Double latitudeSqr = Math.pow(position.latitude, 2.0);
            whereClause += " ORDER BY ((spots.lat - " + position.latitude + ")*(spots.lat - " + position.latitude + ")) + (MIN((spots.lng - " + position.longitude + ")*(spots.lng - " + position.longitude + "), (spots.lng - " + position.longitude + " + 360)*(spots.lng - " + position.longitude + " + 360), (spots.lng - " + position.longitude + " - 360)*(spots.lng - " + position.longitude + " - 360))) * (1 - (((spots.lat * spots.lat) + " + latitudeSqr + ") / 8100)) ASC";
        }
        Cursor cursor = null;
        try {
            if (term == null) {
                cursor = mDataBase.query("spots", new String[]{"id", "name", "location_name", "country_name", "lat", "lng", "private_user_id"}, whereClause + " LIMIT 20", null, null, null, null);
            } else {
                cursor = mDataBase.rawQuery("SELECT spots_fts.docid, spots_fts.name, spots.location_name, spots.country_name, spots.lat, spots.lng FROM spots_fts, spots WHERE spots_fts.docid = spots.id AND " + whereClause + " LIMIT 30", null);
            }
            if (cursor.getCount() == 0) {
                result = new ArrayList<SearchSpot>();
            } else {
                while (cursor.moveToNext()) {
                    SearchSpot spot = new SearchSpot();
                    spot.id = cursor.getInt(0);
                    spot.name = cursor.getString(1);
                    spot.location = cursor.getString(2);
                    spot.cname = cursor.getString(3);
                    spot.lat = cursor.getDouble(4);
                    spot.lng = cursor.getDouble(5);
                    //region is missing for now
                    result.add(spot);
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            errorCallback.execute(context.getResources().getString(R.string.db_generic_error));
        } finally {
            cursor.close();
        }
        callback.execute(result);
    }
}
