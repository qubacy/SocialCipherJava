package com.mcdead.busycoder.socialcipher.utility;

import android.net.Uri;

import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FilesUtility {
    public static Error copyFileTo(
            final Uri sourceUri,
            final Uri destDirUri,
            ObjectWrapper<Uri> resultUri)
    {
        if (sourceUri == null || destDirUri == null || resultUri == null)
            return new Error("Wrong input has been provided!", true);

        File file = new File(sourceUri.getPath());

        if (!file.exists())
            return new Error("File doesn't exist!", false);

        File cacheDir = new File(destDirUri.getPath());

        if (!cacheDir.exists())
            return new Error("Cache Dir path isn't correct!", true);

        File cacheFile = new File(cacheDir.getPath() + '/' + file.getName());

        if (cacheFile.exists()) {
            resultUri.setValue(Uri.fromFile(cacheFile));

            return null;
        }

        byte[] fileBytes = null;

        try (FileInputStream fileIn = new FileInputStream(file)) {
            int bytesToReadCount = fileIn.available();
            fileBytes = new byte[bytesToReadCount];

            if (bytesToReadCount != fileIn.read(fileBytes))
                return new Error("Copying File operation went wrong!", true);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error("Copying File operation went wrong!", true);
        }

        try {
            if (!cacheFile.createNewFile())
                return new Error("Creating New File operation went wrong!", true);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error("Creating File copy operation went wrong!", true);
        }

        try (FileOutputStream cacheFileOut = new FileOutputStream(cacheFile)) {
            cacheFileOut.write(fileBytes);

        } catch (IOException e) {
            e.printStackTrace();

            return new Error("Filling File copy operation went wrong!", true);

        }

        resultUri.setValue(Uri.fromFile(cacheFile));

        return null;
    }
}
