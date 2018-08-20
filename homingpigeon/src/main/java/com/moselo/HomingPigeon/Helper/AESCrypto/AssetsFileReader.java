package com.moselo.HomingPigeon.Helper.AESCrypto;

import android.content.Context;
import android.content.res.AssetManager;

import com.moselo.HomingPigeon.Helper.AESCrypto.Interface.AssetsFileReaderInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/** Reads text file to string from assets/ directory */
public class AssetsFileReader implements AssetsFileReaderInterface {
    private final Context mContext;

    public AssetsFileReader(Context context) {
        mContext = context;
    }

    @Override
    public String ReadFile(String fileName) throws IOException {
        final AssetManager am = mContext.getAssets();
        final InputStream inputStream = am.open(fileName);

        final Scanner scanner = new Scanner(inputStream, "UTF-8");
        final String text = scanner.useDelimiter("\\A").next();
        scanner.close();
        return text;
    }
}
