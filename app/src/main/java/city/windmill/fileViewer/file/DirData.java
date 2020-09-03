package city.windmill.fileViewer.file;

import android.os.Build;
import android.util.JsonReader;
import android.util.JsonWriter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DirData implements IFileData {
    @Nullable
    public final DirData parent;
    public final List<IFileData> fileData = new ArrayList<>();
    public Path name;
    public Path path;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DirData(@Nullable DirData parent, Path name) {
        this.parent = parent;
        //when parent is null, consider name is the path
        this.name = parent != null ? name : name.getFileName();
        path = parent != null ? parent.getPath().resolve(name) : name;
    }

    public DirData(@NonNull DirData parent) {
        this.parent = parent;
    }

    public List<IFileData> getFiles() {
        return fileData;
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

    @Nullable
    @Override
    public DirData getParent() {
        return parent;
    }

    @Override
    public FileType getType() {
        return FileType.DIR;
    }

    @Override
    public void onSave(JsonWriter jsonWriter) throws IOException {
        jsonWriter.value(name.toString());
        jsonWriter.beginArray();
        for (IFileData data : fileData) {
            jsonWriter.value(data.getType() == FileType.DIR);
            data.onSave(jsonWriter);
        }
        jsonWriter.endArray();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLoad(JsonReader jsonReader) throws IOException {
        fileData.clear();
        name = Paths.get(jsonReader.nextString());
        path = getParent() != null ? getParent().getPath().resolve(name) : name;
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            IFileData data;
            if (jsonReader.nextBoolean())
                data = new DirData(this);
            else
                data = new FileData(this);
            data.onLoad(jsonReader);
            fileData.add(data);
        }
        jsonReader.endArray();
    }
    //endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirData dirData = (DirData) o;
        return getPath().equals(dirData.getPath());
    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }

    @Override
    public String toString() {
        return "DirData{" +
                "parent=" + parent +
                ", fileData=" + fileData +
                ", name=" + name +
                ", path=" + path +
                '}';
    }
    //endregion
}
