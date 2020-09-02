package city.windmill.fileViewer;

import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;

public interface ISavable {
    void onSave(JsonWriter jsonWriter) throws IOException;

    void onLoad(JsonReader jsonReader) throws IOException;
}
