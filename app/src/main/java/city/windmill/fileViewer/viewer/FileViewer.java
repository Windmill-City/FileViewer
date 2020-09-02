package city.windmill.fileViewer.viewer;

import android.app.ListActivity;

import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.storage.IStorage;

public abstract class FileViewer extends ListActivity {
    protected DirData curDir;

    public FileViewer(IStorage storage) {
        curDir = storage.getLastDir();
    }
}
