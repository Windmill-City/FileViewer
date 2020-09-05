package city.windmill.fileViewer.viewer;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;

import city.windmill.fileViewer.file.IFileData;

public interface IViewer {
    RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getAdapter();
    
    void viewData(IFileData data) throws IOException;
}
