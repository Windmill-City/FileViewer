package city.windmill.fileViewer;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.windmill.FileViewer.R;

import java.io.IOException;

import city.windmill.fileViewer.storage.StorageMgr;

public class MainActivity extends AppCompatActivity {
    public StorageMgr storageMgr = new StorageMgr();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            storageMgr.LoadStorage(getApplicationContext().getDataDir().toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
