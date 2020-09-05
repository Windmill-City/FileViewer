package city.windmill.fileViewer.storage;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.windmill.FileViewer.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentStorageMgr extends Fragment {
    private StorageMgr storageMgr;
    
    public FragmentStorageMgr(StorageMgr storageMgr) {
        this.storageMgr = storageMgr;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storagemgr, container, false);
        ViewPager2 pager2 = view.findViewById(R.id.ViewPage_Storage);
        StoragePageAdapter adapter = new StoragePageAdapter(getActivity(), storageMgr);
        pager2.setAdapter(adapter);
        //Bind TabLayout and ViewPager2
        TabLayout tabLayout = view.findViewById(R.id.SideNav);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, pager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.remote);
                    break;
                case 1:
                    tab.setText(R.string.local);
                    break;
            }
        });
        tabLayoutMediator.attach();
        return view;
    }
    
    private static class StoragePageAdapter extends FragmentStateAdapter {
        private FragmentStorage[] fStorages;
        
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
            fStorages = new FragmentStorage[]{new FragmentStorage(remote_storages),
                                              new FragmentStorage(local_storages)};
        }
        
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fStorages[position];
        }
        
        @Override
        public int getItemCount() {
            return fStorages.length;
        }
    }
}
