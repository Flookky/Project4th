package com.example.asus.project4th;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Postdata;
import com.example.asus.project4th.Model.Subject;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.String.valueOf;


public class TeacherCoursesFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    private ImageButton searchBtn;
    private TextView textSubjectID, textSubjectname;
    private RecyclerView recyclerViewSubject;
    private FloatingActionButton fab;
    private EditText searchField;
    private View view;
    private FirebaseDatabase database;
    private DatabaseReference table_post,table_Teacher,table_subject,table_subject2,table_subject3,table_Checksubject,table_Checksubject2;
    private Query list_subject;
    private SubjectAdapter subjectAdapter;
    private List<Subject> listSubjectID;
    private List<Subject> listSubjectName;
    private List<Subject> listDate;
    private FirebaseRecyclerAdapter recyclerAdapter;
    private Dialog addSubjectDialog;
    private SubjectAdapter.OnItemClickListener listener;
    private static final String[] ChooseActivity = {"Assignment", "Announcement"};
    private static String Username;
    private final String regexStr = "^[0-9]*$";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_courses1, container, false);

        if (getArguments() != null) {
            Username = getArguments().getString("Username");

            //Toast.makeText(getContext(), Username, Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getContext(), "Bundle == null", Toast.LENGTH_SHORT).show();
        }

        initInstance();
        fabButtomAddSubject();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        SharedPreferences prefs = getActivity().getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        String usernamePref = prefs.getString("USERNAME",null);
        Username=usernamePref;
        //Toast.makeText(getContext(), Username, Toast.LENGTH_SHORT).show();
    }

    private void initInstance() {
        //--------------------- Firebase ----------------------------//
        database = FirebaseDatabase.getInstance();
        table_Teacher = database.getReference("Member").child("Teacher");
        table_subject = database.getReference("Member").child("Teacher");
        table_subject2 = database.getReference("Subject");
        table_subject3 = database.getReference("Subject");
        table_post = database.getReference("Posts");
        //list_subject = database.getReference("Subject").orderByChild("subjectID");
        //-------------------- Search --------------------------//
        textSubjectID = view.findViewById(R.id.textSubjectId);
        textSubjectname = view.findViewById(R.id.textSubject);

        //---------- Fad Button -------------//
        fab = view.findViewById(R.id.fabPlus);
        fab.setOnClickListener(this);

        //--------------- RecyclerView --------------------//
        recyclerViewSubject = view.findViewById(R.id.recyclerViewSubject);
        recyclerViewSubject.setHasFixedSize(true);
        RecyclerView.LayoutManager LM = new LinearLayoutManager(view.getContext());
        recyclerViewSubject.setLayoutManager(LM);
        recyclerViewSubject.setItemAnimator(new DefaultItemAnimator());
        //recyclerViewSubject.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        recyclerViewSubject.setAdapter(recyclerAdapter);

        //----------------- Subject list -------------------------------//
        listSubjectID = new ArrayList<>();
        listSubjectName = new ArrayList<>();
        listDate = new ArrayList<>();
        //subjectAdapter = new SubjectAdapter(listSubjectID, listSubjectName, listener);
        subjectAdapter = new SubjectAdapter(getContext(), listSubjectID, listSubjectName, listDate, new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Subject subject) {
                //selectSubject();
                //Toast.makeText(getContext(), "Subject is clicked", Toast.LENGTH_SHORT).show();
                /*
                Intent isubject = new Intent(getActivity(), TeacherMenuExamsActivity.class);
                isubject.putExtra("username", Username);
                isubject.putExtra("subjectID", subject.getSubjectID());
                isubject.putExtra("subjectname", subject.getSubjectname());
                startActivity(isubject);
                */
                dialogselect(subject.getUsername(),subject.getSubjectID(),subject.getSubjectname());
                //Toast.makeText(getContext(), "Assignment", Toast.LENGTH_SHORT).show();
            }
        });
        GetSubjectFirebase();

    }

    public void dialogselect(final String username,final String subjectid,final String subjectname){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(),R.style.AlertDialogCustom));
        builder.setTitle("Select Option");
        builder.setItems(ChooseActivity, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected = ChooseActivity[which];
                if(selected.equals("Assignment")){
                    Intent isubject = new Intent(getActivity(), TeacherMenuExamsActivity.class);
                    isubject.putExtra("username", username);
                    isubject.putExtra("subjectID", subjectid);
                    isubject.putExtra("subjectname", subjectname);

                    startActivity(isubject);
                    Toast.makeText(getContext(), "You choose " +
                            "Assignment", Toast.LENGTH_SHORT).show();
                }
                else if(selected.equals("Announcement")){
                    Intent isubject2 = new Intent(getActivity(), TeacherPostActivity1.class);
                    isubject2.putExtra("username", Username);
                    isubject2.putExtra("subjectID", subjectid);
                    isubject2.putExtra("subjectname", subjectname);

                    startActivity(isubject2);
                    Toast.makeText(getContext(), "You choose " +
                            "Announcement", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getContext(), "You choose " + selected, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create();

        builder.show();
    }

    //<------------------------ Firebase search field and display list ------------------------------------>//
    void GetSubjectFirebase() {
        listSubjectID.clear();
        listSubjectName.clear();
        listDate.clear();

        Query searchQuery = table_subject2.orderByChild("username").equalTo(Username);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Subject subject = new Subject();
                Subject subject = dataSnapshot.getValue(Subject.class);
                //Add to ArrayList
                listSubjectID.add(subject);
                listSubjectName.add(subject);
                listDate.add(subject);
                //Add List into Adapter/RecyclerView
                recyclerViewSubject.setAdapter(subjectAdapter);
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

    void GetSearchFirebase(final String searchText) {
        //Clear ListSubject
        listSubjectID.clear();
        listSubjectName.clear();
        listDate.clear();
        Query searchQuery = table_subject.orderByChild("username").equalTo(Username);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Subject subject = new Subject();
                subject = dataSnapshot.getValue(Subject.class);

                if (subject.getSubjectname().contains(searchText)){
                    //Add to ArrayList
                    listSubjectID.add(subject);
                    listSubjectName.add(subject);
                    listDate.add(subject);
                }
                //Add List into Adapter/RecyclerView
                recyclerViewSubject.setAdapter(subjectAdapter);
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
    //------------------------------------------------------------------------------------------//

    //---------------- Subject List -------------------------------------------------//
    public static class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

        List<Subject> listArrayID;
        List<Subject> listArrayName;
        List<Subject> listDate;
        final OnItemClickListener listener;
        private Context context;

        public interface OnItemClickListener {
            void onItemClick(Subject subject);
        }

        public SubjectAdapter(Context context, List<Subject> ListID, List<Subject> ListName, List<Subject> ListDate,OnItemClickListener listener) {
            this.context = context;
            this.listArrayID = ListID;
            this.listArrayName = ListName;
            this.listDate = ListDate;
            this.listener = listener;
        }

        @NonNull
        @Override
        public SubjectAdapter.SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_subject_names, parent, false);
            return new SubjectViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectAdapter.SubjectViewHolder holder, final int position) {
            final Subject subjectID = listArrayID.get(position);
            final Subject subjectname = listArrayName.get(position);
            final Subject date = listDate.get(position);
            holder.bind(subjectID, subjectname, date, listener);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog deleteDialog = new Dialog(context);
                    deleteDialog.setContentView(R.layout.dialog_delete_subject);
                    ImageButton btnConfirm = deleteDialog.findViewById(R.id.btnConfirm);
                    ImageButton btnCancel = deleteDialog.findViewById(R.id.btnCancel);
                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteSubject(subjectID.getSubjectID(), subjectname.getSubjectname(), date.getTime(), position);
                            deleteDialog.dismiss();
                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.cancel();
                        }
                    });
                    deleteDialog.show();
                }
            });

        }

        public class SubjectViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout list_item_subject_id;
            TextView textSubjectId;
            TextView textSubject;
            TextView textDate;
            ImageButton btnDelete;

            public SubjectViewHolder(View itemView) {
                super(itemView);
                list_item_subject_id = itemView.findViewById(R.id.list_item_subject_id);
                textSubjectId = itemView.findViewById(R.id.textSubjectId);
                textSubject = itemView.findViewById(R.id.textSubject);
                textDate = itemView.findViewById(R.id.textDate);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }

            public void bind(final Subject subjectID, Subject subjectname, Subject date, final OnItemClickListener listener) {
                textSubjectId.setText(subjectID.getSubjectID());
                textSubject.setText(subjectname.getSubjectname());
                textDate.setText(date.getTime());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(subjectID);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return listArrayID.size();
        }

        //--------------------- Delete subject button ------------------------------//

        //Prepare to edit it
        private void deleteSubject(final String subjectID, final String subjectname, String date, final int position) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Member").child("Teacher").child(Username).child("subject").child(subjectname);
            final DatabaseReference ref_post = FirebaseDatabase.getInstance().getReference("Posts");

            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Subject");
            DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Assign");
            final Query assignDelete = ref3.orderByChild("subjectID").equalTo(subjectID);
            final Query assignQuery2 = ref_post.orderByChild("subjectID").equalTo(subjectID);

            final Query subjectQuery2= ref2.orderByChild("subjectID").equalTo(subjectID);
            subjectQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {

                        assignQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getChildrenCount()!=0){
                                    for(DataSnapshot assign2Snapshot : dataSnapshot.getChildren()) {
                                        assign2Snapshot.getRef().removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        subjectSnapshot.getRef().removeValue();
                        listArrayID.remove(position);
                        listArrayName.remove(position);
                        listDate.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, listArrayID.size());
                        Log.d("Delete subject", "Subject has been deleted");
                        Toast.makeText(context, "Subject has been deleted.", Toast.LENGTH_SHORT).show();

                        ref.removeValue();

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }
    //**************************************************************************************************************************************************//


    //*************************************************************************************************************************************************//
    //-------------------- Dialog Add Subject -----------------------------------------//
    private void showAddItemDialog(final Context c) {
        final Dialog addSubjectDialog = new Dialog(c);
        addSubjectDialog.setContentView(R.layout.dialog_add_subject);
        final EditText editextSubjectID = addSubjectDialog.findViewById(R.id.editextSubjectID);
        final EditText editextSubjectName = addSubjectDialog.findViewById(R.id.editextSubjectName);
        final ImageButton btnAddSubject = addSubjectDialog.findViewById(R.id.btnAddSubject);
        ImageButton btnCancel = addSubjectDialog.findViewById(R.id.btnCancel);

        btnAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                final String currentDate = simpleDataFormat.format(calendar.getTime());
                btnAddSubject.setEnabled(false);
                table_Checksubject=table_subject.getRef().child(Username).child("subject");
                final String SubjectID = editextSubjectID.getText().toString();
                final String SubjectName = editextSubjectName.getText().toString();

                if (editextSubjectID.getText().toString().isEmpty()) {
                    Toast.makeText(c, "Please enter subject id", Toast.LENGTH_SHORT).show();
                    btnAddSubject.setEnabled(true);

                } else if (editextSubjectName.getText().toString().isEmpty()) {
                    Toast.makeText(c, "Please enter subject name", Toast.LENGTH_SHORT).show();
                    btnAddSubject.setEnabled(true);

                } else if(!SubjectID.trim().matches(regexStr)){
                    Toast.makeText(getContext(), "SubjectID must be numeric only", Toast.LENGTH_SHORT).show();
                    btnAddSubject.setEnabled(true);

                } else if (SubjectID.length()<6) {
                    Toast.makeText(c, "Minimum length of SubjectID is 6", Toast.LENGTH_SHORT).show();
                    btnAddSubject.setEnabled(true);

                } else if (SubjectID.length()>12) {
                    Toast.makeText(c, "Maximum length of SubjectID is 12", Toast.LENGTH_SHORT).show();
                    btnAddSubject.setEnabled(true);

                } else if (SubjectName.length()>30) {
                    Toast.makeText(c, "Maximum length of SubjectName is 30", Toast.LENGTH_SHORT).show();
                    btnAddSubject.setEnabled(true);

                } else if (SubjectName.length()<6) {
                    Toast.makeText(c, "Minimum length of SubjectName is 6", Toast.LENGTH_SHORT).show();
                    btnAddSubject.setEnabled(true);

                } else{
                    table_subject3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount()==0){
                                Subject subject = new Subject(SubjectID, SubjectName, Username, currentDate);
                                table_subject.child(Username).child("subject").child(SubjectName).setValue(subject);
                                Toast.makeText(c, "Subject add completed", Toast.LENGTH_SHORT).show();
                                table_subject2.child(SubjectName).setValue(subject);
                                //table_subject2.child(editextSubjectName.getText().toString()).setValue(subject);
                                addSubjectDialog.dismiss();
                                btnAddSubject.setEnabled(true);

                            } else if (dataSnapshot.child(SubjectName).exists()) {
                                Toast.makeText(getContext(), "This subject already exist", Toast.LENGTH_SHORT).show();
                                btnAddSubject.setEnabled(true);
                                //addSubjectDialog.cancel();

                            } else{
                                Integer i=0;
                                for(DataSnapshot subjectSnapshot:dataSnapshot.getChildren()){
                                    Subject subject = subjectSnapshot.getValue(Subject.class);
                                    String string = subject.getSubjectID();
                                    //Log.d(TAG,string);
                                    ++i;
                                    if(SubjectID.equals(string)){
                                        Toast.makeText(c, "Subject ID already exists", Toast.LENGTH_SHORT).show();
                                        i=0;
                                        break;
                                    }
                                }
                                if(i!=0){
                                    Subject subject = new Subject(SubjectID, SubjectName, Username, currentDate);
                                    table_subject.child(Username).child("subject").child(SubjectName).setValue(subject);
                                    Toast.makeText(c, "Subject add completed", Toast.LENGTH_SHORT).show();
                                    table_subject2.child(SubjectName).setValue(subject);

                                    Postdata postdata = new Postdata(SubjectName, SubjectID, currentDate);
                                    table_post.child(SubjectName).setValue(postdata);

                                    addSubjectDialog.dismiss();
                                    btnAddSubject.setEnabled(true);
                                }
                                else{
                                    btnAddSubject.setEnabled(true);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                /*
                table_Checksubject.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String SubjectID=editextSubjectID.getText().toString();
                        //Check if editText is empty
                         if(dataSnapshot.getChildrenCount()==0){
                            Subject subject = new Subject(editextSubjectID.getText().toString(), editextSubjectName.getText().toString(), Username, currentDate);
                            table_subject.child(Username).child("subject").child(editextSubjectName.getText().toString()).setValue(subject);
                            Toast.makeText(c, "Subject add completed", Toast.LENGTH_SHORT).show();
                            table_subject2.child(editextSubjectName.getText().toString()).setValue(subject);
                            //table_subject2.child(editextSubjectName.getText().toString()).setValue(subject);
                            addSubjectDialog.dismiss();
                            btnAddSubject.setEnabled(true);
                        }
                        else{
                            Integer i=0;
                            for(DataSnapshot subjectSnapshot:dataSnapshot.getChildren()){
                                Subject subject = subjectSnapshot.getValue(Subject.class);
                                String string = subject.getSubjectID();
                                //Log.d(TAG,string);
                                ++i;
                                if(SubjectID.equals(string)){
                                    Toast.makeText(c, "Subject ID already exists", Toast.LENGTH_SHORT).show();
                                    i=0;
                                    break;
                                }
                            }
                            if(i!=0){
                                Subject subject = new Subject(editextSubjectID.getText().toString(), editextSubjectName.getText().toString(), Username, currentDate);
                                table_subject.child(Username).child("subject").child(editextSubjectName.getText().toString()).setValue(subject);
                                Toast.makeText(c, "Subject add completed", Toast.LENGTH_SHORT).show();
                                table_subject2.child(editextSubjectName.getText().toString()).setValue(subject);
                                addSubjectDialog.dismiss();
                                btnAddSubject.setEnabled(true);


                            }
                            else{
                                btnAddSubject.setEnabled(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                */
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubjectDialog.cancel();
            }
        });
        addSubjectDialog.show();
    }

    //*************************************************************************************************************************************************//

    //Flot action button: Add subject
    private void fabButtomAddSubject() {
        // Hide Floating Action Button when scrolling in Recycler View
        recyclerViewSubject.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });
        //<-----------------------------------------------------------//>
    }

    @Override
    public void onClick(View v) {
        if (v == searchBtn) {
            if (searchField.getText().toString().isEmpty()) {
                //Toast.makeText(getActivity(), "Please enter subjectname", Toast.LENGTH_SHORT).show();
                GetSubjectFirebase();
            } else {
                Toast.makeText(getActivity(), "Search", Toast.LENGTH_SHORT).show();
                String searchText = searchField.getText().toString().toUpperCase();
                GetSearchFirebase(searchText);
            }
        } else if (v == fab) {
            Toast.makeText(getContext(), "Add a new subject", Toast.LENGTH_SHORT).show();
            showAddItemDialog(getContext());
        }
    }

}