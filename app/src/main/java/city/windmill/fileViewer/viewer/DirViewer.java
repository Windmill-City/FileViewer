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
    private Fragment viewer = this;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dirviewer, container, false);
        RecyclerView files = view.findViewById(R.id.View_Files);
        files.setLayoutManager(new LinearLayoutManager(Utils.getApp().getApplicationContext()));
        files.setAdapter(adapter);
        if (adapter.dataList.size() == 0)
            view.findViewById(R.id.EmptyFolder).setVisibility(View.VISIBLE);
        else
            view.findViewById(R.id.EmptyFolder).setVisibility(View.INVISIBLE);
        return view;
    }
    
    @Override
    public RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getAdapter() {
        return adapter;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void viewData(IFileData data) throws IOException {
        LogUtils.d("View IFileData:", data);
        if (data instanceof DirData) {
            adapter.setDirData((DirData) data);
        } else
            throw new IllegalArgumentException("Only support DirData, dataIn:" + data);
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
                        return Integer.compare(name1.length(), name2.length());//short name at front
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
            dataList.get(position).onBindViewHolder(viewer, this, holder, position);
        }
        
        @Override
        public int getItemCount() {
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
