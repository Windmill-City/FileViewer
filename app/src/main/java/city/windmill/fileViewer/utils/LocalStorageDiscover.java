package city.windmill.fileViewer.utils;

import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.SDCardUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import city.windmill.fileViewer.storage.LocalStorage;

public class LocalStorageDiscover {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Set<LocalStorage> getLocalStorages() throws IOException {
        Set<LocalStorage> localStorages = new HashSet<>();
        //path of /storage/emulated/0.
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            localStorages.add(new LocalStorage("ExternalStorage", PathUtils.getExternalStoragePath()));
        if (SDCardUtils.isSDCardEnableByEnvironment()) {
            for (SDCardUtils.SDCardInfo info : SDCardUtils.getSDCardInfo()) {
                LogUtils.d("Found ExternalStorage:", info);
                String sdcardID = Paths.get(info.getPath()).getFileName().toString();
                localStorages.add(new LocalStorage("SDCard-" + sdcardID, info.getPath()));
            }
        }
        LogUtils.d("Found LocalStorage:", localStorages);
        return localStorages;
    }
}
