package city.windmill.fileViewer.storage;

import android.os.Build;
import android.util.JsonReader;
import android.util.JsonWriter;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;

import city.windmill.fileViewer.utils.MacAddress;

public class RemoteStorage extends LocalStorage {
    protected MacAddress mac;
    
    public void Connect() {
    }
    
    //region IStorage
    @Override
    public void onSave(JsonWriter jsonWriter) throws IOException {
        super.onSave(jsonWriter);
        jsonWriter.value(mac.toString());
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLoad(JsonReader jsonReader) throws IOException {
        super.onLoad(jsonReader);
        mac = new MacAddress(jsonReader.nextString());
    }
    
    @Override
    public int hashCode() {
        return mac.hashCode();
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj == this || (obj instanceof RemoteStorage && ((RemoteStorage) obj).mac.equals(mac));
    }
    
    //endregion
    
    //region Object
    @Override
    public String toString() {
        return "RemoteStorage{" +
                "mac=" + mac +
                ", name='" + name + '\'' +
                ", root=" + root +
                ", lastDir=" + lastDir +
                '}';
    }
    
    //endregion
}
