package com.example.asus.project4th;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.project4th.Model.Answer;
import com.example.asus.project4th.Model.Assign;
import com.example.asus.project4th.Model.Choice2;
import com.example.asus.project4th.Model.Postdata;
import com.example.asus.project4th.Model.Subject;
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

public class TeacherMenuExamsActivity extends AppCompatActivity implements View.OnClickListener {


    //<------------------------------------------------>
    final String TAG = "TTwTT";
    //-- Toolbar --***//
    private Toolbar toolbar;
    //-- DrawerLayout --***//
    private DrawerLayout drawerLayout;
    private TextView textUsername;
    private TextView textStatus;
    private TextView textName;
    private NavigationView navigationView;
    private View headerView;
    //<------------------------------------------------>

    private boolean doubleBackToExitPressedOnce;
    private FloatingActionButton fab;
    private Button setAssign;
    private RecyclerView recyclerViewAssign;
    private FirebaseDatabase database;
    private DatabaseReference table_Teacher,table_assign,table_assign2,table_assignment,table_post;
    private List<Subject> listSubjectID;
    private List<Assign> listAssignName;
    private List<Assign> listAssignDate;
    private List<Assign> listAssignStatus;
    private AssignAdapter assignAdapter;
    private String subjectID;
    private String subjectname;
    private BottomSheetDialog bottomSheetMenu;
    private View sheetView;
    private LinearLayout createAssign;
    private LinearLayout editAssign;
    private LinearLayout checkScore;
    private LinearLayout setLocationSystem;
    private LinearLayout delete;
    private static String Username;
    private String specialChar = "[a-zA-Z0-9_]*";
    private TextView mItemSelected;
    private String[] listItems;
    private ArrayList<String> listofAssignName = new ArrayList<>();
    private ArrayList<String> listofQuestionShow = new ArrayList<>();
    private ArrayList<String> listofStatus = new ArrayList<>();
    private ArrayList<Integer> mUserItems = new ArrayList<>();
    private boolean[] checkedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_menu_exams);
        initInstance();
        fabButtomAddSubject();
        backToolbar();

        setAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_Assign();
            }
        });

    }

    private void initInstance() {

        //-- Toolbar --***//
        toolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();

        Username = intent.getStringExtra("username");
        subjectID = intent.getStringExtra("subjectID");
        subjectname = intent.getStringExtra("subjectname");
        setAssign = findViewById(R.id.assignsetting);
        toolbar.setTitle(subjectname);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        //-----------------------------------------------//
        //------------ Receive Intent from SignIn ------------***//
        headerView = navigationView.getHeaderView(0);
        textUsername = headerView.findViewById(R.id.txtUsername);
        textStatus = headerView.findViewById(R.id.txtStatus);
        textName = headerView.findViewById(R.id.txtName);
        //------------------------------------------------------//

        //----- Firebase ------//
        database = FirebaseDatabase.getInstance();
        table_Teacher = database.getReference("Member").child("Teacher");
        table_post = database.getReference("Posts");
        table_assign = database.getReference().child("Assign").child(subjectname);
        table_assign2 = database.getReference().child("Assign");
        table_assignment = database.getReference("Member").child("Teacher");

        //---------- Fad Button -------------//
        fab = findViewById(R.id.fabPlus);
        fab.setOnClickListener(this);

        //--------------- RecyclerView --------------------//
        recyclerViewAssign = findViewById(R.id.recyclerViewAssign);
        recyclerViewAssign.setHasFixedSize(true);
        RecyclerView.LayoutManager LM = new LinearLayoutManager(this);
        recyclerViewAssign.setLayoutManager(LM);
        recyclerViewAssign.setItemAnimator(new DefaultItemAnimator());
        //recyclerViewAssign.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        //recyclerViewAssign.setAdapter(recyclerAdapter);

        //----------------- Subject list -------------------------------//
        listSubjectID = new ArrayList<>();
        listAssignName = new ArrayList<>();
        listAssignDate = new ArrayList<>();
        listAssignStatus = new ArrayList<>();
        assignAdapter = new AssignAdapter(listAssignName,listAssignStatus,listAssignDate, new AssignAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Assign assign, final int position) {
                bottomSheetSelectMenu();
                displaySelectMenu();
                createAssign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(TeacherMenuExamsActivity.this, "Create assignment", Toast.LENGTH_SHORT).show();
                        String assignnamecheck=assign.getAssignname();
                        //DatabaseReference assignref = FirebaseDatabase.getInstance().getReference("Assign");
                        Intent iassign = new Intent(TeacherMenuExamsActivity.this, AddExamsActivity.class);
                        iassign.putExtra("assignname", assign.getAssignname());
                        iassign.putExtra("subjectID", subjectID);
                        iassign.putExtra("subjectName", subjectname);
                        startActivity(iassign);
                        bottomSheetMenu.dismiss();
                    }
                });
                editAssign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(TeacherMenuExamsActivity.this, "Edit assignment", Toast.LENGTH_SHORT).show();
                        //Intent iassign = new Intent(TeacherMenuExamsActivity.this, EditExamsActivity.class);
                        Intent iassign = new Intent(TeacherMenuExamsActivity.this, EditNoQuestionActivity.class);
                        iassign.putExtra("assignname", assign.getAssignname());
                        iassign.putExtra("subjectID", subjectID);
                        iassign.putExtra("subjectName", subjectname);
                        startActivity(iassign);
                        bottomSheetMenu.dismiss();
                    }
                });
                setLocationSystem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetMenu.dismiss();
                        Toast.makeText(TeacherMenuExamsActivity.this, "Set LocationBase " + assign.getLocation_status(), Toast.LENGTH_SHORT).show();
                        final DatabaseReference ref_status = table_assign.getRef().child(assign.getAssignname());
                        final String[] status = new String[]{assign.getAssignname()};

                        checkedItems = new boolean[status.length];

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TeacherMenuExamsActivity.this);
                        mBuilder.setTitle(R.string.dialog_location);
                        mBuilder.setMultiChoiceItems(status, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                                if(isChecked){
                                    mUserItems.add(position);
                                    ref_status.child("location_status").setValue("ready");
                                    Toast.makeText(TeacherMenuExamsActivity.this, assign.getAssignname()+ " Location Check has enabled", Toast.LENGTH_SHORT).show();
                                } else{
                                    mUserItems.remove((Integer.valueOf(position)));
                                    ref_status.child("location_status").setValue("prepare");
                                    Toast.makeText(TeacherMenuExamsActivity.this, assign.getAssignname()+ " Location Check has disabled", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                GetAssignFirebase();
                                dialogInterface.dismiss();
                            }
                        });

                        for(int i = 0; i < checkedItems.length; i++){
                            if(assign.getLocation_status().equals("ready")){
                                checkedItems[i] = true;
                            } else{
                                checkedItems[i] = false;
                            }
                        }

                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();


                    }
                });
                checkScore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(TeacherMenuExamsActivity.this, "Check scores of students", Toast.LENGTH_SHORT).show();
                        Intent iassign = new Intent(TeacherMenuExamsActivity.this, CheckScoresActivity.class);
                        iassign.putExtra("assignname", assign.getAssignname());
                        iassign.putExtra("subjectID", subjectID);
                        iassign.putExtra("subjectName", subjectname);
                        iassign.putExtra("Username", Username);
                        startActivity(iassign);
                        bottomSheetMenu.dismiss();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(TeacherMenuExamsActivity.this, "Delete assignment", Toast.LENGTH_SHORT).show();
                        final Dialog deleteDialog = new Dialog(TeacherMenuExamsActivity.this);
                        deleteDialog.setContentView(R.layout.dialog_delete_assign);
                        ImageButton btnConfirm = deleteDialog.findViewById(R.id.btnConfirm);
                        ImageButton btnCancel = deleteDialog.findViewById(R.id.btnCancel);
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteAssign(assign.getAssignname(), assign.getTime(), position);
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

                        bottomSheetMenu.dismiss();
                    }

                    //------------------ Delete Assign -----------------//
                    private void deleteAssign(final String assignname, String time, final int position) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Assign").child(subjectname);
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Posts");
                        //Query assignQuery = ref.child("Assign").orderByChild("assignname").equalTo(assign.getAssignname());
                        final Query assignQuery = ref.orderByChild("assignname").equalTo(assignname);

                        assignQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            //TODO;
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot assignSnapshot : dataSnapshot.getChildren()) {
                                    //Key Function for Delete
                                    Assign assign = new Assign();
                                    assign = assignSnapshot.getValue(Assign.class);
                                    if (assign.getAssignname().equals(assignname)){
                                        Log.e("delete_data", assignname);
                                        Log.e("data", assign.getAssignname());

                                        assignSnapshot.getRef().removeValue();
                                        //--------------//
                                        listAssignName.remove(position);
                                        listAssignDate.remove(position);

                                        //listSubjectID.remove(assign.getSubjectID());
                                        //assignAdapter.notifyItemRemoved(listAssignName.indexOf(assign.getAssignname()));
                                        //assignAdapter.notifyItemRangeChanged(listAssignName.indexOf(assign.getAssignname()), listAssignName.size());

                                        assignAdapter.notifyItemRemoved(position);
                                        assignAdapter.notifyItemRangeChanged(position, listAssignName.size());
                                        Log.d("Delete assignment", "Assignment has been deleted");
                                        Toast.makeText(TeacherMenuExamsActivity.this, "Assign has been deleted.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });
                    }
                    //--------------------------------------------------------------//
                });

            }
        });
        GetAssignFirebase();
    }


    //***************************************************** Assignment lists ********************************************************************************//
    //<------------------------ Firebase search field and display list ------------------------------------>//

    private void GetAssignFirebase() {

        listAssignName.clear();
        listAssignDate.clear();
        listAssignStatus.clear();

        Query searchQuery = table_assign.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Assign assign = new Assign();
                assign = dataSnapshot.getValue(Assign.class);
                //Add to ArrayList
                listAssignName.add(assign);
                listAssignDate.add(assign);
                listAssignStatus.add(assign);
                //Add List into Adapter/RecyclerView

                recyclerViewAssign.setAdapter(assignAdapter);
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
        listAssignName.clear();
        listAssignDate.clear();

        Query searchQuery = table_assign.orderByChild("subjectID").equalTo(subjectID);
        searchQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Assign assign = new Assign();
                assign = dataSnapshot.getValue(Assign.class);

                if (assign.getAssignname().contains(searchText)) {
                    //Add to ArrayList
                    listAssignName.add(assign);
                    listAssignDate.add(assign);
                }

                //Add List into Adapter/RecyclerView
                recyclerViewAssign.setAdapter(assignAdapter);
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
    public static class AssignAdapter extends RecyclerView.Adapter<TeacherMenuExamsActivity.AssignAdapter.AssignHolder> {

        List<Assign> listAssignName;
        List<Assign> listAssignStatus;
        List<Assign> listAssignDate;
        final OnItemClickListener listener;
        private Context context;

        public interface OnItemClickListener {
            void onItemClick(Assign assign, int position);
        }

        public AssignAdapter(List<Assign> listAssignName, List<Assign> listAssignStatus, List<Assign> listAssignDate, OnItemClickListener listener) {
            this.listAssignName = listAssignName;
            this.listAssignStatus = listAssignStatus;
            this.listAssignDate = listAssignDate;
            this.listener = listener;
        }

        @NonNull
        @Override
        public AssignAdapter.AssignHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_assign_names, parent, false);
            return new AssignHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AssignAdapter.AssignHolder holder, int position) {
            Assign assignname = listAssignName.get(position);
            Assign assigndate = listAssignDate.get(position);
            Assign assignstatus = listAssignStatus.get(position);
            holder.bind(assignname, assignstatus, assigndate, listener, position);
        }

        @Override
        public int getItemCount() {
            return listAssignName.size();
        }

        public class AssignHolder extends RecyclerView.ViewHolder {
            TextView textAssignName;
            TextView textDate;
            RelativeLayout list_assign_names;

            public AssignHolder(View itemView) {
                super(itemView);
                list_assign_names = itemView.findViewById(R.id.list_item_assign_name);
                textAssignName = itemView.findViewById(R.id.textAssignName);
                textDate = itemView.findViewById(R.id.textDate);
            }

            public void bind(final Assign assignname, Assign assinstatus, Assign assigndate, final OnItemClickListener listener, final int position) {
                textAssignName.setText(assignname.getAssignname());
                textDate.setText(assigndate.getTime());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(assignname, position);
                    }
                });
            }
        }
    }

    private void list_Assign(){
        listofAssignName.clear();
        listofStatus.clear();
        final DatabaseReference ref = database.getReference("Assign").child(subjectname);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String assign_name = postSnapshot.child("assignname").getValue(String.class);
                        Assign assign = new Assign();
                        assign = postSnapshot.getValue(Assign.class);
                        listofAssignName.add(assign.getAssignname());
                        listofStatus.add(assign.getAssign_status());
                    }
                    Log.e("Assign All :",listofAssignName.toString());
                    Log.e("Assign All Status:",listofStatus.toString());


                    checkedItems = new boolean[listofStatus.size()];

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(TeacherMenuExamsActivity.this);
                    mBuilder.setTitle(R.string.dialog_assignment);
                    mBuilder.setMultiChoiceItems(listofAssignName.toArray(new String[listofAssignName.size()])
                            , checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                                    if(isChecked){
                                        mUserItems.add(position);
                                        checkedItems[position] = true;
                                        listofStatus.set(position,"open");
                                        //ref.child(listofAssignName.get(position)).child("assign_status").setValue("open");
                                        Toast.makeText(TeacherMenuExamsActivity.this, "Assignment: "+ (position+1) +" is open", Toast.LENGTH_SHORT).show();
                                    } else{
                                        mUserItems.remove((Integer.valueOf(position)));
                                        checkedItems[position] = false;
                                        listofStatus.set(position,"closed");
                                        //ref.child(listofAssignName.get(position)).child("assign_status").setValue("closed");
                                        Toast.makeText(TeacherMenuExamsActivity.this, "Assignment: "+ (position+1) +" is closed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                    mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            for(int i = 0; i < checkedItems.length;i++){
                               ref.child(listofAssignName.get(i)).child("assign_status").setValue(listofStatus.get(i));
                            }
                            Toast.makeText(TeacherMenuExamsActivity.this, "Set Assignment status completed", Toast.LENGTH_SHORT).show();
                            GetAssignFirebase();
                            dialogInterface.dismiss();
                        }
                    });

                    mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    });

                    for (int i = 0; i < checkedItems.length; i++) {
                        if(listofStatus.get(i).equals("open")){
                            checkedItems[i] = true;
                        } else if(listofStatus.get(i).equals("closed")){
                            checkedItems[i] = false;
                        }
                    }

                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();

                } else{}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //--------------- Bottom sheet dialog: Select menu ---------------------//
    private void bottomSheetSelectMenu() {
        bottomSheetMenu = new BottomSheetDialog(this);
        sheetView = TeacherMenuExamsActivity.this.getLayoutInflater().inflate(R.layout.bottom_sheet_menu, null);
        bottomSheetMenu.setContentView(sheetView);
        initSelectMenu();
    }

    private void initSelectMenu() {
        createAssign = sheetView.findViewById(R.id.menuCreateAssign);
        editAssign = sheetView.findViewById(R.id.menuEditAssign);
        setLocationSystem = sheetView.findViewById(R.id.menuSetLocation);
        checkScore = sheetView.findViewById(R.id.menuScore);
        delete = sheetView.findViewById(R.id.menuDelete);
    }

    private void displaySelectMenu() {
        //Toast.makeText(TeacherMenuExamsActivity. this, "Assignment is clicked", Toast.LENGTH_SHORT).show();
        bottomSheetMenu.show();
    }

    private void showAddItemDialog(final Context c) {
        final Dialog addAssignDialog = new Dialog(c);
        addAssignDialog.setContentView(R.layout.dialog_add_assign_name);
        final EditText editextAssignName = addAssignDialog.findViewById(R.id.editextAssignname);
        ImageButton btnAddAssign = addAssignDialog.findViewById(R.id.btnAddAssign);
        ImageButton btnCancel = addAssignDialog.findViewById(R.id.btnCancel);
        //SAVE
        btnAddAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDataFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                final String currentDate = simpleDataFormat.format(calendar.getTime());
                table_assign2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String AssignName=editextAssignName.getText().toString();
                        String postNum="0";
                        //Check if editText is empty
                        if (AssignName.isEmpty()) {
                            Toast.makeText(c, "Please enter assignment name", Toast.LENGTH_SHORT).show();
                        } else if (dataSnapshot.child(subjectname).child(AssignName).exists()) {
                            Toast.makeText(c, "Assignment name already exist", Toast.LENGTH_SHORT).show();
                        } else if (dataSnapshot.child(subjectname).child(AssignName.trim()).exists()){
                            Toast.makeText(c, "Assignment name already exist", Toast.LENGTH_SHORT).show();
                        } else if (!AssignName.matches(specialChar)){
                            Toast.makeText(c, "Special characters cannot use", Toast.LENGTH_SHORT).show();
                        } else {
                            Assign assign = new Assign(AssignName, subjectID, "prepare", "closed",currentDate);
                            //table_assign2.child(subjectname).child("subjectNumber").setValue(assign.getSubjectID());
                            table_assign2.child(subjectname).child(AssignName).setValue(assign);

                            //Postdata postdata = new Postdata(subjectname, subjectID, currentDate);
                            //table_post.child(subjectname).setValue(postdata);

                            Toast.makeText(c, "Assignment add completed", Toast.LENGTH_SHORT).show();
                            addAssignDialog.dismiss();
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
                addAssignDialog.cancel();
            }
        });
        addAssignDialog.show();
    }


    //Flot action button: Add subject
    private void fabButtomAddSubject() {
        // Hide Floating Action Button when scrolling in Recycler View
        recyclerViewAssign.addOnScrollListener(new RecyclerView.OnScrollListener() {
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


    //--------------------- Back press Toolbar -----------------------//
    private void backToolbar() {
        //toolbar.setTitle(getString(R.string.assignment));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //------------------------------- Back Press --------------------------------------//
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


    @Override
    public void onClick(View v) {
        if (v == fab) {
            Toast.makeText(this, "Add a new assignment", Toast.LENGTH_SHORT).show();
            showAddItemDialog(this);
        }
    }
}

