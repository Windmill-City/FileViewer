package city.windmill.fileViewer.viewer;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.Utils;
import com.windmill.FileViewer.R;

import city.windmill.fileViewer.file.DirData;
import city.windmill.fileViewer.file.IFileData;
import city.windmill.fileViewer.utils.OpenFileUtil;

public class ViewerFactory {
    public static ViewerFactory INSTANCE;
    
    private final FragmentManager manager;
    
    public ViewerFactory(FragmentManager manager) {
        this.manager = manager;
        INSTANCE = this;
    }
    
    public void viewFileData(IFileData data, Fragment caller) {
        try {
            if (data instanceof DirData) {
                DirViewer dirViewer = new DirViewer();
                dirViewer.viewData(data);
                ViewerFactory.INSTANCE.replaceFragment(dirViewer, true);
            } else {
                Intent intent = OpenFileUtil.openFile(data.getPath());
                Utils.getApp().getApplicationContext().startActivity(intent);
            }
        } catch (Exception e) {
            LogUtils.e("Failed to view data:", data);
            LogUtils.e(e);
            SnackbarUtils.with(caller.getView())
                    .setMessage(caller.getString(R.string.Fail_ViewData, e.getLocalizedMessage()))
                    .showError();
        }
    }
    
    public void replaceFragment(Fragment fragment, boolean backStack) {
        FragmentTransaction transaction = manager.beginTransaction();
        if (backStack)
            transaction.addToBackStack(null);
        transaction.replace(R.id.MainActivity, fragment);
        transaction.commit();
    }
}
