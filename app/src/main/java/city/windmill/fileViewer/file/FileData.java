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

public class FileData implements IFileData {
    @NonNull
    public final DirData parent;
    public Path name;
    public Path path;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public FileData(DirData parent, Path name) {
        this.parent = parent;
        this.name = name;
        path = parent.getPath().resolve(name);
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

    @NonNull
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
        return String.format("FileData: Name:%s Path:%s Parent%s", name, path, parent);
    }

    @Override
    public final int hashCode() {
        return getPath().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj == this || (obj instanceof DirData && ((DirData)obj).getPath().equals(getPath()));
    }
    //endregion
}
