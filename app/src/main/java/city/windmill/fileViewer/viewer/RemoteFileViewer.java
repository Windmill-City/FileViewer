package city.windmill.fileViewer.viewer;

import android.os.Build;

import androidx.annotation.RequiresApi;

import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.file.FileType;
import city.windmill.fileViewer.file.IFileData;
import city.windmill.fileViewer.storage.RemoteStorage;

public class RemoteFileViewer extends FileViewer {
    private final RemoteStorage storage;

    public RemoteFileViewer(RemoteStorage storage) {
        super(storage);
        this.storage = storage;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void viewFileData(IFileData fileData) {
        adapter.setFileDataList(storage.getFiles(fileData));
        curDir = fileData.getType() == FileType.DIR ? (DirData) fileData : fileData.getParent();
    }
}
