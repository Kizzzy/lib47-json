package cn.kizzzy.vfs.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonFileHandler<T> extends TextFileHandler<T> {
    
    private final Class<T> clazz;
    
    public JsonFileHandler(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    protected T loadImpl(String s) {
        return new Gson().fromJson(s, clazz);
    }
    
    @Override
    protected String saveImpl(T t) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(t);
    }
}
