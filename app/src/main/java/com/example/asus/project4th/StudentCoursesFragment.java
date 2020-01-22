package com.example.asus.project4th;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Student;
import com.example.asus.project4th.Model.Subject;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class StudentCoursesFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    private TextView textSubjectID, textSubjectname;
    private RecyclerView recyclerViewSubject;
    private FloatingActionButton fab;
    private View view;
    private FirebaseDatabase database;
    private DatabaseReference table_subject,table_subject2,table_Student,table_Teacher;
    private SubjectAdapter subjectAdapter;
    private List<Subject> listSubjectID;
    private List<Subject> listSubjectName;
    private List<Subject> listDate;
    private List<Subject> listteacherusername;
    private FirebaseRecyclerAdapter recyclerAdapter;
    private Dialog addSubjectDialog;
    private SubjectAdapter.OnItemClickListener listener;
    private static final String[] ChooseActivity = {"Assignment", "Announcement"};
    private String Username;
    private String Name;
    private static String Username_del;
    private static String TeacherUsername;

    public StudentCoursesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_courses1, container, false);
        if (getArguments() != null) {
            Username = getArguments().getString("Username");
            Name = getArguments().getString("Name");
            //Toast.makeText(getContext(), Username, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Bundle == null", Toast.LENGTH_SHORT).show();
        }

        initInstance();
        fabButtomAddSubject();

        //Toast.makeText(getContext(), Name, Toast.LENGTH_SHORT).show();
        return view;
    }


    private void initInstance() {
        SharedPreferences prefs2 = getActivity().getSharedPreferences("PREF_NAME2", Context.MODE_PRIVATE);
        String usernamePref2 = prefs2.getString("USERNAME",null);
        Username_del = usernamePref2;
        //--------------------- Firebase ----------------------------//
        database = FirebaseDatabase.getInstance();
        table_Teacher = database.getReference("Member").child("Teacher");
        table_Student = database.getReference("Member").child("Student");
        table_subject2 = database.getReference("Subject");

        //-------------------- Search --------------------------//
        textSubjectID = view.findViewById(R.id.textSubjectId);
        textSubjectname = view.findViewById(R.id.textSubject);


        SharedPreferences mPrefs = this.getActivity().getSharedPreferences("PREF_NAME2", MODE_PRIVATE); //add key
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        Name = mPrefs.getString("NAME", null);

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
        listteacherusername = new ArrayList<>();
        //subjectAdapter = new SubjectAdapter(listSubjectID, listSubjectName, listener);
        subjectAdapter = new SubjectAdapter(getContext(), listSubjectID, listSubjectName, listDate, listteacherusername, new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Subject subject) {
                //Choose assignment or announcement
                /*Toast.makeText(getContext(), "Subject is clicked", Toast.LENGTH_SHORT).show();
                Intent isubject = new Intent(getActivity(), StudentExamsActivity.class);
                isubject.putExtra("subjectID", subject.getSubjectID());
                isubject.putExtra("subjectName", subject.getSubjectname());
                isubject.putExtra("Username", Username);
                isubject.putExtra("name", name);
                startActivity(isubject);
                */
                dialogselect(subject.getUsername(),subject.getSubjectID(),subject.getSubjectname(),subject.getTeacherusername());
            }
        });
        GetSubjectFirebase();

    }

    public void dialogselect(final String username,final String subjectid,final String subjectname,final String teacherUsername){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(),R.style.AlertDialogCustom));
        builder.setTitle("Select Option");
        builder.setItems(ChooseActivity, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected = ChooseActivity[which];
                if(selected.equals("Assignment")){
                    Intent isubject = new Intent(getActivity(), StudentExamsActivity.class);
                    isubject.putExtra("Username", username);
                    isubject.putExtra("TeacherUsername", teacherUsername);
                    isubject.putExtra("subjectID", subjectid);
                    isubject.putExtra("subjectName", subjectname);
                    startActivity(isubject);
                    Toast.makeText(getContext(), "You choose " +
                            "Assignment", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(), teacherUsername, Toast.LENGTH_SHORT).show();
                }
                else if(selected.equals("Announcement")){
                    Intent isubject2 = new Intent(getActivity(), StudentPostActivity1.class);
                    isubject2.putExtra("Username", username);
                    isubject2.putExtra("subjectID", subjectid);
                    isubject2.putExtra("subjectName", subjectname);
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

    //***************************************************** Subject lists ********************************************************************************//
    //<------------------------ Firebase search field and display list ------------------------------------>//
    void GetSubjectFirebase() {

        //Clear ListSubject
        listSubjectID.clear();
        listSubjectName.clear();
        listDate.clear();

        table_subject=table_Student.getRef().child(Username).child("subject");
        Query searchQuery = table_subject.orderByChild("username").equalTo(Username);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Subject subject = new Subject();
                Subject subject = dataSnapshot.getValue(Subject.class);
                //Add to ArrayList
                listSubjectID.add(subject);
                listSubjectName.add(subject);
                listDate.add(subject);

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

    //---------------- Subject List -------------------------------------------------//
    public static class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

        List<Subject> listArrayID;
        List<Subject> listArrayName;
        List<Subject> listDate;
        List<Subject> listteacherusername;
        final OnItemClickListener listener;
        private Context context;

        public interface OnItemClickListener {
            void onItemClick(Subject subject);
        }

        public SubjectAdapter(Context context, List<Subject> ListID, List<Subject> ListName, List<Subject> ListDate, List<Subject> ListTeacherUsername, OnItemClickListener listener) {
            this.context = context;
            this.listArrayID = ListID;
            this.listArrayName = ListName;
            this.listDate = ListDate;
            this.listteacherusername = ListTeacherUsername;
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
        private void deleteSubject(final String subjectID, String subjectname, String date, final int position) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Member").child("Student").child(Username_del);
            Query subjectQuery = ref.child("subject").orderByChild("subjectID").equalTo(subjectID);
            subjectQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                        subjectSnapshot.getRef().removeValue();
                            listArrayID.remove(position);
                            listArrayName.remove(position);
                            listDate.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, listArrayID.size());
                            Log.d("Delete subject", "Subject has been deleted");
                            Toast.makeText(context, "Subject has been deleted.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });
        }
    }

    //*************************************************************************************************************************************************//
    private void showAddItemDialog(final Context c) {
        final Dialog addSubjectDialog = new Dialog(c);
        addSubjectDialog.setContentView(R.layout.dialog_add_subject_stu);
        final EditText editextSubjectID = addSubjectDialog.findViewById(R.id.editextSubjectID);
        final EditText editextSubjectName = addSubjectDialog.findViewById(R.id.editextSubjectName);
        editextSubjectName.setEnabled(false);
        editextSubjectName.setVisibility(View.GONE);
        final ImageButton btnAddSubject = addSubjectDialog.findViewById(R.id.btnAddSubject);
        ImageButton btnCancel = addSubjectDialog.findViewById(R.id.btnCancel);

        btnAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final String subjectname=editextSubjectName.getText().toString();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                final String currentDate = simpleDataFormat.format(calendar.getTime());
                btnAddSubject.setEnabled(false);
                final DatabaseReference table_subjectstd = database.getReference("Member").child("Student").child(Username);
                final DatabaseReference Allsubject = database.getReference("StdinSubject");
                final DatabaseReference refSubject = database.getReference("Subject");
                final DatabaseReference refSubject2 = database.getReference("Subjectstd");
                final DatabaseReference CheckExist = database.getReference("Subjectstd");
                refSubject.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String SubjectID=editextSubjectID.getText().toString();
                        //String SubjectName=editextSubjectName.getText().toString();
                        if (editextSubjectID.getText().toString().isEmpty()) {
                            Toast.makeText(c, "Please enter subject id", Toast.LENGTH_SHORT).show();
                            btnAddSubject.setEnabled(true);
                        } else if(dataSnapshot.getChildrenCount()==0){
                            Toast.makeText(c, "None any subject in this app", Toast.LENGTH_SHORT).show();
                            btnAddSubject.setEnabled(true);
                            addSubjectDialog.dismiss();
                        } else {
                            Integer i=0;
                            for(DataSnapshot subjectSnapshot:dataSnapshot.getChildren()){
                                Subject subject = subjectSnapshot.getValue(Subject.class);
                                String string = subject.getSubjectID();
                                //String string2 = subject.getSubjectname();
                                String teacher = subject.getUsername();
                                //Log.d(TAG,string);
                                ++i;
                                if(SubjectID.equals(string)){
                                    String sub = subject.getSubjectname();
                                    Subject subject3 = new Subject(editextSubjectID.getText().toString(), subject.getSubjectname(), Username, Name, currentDate, teacher);
                                    table_subjectstd.child("subject").child(sub).setValue(subject3);
                                    Toast.makeText(c, "Subject add completed", Toast.LENGTH_SHORT).show();
                                    refSubject2.child(Username).child(subject.getSubjectname()).setValue(subject3);
                                    i=0;
                                    addSubjectDialog.dismiss();
                                    break;
                                }
                                /*
                                if(SubjectID.equals(string) && SubjectName.equals(string2)){
                                    //Subject subject3 = new Subject(editextSubjectID.getText().toString(), editextSubjectName.getText().toString(), Username, currentDate);
                                    Subject subject3 = new Subject(editextSubjectID.getText().toString(), editextSubjectName.getText().toString(), Username, Name, currentDate, teacher);
                                    table_subjectstd.child("subject").child(editextSubjectName.getText().toString()).setValue(subject3);
                                    Toast.makeText(c, "Subject add completed", Toast.LENGTH_SHORT).show();
                                    refSubject2.child(Username).child(editextSubjectName.getText().toString()).setValue(subject3);
                                    i=0;
                                    addSubjectDialog.dismiss();
                                    break;
                                }
                                */
                            }
                            if(i!=0){
                                //Toast.makeText(c, i, Toast.LENGTH_SHORT).show();
                                Toast.makeText(c, "There is no Subject", Toast.LENGTH_SHORT).show();
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
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubjectDialog.cancel();
            }
        });
        addSubjectDialog.show();
    }

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
    }

    @Override
    public void onClick(View v) {
        if (v == fab) {
            Toast.makeText(getContext(), "Add a new subject", Toast.LENGTH_SHORT).show();
            showAddItemDialog(getContext());
        }
    }
}