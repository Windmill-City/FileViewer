package city.windmill.fileViewer.storage;

import android.os.Build;
import android.util.JsonReader;
import android.util.JsonWriter;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.file.FileData;
import city.windmill.fileViewer.file.FileType;
import city.windmill.fileViewer.file.IFileData;

public class LocalStorage implements IStorage {
    public String name;
    public DirData root;
    public DirData lastDir;

    public LocalStorage() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalStorage(String name, Path root) {
        this.name = name;
        this.root = new DirData(null, root);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalStorage(String name, String root) {
        this(name, Paths.get(root));
    }

    //region IStorage
    @Override
    public String getName() {
        return name;
    }

    @Override
    public DirData getRoot() {
        return root;
    }

    @Nullable
    @Override
    public DirData getLastDir() {
        return lastDir;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<IFileData> getFiles(final IFileData data) throws IOException {
        final DirData dirData = data.getType() == FileType.DIR ? (DirData) data : data.getParent();
        final List<IFileData> fileData = new ArrayList<>();
        Files.walkFileTree(dirData.getPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                fileData.add(new DirData(dirData, dir.getFileName()));
                return FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                fileData.add(new FileData(dirData, file.getFileName()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });
        return fileData;
    }

    @Override
    public void onSave(JsonWriter jsonWriter) throws IOException {
        jsonWriter.value(name);
        jsonWriter.value(root.getPath().toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLoad(JsonReader jsonReader) throws IOException {
        name = jsonReader.nextString();
        root = new DirData(null, Paths.get(jsonReader.nextString()));
    }
    //endregion

    @Override
    public String toString() {
        return "LocalStorage{" +
                "name='" + name + '\'' +
                ", root=" + root +
                ", lastDir=" + lastDir +
                '}';
    }

    @Override
    public int hashCode() {
        return root.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj == this || (obj instanceof LocalStorage && ((LocalStorage) obj).getRoot().equals(getRoot()));
    }

    //endregion
}
