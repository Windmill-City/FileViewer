package city.windmill.fileViewer.file;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.Utils;
import com.windmill.FileViewer.R;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import city.windmill.fileViewer.storage.IStorage;
import city.windmill.fileViewer.viewer.DirViewer;
import city.windmill.fileViewer.viewer.ViewerFactory;

public class FileData implements IFileData {
    public final IStorage storage;
    public DirData parent;
    public Path name;
    public Path path;
    public FileTime timeStamp;
    public boolean isHidden;
    public long contentSize;
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public FileData(IStorage storage, DirData parent, Path name, FileTime timeStamp, long contentSize, boolean isHidden) {
        this.storage = storage;
        this.parent = parent;
        this.name = name;
        //Consider name as path when parent null
        path = parent != null ? parent.getPath().resolve(name) : name;
        this.timeStamp = timeStamp;
        this.isHidden = isHidden;
        this.contentSize = contentSize;
    }
    
    public FileData(IStorage storage, @NonNull DirData parent) {
        this.storage = storage;
        this.parent = parent;
    }
    
    @Override
    public void onSave(JsonWriter jsonWriter) throws IOException {
        jsonWriter.value(name.toString());
    }
    
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLoad(JsonReader jsonReader) throws IOException {
        name = Paths.get(jsonReader.nextString());
        path = parent.getPath().resolve(name);
    }
    
    @Override
    public int hashCode() {
        return getPath().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileData fileData = (FileData) o;
        return getPath().equals(fileData.getPath());
    }
    
    @Override
    public String toString() {
        return "FileData{" +
                "parent=" + parent +
                ", name=" + name +
                ", path=" + path +
                ", timeStamp=" + timeStamp +
                ", isHidden=" + isHidden +
                '}';
    }
    
    @SuppressLint("DefaultLocale")
    private String byteCount2Str(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size > kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }
    
    
    @Override
    public boolean isHidden() {
        return isHidden;
    }
    
    
    @Override
    public DirData getParent() {
        return parent;
    }
    
    @Override
    public IStorage getStorage() {
        return storage;
    }
    
    
    @Override
    public Path getName() {
        return name;
    }
    
    
    @Override
    public Path getPath() {
        return path;
    }
    
    
    @Override
    public Icon getIcon(boolean thumbnail) {
        if (thumbnail && ImageUtils.isImage(getPath().toFile())) {
            int[] size = ImageUtils.getSize(getPath().toFile());
            float scale = 128F / Math.max(size[0], size[1]);
            Size size_thumbnail = new Size((int) (size[0] * scale), (int) (size[1] * scale));
            Bitmap bitmap = BitmapFactory.decodeFile(getPath().toString());
            bitmap = Bitmap
                    .createScaledBitmap(bitmap, size_thumbnail.getWidth(), size_thumbnail.getHeight(), true);
            return Icon.createWithBitmap(bitmap);
        }
        int resId = ImageUtils.isImage(getPath().toFile()) ? R.drawable.ic_image : R.drawable.ic_file;
        return Icon.createWithResource(Utils.getApp().getApplicationContext(), resId);
    }
    
    @Override
    public FileTime getTimeStamp() {
        return timeStamp;
    }
    
    @Override
    public long getContentSize() {
        return contentSize;
    }
    
    @Override
    public void onBindViewHolder(Fragment container, RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DirViewer.DirAdapter.DirViewHolder) {
            DirViewer.DirAdapter.DirViewHolder dirViewHolder = (DirViewer.DirAdapter.DirViewHolder) holder;
            dirViewHolder.FileName.setText(getName().toString());
            dirViewHolder.FileIcon.setImageIcon(getIcon(true));
            dirViewHolder.FileTimeStamp.setText(getTimeStamp().toString());
            dirViewHolder.FileSize.setText(byteCount2Str(getContentSize()));
            dirViewHolder.view.setOnClickListener(v -> {
                ViewerFactory.INSTANCE.viewFileData(this, container);
            });
        }
    }
}
