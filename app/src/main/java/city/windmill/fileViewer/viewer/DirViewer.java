package city.windmill.fileViewer.viewer;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.Utils;
import com.windmill.FileViewer.R;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.file.IFileData;

public class DirViewer extends Fragment implements IViewer {
    private DirAdapter adapter = new DirAdapter();
    private boolean showHidden = false;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dirviewer, container, false);
        RecyclerView files = view.findViewById(R.id.View_Files);
        files.setLayoutManager(new LinearLayoutManager(Utils.getApp().getApplicationContext()));
        files.setAdapter(adapter);
        return view;
    }
    
    @Override
    public RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getAdapter() {
        return adapter;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void viewData(IFileData data) throws IOException {
        LogUtils.i("View IFileData:", data);
        if (data instanceof DirData) {
            adapter.setDirData((DirData) data);
        } else
            SnackbarUtils.with(getView())
                    .setMessage("No Impl")
                    .showWarning();
    }
    
    public class DirAdapter extends RecyclerView.Adapter<DirAdapter.DirViewHolder> {
        private List<IFileData> dataList = new ArrayList<>();
        
        private DirAdapter() {
        }
        
        @RequiresApi(api = Build.VERSION_CODES.O)
        private void setDirData(DirData dirData) throws IOException {
            if (dirData != null)
                dataList = dirData.getStorage().getContents(dirData, entry -> showHidden || !Files.isHidden(entry), (o1, o2) -> {
                    if (o1 instanceof DirData != o2 instanceof DirData)
                        return o1 instanceof DirData ? -1 : 0;//Dir first
                    else {
                        String name1 = o1.getName().toString();
                        String name1_ = o1.getName().toString().toLowerCase();
                        String name2 = o2.getName().toString();
                        String name2_ = o2.getName().toString().toLowerCase();
                        int size = Math.min(name1.length(), name2.length());
                        for (int i = 0; i < size; i++) {
                            if (name1_.charAt(i) != name2_.charAt(i))
                                return Integer.compare(name1_.charAt(i), name2_.charAt(i));//ascii smaller at front
                            else
                                return Integer.compare(name1.charAt(i), name2.charAt(i));//lower case at back
                        }
                        return Integer.compare(name1.length(), name2.length());
                    }
                });
            else
                dataList = new ArrayList<>();
            notifyDataSetChanged();
        }
        
        @NonNull
        @Override
        public DirViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_filedataitem, parent, false);
            return new DirViewHolder(view);
        }
        
        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull DirViewHolder holder, int position) {
            IFileData data = dataList.get(position);
            holder.FileName.setText(data.getName().toString());
            holder.FileIcon.setImageIcon(data.getIcon());
            holder.FileTimeStamp.setText(data.getTimeStamp().toString());
            if (data instanceof DirData)
                holder.FileSize.setText(String.format("%d Item", data.getContentSize()));
            else
                holder.FileSize.setText(byteCount2Str(data.getContentSize()));
            holder.view.setOnClickListener(v -> {
                try {
                    viewData(data);
                } catch (Exception e) {
                    LogUtils.e("Failed to view data:", data);
                    SnackbarUtils.with(getView())
                            .setMessage(String.format("%s:%s", getString(R.string.Fail_ViewData), e.getLocalizedMessage()))
                            .showError();
                }
            });
        }
        
        @SuppressLint("DefaultLocale")
        private String byteCount2Str(long size) {
            long kb = 1024;
            long mb = kb * 1024;
            long gb = mb * 1024;
            if (size >= gb) {
                return String.format("%.1f GB", (float) size / gb);
            } else if (size >= mb) {
                float f = (float) size / mb;
                return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
            } else if (size > kb) {
                float f = (float) size / kb;
                return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
            } else {
                return String.format("%d B", size);
            }
        }
        
        @Override
        public int getItemCount() {
            if (dataList.size() == 0)
                getView().findViewById(R.id.EmptyFolder).setVisibility(View.VISIBLE);
            else
                getView().findViewById(R.id.EmptyFolder).setVisibility(View.INVISIBLE);
            return dataList.size();
        }
        
        public class DirViewHolder extends RecyclerView.ViewHolder {
            public View view;
            public TextView FileName;
            public ImageView FileIcon;
            public TextView FileTimeStamp;
            public TextView FileSize;
            
            public DirViewHolder(@NonNull View itemView) {
                super(itemView);
                view = itemView;
                FileName = itemView.findViewById(R.id.FileName);
                FileIcon = itemView.findViewById(R.id.FileIcon);
                FileTimeStamp = itemView.findViewById(R.id.TimeStamp);
                FileSize = itemView.findViewById(R.id.Size);
            }
        }
    }
}
