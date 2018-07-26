package shree.firebaseandroid;

import android.*;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Calendar;
import java.util.Date;


import cz.msebera.android.httpclient.Header;

import shree.firebaseandroid.Activities.AddExpense;
import shree.firebaseandroid.Activities.Announcement;
import shree.firebaseandroid.Activities.DailogAct;

import shree.firebaseandroid.Activities.ViewExpense;
import shree.firebaseandroid.adapter.ChatFirebaseAdapter;
import shree.firebaseandroid.adapter.ClickListenerChatFirebase;
import shree.firebaseandroid.classes.Constants;

import shree.firebaseandroid.model.ChatModel;
import shree.firebaseandroid.model.FileModel;
import shree.firebaseandroid.model.MapModel;
import shree.firebaseandroid.model.UserModel;
import shree.firebaseandroid.utils.SessionManager;
import shree.firebaseandroid.utils.URLConnection;
import shree.firebaseandroid.utils.Util;
import shree.firebaseandroid.view.FullScreenImageActivity;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


import static shree.firebaseandroid.adapter.ProjectListAdapter.SelectedProjectname;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, ClickListenerChatFirebase {

    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private static final int PLACE_PICKER_REQUEST = 3;

    static final String TAG = MainActivity.class.getSimpleName();
    static final String CHAT_REFERENCE = "chatmodel";

    //Firebase and GoogleApiClient
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReference;
    StorageReference mStorageReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    LinearLayout revealLayout2,addexpense,viewexpense,uploadgallary;

    //Class Model
    private UserModel userModel;

    //Views UI
    private RecyclerView rvListMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView btSendMessage,btEmoji;
    private EmojiconEditText edMessage;
    private View contentRoot;
    private EmojIconActions emojIcon;

    //File
    private File filePathImageCamera;
    RelativeLayout subtaskinfo;
    String projectname="",listTitle="",selectedsubtitle="",subactid="",subactidpos="",selectedquter="";
    TextView tv,subactivityname;
    ImageView attachment;
    boolean hidden = true;
    final static int PICK_PDF_CODE = 2342;
    ProgressDialog pd;
    SessionManager sessionManager;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tv=findViewById(R.id.tv);
        subactivityname=findViewById(R.id.subacttitle);
        subtaskinfo=(RelativeLayout)findViewById(R.id.rlsubtaskinfo);

        attachment=(ImageView) findViewById(R.id.buttonAttach);
        addexpense=(LinearLayout)findViewById(R.id.attach_add_expense);
        viewexpense=(LinearLayout)findViewById(R.id.attach_view_expense);
        uploadgallary=(LinearLayout)findViewById(R.id.loutuploadphoto);

        revealLayout2 = (LinearLayout)findViewById(R.id.reveal_items2);
        revealLayout2.setVisibility(View.GONE);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage(getString(R.string.wait));

        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cx = revealLayout2.getLeft();
                int cy =  revealLayout2.getBottom();
                makeEffect(revealLayout2,cx,cy);
            }
        });


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                projectname= null;
            } else {
                projectname= extras.getString("ProjectTitle");
                listTitle=extras.getString("listTitle");
                subactid=extras.getString("sublistid");
                subactidpos=extras.getString("sublistidpos");
                selectedquter=extras.getString("selectedquater");
                selectedsubtitle=extras.getString("selectedsubactivity");
            }
        } else {
            projectname= (String) savedInstanceState.getSerializable("ProjectTitle");
        }
        setTitle(""+SelectedProjectname);
        tv.setText(selectedsubtitle);
        subactivityname.setText(listTitle);

        subtaskinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DailogAct.class);
                intent.putExtra("implementationplanchecklistid",subactid);
                intent.putExtra("sublistidpos",subactidpos);
                intent.putExtra("selectedquater",selectedquter);
                intent.putExtra("taskname",selectedsubtitle);
                startActivity(intent);
            }
        });



        if (!Util.verificaConexao(this)){
            Util.initToast(this,"You do not have an internet connection");
            //finish();

            bindViews();
            verificaUsuarioLogado();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API)
                    .build();
        }else{
            bindViews();
            verificaUsuarioLogado();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API)
                    .build();
        }

        addexpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, AddExpense.class);
                intent.putExtra("ProjectTitle", projectname);
                intent.putExtra("selectedquaterid",selectedquter);
                intent.putExtra("subactid",subactid);
                intent.putExtra("subactname",selectedsubtitle);
                intent.putExtra("sublistidpos", subactidpos);
                startActivity(intent);

                revealLayout2.setVisibility(View.GONE);
                attachment.setImageResource(R.drawable.ic_attach_file_black_24dp);
                attachment.setEnabled(true);
            }
        });

        viewexpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this, ViewExpense.class);
                intent.putExtra("ProjectTitle", projectname);
                intent.putExtra("selectedquaterid",selectedquter);
                intent.putExtra("subactid",subactid);
                startActivity(intent);
                revealLayout2.setVisibility(View.GONE);
                attachment.setImageResource(R.drawable.ic_attach_file_black_24dp);
                attachment.setEnabled(true);
            }
        });

        uploadgallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoGalleryIntent();

                revealLayout2.setVisibility(View.GONE);
                attachment.setImageResource(R.drawable.ic_attach_file_black_24dp);
                attachment.setEnabled(true);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        StorageReference storageRef = storage.getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG);

        if (requestCode == IMAGE_GALLERY_REQUEST){
            if (resultCode == RESULT_OK){
                pd.show();
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){

                    sendFileFirebase(storageRef,selectedImageUri);

                   // new ImageCompression().execute(String.valueOf(selectedImageUri));
                }else{
                    //URI IS NULL
                }
            }
        }else if (requestCode == IMAGE_CAMERA_REQUEST){
            if (resultCode == RESULT_OK){
                    pd.show();
                if (filePathImageCamera != null && filePathImageCamera.exists()){
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName()+"_camera");
                    sendFileFirebase(imageCameraRef,filePathImageCamera);
                }else{
                    //IS NULL
                }
            }
        }else if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place!=null){
                    LatLng latLng = place.getLatLng();
                    MapModel mapModel = new MapModel(latLng.latitude+"",latLng.longitude+"");
                    ChatModel chatModel = new ChatModel(userModel,Calendar.getInstance().getTime().getTime()+"",mapModel);
                    mFirebaseDatabaseReference.child("/"+projectname+"/"+CHAT_REFERENCE).push().setValue(chatModel);
                }else{
                    //PLACE IS NULL
                }
            }
        }else if(requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {

            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                uploadFile(data.getData());
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void makeEffect(final LinearLayout layout,int cx,int cy){

        int radius = Math.max(layout.getWidth(), layout.getHeight());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            /*SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(layout, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(800);

            SupportAnimator animator_reverse = animator.reverse();*/

            if (hidden) {
                layout.setVisibility(View.VISIBLE);
                attachment.setImageResource(R.drawable.ic_clear_black_24dp);
                // animator.start();
                hidden = false;
            } /*else {
                animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        layout.setVisibility(View.INVISIBLE);
                        hidden = true;

                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                animator_reverse.start();

            }*/
        } else {
            if (hidden) {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(layout, cx, cy, 0, radius);
                layout.setVisibility(View.VISIBLE);
                attachment.setImageResource(R.drawable.ic_clear_black_24dp);
                anim.start();
                hidden = false;

            } else {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(layout, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(View.GONE);
                        attachment.setImageResource(R.drawable.ic_attach_file_black_24dp);
                        hidden = true;
                    }
                });
                anim.start();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.showHome:
                onBackPressed();
                return true;
            case R.id.sendPhoto:
                verifyStoragePermissions();
              //  photoCameraIntent();
                break;
            case R.id.sendPhotoGallery:
                photoGalleryIntent();
                break;
            case R.id.sendLocation:
                locationPlacesIntent();
                break;
            case R.id.sign_out:
                signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Util.initToast(this,"Google Play Services error.");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonMessage:
                sendMessageFirebase();
                break;
        }
    }

    @Override
    public void clickImageChat(View view, int position,String nameUser,String urlPhotoUser,String urlPhotoClick) {
        Intent intent = new Intent(this,FullScreenImageActivity.class);
        intent.putExtra("nameUser",nameUser);
        intent.putExtra("urlPhotoUser",urlPhotoUser);
        intent.putExtra("urlPhotoClick",urlPhotoClick);
        startActivity(intent);
    }

    @Override
    public void clickImageMapChat(View view, int position,String latitude,String longitude) {
        String uri = String.format("geo:%s,%s?z=17&q=%s,%s", latitude,longitude,latitude,longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    /**
     *
     Send the file to the firebase(Image)
     */
    private void sendFileFirebase(StorageReference storageReference, final Uri file){

        if (storageReference != null){

            //Uri compressed=Uri.parse(compressImage(file));
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name+"_gallery");
                UploadTask uploadTask = imageGalleryRef.putFile(file);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"onFailure sendFileFirebase "+e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG,"onSuccess sendFileFirebase");
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        FileModel fileModel = new FileModel("img",downloadUrl.toString(),name,"");
                        ChatModel chatModel = new ChatModel(userModel,"",Calendar.getInstance().getTime().getTime()+"",fileModel);
                        mFirebaseDatabaseReference.child("/"+projectname+"/"+CHAT_REFERENCE).push().setValue(chatModel);
                        pd.hide();
                    }
                });
        }else{
            //IS NULL
        }

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(Uri contentURI) {
        Uri contentUri = contentURI;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String compressImage(Uri imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    /**
     * Envia o arvquivo para o firebase
     */
    private void sendFileFirebase(StorageReference storageReference, final File file){
        if (storageReference != null){
            Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
            UploadTask uploadTask = storageReference.putFile(photoURI);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,"onFailure sendFileFirebase "+e.getMessage());

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG,"onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FileModel fileModel = new FileModel("img",downloadUrl.toString(),file.getName(),file.length()+"");
                    ChatModel chatModel = new ChatModel(userModel,"",Calendar.getInstance().getTime().getTime()+"",fileModel);
                    mFirebaseDatabaseReference.child("/"+projectname+"/"+CHAT_REFERENCE).push().setValue(chatModel);
                    pd.hide();
                }

            });
        }else{
            //IS NULL
        }

    }

    /**
     * Obter local do usuario
     */
    private void locationPlacesIntent(){
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enviar foto tirada pela camera
     */
    private void photoCameraIntent(){
        String nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto+"camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",
                filePathImageCamera);
        it.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    /**
     * Enviar foto pela galeria
     */
    private void photoGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    /**
     * Send Message To firebase Database
     */
    private void sendMessageFirebase(){
        if(edMessage.getText().toString().equals("")) {
            edMessage.setError("Enter Text!");
        }
        else
        {
            ChatModel model = new ChatModel(userModel,edMessage.getText().toString(), Calendar.getInstance().getTime().getTime()+"",null);
            mFirebaseDatabaseReference.child("/"+projectname+"/"+CHAT_REFERENCE).push().setValue(model);
            edMessage.setText(null);
        }

    }

    /**
     * Read collections chatmodel Firebase
     */
    private void lerMessagensFirebase(){
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.keepSynced(true);
        final ChatFirebaseAdapter firebaseAdapter = new ChatFirebaseAdapter(getApplicationContext(),mFirebaseDatabaseReference.child("/"+projectname+"/"+CHAT_REFERENCE),userModel.getName(),this);
        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvListMessage.scrollToPosition(positionStart);
                    pd.hide();
                }
            }
        });
        rvListMessage.setLayoutManager(mLinearLayoutManager);
        rvListMessage.setAdapter(firebaseAdapter);

        rvListMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Util.initToast(getApplicationContext(),"Message Selected");
                return true;
            }
        });
    }

    /**
     *  Verify user is logged in
     */
    private void verificaUsuarioLogado(){
        /*mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else{
            userModel = new UserModel(mFirebaseUser.getDisplayName(), mFirebaseUser.getPhotoUrl().toString(), mFirebaseUser.getUid() );
            pd = new ProgressDialog(this);
            pd.setMessage(getString(R.string.wait));
            pd.show();
            lerMessagensFirebase();

        }*/

        sessionManager = new SessionManager(this);
        sessionManager.getCreatorId();
        userModel = new UserModel(sessionManager.getName(), sessionManager.getCreatorId().toString(), "5tJKB04rhUNjFQuNjpFxlQI6Gin1" );

        pd.show();
        lerMessagensFirebase();
    }

    /**
     *Link views with Java API
     */
    private void bindViews(){
        contentRoot = findViewById(R.id.contentRoot);
        edMessage = (EmojiconEditText)findViewById(R.id.editTextMessage);
        btSendMessage = (ImageView)findViewById(R.id.buttonMessage);
        btSendMessage.setOnClickListener(this);
        btEmoji = (ImageView)findViewById(R.id.buttonEmoji);
        emojIcon = new EmojIconActions(this,contentRoot,edMessage,btEmoji);
        emojIcon.ShowEmojIcon();
        rvListMessage = (RecyclerView)findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

    }

    /**
     * Sign Out no login
     */
    private void signOut(){
       /* mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        startActivity(new Intent(this, LoginActivity.class));
        finish();*/
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }else{
            // we already have permission, lets go ahead and call camera intent
            photoCameraIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    photoCameraIntent();
                }
                break;
        }
    }

    private void updateStatus(String state,String subtaskid) {

        String tasklistid=subtaskid, status="";

        if(state.equals("start"))
        {
            status="2";
        }
        else if(state.equals("update"))
        {
            status="3";
        }


        URLConnection.put("/LoginAndroidService/UpdateStatusOfSubActivityForAndroid?ImplementationPlanCheckListID=" + tasklistid + "&status=" + status, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    if(response.getString("result").equals("InProgress"))
                    {
                      Toast.makeText(MainActivity.this,"Task Started.",Toast.LENGTH_SHORT).show();
                    }
                    else if(response.getString("result").equals("Completed"))
                    {
                        Toast.makeText(MainActivity.this,"Task Completed.",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });

    }


  /*  public void openAddExpense(View view) {
        Intent intent=new Intent(this, AddExpense.class);
        startActivity(intent);
    }*/

    public void uploadPhoto(View view) {
        verifyStoragePermissions();
        revealLayout2.setVisibility(View.GONE);
        attachment.setImageResource(R.drawable.ic_attach_file_black_24dp);
        attachment.setEnabled(true);
    }

    public void uploadPDFFile(View view) {
        getPDF();
        revealLayout2.setVisibility(View.GONE);
        attachment.setImageResource(R.drawable.ic_attach_file_black_24dp);
        attachment.setEnabled(true);
    }

    public void uploadAudio(View view)
    {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Open Audio (mp3) file"), 1);
        revealLayout2.setVisibility(View.GONE);
        attachment.setImageResource(R.drawable.ic_attach_file_black_24dp);
        attachment.setEnabled(true);

    }

    public void letsMeet(View view) {
       // Intent intent=new Intent(MainActivity.this, Letsmeet.class);
       // startActivity(intent);
    }

    public void announcement(View view) {
        Intent intent=new Intent(MainActivity.this, Announcement.class);
        startActivity(intent);
        revealLayout2.setVisibility(View.GONE);
        attachment.setImageResource(R.drawable.ic_attach_file_black_24dp);
        attachment.setEnabled(true);
    }

    public void sendLocation(View view) {
        locationPlacesIntent();
        revealLayout2.setVisibility(View.GONE);
        attachment.setImageResource(R.drawable.ic_attach_file_black_24dp);
        attachment.setEnabled(true);
    }

    //this function will get the pdf from the storage.
    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);

    }

    //this method is uploading the file.
    //the code is same as the previous tutorial.
    //so we are not explaining it.
    private void uploadFile(Uri data) {
       // progressBar.setVisibility(View.VISIBLE);

        final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        StorageReference sRef = mStorageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
        UploadTask uploadTask = sRef.putFile(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
           Log.e(TAG,"onFailure sendFileFirebase "+e.getMessage());
        }
        })
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //progressBar.setVisibility(View.GONE);
                        //textViewStatus.setText("File Uploaded Successfully");

              Uri downloadUrl = taskSnapshot.getDownloadUrl();
              FileModel fileModel = new FileModel("pdffile",downloadUrl.toString(),name,"");
              ChatModel chatModel = new ChatModel(userModel,"",Calendar.getInstance().getTime().getTime()+"",fileModel);
              mFirebaseDatabaseReference.child("/"+projectname+"/"+CHAT_REFERENCE).push().setValue(chatModel);

               /*Upload upload = new Upload("File", taskSnapshot.getDownloadUrl().toString());
               mFirebaseDatabaseReference.child(mFirebaseDatabaseReference.push().getKey()).setValue(upload);*/
             }
           }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
           @Override
             public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //  textViewStatus.setText((int) progress + "% Uploading...");
                }
           });
    }

    public void attackClicked(View view) {

        int cx = revealLayout2.getLeft();
        int cy =  revealLayout2.getBottom();
        makeEffect(revealLayout2,cx,cy);
    }

}
