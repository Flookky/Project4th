package com.example.asus.project4th;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Post;
import com.example.asus.project4th.Model.Postdata;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class StudentPostActivity1 extends AppCompatActivity {
    private RecyclerView recyclerViewNoPost;
    private FirebaseDatabase database;
    private DatabaseReference table_post;
    private View view;
    private Toolbar toolbar;
    private String assignname;
    private static String subjectID;
    private static String subjectName;
    private String Username;
    private boolean doubleBackToExitPressedOnce;
    private Button createPost;
    private List<Post> listPostTitle = new ArrayList<>();
    private List<Post> listPostContent = new ArrayList<>();
    private List<Post> listDate = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private StudentPostActivity1.PostAdapter PostAdapter;
    private FirebaseRecyclerAdapter recyclerAdapter;
    private FloatingActionButton fab_addpost;
    private int STATIC_INTEGER_VALUE=1;
    private TextView txtPosttitle, txtPost;
    private static String nodekey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_posts);
        init();
        initFirebase();
        backToolbar();
    }

    void init() {
        txtPosttitle = findViewById(R.id.textPostTitle);
        txtPost = findViewById(R.id.textPost);
        createPost = findViewById(R.id.createPost);

        Intent intent = getIntent();
        Username = intent.getStringExtra("Username");
        subjectID = intent.getStringExtra("subjectID");
        subjectName = intent.getStringExtra("subjectName");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(subjectName);

        database = FirebaseDatabase.getInstance();
        table_post = database.getReference("Posts");

        //RecyclerView//
        recyclerViewNoPost = findViewById(R.id.recyclerViewNoPost);
        recyclerViewNoPost.setHasFixedSize(true);
        RecyclerView.LayoutManager LM = new LinearLayoutManager(this);
        recyclerViewNoPost.setLayoutManager(LM);
        recyclerViewNoPost.setItemAnimator(new DefaultItemAnimator());
        recyclerViewNoPost.setAdapter(recyclerAdapter);

        PostAdapter = new StudentPostActivity1.PostAdapter(StudentPostActivity1.this, listPostTitle, listPostContent, listDate, new StudentPostActivity1.PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                Intent eachPost = new Intent(StudentPostActivity1.this,StudentEachPost.class);
                eachPost.putExtra("PostTitle",post.getPostTitle());
                eachPost.putExtra("PostContent",post.getPostContent());
                eachPost.putExtra("PostTime",post.getTime());
                eachPost.putExtra("username",Username);
                eachPost.putExtra("subjectID",subjectID);
                eachPost.putExtra("subjectName",subjectName);
                startActivity(eachPost);

            }
        });

    }

    private void initFirebase() {
        listPostTitle.clear();
        listPostContent.clear();
        listDate.clear();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference all_posts = database.getReference("Posts");
        final DatabaseReference all_posts2 = all_posts.getRef().child(subjectName);
        Query searchQuery = all_posts.orderByChild("subjectID").equalTo(subjectID);

        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Postdata postdata = new Postdata();
                postdata = dataSnapshot.getValue(Postdata.class);

                if (postdata.getSubjectName().equals(subjectName)) {
                    if (dataSnapshot.hasChild("Post")) {
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Post").getChildren()) {
                            Post post = new Post();
                            post = postSnapshot.getValue(Post.class);
                            listPostTitle.add(post);
                            listPostContent.add(post);
                            listDate.add(post);

                            recyclerViewNoPost.setAdapter(PostAdapter);
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

    }

    public static class PostAdapter extends RecyclerView.Adapter<StudentPostActivity1.PostAdapter.PostViewHolder> {

        List<Post> listPostTitle;
        List<Post> listPostContent;
        List<Post> listDate;
        final StudentPostActivity1.PostAdapter.OnItemClickListener listener;
        private Context context;

        public interface OnItemClickListener {
            void onItemClick(Post post);
        }

        public PostAdapter(Context context, List<Post> postTitle, List<Post> postContent, List<Post> ListDate,StudentPostActivity1.PostAdapter.OnItemClickListener listener) {
            this.context = context;
            this.listPostTitle = postTitle;
            this.listPostContent = postContent;
            this.listDate = ListDate;
            this.listener = listener;
        }

        @NonNull
        @Override
        public StudentPostActivity1.PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_no_post_stu, parent, false);
            return new StudentPostActivity1.PostAdapter.PostViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentPostActivity1.PostAdapter.PostViewHolder holder, final int position) {
            final Post postTitle = listPostTitle.get(position);
            final Post postContent = listPostContent.get(position);
            final Post date = listDate.get(position);
            holder.bind(postTitle, postContent, date, listener);
            /*
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Delete click completed", Toast.LENGTH_SHORT).show();
                    final Dialog deleteDialog = new Dialog(context);
                    deleteDialog.setContentView(R.layout.dialog_delete_post);
                    ImageButton btnConfirm = deleteDialog.findViewById(R.id.btnConfirm);
                    ImageButton btnCancel = deleteDialog.findViewById(R.id.btnCancel);
                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //deletePost(postTitle.getPostTitle(),postContent.getPostContent(),date.getTime(),position);
                            deleteDialog.dismiss();
                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();
                        }
                    });
                    deleteDialog.show();
                }
            });
            */
        }


        public class PostViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout list_item_post;
            TextView textPostTitle;
            TextView textPostContent;
            TextView textDate;
            ImageButton btnDelete;

            public PostViewHolder(View itemView) {
                super(itemView);
                list_item_post = itemView.findViewById(R.id.list_item_post);
                textPostTitle = itemView.findViewById(R.id.textPostTitle);
                textPostContent = itemView.findViewById(R.id.textPost);
                textDate = itemView.findViewById(R.id.textDate);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }

            public void bind(final Post postTitle, Post post, Post date, final StudentPostActivity1.PostAdapter.OnItemClickListener listener) {
                textPostTitle.setText(postTitle.getPostTitle());
                textPostContent.setText(post.getPostContent());
                textDate.setText(date.getTime());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(postTitle);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return listPostTitle.size();
        }

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(1);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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
        initFirebase();

    }

}
