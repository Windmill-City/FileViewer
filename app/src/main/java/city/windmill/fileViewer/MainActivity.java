package city.windmill.fileViewer;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.windmill.FileViewer.R;

import java.io.IOException;

import city.windmill.fileViewer.storage.StorageMgr;

import static com.blankj.utilcode.util.LogUtils.D;

public class MainActivity extends AppCompatActivity {
    public StorageMgr storageMgr = new StorageMgr();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initLogConfig();

        loadStorageData();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadStorageData() {
        try {
            storageMgr.LoadStorage(getApplicationContext().getDataDir().toPath());
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }
}
