package city.windmill.fileViewer.storage;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.file.FileData;
import city.windmill.fileViewer.file.IFileData;

public interface IStorage {
    String getName();
    
    DirData getRoot();
    
    @Nullable
    DirData getLastDir();
    
    void addContent(IFileData data) throws IOException;
    
    void deleteContent(IFileData data) throws IOException;
    
    void moveContent(IFileData data, Path newPath, boolean keepSource, CopyOption... options) throws IOException;
    
    List<IFileData> getContents(DirData data, DirectoryStream.Filter<Path> filter, Comparator<IFileData> sorter) throws IOException;
    
    byte[] getFile(FileData data) throws IOException;
}
