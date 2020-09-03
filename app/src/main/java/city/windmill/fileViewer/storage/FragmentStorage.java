package city.windmill.fileViewer.storage;

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

import com.blankj.utilcode.util.Utils;
import com.windmill.FileViewer.R;

import java.util.List;

public class FragmentStorage extends Fragment {
    private RecyclerView viewStorageItems;
    private List<IStorage> storages;

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
            IStorage storage = storages.get(position);
            holder.StorageName.setText(storage.getName());
        }

        @Override
        public int getItemCount() {
            return storages.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView StateText;
            ImageView State;
            TextView StorageName;
            Button BtnWOL;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                StorageName = itemView.findViewById(R.id.StorageName);
                BtnWOL = itemView.findViewById(R.id.BtnWOL);
                State = itemView.findViewById(R.id.State);
                StateText = itemView.findViewById(R.id.StateText);
            }
        }
    }
}
