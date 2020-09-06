package city.windmill.fileViewer.storage;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.file.FileData;
import city.windmill.fileViewer.file.IFileData;

public class LocalStorage implements IStorage {
    public String name;
    public DirData root;
    public DirData lastDir;
    
    public LocalStorage() {
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalStorage(String name, String root) throws IOException {
        this(name, Paths.get(root), Files.getLastModifiedTime(Paths.get(root)));
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalStorage(String name, Path root, FileTime timeStamp) {
        this.name = name;
        this.root = new DirData(this, null, root, timeStamp, 0, false);
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
    public void addContent(IFileData data) throws IOException {
        if (data instanceof DirData)
            Files.createDirectory(data.getPath());
        else
            Files.createFile(data.getPath());
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void deleteContent(IFileData data) throws IOException {
        Files.delete(data.getPath());
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void moveContent(IFileData data, String newName, CopyOption copyOption, boolean keepSource) throws IOException {
        Path dest = data.getParent().getPath().resolve(data.getName());
        if (keepSource)
            Files.copy(data.getPath(), dest, copyOption);
        else
            Files.move(data.getPath(), dest, copyOption);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public List<IFileData> getContents(final DirData dirData, DirectoryStream.Filter<Path> filter, Comparator<IFileData> sorter) throws IOException {
        final List<IFileData> fileData = new ArrayList<>();
        IStorage storage = this;
        Files.walkFileTree(dirData.getPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir == dirData.getPath()) return FileVisitResult.CONTINUE;
                if (filter.accept(dir)) {
                    Stream<Path> paths = Files.list(dir);
                    fileData.add(new DirData(storage, dirData, dir.getFileName(), Files.getLastModifiedTime(dir), paths.count(), Files.isHidden(dir)));
                    paths.close();
                }
                return FileVisitResult.SKIP_SUBTREE;
            }
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (filter.accept(file))
                    fileData.add(new FileData(storage, dirData, file.getFileName(), Files.getLastModifiedTime(file), Files.size(file), Files.isHidden(file)));
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });
        fileData.sort(sorter);
        return fileData;
    }
    
    @Override
    public byte[] getFile(FileData data) throws IOException {
        return null;
    }
    
    //endregion
    
    //region Object
    @Override
    public int hashCode() {
        return root.hashCode();
    }
    
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj == this || (obj instanceof LocalStorage && ((LocalStorage) obj).getRoot().equals(getRoot()));
    }
    
    @Override
    public String toString() {
        return "LocalStorage{" +
                "name='" + name + '\'' +
                ", root=" + root +
                ", lastDir=" + lastDir +
                '}';
    }
    
    //endregion
}
