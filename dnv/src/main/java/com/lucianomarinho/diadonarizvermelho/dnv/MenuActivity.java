package com.lucianomarinho.diadonarizvermelho.dnv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuActivity extends ActionBarActivity {
    private AdView adView;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String title = getString(R.string.app_name);
        getActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + title + "</font>"));
        setContentView(R.layout.activity_menu);

        adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        startApp();

        ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView ivPutNose = (ImageView) findViewById(R.id.ivPutNose);
                ImageView ivImageOk = (ImageView) findViewById(R.id.ivImageOk);
                ivPutNose.setImageResource(0);
                ivPutNose.destroyDrawingCache();
                ivImageOk.setImageResource(0);
                ivImageOk.destroyDrawingCache();
                startApp();
            }
        });
        ImageView ivEngine = (ImageView) findViewById(R.id.ivEngine);
        ivEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                putNose();
            }
        });
    }

    private void startApp() {
        final CharSequence[] items = { getString(R.string.take_picture), getString(R.string.choose_from_gallery), getString(R.string.cancel) };
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
        builder.setTitle(getString(R.string.add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_picture))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        if (photoFile != null) {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            startActivityForResult(intent, 1);
                        }
                    } catch (IOException ex) {
                        // error to create the file
                    }
                } else if (items[item].equals(getString(R.string.choose_from_gallery))) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (items[item].equals(getString(R.string.cancel))) {
                    ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
                    ImageView ivPutNose = (ImageView) findViewById(R.id.ivPutNose);
                    final TouchImageView ivNose = (TouchImageView) findViewById(R.id.ivNose);
                    ImageView ivEngine = (ImageView) findViewById(R.id.ivEngine);
                    ImageView ivImageOk = (ImageView) findViewById(R.id.ivImageOk);
                    ivImage.setVisibility(View.VISIBLE);
                    ivPutNose.setImageResource(0);
                    ivPutNose.destroyDrawingCache();
                    ivPutNose.setVisibility(View.INVISIBLE);
                    ivNose.setVisibility(View.INVISIBLE);
                    ivEngine.setVisibility(View.INVISIBLE);
                    ivImageOk.setImageResource(0);
                    ivImageOk.destroyDrawingCache();
                    ivImageOk.setVisibility(View.INVISIBLE);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if(mCurrentPhotoPath != null){
                    File file = new File(mCurrentPhotoPath);
                    ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
                    ImageView ivPutNose = (ImageView) findViewById(R.id.ivPutNose);
                    Picasso.with(getApplicationContext())
                            .load(file)
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_error)
                            .fit()
                            .centerCrop()
                            .into(ivPutNose);
                    ivPutNose.setVisibility(View.VISIBLE);
                    final TouchImageView ivNose = (TouchImageView) findViewById(R.id.ivNose);
                    ivNose.setVisibility(View.VISIBLE);
                    ImageView ivEngine = (ImageView) findViewById(R.id.ivEngine);
                    ImageView ivImageOk = (ImageView) findViewById(R.id.ivImageOk);
                    ivImage.setVisibility(View.INVISIBLE);
                    ivEngine.setVisibility(View.VISIBLE);
                    ivImageOk.setVisibility(View.INVISIBLE);
                }
            } else if (requestCode == 2) {
                Uri imageUri = intent.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
                    ImageView ivPutNose = (ImageView) findViewById(R.id.ivPutNose);
                    final TouchImageView ivNose = (TouchImageView) findViewById(R.id.ivNose);
                    ImageView ivEngine = (ImageView) findViewById(R.id.ivEngine);
                    ImageView ivImageOk = (ImageView) findViewById(R.id.ivImageOk);
                    ivImage.setVisibility(View.INVISIBLE);
                    ivPutNose.setImageBitmap(bitmap);
                    ivPutNose.setVisibility(View.VISIBLE);
                    ivNose.setVisibility(View.VISIBLE);
                    ivNose.setAdjustViewBounds(true);
                    ivEngine.setVisibility(View.VISIBLE);
                    ivImageOk.setVisibility(View.INVISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "DNV_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath(); // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    private void putNose(){
        final CharSequence[] items = { getString(R.string.save_nose), getString(R.string.share_your_pic), getString(R.string.choose_another) };
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
        builder.setTitle(getString(R.string.options));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.save_nose))) {
                    savePhoto();
                } else if (items[item].equals(getString(R.string.share_your_pic))) {
                    ImageView ivImageOk = (ImageView) findViewById(R.id.ivImageOk);
                    ivImageOk.setDrawingCacheEnabled(true);
                    Bitmap b3 = ivImageOk.getDrawingCache();
                    String fname = "/DCIM/Camera/dnv_" + System.currentTimeMillis() + ".jpg";
                    File root = Environment.getExternalStorageDirectory();
                    File file = new File(root.getAbsolutePath() + fname);
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        b3.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                        ostream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(), getString(R.string.saved_image), Toast.LENGTH_SHORT).show();

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    Uri phototUri = Uri.parse(fname);
                    shareIntent.setData(phototUri);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, phototUri);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_your_pic)));

                } else if (items[item].equals(getString(R.string.choose_another))) {
                    ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
                    ImageView ivPutNose = (ImageView) findViewById(R.id.ivPutNose);
                    final TouchImageView ivNose = (TouchImageView) findViewById(R.id.ivNose);
                    ImageView ivEngine = (ImageView) findViewById(R.id.ivEngine);
                    ImageView ivImageOk = (ImageView) findViewById(R.id.ivImageOk);
                    ivImage.setVisibility(View.VISIBLE);
                    ivPutNose.setImageResource(0);
                    ivPutNose.destroyDrawingCache();
                    ivPutNose.setVisibility(View.INVISIBLE);
                    ivNose.setVisibility(View.INVISIBLE);
                    ivEngine.setVisibility(View.INVISIBLE);
                    ivImageOk.setImageResource(0);
                    ivImageOk.destroyDrawingCache();
                    ivImageOk.setVisibility(View.INVISIBLE);
                    startApp();
                }
            }
        });
        builder.show();
    }

    private void savePhoto(){
        ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
        ImageView ivPutNose = (ImageView) findViewById(R.id.ivPutNose);
        final TouchImageView ivNose = (TouchImageView) findViewById(R.id.ivNose);
        ImageView ivEngine = (ImageView) findViewById(R.id.ivEngine);
        ImageView ivImageOk = (ImageView) findViewById(R.id.ivImageOk);

        BitmapDrawable bd1 = (BitmapDrawable)ivPutNose.getDrawable();
        BitmapDrawable bd2 = (BitmapDrawable)ivNose.getDrawable();
        Bitmap b1 = bd1.getBitmap();
        Bitmap b2 = bd2.getBitmap();

        Bitmap bitmap = Bitmap.createBitmap(b1.getWidth(), b1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //int adWDelta = (int)(b1.getWidth() - b2.getWidth())/2 ;
        //int adHDelta = (int)(b1.getHeight() - b2.getHeight())/2;
        //canvas.drawBitmap(b1, 0, 0, null);
        //canvas.drawBitmap(b2, adWDelta, adHDelta, null);

        canvas.drawBitmap(b1, 0, 0, null);
        canvas.drawBitmap(b2, b2.getWidth(), b2.getHeight(), null);

        ivImageOk.setImageBitmap(bitmap);
        ivImageOk.setVisibility(View.VISIBLE);

        ivImage.setVisibility(View.INVISIBLE);
        ivPutNose.setImageResource(0);
        ivPutNose.destroyDrawingCache();
        ivPutNose.setVisibility(View.INVISIBLE);
        ivNose.setVisibility(View.INVISIBLE);
        ivEngine.setVisibility(View.VISIBLE);

        ivImageOk.setDrawingCacheEnabled(true);
        Bitmap b3 = ivImageOk.getDrawingCache();
        String fname = "/DCIM/Camera/dnv_" + System.currentTimeMillis() + ".jpg";
        File root = Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + fname);
        try {
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            b3.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), getString(R.string.saved_image), Toast.LENGTH_SHORT).show();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri phototUri = Uri.parse(fname);
        shareIntent.setData(phototUri);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, phototUri);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_your_pic)));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
