package city.windmill.fileViewer.file;

import android.os.Build;
import android.util.JsonReader;
import android.util.JsonWriter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

public class FileData implements IFileData {
    public final DirData parent;
    public Path name;
    public Path path;
    public FileTime timeStamp;
    public boolean isHidden;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public FileData(DirData parent, Path name) {
        this.parent = parent;
        this.name = name;
        //Consider name as path when parent null
        path = parent != null ? parent.getPath().resolve(name) : name;
        try {
            timeStamp = Files.getLastModifiedTime(path);
            isHidden = Files.isHidden(path);
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    public FileData(@NonNull DirData parent) {
        this.parent = parent;
    }

    //region IFileData
    @Override
    public Path getName() {
        return name;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public FileTime getTimeStamp() {
        return timeStamp;
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
    public FileType getType() {
        return FileType.FILE;
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
    //endregion

    //region Object
    @NonNull
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileData fileData = (FileData) o;
        return getPath().equals(fileData.getPath());
    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }

    //endregion
}
