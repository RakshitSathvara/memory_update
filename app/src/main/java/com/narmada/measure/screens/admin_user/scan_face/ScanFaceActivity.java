package com.narmada.measure.screens.admin_user.scan_face;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.narmada.measure.R;
import com.narmada.measure.face_recognization.FaceClassifier;
import com.narmada.measure.face_recognization.TFLiteFaceRecognition;
import com.narmada.measure.face_recognization.drawing.MultiBoxTracker;
import com.narmada.measure.face_recognization.drawing.OverlayView;
import com.narmada.measure.face_recognization.livefeed.CameraConnectionFragment;
import com.narmada.measure.face_recognization.livefeed.ImageUtils;
import com.narmada.measure.utils.Constant;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ScanFaceActivity extends AppCompatActivity implements ImageReader.OnImageAvailableListener {

    Handler handler;
    private Matrix frameToCropTransform;
    private int sensorOrientation;
    private Matrix cropToFrameTransform;

    private static final boolean MAINTAIN_ASPECT = false;
    OverlayView trackingOverlay;
    private MultiBoxTracker tracker;
    private Integer useFacing = null;
    private static final String KEY_USE_FACING = "use_facing";
    private static final int CROP_SIZE = 1000;
    private static final int TF_OD_API_INPUT_SIZE2 = 160;

    //TODO delcare face detector
    FaceDetector detector;

    //TODO declare face recognizer
    private FaceClassifier faceClassifier;

    boolean isRegister = false;
    boolean startCapture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_scan);

        View toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.txt_title);
        ImageView imgBack = toolbar.findViewById(R.id.iv_back);
        imgBack.setOnClickListener(v -> finish());

        isRegister = getIntent().getBooleanExtra("isRegister", false);
        if(isRegister) {
            title.setText(R.string.chehro_odakh_nondhni);
        } else {
            title.setText(R.string.chahero_odakho);
        }

        handler = new Handler();

        Intent intent = getIntent();
        useFacing = intent.getIntExtra(KEY_USE_FACING, CameraCharacteristics.LENS_FACING_BACK);

        setFragment();

        tracker = new MultiBoxTracker(this);

        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                        .build();
        detector = FaceDetection.getClient(highAccuracyOpts);

        try {
            faceClassifier =
                    TFLiteFaceRecognition.create(
                            getAssets(),
                            "facenet.tflite",
                            TF_OD_API_INPUT_SIZE2,
                            false, getApplicationContext());

        } catch (final IOException e) {
            e.printStackTrace();
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        findViewById(R.id.btn_submit).setOnClickListener(v -> startCapture = true);

    }

    int previewHeight = 0, previewWidth = 0;

    protected void setFragment() {
        final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraId = null;
        try {
            cameraId = manager.getCameraIdList()[useFacing];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        Fragment fragment;
        CameraConnectionFragment camera2Fragment =
                CameraConnectionFragment.newInstance(
                        new CameraConnectionFragment.ConnectionCallback() {
                            @Override
                            public void onPreviewSizeChosen(final Size size, final int rotation) {
                                previewHeight = size.getHeight();
                                previewWidth = size.getWidth();

                                int cropSize = CROP_SIZE;

                                previewWidth = size.getWidth();
                                previewHeight = size.getHeight();

                                sensorOrientation = rotation - getScreenOrientation();

                                rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
                                croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);

                                frameToCropTransform =
                                        ImageUtils.getTransformationMatrix(
                                                previewWidth, previewHeight,
                                                cropSize, cropSize,
                                                sensorOrientation, MAINTAIN_ASPECT);

                                cropToFrameTransform = new Matrix();
                                frameToCropTransform.invert(cropToFrameTransform);

                                trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
                                trackingOverlay.addCallback(
                                        new OverlayView.DrawCallback() {
                                            @Override
                                            public void drawCallback(final Canvas canvas) {
                                                tracker.draw(canvas);
                                                Log.d("tryDrawRect", "inside draw");
                                            }
                                        });
                                tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
                            }
                        },
                        this,
                        R.layout.camera_fragment,
                        new Size(640, 480));

        camera2Fragment.setCamera(cameraId);
        fragment = camera2Fragment;
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    //TODO getting frames of live camera footage and passing them to model
    private boolean isProcessingFrame = false;
    private byte[][] yuvBytes = new byte[3][];
    private int[] rgbBytes = null;
    private int yRowStride;
    private Runnable postInferenceCallback;
    private Runnable imageConverter;
    private Bitmap rgbFrameBitmap;

    @Override
    public void onImageAvailable(ImageReader reader) {
        // We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return;
        }
        if (rgbBytes == null) {
            rgbBytes = new int[previewWidth * previewHeight];
        }
        try {
            final Image image = reader.acquireLatestImage();

            if (image == null) {
                return;
            }

            if (isProcessingFrame) {
                image.close();
                return;
            }

            isProcessingFrame = true;
            final Image.Plane[] planes = image.getPlanes();
            fillBytes(planes, yuvBytes);
            yRowStride = planes[0].getRowStride();
            final int uvRowStride = planes[1].getRowStride();
            final int uvPixelStride = planes[1].getPixelStride();

            imageConverter =
                    () -> ImageUtils.convertYUV420ToARGB8888(
                            yuvBytes[0],
                            yuvBytes[1],
                            yuvBytes[2],
                            previewWidth,
                            previewHeight,
                            yRowStride,
                            uvRowStride,
                            uvPixelStride,
                            rgbBytes);

            postInferenceCallback =
                    () -> {
                        image.close();
                        isProcessingFrame = false;
                    };

            performFaceDetection();

        } catch (final Exception e) {
            Log.d("tryError", e.getMessage() + "abc ");
            return;
        }

    }

    protected void fillBytes(final Image.Plane[] planes, final byte[][] yuvBytes) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (int i = 0; i < planes.length; ++i) {
            final ByteBuffer buffer = planes[i].getBuffer();
            if (yuvBytes[i] == null) {
                yuvBytes[i] = new byte[buffer.capacity()];
            }
            buffer.get(yuvBytes[i]);
        }
    }

    protected int getScreenOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }

    Bitmap croppedBitmap;
    List<FaceClassifier.Recognition> mappedRecognitions;

    //TODO Perform face detection
    public void performFaceDetection() {
        imageConverter.run();
        rgbFrameBitmap.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight);

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mappedRecognitions = new ArrayList<>();
                InputImage image = InputImage.fromBitmap(croppedBitmap, 0);
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {

                                        if (faces.size() <= 1) {
                                            for (Face face : faces) {
                                                final Rect bounds = face.getBoundingBox();
                                                performFaceRecognition(face);
                                            }
                                        } else {
//                                            Toast.makeText(getApplicationContext(), "Multiple faces detected", Toast.LENGTH_SHORT).show();
                                        }

                                        tracker.trackResults(mappedRecognitions, 10);
                                        trackingOverlay.postInvalidate();
                                        postInferenceCallback.run();

                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
            }
        });
    }

    public void performFaceRecognition(Face face) {
        //TODO crop the face
        Rect bounds = face.getBoundingBox();
        if (bounds.top < 0) {
            bounds.top = 0;
        }
        if (bounds.left < 0) {
            bounds.left = 0;
        }
        if (bounds.left + bounds.width() > croppedBitmap.getWidth()) {
            bounds.right = croppedBitmap.getWidth() - 1;
        }
        if (bounds.top + bounds.height() > croppedBitmap.getHeight()) {
            bounds.bottom = croppedBitmap.getHeight() - 1;
        }

        Bitmap crop = Bitmap.createBitmap(croppedBitmap,
                bounds.left,
                bounds.top,
                bounds.width(),
                bounds.height());
        crop = Bitmap.createScaledBitmap(crop, TF_OD_API_INPUT_SIZE2, TF_OD_API_INPUT_SIZE2, false);

        final FaceClassifier.Recognition result = faceClassifier.recognizeImage(crop, isRegister);

        String title = "";
        Float confidence = 0.0f;

        if (result != null) {
            if (isRegister) {
                if(startCapture) {
                    startCapture = false;
                    Constant.faceBitmap = rotateBitmap(rgbFrameBitmap, sensorOrientation);
                    Constant.faceEmbeeding = result.getEmbeeding();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            } else {
                title = result.getTitle();
                confidence = result.getDistance();

                if(startCapture) {
                    if (result.getDistance() < Constant.accuracy) {
                        startCapture = false;
                        Constant.faceBitmap = rotateBitmap(rgbFrameBitmap, sensorOrientation);

                        Intent intent = new Intent();
                        intent.putExtra("name", result.getTitle());
                        intent.putExtra("id", result.getCode());
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            }
        }

        RectF location = new RectF(bounds);
        if (bounds != null) {
            if (useFacing == CameraCharacteristics.LENS_FACING_BACK) {
                location.right = croppedBitmap.getWidth() - location.right;
                location.left = croppedBitmap.getWidth() - location.left;
            }
            cropToFrameTransform.mapRect(location);
            FaceClassifier.Recognition recognition = new FaceClassifier.Recognition(face.getTrackingId() + "", title, "", confidence, location);
            mappedRecognitions.add(recognition);
        }

    }

    public Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void switchCamera() {

        Intent intent = getIntent();

        if (useFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            useFacing = CameraCharacteristics.LENS_FACING_BACK;
        } else {
            useFacing = CameraCharacteristics.LENS_FACING_FRONT;
        }

        intent.putExtra(KEY_USE_FACING, useFacing);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        restartWith(intent);
    }

    private void restartWith(Intent intent) {
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.close();
    }
}