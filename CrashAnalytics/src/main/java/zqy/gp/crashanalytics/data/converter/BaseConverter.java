package zqy.gp.crashanalytics.data.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BaseConverter<T> {

    private Gson buildGson() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }

    @TypeConverter
    public String convert(T data) {
        return buildGson().toJson(data);
    }

    @TypeConverter
    public T revert(String data) {
        return buildGson().fromJson(data, new TypeToken<T>() {
        }.getType());
    }

    @TypeConverter
    public String convertList(List<T> data) {
        return buildGson().toJson(data);
    }

    @TypeConverter
    public List<T> revertList(String data) {
        List<T> list = new ArrayList<>();
        Class<T> tClass = getTClass();
        JsonArray array = JsonParser.parseString(data).getAsJsonArray();
        for (final JsonElement element : array) {
            list.add(buildGson().fromJson(element, tClass));
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private Class<T> getTClass() {
        return (Class<T>) ((ParameterizedType) Objects.requireNonNull(getClass().getGenericSuperclass())).getActualTypeArguments()[0];
    }
}
