package city.windmill.fileViewer.viewer;

import city.windmill.fileViewer.storage.IStorage;

public class RemoteFileViewer extends FileViewer {
    public RemoteFileViewer(IStorage storage) {
        super(storage);
    }
}
