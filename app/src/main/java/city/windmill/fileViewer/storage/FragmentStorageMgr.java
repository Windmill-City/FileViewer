package city.windmill.fileViewer.storage;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.windmill.FileViewer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentStorageMgr extends Fragment {
    public StorageMgr storageMgr;
    private AppCompatActivity activity;

    public FragmentStorageMgr(AppCompatActivity activity) {
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loadStorageData();
        View view = inflater.inflate(R.layout.fragment_storagemgr, container, false);
        ViewPager2 pager2 = view.findViewById(R.id.ViewPage_Storage);
        StoragePageAdapter adapter = new StoragePageAdapter(activity, storageMgr);
        pager2.setAdapter(adapter);
        //Bind TabLayout and ViewPager2
        TabLayout tabLayout = view.findViewById(R.id.SideNav);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, pager2, new TabLayoutMediator.TabConfigurationStrategy() {

            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(R.string.Remote);
                        break;
                    case 1:
                        tab.setText(R.string.Local);
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadStorageData() {
        try {
            storageMgr = new StorageMgr(PathUtils.getInternalAppDataPath());
            storageMgr.LoadStorage();
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    static class StoragePageAdapter extends FragmentStateAdapter {
        private FragmentStorage fRemote;
        private FragmentStorage fLocal;

        public StoragePageAdapter(@NonNull FragmentActivity fragmentActivity, StorageMgr storageMgr) {
            super(fragmentActivity);
            List<IStorage> remote_storages = new ArrayList<>();
            List<IStorage> local_storages = new ArrayList<>();
            for (IStorage storage : storageMgr.storages) {
                if (storage instanceof RemoteStorage)
                    remote_storages.add(storage);
                else
                    local_storages.add(storage);
            }
            fRemote = new FragmentStorage(remote_storages);
            fLocal = new FragmentStorage(local_storages);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return fRemote;
                case 1:
                    return fLocal;
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
