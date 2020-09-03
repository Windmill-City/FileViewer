package city.windmill.fileViewer;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.windmill.FileViewer.R;

import java.io.IOException;

import city.windmill.fileViewer.storage.StorageMgr;

public class StorageMgrActivity {
    public final AppCompatActivity compatActivity;
    public StorageMgr storageMgr;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public StorageMgrActivity(AppCompatActivity activity) {
        this.compatActivity = activity;
        loadStorageData();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void open() {
        compatActivity.setContentView(R.layout.activity_storagemgr);
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
}
