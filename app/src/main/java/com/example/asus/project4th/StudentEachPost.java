package com.example.asus.project4th;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.asus.project4th.Model.Comment;
import com.example.asus.project4th.Model.Image;
import com.example.asus.project4th.Model.Post;
import com.example.asus.project4th.Model.Postdata;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class StudentEachPost extends Activity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_PDF = 1212;
    private static final int ACTIVITY_CHOOSE_FILE = 112;
    private static String nodekey = "";
    private static String Username;
    private static String pdfFile;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ListView myList;
    private FirebaseDatabase database;
    private DatabaseReference table_post;
    private String noPost,PostTitle,PostContent,PostTime;
    private String subjectID,subjectName;
    private TextView PostTitletxt,Posttxt;
    private TextView filechosen,filechosenstu;
    private EditText Comments;
    private ImageView picbase,picbase2,picbase3,picbase4,picbase5;
    private ImageView picture;
    private ImageView picsel,selectfile;
    private Button Apply, yourComments;
    private Toolbar toolbar;
    private Uri selectedImage,pdfUri;
    private String imageURL,imageURL2,imageURL3,imageURL4,imageURL5;
    private String noImg = "No";
    private StringBuilder imgName1,fileName;
    private static int selImg=1;
    private int length = 7;
    private static final char[] chars ="0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm".toCharArray();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference("Picture");
    private StorageReference storageRef1 = FirebaseStorage.getInstance().getReference("File");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_each_post);
        init();
        backToolbar();
        downloadInMemory();
        onClickImage();

        filechosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = filechosen.getText().toString();
                if(a.equals("No file is selected")){
                    //Toast.makeText(StudentEachPost.this, "NO NO NO", Toast.LENGTH_SHORT).show();
                } else{
                    //Toast.makeText(StudentEachPost.this, "Hello file"+a, Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(StudentEachPost.this, "PDF file is ready to download", Toast.LENGTH_SHORT).show();
                                                gofile = uri.toString();
                                                Log.e("File name is ",gofile);

                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(StudentEachPost.this);
                                                builder1.setMessage("Do you want to download this PDF File now ? ");
                                                builder1.setCancelable(true);

                                                builder1.setPositiveButton(
                                                        "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                DownloadFile(StudentEachPost.this, filename,".pdf",DIRECTORY_DOWNLOADS, gofile);
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
                                                Toast.makeText(StudentEachPost.this, "Get File Failure", Toast.LENGTH_SHORT).show();
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

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        });

        selectfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(StudentEachPost.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    selectPdf();
                } else{
                    ActivityCompat.requestPermissions(StudentEachPost.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });

        Apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadComment();
            }
        });

        yourComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               your_comments();
            }
        });

    }

    private void UploadComment(){
        final Random random = new Random();
        for(int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            fileName.append(c);
        }

        for(int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            imgName1.append(c);
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        final DatabaseReference ref = database.getReference("Posts").child(subjectName);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final String currentDate = simpleDataFormat.format(calendar.getTime());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String pushkey = ref.push().getKey();
                final String selImage="image";
                final String filestr ="file";
                final String fileUrl ="fileUrl";
                final String Noimage ="No";
                final String Nofile ="No";
                final String commenttxt = Comments.getText().toString();

                if(commenttxt.isEmpty()){
                    Toast.makeText(StudentEachPost.this, "Please fill Comment text", Toast.LENGTH_SHORT).show();
                } else if(commenttxt.trim().isEmpty()){
                    Toast.makeText(StudentEachPost.this, "Please fill Comment text", Toast.LENGTH_SHORT).show();
                } else if(commenttxt.length()>50){
                    Toast.makeText(StudentEachPost.this, "Maximum PostTitle is 50 Character", Toast.LENGTH_SHORT).show();
                } else{
                    if(dataSnapshot.hasChild("Post")){
                        for(DataSnapshot postSnapshot : dataSnapshot.child("Post").getChildren()){
                            Post post = new Post();
                            post = postSnapshot.getValue(Post.class);
                            if(PostTitle.equals(post.getPostTitle())
                                    && PostContent.equals(post.getPostContent())
                                    && PostTime.equals(post.getTime()))
                            {
                                nodekey = postSnapshot.getKey();

                                //Comment Model to get data object here
                                //ref.child("Post").child(nodekey).child("Comment").child(pushkey).child("comment").setValue(commenttxt);

                                final DatabaseReference ref_Upload = ref.getRef().child("Post").child(nodekey).child("Comment").child(pushkey);

                                if(pdfUri == null){
                                    //ref_Upload.child(filestr).setValue(Nofile);
                                    if(selectedImage == null){
                                        //ref_Upload.child(selImage).setValue(Noimage);
                                        Comment comment = new Comment(commenttxt,"No","No",currentDate,Username);
                                        ref_Upload.setValue(comment);

                                    } else{
                                        final String image1 = selImage;
                                        progressDialog.setTitle("Uploading Image...");
                                        progressDialog.show();
                                        storageRef.child(subjectName).child("Comment").child(imgName1.toString()).putFile(selectedImage)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        Log.d("Success???", "Upload Image2 Success");
                                                        Comment comment = new Comment(commenttxt,imgName1.toString(),"No",currentDate,Username);
                                                        ref_Upload.setValue(comment);

                                                        progressDialog.dismiss();
                                                        //ref_Upload.child(image1).setValue(imgName1.toString());

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Log.d("Success???", "Upload Image Failure" + e.toString());
                                                Toast.makeText(StudentEachPost.this, "Add post Failure", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                                progressDialog.setProgress(currentProgress);
                                                progressDialog.setMessage("Uploaded Image "+(int) currentProgress+"%");

                                            }
                                        });
                                    }

                                } else{
                                    final String out = fileName.toString();
                                    progressDialog.setTitle("Uploading File...");
                                    progressDialog.show();
                                    storageRef1.child(subjectName).child("Comment").child(out).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            progressDialog.dismiss();
                                            Log.d("Success???", "Upload File Success");
                                            //ref_Upload.child(filestr).setValue(out);
                                            //ref_Upload.child(fileUrl).setValue(pdfFile);

                                            if(selectedImage == null){
                                                //ref_Upload.child(selImage).setValue(Noimage);
                                                Comment comment = new Comment(commenttxt,"No",out,currentDate,Username);
                                                ref_Upload.setValue(comment);

                                            } else{
                                                final String image1 = selImage;
                                                progressDialog.setTitle("Uploading Image...");
                                                progressDialog.show();
                                                storageRef.child(subjectName).child("Comment").child(imgName1.toString()).putFile(selectedImage)
                                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                progressDialog.dismiss();
                                                                Log.d("Success???", "Upload Image2 Success");
                                                                //ref_Upload.child(image1).setValue(imgName1.toString());
                                                                Comment comment = new Comment(commenttxt,imgName1.toString(),out,currentDate,Username);
                                                                ref_Upload.setValue(comment);

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Log.d("Success???", "Upload Image1 Failure" + e.toString());
                                                        Toast.makeText(StudentEachPost.this, "Add post Failure", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                        int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                                        progressDialog.setProgress(currentProgress);
                                                        progressDialog.setMessage("Uploaded Image "+(int) currentProgress+"%");

                                                    }
                                                });
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Success???", "Upload File Failure" + e.toString());
                                            Toast.makeText(StudentEachPost.this, "Add post Failure", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            int currentProgress =(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                            progressDialog.setProgress(currentProgress);
                                            progressDialog.setMessage("Uploaded Image "+(int) currentProgress+"%");

                                        }
                                    });
                                }

                                Toast.makeText(StudentEachPost.this, "Comment Comeplete", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                Comments.getText().clear();
                //finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void your_comments(){
        Intent i = new Intent(StudentEachPost.this,StudentCommentsActivity.class);
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

    public long DownloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        return downloadmanager.enqueue(request);
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

        imgName1 = new StringBuilder();
        fileName = new StringBuilder();

        PostTitletxt = (TextView)findViewById(R.id.TextPostTitle);
        Posttxt = (TextView)findViewById(R.id.TextPost);
        picbase = (ImageView)findViewById(R.id.picbase);
        picbase2 = (ImageView)findViewById(R.id.picbase2);
        picbase3 = (ImageView)findViewById(R.id.picbase3);
        picbase4 = (ImageView)findViewById(R.id.picbase4);
        picbase5 = (ImageView)findViewById(R.id.picbase5);
        picsel = findViewById(R.id.picfile);
        picture = findViewById(R.id.pic);
        selectfile = findViewById(R.id.file);
        filechosen = findViewById(R.id.filechosen);
        filechosenstu = findViewById(R.id.filechosenstu);
        Comments = findViewById(R.id.comment);
        yourComments = findViewById(R.id.your_comment);
        Apply = findViewById(R.id.apply);
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
                                //Toast.makeText(StudentEachPost.this, nodekey, Toast.LENGTH_SHORT).show();
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

                    if(image.getFile().equals("No")){ }
                    else{
                        final String gofile = image.getFile();
                        final String gofileUrl = image.getFileUrl();
                        filechosen.setText("File name: "+gofile +".pdf Click to download");
                        fileRef = ref2.child(image.getFile());
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                filechosen.setText("File name: "+gofile +".pdf Click to download");
                                //Toast.makeText(StudentEachPost.this, "Get File Completed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(StudentEachPost.this, "Get File Failure", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(StudentEachPost.this, "Get Image Failure", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(StudentEachPost.this, "Get Image Failure", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(StudentEachPost.this, "Get Image Failure", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(StudentEachPost.this, "Get Image Failure", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(StudentEachPost.this, "Get Image Failure", Toast.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectPdf();
        } else{
            Toast.makeText(this, "Please provide permission..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data !=null){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 300);
            layoutParams.setMargins(40,10,0,0);
            selectedImage = data.getData();
            picsel.setLayoutParams(layoutParams);
            picsel.setImageURI(selectedImage);
        }

        if (requestCode == ACTIVITY_CHOOSE_FILE && resultCode == RESULT_OK) {
            if(data.getData() != null){
                //pdfUri = data.getData();
                //pdfFile = data.getData().getLastPathSegment();
                pdfUri = data.getData();
                pdfFile = pdfUri.toString();
                filechosenstu.setText("File: " + pdfFile);

            } else{
                Toast.makeText(this, "Please Select a file", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
