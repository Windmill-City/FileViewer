package city.windmill.fileViewer.storage;

import android.os.Build;
import android.util.JsonReader;
import android.util.JsonWriter;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import city.windmill.fileViewer.utils.LocalStorageDiscover;

public class StorageMgr {
    public static final String STORAGE_FILE_SUFFIX = ".storage.json";
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static final PathMatcher MATCHER_STORAGE_FILE = FileSystems.getDefault().getPathMatcher("glob:*" + STORAGE_FILE_SUFFIX);

    public final List<IStorage> storages = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void LoadStorage(Path path) throws IOException {
        storages.clear();
        final List<Path> pathStorages = new ArrayList<>();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                return FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if(MATCHER_STORAGE_FILE.matches(file))
                    pathStorages.add(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });

        for(Path pathStorage : pathStorages){
            BufferedReader reader = Files.newBufferedReader(pathStorage, StandardCharsets.UTF_8);
            doLoadStorageData(reader);
        }
        //Check if local storage valid
        validLocals();
        //Save data
        SaveStorage(path);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void validLocals(){
        Iterator<IStorage> enumStorage = storages.iterator();
        List<LocalStorage> discoveredLocals = LocalStorageDiscover.getLocalStorages();

        NextStorage:
        while (enumStorage.hasNext()){
            IStorage storage = enumStorage.next();
            if(storage instanceof RemoteStorage)
                continue;//only check local storage

            Iterator<LocalStorage> enumDiscoveredLocals = discoveredLocals.iterator();
            while (enumDiscoveredLocals.hasNext()) {
                LocalStorage localStorage = enumDiscoveredLocals.next();
                if (localStorage.getRoot().getPath().equals(storage.getRoot().getPath())) {
                    enumDiscoveredLocals.remove();//Remove exist storage
                    continue NextStorage;
                }
            }
            //Not exist in local storage, remove it
            enumStorage.remove();
        }

        //Add not exist storage
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
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void SaveStorage(Path path) throws IOException {
        for(IStorage storage : storages){
            Path destPath = path.resolve(storage.getName().toLowerCase() + STORAGE_FILE_SUFFIX);
            BufferedWriter writer = Files.newBufferedWriter(destPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            doSaveStorageData(storage, writer);
            writer.flush();
            writer.close();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void doSaveStorageData(IStorage storage, BufferedWriter bufWriter) throws IOException {
        JsonWriter writer = new JsonWriter(bufWriter);
        writer.beginArray();
        writer.value(storage instanceof LocalStorage);
        storage.onSave(writer);
        writer.endArray();
        writer.close();
    }
}
