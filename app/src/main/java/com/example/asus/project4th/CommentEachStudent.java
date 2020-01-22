package com.example.asus.project4th;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.asus.project4th.Model.Comment;
import com.example.asus.project4th.Model.Post;
import com.example.asus.project4th.Model.Postdata;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommentEachStudent extends Activity {
    private static RecyclerView recyclerViewComment;
    private Toolbar toolbar;
    private static String subjectName,subjectID,PostTitle,PostContent,PostTime,Username;
    private static List<Comment> listofUsername = new ArrayList<>();
    private static List<Comment> listofComment = new ArrayList<>();
    private static List<Comment> listofTime = new ArrayList<>();
    private static List<Comment> listofimage = new ArrayList<>();
    private static List<Comment> listoffile = new ArrayList<>();
    private static List<Comment> listoffileUrl = new ArrayList<>();
    private static CommentAdapter CommentAdapter;
    private FirebaseDatabase database;
    private static DatabaseReference table_comment;
    private static FirebaseRecyclerAdapter recyclerAdapter;
    private static String nodekey = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_each_stu);
        initInstance();
        backToolbar();
        GetCommentFirebase();
    }

    private void initInstance(){
        Intent intent = getIntent();
        subjectID = intent.getStringExtra("subjectID");
        subjectName = intent.getStringExtra("subjectName");
        PostTitle = intent.getStringExtra("PostTitle");
        PostContent = intent.getStringExtra("PostContent");
        PostTime = intent.getStringExtra("PostTime");
        Username = intent.getStringExtra("Username");
        nodekey = intent.getStringExtra("NodeKey");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(subjectName);
        database = FirebaseDatabase.getInstance();
        table_comment = database.getReference("Posts");

        //RecyclerView
        recyclerViewComment = findViewById(R.id.recyclerViewComment);
        recyclerViewComment.setHasFixedSize(true);
        RecyclerView.LayoutManager LM = new LinearLayoutManager(this);
        recyclerViewComment.setLayoutManager(LM);
        recyclerViewComment.setItemAnimator(new DefaultItemAnimator());
        recyclerViewComment.setAdapter(recyclerAdapter);


        CommentAdapter = new CommentAdapter(CommentEachStudent.this, listofUsername, listofComment, listofTime, listofimage, listoffile, listoffileUrl, new CommentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Comment comment) {
                Intent goComment = new Intent(CommentEachStudent.this,EachStuComment.class);
                goComment.putExtra("subjectName",subjectName);
                goComment.putExtra("Username",comment.getUsername());
                goComment.putExtra("comment",comment.getCommenttxt());
                goComment.putExtra("time",comment.getTime());
                goComment.putExtra("image",comment.getImage());
                goComment.putExtra("file",comment.getFile());
                goComment.putExtra("fileUrl",comment.getFileUrl());

                startActivity(goComment);

            }
        });

    }

    private static void GetCommentFirebase(){
        listofComment.clear();
        listofUsername.clear();
        listofTime.clear();

        final DatabaseReference ref = table_comment.getRef().child(subjectName).child("Post").child(nodekey).child("Comment");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Comment comment = dataSnapshot.getValue(Comment.class);
                listofUsername.add(comment);
                listofComment.add(comment);
                listofTime.add(comment);

                recyclerViewComment.setAdapter(CommentAdapter);

                Log.e("listofUsername",listofUsername.toString());
                Log.e("listofComment",listofComment.toString());
                Log.e("listofTime",listofTime.toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{

        List<Comment> listofUsername;
        List<Comment> listofComment;
        List<Comment> listofTime;
        List<Comment> listofimage;
        List<Comment> listoffile;
        List<Comment> listoffileUrl;

        final OnItemClickListener listener;
        private Context context;

        public interface OnItemClickListener {
            void onItemClick(Comment comment);
        }

        public CommentAdapter(Context context, List<Comment> Username, List<Comment> comment, List<Comment> time,
                              List<Comment> image, List<Comment> file, List<Comment> fileUrl, OnItemClickListener listener) {
            this.context = context;
            this.listofUsername = Username;
            this.listofComment = comment;
            this.listofTime = time;
            this.listofimage = image;
            this.listoffile = file;
            this.listoffileUrl = fileUrl;
            this.listener = listener;
        }

        @NonNull
        @Override
        public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_comment_stu, parent, false);
            return new CommentViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, final int position) {

            final Comment Username = listofUsername.get(position);
            final Comment comment = listofComment.get(position);
            final Comment time = listofTime.get(position);
            holder.bind(Username, comment, time, listener);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Delete click completed", Toast.LENGTH_SHORT).show();
                    checkbeforeDelete(comment.getCommenttxt(),comment.getTime(),context);
                }
            });

        }

        private void checkbeforeDelete(final String comment, final String time, final Context context){
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            //AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(),R.style.AlertDialogCustom));
            builder.setTitle("Do you want to delete this comment");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(context,comment, Toast.LENGTH_SHORT).show();
                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Posts").child(subjectName).child("Post").child(nodekey);
                    ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("Comment")){
                                //Toast.makeText(context, "Hello world", Toast.LENGTH_SHORT).show();
                                for(DataSnapshot CommentSnapshot : dataSnapshot.child("Comment").getChildren()){
                                    Comment del = new Comment();
                                    del = CommentSnapshot.getValue(Comment.class);
                                    if(del.getCommenttxt().equals(comment) && del.getTime().equals(time)){
                                        CommentSnapshot.getRef().removeValue();
                                        GetCommentFirebase();
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Delete Comment complete", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GetCommentFirebase();
                    dialog.dismiss();
                }
            });
            builder.create();

            builder.show();
        }

        public class CommentViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout list_item_comment;
            TextView textUsername;
            TextView textComment;
            TextView texttime;
            ImageButton btnDelete;

            public CommentViewHolder(View itemView) {
                super(itemView);
                list_item_comment = itemView.findViewById(R.id.list_item_comment);
                textUsername = itemView.findViewById(R.id.textUsername);
                textComment = itemView.findViewById(R.id.textComment);
                texttime = itemView.findViewById(R.id.textTime);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }

            public void bind(final Comment Username, Comment comment, Comment time, final OnItemClickListener listener) {
                textUsername.setText(Username.getUsername());
                textComment.setText(comment.getCommenttxt());
                texttime.setText(time.getTime());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(Username);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return listofUsername.size();
        }
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
