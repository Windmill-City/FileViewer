package city.windmill.fileViewer.storage;

import android.os.Build;
import android.util.JsonReader;
import android.util.JsonWriter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.List;

import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.file.FileType;
import city.windmill.fileViewer.file.IFileData;
import city.windmill.fileViewer.utils.MacAddress;

public class RemoteStorage extends LocalStorage {
    public boolean isConnected;
    protected MacAddress mac;

    public void Connect() {
    }

    //region IStorage
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<IFileData> getFiles(IFileData data) {
        return data.getType() == FileType.DIR ? ((DirData)data).getFiles() : data.getParent().getFiles();
    }

    @Override
    public void onSave(JsonWriter jsonWriter) throws IOException {
        super.onSave(jsonWriter);
        root.onSave(jsonWriter);
        jsonWriter.value(mac.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLoad(JsonReader jsonReader) throws IOException {
        super.onLoad(jsonReader);
        root.onLoad(jsonReader);
        mac = new MacAddress(jsonReader.nextString());
    }
    //endregion


    @NonNull
    @Override
    public String toString() {
        return String.format("RemoteStorage: Name:%s root:%s lastDirData:%s mac:%s", name, root, lastDir, mac);
    }
}
