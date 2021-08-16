package zqy.gp.crashanalytics.data.converter;

import android.os.Bundle;
import android.util.Log;

import androidx.room.TypeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

public class BundleConverter extends BaseConverter<Bundle> {

    @TypeConverter
    @Override
    public String convert(Bundle data) {
        JSONObject jsonObject = new JSONObject();
        Set<String> keys = data.keySet();
        for (String key : keys) {
            try {
                jsonObject.put(key, data.get(key));
            } catch (JSONException ignored) {
            }
        }
        return jsonObject.toString();
    }

    @TypeConverter
    @Override
    public Bundle revert(String data) {
        Bundle bundle = new Bundle();
        try {
            JSONObject jsonObject = new JSONObject(data);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = String.valueOf(jsonObject.get(key));
                bundle.putString(key, value);
            }
        } catch (JSONException ignored) {
        }
        return bundle;
    }
}
