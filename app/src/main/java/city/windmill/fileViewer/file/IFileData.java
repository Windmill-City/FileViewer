package city.windmill.fileViewer.file;

import java.nio.file.Path;

import city.windmill.fileViewer.ISavable;

public interface IFileData extends ISavable {
    Path getName();

    Path getPath();

    DirData getParent();

    FileType getType();
}
