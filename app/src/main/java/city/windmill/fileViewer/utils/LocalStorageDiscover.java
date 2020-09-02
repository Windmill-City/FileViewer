package city.windmill.fileViewer.utils;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import city.windmill.fileViewer.storage.LocalStorage;

public class LocalStorageDiscover {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<LocalStorage> getLocalStorages() {
        List<LocalStorage> localStorages = new ArrayList<>();
        localStorages.add(new LocalStorage("ExternalStorage", Environment.getExternalStorageDirectory().toPath()));
        return localStorages;
    }
}
