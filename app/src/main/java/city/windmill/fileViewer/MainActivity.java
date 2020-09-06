package city.windmill.fileViewer;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.windmill.FileViewer.R;

import java.io.IOException;

import city.windmill.fileViewer.storage.FragmentStorageMgr;
import city.windmill.fileViewer.storage.StorageMgr;

import static com.blankj.utilcode.util.LogUtils.D;
import static com.blankj.utilcode.util.LogUtils.I;

public class MainActivity extends AppCompatActivity {
    public StorageMgr storageMgr;
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initLogConfig();
        loadStorageData();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        replaceFragment(new FragmentStorageMgr(storageMgr));
    }
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initLogConfig() {
        LogUtils.Config cfg = LogUtils.getConfig();
        //console
        cfg.setConsoleFilter(D);
        //log2file
        cfg.setDir(PathUtils.getInternalAppCachePath() + "/logs");
        cfg.setLog2FileSwitch(true);
        cfg.setFileFilter(I);
        cfg.setFileExtension(".log");
        cfg.setFilePrefix("FileViewer-Log");
        cfg.setSaveDays(7);
    
        LogUtils.i(cfg);
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
    
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.MainActivity, fragment);
        transaction.commit();
    }
}
