package city.windmill.fileViewer;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;

import static com.blankj.utilcode.util.LogUtils.D;

public class MainActivity extends AppCompatActivity {
    public StorageMgrActivity storageMgrActivity;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initLogConfig();
        super.onCreate(savedInstanceState);

        storageMgrActivity = new StorageMgrActivity(this);
        storageMgrActivity.open();
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
