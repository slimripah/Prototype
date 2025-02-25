package com.slimripah.prototype;

import static org.tensorflow.lite.support.common.FileUtil.loadLabels;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

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
        FileInputStream fileInputStream = new FileInputStream(context.getAssets().openFd(MODEL_NAME).getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = context.getAssets().openFd(MODEL_NAME).getStartOffset();
        long declaredLength = context.getAssets().openFd(MODEL_NAME).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabels(Context context) throws IOException {
        List<String> labelList = new ArrayList<>();
        FileInputStream fileInputStream = context.openFileInput(LABELS_FILE);
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes);
        fileInputStream.close();
        String[] lines = new String(bytes).split("\n");
        for (String line : lines) {
            labelList.add(line.trim());
        }
        return labelList;
    }

    public String classifyFood(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        TensorImage tensorImage = new TensorImage();
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
        interpreter.close();
    }

}
