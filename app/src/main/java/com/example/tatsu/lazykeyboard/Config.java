package com.example.tatsu.lazykeyboard;

import android.os.Environment;
import android.util.Log;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {
    private static final String TAG = "Config";
    private int mDelay;
    private final List<Credential> mCredentials = new ArrayList<>();

    private Config() throws IOException {
        File file = getPath();
        Log.d(TAG, "Loading " + file.getAbsolutePath());
        Map rootMap;
        try (FileInputStream stream = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            rootMap = yaml.load(stream);
        }
        Object delay = rootMap.get("delay");
        if (delay instanceof Integer) {
            mDelay = (int) delay;
        }
        if (mDelay < 50) {
            mDelay = 50;
        }
        if (mDelay > 5000) {
            mDelay = 5000;
        }
        String globalPassword = (String) rootMap.get("password");
        List members = (List) rootMap.get("members");
        for (Object member : members) {
            Map memberMap = (Map) member;
            String name = (String) memberMap.get("name");
            String email = (String) memberMap.get("email");
            String password = (String) memberMap.get("password");
            String info = (String) memberMap.get("description");
            if (password == null) {
                password = globalPassword;
            }
            mCredentials.add(new Credential(name, info, email, password));
//            Log.d(TAG, String.format("%s : %s (%s) [%s]", name, email, password, info));
        }
    }

    public int getDelay() {
        return mDelay;
    }

    public Credential[] getCredentials() {
        return mCredentials.toArray(new Credential[0]);
    }

    public static File getPath() throws IOException {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            return new File(Environment.getExternalStorageDirectory().getPath() + "/teretere.yml");
        }
        throw new IOException("External storage is not available");
    }

    public static Config load() throws IOException {
        return new Config();
    }
}
