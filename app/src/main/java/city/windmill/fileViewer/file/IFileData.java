package city.windmill.fileViewer.file;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import city.windmill.fileViewer.ISavable;

public interface IFileData extends ISavable {
    Path getName();

    Path getPath();

    FileTime getTimeStamp();

    boolean isHidden();

    DirData getParent();

    FileType getType();
}
