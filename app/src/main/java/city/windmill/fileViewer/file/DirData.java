package city.windmill.fileViewer.file;

import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.Utils;
import com.windmill.FileViewer.R;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import city.windmill.fileViewer.storage.IStorage;

public class DirData extends FileData {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public DirData(IStorage storage, DirData parent, Path name, FileTime timeStamp, long contentSize, boolean isHidden) {
        super(storage, parent, name, timeStamp, contentSize, isHidden);
    }
    
    public DirData(IStorage storage, @NonNull DirData parent) {
        super(storage, parent);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Icon getIcon() {
        return Icon.createWithResource(Utils.getApp().getApplicationContext(), R.drawable.ic_folder);
    }
}
