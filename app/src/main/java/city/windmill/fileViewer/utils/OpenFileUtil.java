package city.windmill.fileViewer.utils;

import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.Utils;

import java.nio.file.Files;
import java.nio.file.Path;

public class OpenFileUtil {
    
    public static Intent openFile(Path path) {
        if (!Files.exists(path))
            return null;
        /* 取得扩展名 */
        String end = path.getFileName().toString().substring(path.getFileName().toString().lastIndexOf(".") + 1).toLowerCase();
        Uri uri = getUriFromPath(path);
        /* 依扩展名的类型决定MimeType */
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            return getAudioFileIntent(uri);
        } else if (end.equals("3gp") || end.equals("mp4")) {
            return getVideoFileIntent(uri);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
            return getImageFileIntent(uri);
        } else if (end.equals("apk")) {
            return getApkFileIntent(uri);
        } else if (end.equals("ppt")) {
            return getPPTFileIntent(uri);
        } else if (end.equals("xls")) {
            return getExcelFileIntent(uri);
        } else if (end.equals("doc")) {
            return getWordFileIntent(uri);
        } else if (end.equals("pdf")) {
            return getPdfFileIntent(uri);
        } else if (end.equals("chm")) {
            return getChmFileIntent(uri);
        } else if (end.equals("txt")) {
            return getTextFileIntent(uri);
        } else {
            return getAllIntent(uri);
        }
    }
    
    public static Uri getUriFromPath(Path path) {
        Uri uri;
        uri = FileProvider.getUriForFile(Utils.getApp().getApplicationContext(), Utils.getApp().getApplicationContext().getPackageName() + ".provider", path.toFile());
        return uri;
    }
    
    //region getIntents
    
    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(Uri uri) {
        Intent intent = getDefaultIntent();
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }
    
    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(Uri uri) {
        Intent intent = getDefaultIntent();
        intent.setDataAndType(uri, "video/*");
        return intent;
    }
    
    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(Uri uri) {
        Intent intent = getDefaultIntent();
        intent.setDataAndType(uri, "image/*");
        return intent;
    }
    
    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(Uri uri) {
        Intent intent = getDefaultIntent();
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }
    
    // Android获取一个用于打开PPT文件的intent
    public static Intent getPPTFileIntent(Uri uri) {
        Intent intent = getDefaultIntent();
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }
    
    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(Uri uri) {
        Intent intent = getDefaultIntent();
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }
    
    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(Uri uri) {
        Intent intent = getDefaultIntent();
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }
    
    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(Uri uri) {
        Intent intent = getDefaultIntent();
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }
    
    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(Uri uri) {
        Intent intent = getDefaultIntent();
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }
    
    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(Uri uri) {
        Intent intent = getDefaultIntent();
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }
    
    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(Uri uri) {
        Intent intent = getDefaultIntent();
        intent.setDataAndType(uri, "*/*");
        return intent;
    }
    
    public static Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }
    
    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {
        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content")
                .encodedPath(param).build();
        Intent intent = getDefaultIntent();
        intent.setDataAndType(uri, "text/html");
        return intent;
    }
    
    //endregion
}
