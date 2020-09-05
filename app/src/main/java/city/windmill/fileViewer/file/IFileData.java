package city.windmill.fileViewer.file;

import android.graphics.drawable.Icon;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import city.windmill.fileViewer.ISavable;
import city.windmill.fileViewer.storage.IStorage;

public interface IFileData extends ISavable {
    Path getName();
    
    Path getPath();
    
    Icon getIcon();
    
    FileTime getTimeStamp();
    
    long getContentSize();
    
    boolean isHidden();
    
    DirData getParent();
    
    IStorage getStorage();
}
