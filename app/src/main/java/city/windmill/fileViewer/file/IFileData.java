package city.windmill.fileViewer.file;

import android.graphics.drawable.Icon;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import city.windmill.fileViewer.ISavable;
import city.windmill.fileViewer.storage.IStorage;

public interface IFileData extends ISavable {
    Path getName();
    
    Path getPath();
    
    Icon getIcon(boolean thumbnail);
    
    FileTime getTimeStamp();
    
    long getContentSize();
    
    boolean isHidden();
    
    void onBindViewHolder(Fragment container, RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, RecyclerView.ViewHolder holder, int position);
    
    DirData getParent();
    
    IStorage getStorage();
}
