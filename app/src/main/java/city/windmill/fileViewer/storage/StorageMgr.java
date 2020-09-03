package city.windmill.fileViewer.storage;

import android.os.Build;
import android.util.JsonReader;
import android.util.JsonWriter;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import city.windmill.fileViewer.utils.LocalStorageDiscover;

public class StorageMgr {
    public static final String STORAGE_FILE_SUFFIX = ".storage.json";
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static final PathMatcher MATCHER_STORAGE_FILE = FileSystems.getDefault().getPathMatcher("glob:**" + STORAGE_FILE_SUFFIX);

    public final Set<IStorage> storages = new HashSet<>();
    public final Path savePath;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public StorageMgr(String savePath) {
        this(Paths.get(savePath));
    }

    public StorageMgr(Path savePath) {
        this.savePath = savePath;
    }

    //region Save/Load
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void LoadStorage() throws IOException {
        LogUtils.d("Begin loading storage data from:", savePath);
        storages.clear();
        final List<Path> pathStorages = new ArrayList<>();
        Files.walkFileTree(savePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                LogUtils.v("Visited Dir:", dir);
                if (dir.equals(savePath)) return FileVisitResult.CONTINUE;
                return FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                LogUtils.v("Visited file:", file);
                if (MATCHER_STORAGE_FILE.matches(file)) {
                    pathStorages.add(file);
                    LogUtils.v("Added storage file:", file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                LogUtils.w("Failed to visit file:", file);
                return FileVisitResult.CONTINUE;
            }
        });
        LogUtils.d("Founded Storage Files:", pathStorages);

        for (Path pathStorage : pathStorages) {
            LogUtils.d("Begin Load File:", pathStorage);
            BufferedReader reader = Files.newBufferedReader(pathStorage, StandardCharsets.UTF_8);
            doLoadStorageData(reader);
        }
        //Check if local storage valid
        validLocals();
        //Save data
        SaveStorage();
        LogUtils.d("Finally loaded storage:", storages);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void validLocals() throws IOException {
        Set<LocalStorage> discoveredLocals = LocalStorageDiscover.getLocalStorages();
        Iterator<IStorage> enumStorages = storages.iterator();
        while (enumStorages.hasNext()) {
            IStorage storage = enumStorages.next();
            if (storage instanceof RemoteStorage) continue;//Skip Remote

            if (!discoveredLocals.contains(storage)) {
                enumStorages.remove();
                LogUtils.w("Removed non exist storage:", storage);
                delete(storage);
            }
        }
        //Add non exist storage
        storages.addAll(discoveredLocals);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void doLoadStorageData(BufferedReader bufreader) throws IOException {
        JsonReader reader = new JsonReader(bufreader);
        reader.beginArray();
        IStorage storage;
        if (reader.nextBoolean())
            storage = new LocalStorage();
        else
            storage = new RemoteStorage();
        storage.onLoad(reader);
        reader.endArray();
        reader.close();
        storages.add(storage);
        LogUtils.d("Do loaded storage:", storage);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SaveStorage() throws IOException {
        for (IStorage storage : storages) {
            Path destPath = savePath.resolve(storage.getName().toLowerCase() + STORAGE_FILE_SUFFIX);
            BufferedWriter writer = Files.newBufferedWriter(destPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            doSaveStorageData(storage, writer);
            LogUtils.d("Saved storage data to:", destPath);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void doSaveStorageData(IStorage storage, BufferedWriter bufWriter) throws IOException {
        JsonWriter writer = new JsonWriter(bufWriter);
        writer.beginArray();
        writer.value(storage instanceof LocalStorage);
        storage.onSave(writer);
        writer.endArray();
        writer.flush();
        writer.close();
        LogUtils.d("Do saved storage:", storage);
    }
    //endregion

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void delete(IStorage storage) throws IOException {
        Path toDel = savePath.resolve(storage.getName().toLowerCase() + STORAGE_FILE_SUFFIX);
        Files.delete(toDel);
        LogUtils.i("Deleted storage:", toDel);
    }
}
