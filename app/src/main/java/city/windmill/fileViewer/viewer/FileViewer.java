package city.windmill.fileViewer.viewer;

import android.os.Bundle;
import android.view.KeyEvent;
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

import java.util.List;

import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.file.FileDataHolder;
import city.windmill.fileViewer.file.IFileData;
import city.windmill.fileViewer.storage.IStorage;

public abstract class FileViewer extends Fragment {
    @Nullable
    protected DirData curDir;
    protected RecyclerView files;
    protected FileDataItemAdapter adapter = new FileDataItemAdapter();
    protected IStorage storage;

    public FileViewer(IStorage storage) {
        this.storage = storage;
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

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK && curDir != null && curDir.getParent() != null) {
                viewFileData(curDir.getParent());
                return true;
            }
            return false;
        });
    }

    public abstract void viewFileData(IFileData fileData);

    class FileDataItemAdapter extends RecyclerView.Adapter<FileDataItemAdapter.ViewHolder> {
        private FileDataHolder fileDataHolder;

        public void setFileDataList(List<IFileData> fileDataList) {
            fileDataHolder = new FileDataHolder(fileDataList);
            fileDataHolder.showHidden = true;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_filedataitem, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.fileData = fileDataHolder.get(position);
            holder.FileName.setText(holder.fileData.getName().toString());
            holder.view.setOnClickListener(v -> viewFileData(holder.fileData));
        }

        @Override
        public int getItemCount() {
            if (fileDataHolder.getDataSize() == 0)
                getView().findViewById(R.id.EmptyFolder).setVisibility(View.VISIBLE);
            else
                getView().findViewById(R.id.EmptyFolder).setVisibility(View.INVISIBLE);
            return fileDataHolder.getDataSize();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View view;
            TextView FileName;
            ImageView FileIcon;
            IFileData fileData;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                view = itemView;
                FileName = itemView.findViewById(R.id.FileName);
                FileIcon = itemView.findViewById(R.id.FileIcon);
            }
        }
    }
}
