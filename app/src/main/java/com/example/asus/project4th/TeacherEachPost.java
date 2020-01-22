package com.example.asus.project4th;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Helper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.asus.project4th.Model.Image;
import com.example.asus.project4th.Model.Post;
import com.example.asus.project4th.Model.Postdata;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class TeacherEachPost extends Activity {
    private ArrayList<String> arrayList = new ArrayList<>();
    private ListView myList;
    private FirebaseDatabase database;
    private DatabaseReference table_post;
    private String noPost,PostTitle,PostContent,PostTime;
    private String subjectID,subjectName;
    private static String Username,filetodownload,filetourl;
    private TextView PostTitletxt,Posttxt,fileChosen;
    private ImageView picbase,picbase2,picbase3,picbase4,picbase5;
    private Button AllComments;
    private Toolbar toolbar;
    private String imageURL,imageURL2,imageURL3,imageURL4,imageURL5,fileURL;
    private String noImg = "No";
    private static Uri fileUri;
    private static String nodekey = "";
    //private static Uri fileuri;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference("Picture");
    private StorageReference storageRef1 = FirebaseStorage.getInstance().getReference("File");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_each_post);
        init();
        backToolbar();
        downloadInMemory();
        onClickImage();


        fileChosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = fileChosen.getText().toString();
                if(a.equals("No file is selected")){

                }
                else{
                    //Toast.makeText(TeacherEachPost.this, "Hello PDF", Toast.LENGTH_SHORT).show();

                    final Post postIntent = new Post(PostTitle,PostContent,PostTime);
                    final StorageReference ref2 = storageRef1.child(subjectName);
                    final DatabaseReference postPDF = database.getReference("Posts").child(subjectName);
                    postPDF.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Image image = new Image();
                            Post post = new Post();
                            if(dataSnapshot.hasChild("Post")){
                                for(DataSnapshot postSnapshot : dataSnapshot.child("Post").getChildren()){
                                    image = postSnapshot.getValue(Image.class);
                                    post = postSnapshot.getValue(Post.class);
                                    if(postIntent.getPostTitle().equals(post.getPostTitle())
                                            && postIntent.getPostContent().equals(post.getPostContent())
                                            && postIntent.getTime().equals(post.getTime()))
                                    {
                                        Log.e("PDF FILE is",image.getFile());

                                        final StorageReference fileRef;

                                        final String filename = image.getFile();

                                        fileRef = ref2.child(image.getFile());
                                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final String gofile;
                                                Toast.makeText(TeacherEachPost.this, "PDF file is ready to download", Toast.LENGTH_SHORT).show();
                                                gofile = uri.toString();
                                                Log.e("File name is ",gofile);

                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(TeacherEachPost.this);
                                                builder1.setMessage("Do you want to download this PDF File now ? ");
                                                builder1.setCancelable(true);

                                                builder1.setPositiveButton(
                                                        "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                DownloadFile(TeacherEachPost.this, filename,".pdf",DIRECTORY_DOWNLOADS, gofile);
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

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(TeacherEachPost.this, "Get File Failure", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }


                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        AllComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(TeacherEachPost.this, nodekey, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(TeacherEachPost.this,CommentEachStudent.class);
                i.putExtra("subjectName",subjectName);
                i.putExtra("subjectID",subjectID);
                i.putExtra("PostTitle",PostTitle);
                i.putExtra("PostContent",PostContent);
                i.putExtra("PostTime",PostTime);
                i.putExtra("Username",Username);
                i.putExtra("NodeKey",nodekey);

                Log.e("SubName",subjectName);
                Log.e("SubID",subjectID);
                Log.e("PostTitle",PostTitle);
                Log.e("PostContent",PostContent);
                Log.e("Time",PostTime);
                Log.e("Username",Username);

                startActivity(i);

            }
        });

    }

    void init(){
        Intent intent = getIntent();
        noPost = intent.getStringExtra("NumberPost");
        PostTitle = intent.getStringExtra("PostTitle");
        PostContent = intent.getStringExtra("PostContent");
        PostTime = intent.getStringExtra("PostTime");
        Username = intent.getStringExtra("username");
        subjectID = intent.getStringExtra("subjectID");
        subjectName = intent.getStringExtra("subjectName");

        PostTitletxt = (TextView)findViewById(R.id.TextPostTitle);
        Posttxt = (TextView)findViewById(R.id.TextPost);
        picbase = (ImageView)findViewById(R.id.picbase);
        picbase2 = (ImageView)findViewById(R.id.picbase2);
        picbase3 = (ImageView)findViewById(R.id.picbase3);
        picbase4 = (ImageView)findViewById(R.id.picbase4);
        picbase5 = (ImageView)findViewById(R.id.picbase5);
        fileChosen = findViewById(R.id.filechosen);
        AllComments = findViewById(R.id.all_comments);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Post");

        database = FirebaseDatabase.getInstance();
        table_post = database.getReference("Posts");

        PostTitletxt.setText(PostTitle);
        Posttxt.setText(PostContent);

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

        picbase2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picbase2.getDrawable()!=null){
                    Uri UriImg=Uri.parse(imageURL2);
                    showFullImage(UriImg);
                }
            }
        });

        picbase3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picbase3.getDrawable()!=null){
                    Uri UriImg=Uri.parse(imageURL3);
                    showFullImage(UriImg);
                }
            }
        });

        picbase4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picbase4.getDrawable()!=null){
                    Uri UriImg=Uri.parse(imageURL4);
                    showFullImage(UriImg);
                }
            }
        });

        picbase5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(picbase5.getDrawable()!=null){
                    Uri UriImg=Uri.parse(imageURL5);
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

    private void downloadInMemory(){
        final Post postIntent = new Post(PostTitle,PostContent,PostTime);
        final DatabaseReference check_key = database.getReference("Posts");
        Query IntentQuery = check_key.orderByChild("subjectID").equalTo(subjectID);

        IntentQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Postdata postdata = new Postdata();
                postdata = dataSnapshot.getValue(Postdata.class);

                if (postdata.getSubjectName().equals(subjectName)) {
                    if (dataSnapshot.hasChild("Post")) {
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Post").getChildren()) {
                            Post post = new Post();
                            post = postSnapshot.getValue(Post.class);
                            if(post.getPostTitle().equals(postIntent.getPostTitle())
                                    && post.getPostContent().equals(postIntent.getPostContent())
                                    && post.getTime().equals(postIntent.getTime())){
                                nodekey = postSnapshot.getKey();
                                //Toast.makeText(TeacherEachPost.this, nodekey, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    //TODO NoQuest;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference ref_1 = table_post.getRef().child(subjectName);
        final DatabaseReference ref_2 = ref_1.getRef();
        final StorageReference ref1 = storageRef.child(subjectName);
        final StorageReference ref2 = storageRef1.child(subjectName);

        ref_2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Post")){
                    Image image = new Image();
                    image = dataSnapshot.child("Post").child(nodekey).getValue(Image.class);

                    //Toast.makeText(TeacherEachPost.this, image.getImage1(), Toast.LENGTH_SHORT).show();
                    StorageReference fileRef;

                    //Toast.makeText(TeacherEachPost.this, image.getFile(), Toast.LENGTH_SHORT).show();

                    if(image.getFile().equals("No")){ }
                    else{
                        final String gofile = image.getFile();
                        final String gofileUrl = image.getFileUrl();
                        filetodownload = gofile;
                        filetourl = gofileUrl;
                        fileChosen.setText("File name: "+gofile +".pdf Click to download");;
                        fileRef = ref2.child(image.getFile());
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                fileURL = uri.toString();
                                fileUri = uri;
                                fileChosen.setText("File name: "+gofile +".pdf Click to download");;
                                //Toast.makeText(TeacherEachPost.this, "Get File Completed", Toast.LENGTH_SHORT).show();
                                //DownloadFile(TeacherEachPost.this, gofile,".pdf",DIRECTORY_DOWNLOADS, gofileUrl);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TeacherEachPost.this, "Get File Failure", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                    if(image.getImage1().equals(noImg)){ }
                    else{
                        fileRef = ref1.child(image.getImage1());
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageURL = uri.toString();
                                Glide.with(getApplicationContext())
                                        .load(imageURL)
                                        .into(picbase);
                                //Toast.makeText(TeacherEachPost.this, "Image1 OK", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(TeacherEachPost.this, "Get Image Failure", Toast.LENGTH_SHORT).show();
                                // Handle any errors
                            }
                        });


                        if(image.getImage2().equals(noImg)){ }
                        else {
                            fileRef = ref1.child(image.getImage2());
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageURL2 = uri.toString();
                                    Glide.with(getApplicationContext())
                                            .load(imageURL2)
                                            .into(picbase2);
                                    //Toast.makeText(TeacherEachPost.this, "Image2 OK", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(TeacherEachPost.this, "Get Image Failure", Toast.LENGTH_SHORT).show();
                                    // Handle any errors
                                }
                            });
                        }

                        if(image.getImage3().equals(noImg)){}
                        else {
                            fileRef = ref1.child(image.getImage3());
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageURL3 = uri.toString();
                                    Glide.with(getApplicationContext())
                                            .load(imageURL3)
                                            .into(picbase3);
                                    //Toast.makeText(TeacherEachPost.this, "Image3 OK", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(TeacherEachPost.this, "Get Image Failure", Toast.LENGTH_SHORT).show();
                                    // Handle any errors
                                }
                            });
                        }

                        if(image.getImage4().equals(noImg)){}
                        else {
                            fileRef = ref1.child(image.getImage4());
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageURL4 = uri.toString();
                                    Glide.with(getApplicationContext())
                                            .load(imageURL4)
                                            .into(picbase4);
                                    //Toast.makeText(TeacherEachPost.this, "Image3 OK", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(TeacherEachPost.this, "Get Image Failure", Toast.LENGTH_SHORT).show();
                                    // Handle any errors
                                }
                            });
                        }

                        if(image.getImage5().equals(noImg)){}
                        else {
                            fileRef = ref1.child(image.getImage5());
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageURL5 = uri.toString();
                                    Glide.with(getApplicationContext())
                                            .load(imageURL5)
                                            .into(picbase5);
                                    //Toast.makeText(TeacherEachPost.this, "Image3 OK", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(TeacherEachPost.this, "Get Image Failure", Toast.LENGTH_SHORT).show();
                                    // Handle any errors
                                }
                            });
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

