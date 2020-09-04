package city.windmill.fileViewer.storage;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.Utils;
import com.windmill.FileViewer.R;

import java.util.List;

import city.windmill.fileViewer.MainActivity;
import city.windmill.fileViewer.viewer.FileViewer;
import city.windmill.fileViewer.viewer.LocalFileViewer;
import city.windmill.fileViewer.viewer.RemoteFileViewer;

public class FragmentStorage extends Fragment {
    private RecyclerView viewStorageItems;
    private List<IStorage> storages;

    public FragmentStorage() {
    }

    public FragmentStorage(List<IStorage> storages) {
        this.storages = storages;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        viewStorageItems = view.findViewById(R.id.View_StorageItems);
        viewStorageItems.setLayoutManager(new LinearLayoutManager(Utils.getApp().getApplicationContext()));
        viewStorageItems.setAdapter(new StorageItemAdapter());
        return view;
    }

    class StorageItemAdapter extends RecyclerView.Adapter<StorageItemAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_storageitem, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final IStorage storage = storages.get(position);
            holder.StorageName.setText(storage.getName());
            holder.view.setOnClickListener(v -> {
                if (PermissionUtils.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    FileViewer viewer = storage instanceof LocalStorage ? new LocalFileViewer((LocalStorage) storage) : new RemoteFileViewer((RemoteStorage) storage);
                    ((MainActivity) getActivity()).replaceFragment(viewer, true);
                    viewer.viewFileData(storage.getRoot());
                } else
                    RequestStoragePermission();
            });
        }

        private void RequestStoragePermission() {
            PermissionUtils.permission(PermissionConstants.STORAGE).callback(new PermissionUtils.FullCallback() {
                @Override
                public void onGranted(List<String> permissionsGranted) {
                    LogUtils.i("Granted Storage Permission");
                    SnackbarUtils.with(getView())
                            .setMessage(getString(R.string.Success_Storage))
                            .showSuccess();
                }

                @Override
                public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                    LogUtils.w("User denied Storage Permission Request");
                    SnackbarUtils.with(getView())
                            .setMessage(getString(R.string.Failed_Storage))
                            .showWarning();
                }
            }).request();
        }

        @Override
        public int getItemCount() {
            if (storages == null) return 0;
            if (storages.isEmpty())
                getView().findViewById(R.id.EmptyStorage).setVisibility(View.VISIBLE);
            else
                getView().findViewById(R.id.EmptyStorage).setVisibility(View.INVISIBLE);
            return storages.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View view;
            TextView StateText;
            ImageView State;
            TextView StorageName;
            Button BtnWOL;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                view = itemView;
                StorageName = itemView.findViewById(R.id.StorageName);
                BtnWOL = itemView.findViewById(R.id.BtnWOL);
                State = itemView.findViewById(R.id.State);
                StateText = itemView.findViewById(R.id.StateText);
            }
        }
    }
}
