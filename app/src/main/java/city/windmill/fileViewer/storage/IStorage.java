package city.windmill.fileViewer.storage;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import city.windmill.fileViewer.ISavable;
import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.file.IFileData;

public interface IStorage extends ISavable {
    String getName();

    DirData getRoot();

    @Nullable
    DirData getLastDir();

    List<IFileData> getFiles(IFileData data) throws IOException;
}
