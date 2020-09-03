package city.windmill.fileViewer;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.LogUtils;
import com.windmill.FileViewer.R;

import city.windmill.fileViewer.storage.FragmentStorageMgr;

import static com.blankj.utilcode.util.LogUtils.D;

public class MainActivity extends AppCompatActivity {
    public FragmentStorageMgr fragmentStorageMgr;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initLogConfig();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(new FragmentStorageMgr());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.MainActivity, fragment);
        transaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initLogConfig() {
        LogUtils.Config cfg = LogUtils.getConfig();
        //console
        cfg.setConsoleFilter(D);
        //log2file
        cfg.setLog2FileSwitch(true);
        cfg.setFileFilter(D);
        cfg.setFileExtension(".log");
        cfg.setFilePrefix("FileViewer-Log");
        cfg.setSaveDays(7);

        LogUtils.i(cfg);
    }
}
