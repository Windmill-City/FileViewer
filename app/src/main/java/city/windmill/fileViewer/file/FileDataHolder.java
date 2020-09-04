package city.windmill.fileViewer.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileDataHolder {
    public List<IFileData> hiddenFolders = new ArrayList<>();
    public List<IFileData> folders = new ArrayList<>();
    public List<IFileData> hiddenFiles = new ArrayList<>();
    public List<IFileData> files = new ArrayList<>();

    public boolean showHidden = false;

    private int size;
    private int hidden_size;

    public FileDataHolder(List<IFileData> fileData, SortTarget target, boolean reverse) {
        separateFileData(fileData, hiddenFolders, folders, hiddenFiles, files);
        //Sort
        doSort(target, hiddenFolders, reverse);
        doSort(target, hiddenFiles, reverse);
        doSort(target, folders, reverse);
        doSort(target, files, reverse);

        size = folders.size() + files.size();
        hidden_size = hiddenFolders.size() + hiddenFiles.size();
    }

    public FileDataHolder(List<IFileData> fileData, SortTarget target) {
        this(fileData, target, false);
    }

    public FileDataHolder(List<IFileData> fileData) {
        this(fileData, SortTarget.FileName);
    }

    public static void doSort(SortTarget target, List<IFileData> fileData, boolean reverse) {
        switch (target) {
            case FileName:
                sortByName(fileData, reverse);
                break;
            case FileTimeStamp:
                sortByTimeStamp(fileData, reverse);
                break;
        }
    }

    public static void sortByName(List<IFileData> fileData, boolean reverse) {
        Collections.sort(fileData, (o1, o2) -> {
            int result = o1.getName().compareTo(o2.getName());
            return reverse ? -result : result;
        });
    }

    public static void sortByTimeStamp(List<IFileData> fileData, boolean reverse) {
        Collections.sort(fileData, (o1, o2) -> {
            int result = o1.getTimeStamp().compareTo(o2.getTimeStamp());
            return reverse ? -result : result;
        });
    }

    public static void separateFileData(List<IFileData> source,
                                        List<IFileData> hiddenFolders, List<IFileData> folders,
                                        List<IFileData> hiddenFiles, List<IFileData> files) {
        for (IFileData fileData : source) {
            if (fileData.isHidden()) {
                if (fileData.getType() == FileType.DIR)
                    hiddenFolders.add(fileData);
                else
                    hiddenFiles.add(fileData);
            } else {
                if (fileData.getType() == FileType.DIR)
                    folders.add(fileData);
                else
                    files.add(fileData);
            }
        }
    }

    public int getDataSize() {
        return showHidden ? size + hidden_size : size;
    }

    public IFileData get(int index) {
        if (index >= getDataSize() || index < 0)
            throw new IndexOutOfBoundsException("Index >= getDataSize || Index < 0");
        if (showHidden) {
            if (index < folders.size())
                return folders.get(index);
            else if (index - folders.size() < hiddenFolders.size())
                return hiddenFolders.get(index - folders.size());
            else if (index - (folders.size() + hiddenFolders.size()) < files.size())
                return files.get(index - (folders.size() + hiddenFolders.size()));
            else
                return hiddenFiles.get(index - (folders.size() + hiddenFolders.size() + files.size()));
        } else {
            if (index < folders.size())
                return folders.get(index);
            else
                return files.get(index - folders.size());
        }
    }

    public enum SortTarget {
        FileName,
        FileTimeStamp
    }
}
