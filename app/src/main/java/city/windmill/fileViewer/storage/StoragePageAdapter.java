package city.windmill.fileViewer.storage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StoragePageAdapter extends FragmentStateAdapter {
    private FragmentStorage fRemote = new FragmentStorage();
    private FragmentStorage fLocal = new FragmentStorage();

    public StoragePageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
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
