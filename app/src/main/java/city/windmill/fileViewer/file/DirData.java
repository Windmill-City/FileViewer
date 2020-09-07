package city.windmill.fileViewer.file;

import android.annotation.SuppressLint;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.JsonReader;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.Utils;
import com.windmill.FileViewer.R;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import city.windmill.fileViewer.storage.IStorage;
import city.windmill.fileViewer.viewer.DirViewer.DirAdapter.DirViewHolder;

public class DirData extends FileData {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public DirData(IStorage storage, DirData parent, Path name, FileTime timeStamp, long contentSize, boolean isHidden) {
        super(storage, parent, name, timeStamp, contentSize, isHidden);
    }
    
    public DirData(IStorage storage, @Nullable DirData parent) {
        super(storage, parent);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLoad(JsonReader jsonReader) throws IOException {
        name = Paths.get(jsonReader.nextString());
        //consider name as path when parent null
        path = parent != null ? parent.getPath().resolve(name) : name;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Icon getIcon(boolean thumbnail) {
        return Icon.createWithResource(Utils.getApp().getApplicationContext(), R.drawable.ic_folder);
    }
    
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(Fragment container, RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(container, adapter, holder, position);
        DirViewHolder dirViewHolder = (DirViewHolder) holder;
        dirViewHolder.FileSize.setText(String.format("%d Item", getContentSize()));
    }
}
