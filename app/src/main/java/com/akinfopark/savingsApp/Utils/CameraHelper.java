package com.akinfopark.savingsApp.Utils;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.databinding.BottomsheetCameraGalleryBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraHelper implements CommonConstants {

    public static boolean checkPermissions(Fragment fragment) {
        return ContextCompat.checkSelfPermission(fragment.getContext(), CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(fragment.getContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void launchPictureIntentProfile(final Activity activity, final Fragment fragment, View view, final File file, final int code1, final int code2) {


        BottomSheetDialog filesBottom;
        BottomsheetCameraGalleryBinding cameraGalleryBinding;
        cameraGalleryBinding = BottomsheetCameraGalleryBinding.inflate(activity.getLayoutInflater(), null, false);
        filesBottom = DialogUtils.getBottomDialog(activity, cameraGalleryBinding.getRoot());

        cameraGalleryBinding.openfileTitle.setText("Profile Picture");


        cameraGalleryBinding.CameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filesBottom.dismiss();
                dispatchTakePictureIntentFragment(activity, fragment, file, code1);
            }
        });

        cameraGalleryBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filesBottom.dismiss();
            }
        });


        cameraGalleryBinding.GalleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filesBottom.dismiss();
                Intent takePictureIntent;
                takePictureIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                takePictureIntent.setType("image/*");

                if (fragment != null)

                    fragment.startActivityForResult(takePictureIntent, code2);
                else activity.startActivityForResult(takePictureIntent, code2);
            }
        });

        filesBottom.show();

    }

    public static void launchPictureIntentExpense(final Activity activity, final Fragment fragment, View view, final File file, final int code1, final int code2, String Title) {


        BottomSheetDialog filesBottom;
        BottomsheetCameraGalleryBinding cameraGalleryBinding;
        cameraGalleryBinding = BottomsheetCameraGalleryBinding.inflate(activity.getLayoutInflater(), null, false);
        filesBottom = DialogUtils.getBottomDialog(activity, cameraGalleryBinding.getRoot());

        cameraGalleryBinding.openfileTitle.setText(Title);

        cameraGalleryBinding.CameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filesBottom.dismiss();
                dispatchTakePictureIntentFragment(activity, fragment, file, code1);
            }
        });

        cameraGalleryBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filesBottom.dismiss();
            }
        });


        cameraGalleryBinding.GalleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filesBottom.dismiss();
                Intent takePictureIntent;
                takePictureIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                takePictureIntent.setType("image/*");

                if (fragment != null)

                    fragment.startActivityForResult(takePictureIntent, code2);
                else activity.startActivityForResult(takePictureIntent, code2);
            }
        });

        filesBottom.show();


    }


    public static void launchPictureIntentDocuments(final Activity activity, ActivityResultLauncher<Intent> activityResultLauncher, ActivityResultLauncher<Intent> launcher, View view, final File file) {

        PopupMenu popup = new PopupMenu(activity, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.profile_image_menu_item, popup.getMenu());

        //inflater.inflate(R.menu.menu_pdf, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_gallery_item) {
                    Intent takePictureIntent;
                    takePictureIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    takePictureIntent.setType("image/*");

                    launcher.launch(takePictureIntent);

                }

                if (id == R.id.menu_camera_item) {
                    dispatchTakePictureIntentFragmentDocuments(activity, activityResultLauncher, file);
                }

                return false;
            }
        });
        popup.show();

    }

    public static void dispatchTakePictureIntentFragmentDocuments(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher, File file) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go

            // Continue only if the File was successfully created
            if (file != null) {
                Log.i("Image", activity.getPackageName());
                Uri photoURI = FileProvider.getUriForFile(activity,
                        activity.getPackageName() + ".provider",
                        file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activityResultLauncher.launch(takePictureIntent);
            } else {
                Toast.makeText(activity, "File is empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Intent is empty", Toast.LENGTH_SHORT).show();
        }
    }

    public static void dispatchTakePictureIntentFragment(Activity activity, Fragment fragment, File file, int code) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go

            // Continue only if the File was successfully created
            if (file != null) {
                Log.i("Image", activity.getPackageName());
                Uri photoURI = FileProvider.getUriForFile(activity,
                        activity.getPackageName() + ".provider",
                        file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if (fragment != null)
                    fragment.startActivityForResult(takePictureIntent, code);
                else
                    activity.startActivityForResult(takePictureIntent, code);
            } else {
                Toast.makeText(activity, "File is empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Intent is empty", Toast.LENGTH_SHORT).show();
        }
    }

    public static void launchPictureIntentLauncher(final Activity activity, ActivityResultLauncher<Intent> activityResultLauncher, ActivityResultLauncher<Intent> launcher, View view, final File file) {

        PopupMenu popup = new PopupMenu(activity, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.profile_image_menu_item, popup.getMenu());

        //inflater.inflate(R.menu.menu_pdf, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_gallery_item) {
                    Intent takePictureIntent;
                    takePictureIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    takePictureIntent.setType("image/*");

                    launcher.launch(takePictureIntent);

                }

                if (id == R.id.menu_camera_item) {
                    dispatchTakePictureIntentFragmentLauncher(activity, activityResultLauncher, file);
                }

                return false;
            }
        });
        popup.show();

    }

    public static void dispatchTakePictureIntentFragmentLauncher(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher, File file) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go

            // Continue only if the File was successfully created
            if (file != null) {
                Log.i("Image", activity.getPackageName());
                Uri photoURI = FileProvider.getUriForFile(activity,
                        activity.getPackageName() + ".provider",
                        file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activityResultLauncher.launch(takePictureIntent);
            } else {
                Toast.makeText(activity, "File is empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Intent is empty", Toast.LENGTH_SHORT).show();
        }
    }

    public static void launchPictureIntent(final Activity activity, final Fragment fragment, View view, final File file, final int code1, final int code2) {
        PopupMenu popup = new PopupMenu(activity, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_camera, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.menu_camera) {
                    dispatchTakePictureIntentFragment(activity, fragment, file, code1);
                }

                return false;
            }
        });
        popup.show();

    }


    public static File createImageFileMultiple(Context context, Activity activity) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                timeStamp,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        return image;

    }

    public static File createImageFile1(Context context) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                timeStamp,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        return image;

    }


    public static File createImageFile(Context context, Activity activity) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(timeStamp,  /* prefix */".jpg",         /* suffix */storageDir      /* directory */);
        return image;
    }


    public static void launchPictureIntentMulti(final Activity activity, ActivityResultLauncher<Intent> activityResultLauncher, ActivityResultLauncher<Intent> launcher, View view, final File file) {

        PopupMenu popup = new PopupMenu(activity, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.profile_image_menu_item, popup.getMenu());

        //inflater.inflate(R.menu.menu_pdf, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_gallery_item) {
                    Intent takePictureIntent;

                    takePictureIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    takePictureIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    takePictureIntent.setType("image/*");

                    launcher.launch(takePictureIntent);

                }

                if (id == R.id.menu_camera_item) {

                    dispatchTakePictureIntentFragmentMultipleImages(activity, activityResultLauncher, file);
                }

                return false;
            }
        });

        popup.show();

    }

    public static void dispatchTakePictureIntentFragmentMultipleImages(Activity activity, ActivityResultLauncher<Intent> activityResultLauncher, File file) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            if (file != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        activity.getPackageName() + ".provider",
                        file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activityResultLauncher.launch(takePictureIntent);
            } else {
                Toast.makeText(activity, "File is empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Intent is empty", Toast.LENGTH_SHORT).show();
        }
    }


    public static byte[] getBytesFromBitmap(String file) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap bitmap = ResizeImage.rotateBitmap(BitmapFactory.decodeFile(file, bmOptions), orientation);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }


}
