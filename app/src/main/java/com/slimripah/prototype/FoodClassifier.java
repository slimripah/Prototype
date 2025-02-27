package com.slimripah.prototype;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.content.res.AssetFileDescriptor;
import java.io.FileInputStream;

public class FoodClassifier {

    private static final String MODEL_NAME = "food_model.tflite";
    private static final String LABELS_FILE = "labels.txt";

    private Interpreter interpreter;
    private List<String> labels;

    public FoodClassifier(Context context) throws IOException {
        interpreter = new Interpreter(loadModelFile(context));
        labels = loadLabels(context);
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_NAME);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.getStartOffset(), fileDescriptor.getDeclaredLength());
    }

    private List<String> loadLabels(Context context) throws IOException {
        List<String> labelList = new ArrayList<>();
        try (InputStream inputStream = context.getAssets().open(LABELS_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                labelList.add(line.trim());
            }
        }
        return labelList;
    }

    public String classifyFood(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        TensorImage tensorImage = new TensorImage(org.tensorflow.lite.DataType.FLOAT32);
        tensorImage.load(resizedBitmap);

        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, labels.size()}, org.tensorflow.lite.DataType.FLOAT32);
        interpreter.run(tensorImage.getBuffer(), outputBuffer.getBuffer());

        float[] results = outputBuffer.getFloatArray();
        int maxIndex = 0;
        for (int i = 1; i < results.length; i++) {
            if (results[i] > results[maxIndex]) {
                maxIndex = i;
            }
        }

        return labels.get(maxIndex);
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
        }
    }
}
