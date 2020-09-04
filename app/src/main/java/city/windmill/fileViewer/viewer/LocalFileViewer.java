package city.windmill.fileViewer.viewer;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;

import java.io.IOException;

import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.file.FileType;
import city.windmill.fileViewer.file.IFileData;
import city.windmill.fileViewer.storage.LocalStorage;

public class LocalFileViewer extends FileViewer {
    private LocalStorage storage;

    public LocalFileViewer(LocalStorage storage) {
        super(storage);
        this.storage = storage;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void viewFileData(IFileData fileData) {
        LogUtils.d("View FileData:", fileData);
        try {
            adapter.setFileDataList(storage.getFiles(fileData));
            curDir = fileData.getType() == FileType.DIR ? (DirData) fileData : fileData.getParent();
            storage.lastDir = curDir;
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }


}
