package city.windmill.fileViewer.viewer;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.Utils;
import com.windmill.FileViewer.R;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.file.IFileData;

public class DirViewer extends Fragment implements IViewer {
    public HashSet<IFileData> selected = new HashSet<>();
    private DirAdapter adapter = new DirAdapter();
    private boolean showHidden = false;
    private Fragment viewer = this;
    private LinearLayout editFile;
    private boolean isSelectMode = false;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dirviewer, container, false);
        //Files
        RecyclerView files = view.findViewById(R.id.View_Files);
        files.setLayoutManager(new LinearLayoutManager(Utils.getApp().getApplicationContext()));
        files.setAdapter(adapter);
        if (adapter.dataList.size() == 0)
            view.findViewById(R.id.EmptyFolder).setVisibility(View.VISIBLE);
        //Path display
        TextView view_path = view.findViewById(R.id.View_Path);
        view_path.setText(adapter.curDirData != null ? adapter.curDirData.getPath().toString() : getString(R.string.empty_folder));
        //EditFile
        editFile = view.findViewById(R.id.EditFile);
        initEditFileBtns();
        return view;
    }
    
    private void initEditFileBtns() {
        Button copy = editFile.findViewById(R.id.BtnCopy);
        Button cut = editFile.findViewById(R.id.BtnCut);
        Button del = editFile.findViewById(R.id.BtnDel);
        Button rename = editFile.findViewById(R.id.BtnRename);
        copy.setOnClickListener(v -> {
            if (selected.isEmpty()) {
                SnackbarUtils.with(v).setMessage(getString(R.string.AtLeastOne)).showWarning();
                return;
            }
        });
        cut.setOnClickListener(v -> {
            if (selected.isEmpty()) {
                SnackbarUtils.with(v).setMessage(getString(R.string.AtLeastOne)).showWarning();
                return;
            }
        });
        del.setOnClickListener(v -> {
            if (selected.isEmpty()) {
                SnackbarUtils.with(v).setMessage(getString(R.string.AtLeastOne)).showWarning();
                return;
            }
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.Ensure_Del)
                    .setMessage(getString(R.string.Msg_DelCount, selected.size()))
                    .setPositiveButton(R.string.Yes, (dialog, which) -> {
                        for (IFileData data : selected) {
                            try {
                                data.getStorage().deleteContent(data);
                                int index = adapter.dataList.indexOf(data);
                                adapter.dataList.remove(index);
                                adapter.notifyItemRemoved(index);
                            } catch (IOException e) {
                                LogUtils.e("Failed to del content:", e);
                                SnackbarUtils.with(v).setMessage(e.getLocalizedMessage()).showError();
                            }
                        }
                        selected.clear();
                    })
                    .setNegativeButton(R.string.No, null)
                    .show();
        });
        rename.setOnClickListener(v -> {
            if (selected.isEmpty()) {
                SnackbarUtils.with(v).setMessage(getString(R.string.AtLeastOne)).showWarning();
                return;
            }
            if (selected.size() > 1) {
                SnackbarUtils.with(v).setMessage(getString(R.string.OneFileOnly)).showWarning();
                return;
            }
            final EditText et = new EditText(getActivity());
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.NewName)
                    .setView(et)
                    .setPositiveButton(R.string.Ok, (dialog, which) -> {
                        if (et.getText().length() > 0) {
                            IFileData data = selected.iterator().next();
                            Path dest = data.getParent().getPath().resolve(et.getText().toString());
                            try {
                                data.getStorage().moveContent(data, dest, false);
                                adapter.notifyItemChanged(adapter.dataList.indexOf(data));
                            } catch (IOException e) {
                                LogUtils.e("Failed to Rename:", data, et.getText().toString());
                                SnackbarUtils.with(v).setMessage(getString(R.string.Fail_Rename, data.getName().toString(), et.getText().toString()));
                            }
                        } else
                            SnackbarUtils.with(v).setMessage(getString(R.string.EmptyName)).showWarning();
                    })
                    .setNegativeButton(R.string.Cancel, null)
                    .show();
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK && isSelectMode) {
                isSelectMode = false;
                selected.clear();
                adapter.notifyDataSetChanged();
                editFile.setVisibility(View.GONE);
                return true;
            }
            return false;
        });
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
        private DirData curDirData;
        private List<IFileData> dataList = new ArrayList<>();
        
        private DirAdapter() {
        }
        
        @RequiresApi(api = Build.VERSION_CODES.O)
        private void setDirData(DirData dirData) throws IOException {
            curDirData = dirData;
            if (dirData != null) {
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
            } else
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
            holder.view.setSelected(selected.contains(data));
            holder.FileName.setText(data.getName().toString());
            holder.FileIcon.setImageIcon(data.getIcon(true));
            holder.FileTimeStamp.setText(data.getTimeStamp().toString());
            if (data instanceof DirData)
                holder.FileSize.setText(String.format("%d Item", data.getContentSize()));
            else
                holder.FileSize.setText(byteCount2Str(data.getContentSize()));
            //Click
            holder.view.setOnClickListener(v -> {
                if (isSelectMode) {
                    if (selected.contains(data))
                        selected.remove(data);
                    else
                        selected.add(data);
                    adapter.notifyItemChanged(adapter.dataList.indexOf(data));
                } else
                    ViewerFactory.INSTANCE.viewFileData(data, viewer);
            });
            //LongClick
            holder.view.setOnLongClickListener(v -> {
                isSelectMode = true;
                selected.add(data);
                adapter.notifyItemChanged(adapter.dataList.indexOf(data));
                editFile.setVisibility(View.VISIBLE);
                return true;
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
