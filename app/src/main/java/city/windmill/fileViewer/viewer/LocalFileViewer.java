package city.windmill.fileViewer.viewer;

import city.windmill.fileViewer.storage.IStorage;

public class LocalFileViewer extends FileViewer {
    public LocalFileViewer(IStorage storage) {
        super(storage);
    }
}
