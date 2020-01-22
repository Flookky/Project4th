package com.example.asus.project4th;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.view.View.GONE;

public class EachStuComment extends Activity {
    private TextView filechosen,textcomment;
    private Toolbar toolbar;
    private ImageView picbase;
    private String subjectName,Username,comment,time,image,file,fileUrl,imageURL;
    private FirebaseDatabase database;
    private DatabaseReference table_data;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference("Picture");
    private StorageReference storageRef1 = FirebaseStorage.getInstance().getReference("File");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_stu_comment);
        initInstance();
        downloadData();
        backToolbar();
        onClickImage();

        filechosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = filechosen.getText().toString();
                if(a.equals("No file is selected")){
                    //Toast.makeText(StudentEachPost.this, "NO NO NO", Toast.LENGTH_SHORT).show();
                } else{
                    final StorageReference ref2 = storageRef1.child(subjectName);
                    StorageReference fileRef;

                    if(!file.equals("No")){
                        fileRef = ref2.child("Comment").child(file);
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String gofile;
                                gofile = uri.toString();

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(EachStuComment.this);
                                builder1.setMessage("Do you want to download this PDF File now ? ");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                DownloadFile(EachStuComment.this, file,".pdf",DIRECTORY_DOWNLOADS, gofile);
                                                dialog.cancel();
                                            }
                                        });

                                builder1.setNegativeButton(
                                        "No Thanks",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();

                                Toast.makeText(EachStuComment.this, "Get File Completed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EachStuComment.this, "Get File Failure", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

    }

    private void initInstance(){
        Intent intent = getIntent();
        subjectName = intent.getStringExtra("subjectName");
        Username = intent.getStringExtra("Username");
        comment = intent.getStringExtra("comment");
        time = intent.getStringExtra("time");
        image = intent.getStringExtra("image");
        file = intent.getStringExtra("file");
        fileUrl = intent.getStringExtra("fileUrl");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Username + "Comment");
        filechosen = findViewById(R.id.filechosen);
        textcomment = findViewById(R.id.TextComment);
        picbase = findViewById(R.id.picbase);
        database = FirebaseDatabase.getInstance();
        table_data = database.getReference();

        textcomment.setText(comment);

    }

    public long DownloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        return downloadmanager.enqueue(request);
    }

    private void downloadData(){
        final StorageReference ref1 = storageRef.child(subjectName);
        final StorageReference ref2 = storageRef1.child(subjectName);
        StorageReference fileRef,imageRef;
        if(!file.equals("No")){
            filechosen.setText("File name: "+ file +".pdf Click to download");
            fileRef = ref2.child("Comment").child(file);
            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    filechosen.setText("File name: "+ file +".pdf Click to download");
                    Toast.makeText(EachStuComment.this, "Get File Completed", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EachStuComment.this, "Get File Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(!image.equals("No")){
            imageRef = ref1.child("Comment").child(image);
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imageURL = uri.toString();
                    Glide.with(getApplicationContext())
                            .load(imageURL)
                            .into(picbase);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(EachStuComment.this, "Get Image Failure", Toast.LENGTH_SHORT).show();

                }
            });
        } else{
            picbase.setVisibility(GONE);
        }
    }

    private void onClickImage(){
        picbase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picbase.getDrawable()!=null){
                    Uri UriImg=Uri.parse(imageURL);
                    showFullImage(UriImg);
                }
            }
        });
    }

    private void showFullImage(Uri imageUri){
        Intent fullScreenIntent = new Intent(this,FullScreenImageActivity.class);
        fullScreenIntent.setData(imageUri);
        startActivity(fullScreenIntent);
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
}
