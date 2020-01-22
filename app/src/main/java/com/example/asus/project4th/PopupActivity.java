package com.example.asus.project4th;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.renderscript.Script;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Image;
import com.example.asus.project4th.Model.Post;
import com.example.asus.project4th.Model.Postdata;
import com.example.asus.project4th.Model.Subject;
import com.example.asus.project4th.Model.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class PopupActivity extends Activity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_PDF = 1212;
    private static final int ACTIVITY_CHOOSE_FILE = 112;
    private Uri selectedImage,selectedImage2,selectedImage3,selectedImage4,selectedImage5;
    private Uri pdfUri;
    private static String pdfFile;
    private FirebaseDatabase database;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference table_post;
    private EditText post,postTitle;
    private TextView filechosen;
    private Button btn_post;
    private ImageView picture,selectfile;
    private ImageView picsel,picsel2,picsel3,picsel4,picsel5;
    private static String Username;
    private static int selImg=1;
    private String subjectID,subjectName;
    private String TextPost,TextPostTitle;
    private static int pic=0;
    private boolean isImageFitToScreen;
    private Toolbar toolbar;
    private String NoImg = "No";
    private int length = 7;
    private static final char[] chars ="0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm".toCharArray();
    private static StringBuilder imgName1,imgName2,imgName3,imgName4,imgName5,fileName;
    private static String dataKey = "";
    private String file_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        init();
        backToolbar();
        randomImagename();
        onClickpic();

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picsel.getDrawable() != null && picsel2.getDrawable() != null &&
                        picsel3.getDrawable() != null && picsel4.getDrawable() != null &&
                        picsel5.getDrawable() != null){
                    Toast.makeText(PopupActivity.this, "Sorry it's full of images ", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
                }
            }
        });

        selectfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(PopupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    selectPdf();
                } else{
                    ActivityCompat.requestPermissions(PopupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextPost = post.getText().toString();
                TextPostTitle = postTitle.getText().toString();
                if(TextPost.isEmpty() || TextPostTitle.isEmpty()){
                    Toast.makeText(PopupActivity.this, "Press your post", Toast.LENGTH_SHORT).show();
                } else{
                    UploadPost();
                }
            }
        });

    }

    void init(){
        SharedPreferences prefs = getApplication().getSharedPreferences("SUBJECT_TEACHER", Context.MODE_PRIVATE);
        String usernamePref = prefs.getString("username",null);
        subjectID = prefs.getString("subjectID",null);
        subjectName = prefs.getString("subjectName",null);
        Username = usernamePref;

        database = FirebaseDatabase.getInstance();
        table_post = database.getReference("Posts");

        imgName1 = new StringBuilder();
        imgName2 = new StringBuilder();
        imgName3 = new StringBuilder();
        imgName4 = new StringBuilder();
        imgName5 = new StringBuilder();
        fileName = new StringBuilder();
        post =(EditText)findViewById(R.id.TextPost);
        postTitle =(EditText)findViewById(R.id.TextPostTitle);
        picture =(ImageView) findViewById(R.id.pic);

        selectfile =(ImageView) findViewById(R.id.file);
        picsel =(ImageView) findViewById(R.id.picfile);
        picsel2 =(ImageView) findViewById(R.id.picfile2);
        picsel3 =(ImageView) findViewById(R.id.picfile3);
        picsel4 =(ImageView) findViewById(R.id.picfile4);
        picsel5 =(ImageView) findViewById(R.id.picfile5);
        filechosen =(TextView) findViewById(R.id.filechosen);
        btn_post =(Button)findViewById(R.id.btn_post);
        toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("CreatePost");

        DisplayMetrics dm =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.9));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

    }

    private void onClickpic(){
        picsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picsel.getDrawable()!=null){
                    showFullImage(selectedImage);
                }
            }
        });
        picsel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picsel2.getDrawable()!=null){
                    showFullImage(selectedImage2);
                }
            }
        });
        picsel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picsel3.getDrawable()!=null){
                    showFullImage(selectedImage3);
                }
            }
        });
        picsel4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picsel4.getDrawable()!=null){
                    showFullImage(selectedImage4);
                }
            }
        });
        picsel5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picsel5.getDrawable()!=null){
                    showFullImage(selectedImage5);
                }
            }
        });
    }

    private void showFullImage(Uri imageUri){
        Intent fullScreenIntent = new Intent(this,FullScreenImageActivity.class);
        fullScreenIntent.setData(imageUri);
        startActivity(fullScreenIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectPdf();
        } else{
            Toast.makeText(this, "Please provide permission..", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPdf(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }
        Intent chooseFile= new Intent();
        chooseFile.setType("application/pdf");
        chooseFile.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(chooseFile,"Select PDF"), ACTIVITY_CHOOSE_FILE);
    }

    private void randomImagename(){
        Random random = new Random(); // perhaps make it a class variable so you don't make a new one every time

        for(int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            imgName1.append(c);
        }
        for(int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            imgName2.append(c);
        }
        for(int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            imgName3.append(c);
        }
        for(int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            imgName4.append(c);
        }
        for(int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            imgName5.append(c);
        }
        for(int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            fileName.append(c);
        }
    }

    private void UploadPost() {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final String currentDate = simpleDataFormat.format(calendar.getTime());
        final DatabaseReference ref = database.getReference("Posts");

        Query searchQuery = ref.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String posttxt = post.getText().toString();
                final String postTitletxt = postTitle.getText().toString();
                final String pushkey = ref.push().getKey();
                final String selImage="image";
                final String filestr ="file";
                final String fileUrl ="fileUrl";

                if(postTitletxt.length()>25){
                    Toast.makeText(PopupActivity.this, "Maximum PostTitle is 25 Character", Toast.LENGTH_SHORT).show();
                } else if(posttxt.length()>80){
                    Toast.makeText(PopupActivity.this, "Maximum PostContent is 80 Character", Toast.LENGTH_SHORT).show();
                } else{

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Postdata postdata = new Postdata();
                    postdata = postSnapshot.getValue(Postdata.class);

                    if (postdata.getSubjectID().equals(subjectID)) {
                        //Post post = new Post(postTitletxt, posttxt, currentDate);
                        final DatabaseReference ref_file = ref.getRef().child(postSnapshot.getKey()).child("Post").child(pushkey);
                        final StorageReference storageReference = storage.getReference("Picture").child(subjectName);
                        final StorageReference file = storage.getReference("File").child(subjectName);

                        // New Condition
                        if(pdfUri == null){
                            if(selectedImage == null){
                                //set file ,fileUrl, image1-5 = No to Upload MVC
                                Upload upload = new Upload("No","No","No","No","No"
                                        ,"No","No",postTitletxt, posttxt, currentDate);
                                ref_file.setValue(upload);
                                Toast.makeText(PopupActivity.this, "Add Announcement Completed", Toast.LENGTH_SHORT).show();
                                finish();

                            } else{
                                progressDialog.setTitle("Uploading Image1...");
                                progressDialog.show();
                                storageReference.child(subjectName + "_" + imgName1).putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.d("Success???", "Upload Image2 Success");
                                        progressDialog.dismiss();

                                        if(selectedImage2 == null){
                                            Upload upload = new Upload("No","No",subjectName+"_"+imgName1,"No","No"
                                                    ,"No","No",postTitletxt, posttxt, currentDate);
                                            ref_file.setValue(upload);
                                            Toast.makeText(PopupActivity.this, "Add Announcement Completed", Toast.LENGTH_SHORT).show();
                                            finish();

                                        } else {
                                            ++selImg;
                                            progressDialog.setTitle("Uploading Image2...");
                                            progressDialog.show();
                                            storageReference.child(subjectName + "_" + imgName2).putFile(selectedImage2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    progressDialog.dismiss();
                                                    Log.d("Success???", "Upload Image2 Success");

                                                    if(selectedImage3 == null){
                                                        Upload upload = new Upload("No","No",subjectName+"_"+imgName1,subjectName+"_"+imgName2,"No"
                                                                ,"No","No",postTitletxt, posttxt, currentDate);
                                                        ref_file.setValue(upload);
                                                        Toast.makeText(PopupActivity.this, "Add Announcement Completed", Toast.LENGTH_SHORT).show();
                                                        finish();

                                                    } else {
                                                        ++selImg;
                                                        progressDialog.setTitle("Uploading Image3...");
                                                        progressDialog.show();
                                                        storageReference.child(subjectName + "_" + imgName3).putFile(selectedImage3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                progressDialog.dismiss();
                                                                Log.d("Success???", "Upload Image3 Success");

                                                                if(selectedImage4 == null){
                                                                    Upload upload = new Upload("No","No",subjectName+"_"+imgName1,subjectName+"_"+imgName2,subjectName+"_"+imgName3
                                                                            ,"No","No",postTitletxt, posttxt, currentDate);
                                                                    ref_file.setValue(upload);
                                                                    Toast.makeText(PopupActivity.this, "Add Announcement Completed", Toast.LENGTH_SHORT).show();
                                                                    finish();

                                                                } else {
                                                                    ++selImg;
                                                                    progressDialog.setTitle("Uploading Image4...");
                                                                    progressDialog.show();
                                                                    storageReference.child(subjectName + "_" + imgName4).putFile(selectedImage4).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                            progressDialog.dismiss();
                                                                            Log.d("Success???", "Upload Image4 Success");

                                                                            if(selectedImage5 == null){
                                                                                Upload upload = new Upload("No","No",subjectName+"_"+imgName1,subjectName+"_"+imgName2,subjectName+"_"+imgName3
                                                                                        ,subjectName+"_"+imgName4,"No",postTitletxt, posttxt, currentDate);
                                                                                ref_file.setValue(upload);
                                                                                Toast.makeText(PopupActivity.this, "Add Announcement Completed", Toast.LENGTH_SHORT).show();
                                                                                finish();

                                                                            } else {
                                                                                ++selImg;
                                                                                storageReference.child(subjectName + "_" + imgName5).putFile(selectedImage5).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                        progressDialog.dismiss();
                                                                                        Log.d("Success???", "Upload Image5 Success");
                                                                                        Upload upload = new Upload("No", "No", subjectName + "_" + imgName1, subjectName + "_" + imgName2, subjectName + "_" + imgName3
                                                                                                , subjectName + "_" + imgName4, subjectName + "_" + imgName5, postTitletxt, posttxt, currentDate);
                                                                                        ref_file.setValue(upload);
                                                                                        Toast.makeText(PopupActivity.this, "Add Announcement Completed", Toast.LENGTH_SHORT).show();
                                                                                        finish();

                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        progressDialog.dismiss();
                                                                                        Log.d("Success???", "Upload Image5 Failure" + e.toString());
                                                                                    }
                                                                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                    @Override
                                                                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                        int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                                                                        progressDialog.setProgress(currentProgress);
                                                                                        progressDialog.setMessage("Uploaded Image5 "+(int) currentProgress+"%");

                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            progressDialog.dismiss();
                                                                            Log.d("Success???", "Upload Image4 Failure" + e.toString());
                                                                        }
                                                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                                            int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                                                            progressDialog.setProgress(currentProgress);
                                                                            progressDialog.setMessage("Uploaded Image4 "+(int) currentProgress+"%");
                                                                        }
                                                                    });
                                                                }

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressDialog.dismiss();
                                                                Log.d("Success???", "Upload Image3 Failure" + e.toString());
                                                            }
                                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                                int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                                                progressDialog.setProgress(currentProgress);
                                                                progressDialog.setMessage("Uploaded Image3 "+(int) currentProgress+"%");
                                                            }
                                                        });
                                                    }

                                                    Upload upload = new Upload("No","No",subjectName+"_"+imgName1,subjectName+"_"+imgName2,"No"
                                                            ,"No","No",postTitletxt, posttxt, currentDate);
                                                    ref_file.setValue(upload);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Log.d("Success???", "Upload Image2 Failure" + e.toString());
                                                    Toast.makeText(PopupActivity.this, "Upload Image2 Failure", Toast.LENGTH_SHORT).show();

                                                }
                                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                    int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                                    progressDialog.setProgress(currentProgress);
                                                    progressDialog.setMessage("Uploaded Image2 "+(int) currentProgress+"%");
                                                }
                                            });
                                        }
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Log.d("Success???", "Upload Image1 Failure" + e.toString());
                                        Toast.makeText(PopupActivity.this, "Upload Image1 Failure", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                        progressDialog.setProgress(currentProgress);
                                        progressDialog.setMessage("Uploaded Image1 "+(int) currentProgress+"%");
                                    }
                                });
                            }
                        }

                        else{
                            //Upload file
                            progressDialog.setTitle("Uploading File...");
                            progressDialog.show();
                            final String out = fileName.toString();
                            file.child(out).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Log.d("Success???", "Upload File Success");

                                    if(selectedImage == null){
                                        Upload upload = new Upload(out,pdfFile,"No","No","No"
                                                ,"No","No",postTitletxt, posttxt, currentDate);
                                        ref_file.setValue(upload);
                                        Toast.makeText(PopupActivity.this, "Add Announcement Completed", Toast.LENGTH_SHORT).show();
                                        finish();

                                    } else {
                                        progressDialog.setTitle("Uploading Image1...");
                                        progressDialog.show();
                                        storageReference.child(subjectName + "_" + imgName1).putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                progressDialog.dismiss();
                                                Log.d("Success???", "Upload Image1 Success");

                                                if(selectedImage2 == null){
                                                    Upload upload = new Upload(out,pdfFile,subjectName+"_"+imgName1,"No","No"
                                                            ,"No","No",postTitletxt, posttxt, currentDate);
                                                    ref_file.setValue(upload);
                                                    Toast.makeText(PopupActivity.this, "Add Announcement Completed", Toast.LENGTH_SHORT).show();
                                                    finish();

                                                } else {
                                                    ++selImg;
                                                    progressDialog.setTitle("Uploading Image2...");
                                                    progressDialog.show();
                                                    storageReference.child(subjectName + "_" + imgName2).putFile(selectedImage2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            progressDialog.dismiss();
                                                            Log.d("Success???", "Upload Image2 Success");

                                                            if(selectedImage3 == null){
                                                                Upload upload = new Upload(out,pdfFile,subjectName+"_"+imgName1,subjectName+"_"+imgName2,"No"
                                                                        ,"No","No",postTitletxt, posttxt, currentDate);
                                                                ref_file.setValue(upload);
                                                                Toast.makeText(PopupActivity.this, "Add Announcement Completed", Toast.LENGTH_SHORT).show();
                                                                finish();

                                                            } else {
                                                                ++selImg;
                                                                progressDialog.setTitle("Uploading Image3...");
                                                                progressDialog.show();
                                                                storageReference.child(subjectName + "_" + imgName3).putFile(selectedImage3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                        progressDialog.dismiss();
                                                                        Log.d("Success???", "Upload Image3 Success");

                                                                        if(selectedImage4 == null){
                                                                            Upload upload = new Upload(out,pdfFile,subjectName+"_"+imgName1,subjectName+"_"+imgName2,subjectName+"_"+imgName3
                                                                                    ,"No","No",postTitletxt, posttxt, currentDate);
                                                                            ref_file.setValue(upload);
                                                                            Toast.makeText(PopupActivity.this, "Add Announcement Completed", Toast.LENGTH_SHORT).show();
                                                                            finish();

                                                                        } else {
                                                                            ++selImg;
                                                                            progressDialog.setTitle("Uploading Image4...");
                                                                            progressDialog.show();
                                                                            final String image4 = selImage + String.valueOf(new Integer(selImg));
                                                                            storageReference.child(subjectName + "_" + imgName4).putFile(selectedImage4).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                    progressDialog.dismiss();
                                                                                    Log.d("Success???", "Upload Image4 Success");

                                                                                    if(selectedImage5 == null){
                                                                                        Upload upload = new Upload(out,pdfFile,subjectName+"_"+imgName1,subjectName+"_"+imgName2,subjectName+"_"+imgName3
                                                                                                ,subjectName+"_"+imgName4,"No",postTitletxt, posttxt, currentDate);
                                                                                        ref_file.setValue(upload);
                                                                                        Toast.makeText(PopupActivity.this, "Add Announcement Completed", Toast.LENGTH_SHORT).show();
                                                                                        finish();

                                                                                    } else{
                                                                                        ++selImg;
                                                                                        progressDialog.setTitle("Uploading Image5...");
                                                                                        progressDialog.show();
                                                                                        storageReference.child(subjectName+"_"+imgName5).putFile(selectedImage5).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                            @Override
                                                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                Log.d("Success???", "Upload Image5 Success");
                                                                                                //ref2.child(image5).setValue(subjectName+"_"+imgName5);
                                                                                                Upload upload = new Upload(out,pdfFile,subjectName+"_"+imgName1,subjectName+"_"+imgName2,subjectName+"_"+imgName3
                                                                                                        ,subjectName+"_"+imgName4,subjectName+"_"+imgName5,postTitletxt, posttxt, currentDate);
                                                                                                ref_file.setValue(upload);
                                                                                                Toast.makeText(PopupActivity.this, "Add Announcement Completed", Toast.LENGTH_SHORT).show();
                                                                                                finish();

                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                progressDialog.dismiss();
                                                                                                Log.d("Success???", "Upload Image5 Failure" + e.toString());
                                                                                            }
                                                                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                            @Override
                                                                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                                                                                progressDialog.setProgress(currentProgress);
                                                                                                progressDialog.setMessage("Uploaded Image5 "+(int) currentProgress+"%");
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    progressDialog.dismiss();
                                                                                    Log.d("Success???", "Upload Image4 Failure" + e.toString());
                                                                                }
                                                                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                    int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                                                                    progressDialog.setProgress(currentProgress);
                                                                                    progressDialog.setMessage("Uploaded Image4 "+(int) currentProgress+"%");
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        progressDialog.dismiss();
                                                                        Log.d("Success???", "Upload Image3 Failure" + e.toString());
                                                                    }
                                                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                                        int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                                                        progressDialog.setProgress(currentProgress);
                                                                        progressDialog.setMessage("Uploaded Image3 "+(int) currentProgress+"%");
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();
                                                            Log.d("Success???", "Upload Image2 Failure" + e.toString());
                                                        }
                                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                            int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                                            progressDialog.setProgress(currentProgress);
                                                            progressDialog.setMessage("Uploaded Image2 "+(int) currentProgress+"%");
                                                        }
                                                    });
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Log.d("Success???", "Upload Image1 Failure" + e.toString());
                                            }
                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                                progressDialog.setProgress(currentProgress);
                                                progressDialog.setMessage("Uploaded Image1 "+(int) currentProgress+"%");
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Log.d("Success???", "Upload File Failure" + e.toString());
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                    progressDialog.setProgress(currentProgress);
                                    progressDialog.setMessage("Uploaded File "+(int) currentProgress+"%");
                                }
                            });

                        }
                    }
                }
            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void backToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data !=null){

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 300);
            layoutParams.setMargins(40,10,0,0);

            if(picsel.getDrawable()==null && picsel2.getDrawable()==null
               && picsel3.getDrawable()==null && picsel4.getDrawable()==null && picsel5.getDrawable()==null){
                selectedImage = data.getData();
                picsel.setLayoutParams(layoutParams);
                picsel.setImageURI(selectedImage);
            }
            else if(picsel.getDrawable()!=null && picsel2.getDrawable()==null && picsel3.getDrawable()==null
               && picsel4.getDrawable()==null && picsel5.getDrawable()==null){
                selectedImage2 = data.getData();
                picsel2.setLayoutParams(layoutParams);
                picsel2.setImageURI(selectedImage2);
            }
            else if(picsel.getDrawable()!=null && picsel2.getDrawable()!=null && picsel3.getDrawable()==null
                && picsel4.getDrawable()==null && picsel5.getDrawable()==null){
                selectedImage3 = data.getData();
                picsel3.setLayoutParams(layoutParams);
                picsel3.setImageURI(selectedImage3);
            }
            else if(picsel.getDrawable()!=null && picsel2.getDrawable()!=null && picsel3.getDrawable()!=null
                && picsel4.getDrawable()==null && picsel5.getDrawable()==null){
                selectedImage4 = data.getData();
                picsel4.setLayoutParams(layoutParams);
                picsel4.setImageURI(selectedImage4);
            }
            else if(picsel.getDrawable()!=null && picsel2.getDrawable()!=null && picsel3.getDrawable()!=null
                && picsel4.getDrawable()!=null && picsel5.getDrawable()==null){
                selectedImage5 = data.getData();
                picsel5.setLayoutParams(layoutParams);
                picsel5.setImageURI(selectedImage5);
            }
            else if(picsel.getDrawable()!=null && picsel2.getDrawable()!=null && picsel3.getDrawable()!=null
                && picsel4.getDrawable()!=null && picsel5.getDrawable()!=null){
                Toast.makeText(this, "All images are chosen", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == ACTIVITY_CHOOSE_FILE && resultCode == RESULT_OK) {
            if(data.getData() != null){
                //pdfUri = data.getData();
                //pdfFile = data.getData().getLastPathSegment();
                pdfUri = data.getData();
                pdfFile = pdfUri.toString();
                filechosen.setText("File: " + pdfFile);

            } else{
                Toast.makeText(this, "Please Select a file", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
