package city.windmill.fileViewer.viewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.Utils;
import com.windmill.FileViewer.R;

import java.util.ArrayList;
import java.util.List;

import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.file.IFileData;
import city.windmill.fileViewer.storage.IStorage;

public abstract class FileViewer extends Fragment {
    @Nullable
    protected DirData curDir;
    protected RecyclerView files;
    protected FileDataItemAdapter adapter = new FileDataItemAdapter();

    public FileViewer(IStorage storage) {
        curDir = storage.getLastDir();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fileviewer, container, false);
        files = view.findViewById(R.id.View_Files);
        files.setLayoutManager(new LinearLayoutManager(Utils.getApp().getApplicationContext()));
        files.setAdapter(adapter);
        return view;
    }

    public abstract void viewFileData(IFileData fileData);

    class FileDataItemAdapter extends RecyclerView.Adapter<FileDataItemAdapter.ViewHolder> {
        private List<IFileData> fileDataList = new ArrayList<>();

        public void setFileDataList(List<IFileData> fileDataList) {
            this.fileDataList = fileDataList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_filedataitem, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.FileName.setText(fileDataList.get(position).getName().toString());
        }

        @Override
        public int getItemCount() {
            return fileDataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView FileName;
            ImageView FileIcon;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                FileName = itemView.findViewById(R.id.FileName);
                FileIcon = itemView.findViewById(R.id.FileIcon);
            }
        }
    }
}
