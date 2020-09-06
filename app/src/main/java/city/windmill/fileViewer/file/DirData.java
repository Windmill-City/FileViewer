package city.windmill.fileViewer.file;

import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.JsonReader;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.Utils;
import com.windmill.FileViewer.R;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import city.windmill.fileViewer.storage.IStorage;

public class DirData extends FileData {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public DirData(IStorage storage, DirData parent, Path name, FileTime timeStamp, long contentSize, boolean isHidden) {
        super(storage, parent, name, timeStamp, contentSize, isHidden);
    }
    
    public DirData(IStorage storage, @Nullable DirData parent) {
        super(storage, parent);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Icon getIcon() {
        return Icon.createWithResource(Utils.getApp().getApplicationContext(), R.drawable.ic_folder);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLoad(JsonReader jsonReader) throws IOException {
        name = Paths.get(jsonReader.nextString());
        //consider name as path when parent null
        path = parent != null ? parent.getPath().resolve(name) : name;
    }
}
