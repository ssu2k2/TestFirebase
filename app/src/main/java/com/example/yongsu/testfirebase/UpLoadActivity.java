package com.example.yongsu.testfirebase;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class UpLoadActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private final int PERMISSION_REQUEST = 100;
    private final int REQUEST_TAKE_GALLERY = 12;

    FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    ProgressDialog progDialog ;
    int index = 1;

    String folder = "app_test";
    String fileName;

    Button btnSelect;
    ListView lvList;
    RecyclerView recyclerView;
    RecyclerViewDemoAdapter recyclerAdapter;
    StaggeredGridLayoutManager  layoutManager;


    ArrayList<ImageInfo> alImageInfo;
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_load);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getEmail();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://testfirebase-7e607.appspot.com/");

        progDialog = new ProgressDialog(UpLoadActivity.this);

        //lvList = (ListView)findViewById(R.id.lvList);
        alImageInfo = new ArrayList<>();
        //listAdapter = new ListAdapter(UpLoadActivity.this, R.layout.items, alImageInfo);
        //lvList.setAdapter(listAdapter);

//        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ShowDetail(position);
//            }
//        });

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerAdapter = new RecyclerViewDemoAdapter(alImageInfo,this);
//        layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
//        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
//        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);


        btnSelect = (Button)findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(i, REQUEST_TAKE_GALLERY);
            }
        });

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE , android.Manifest.permission.WRITE_EXTERNAL_STORAGE} ,
                        PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE , android.Manifest.permission.WRITE_EXTERNAL_STORAGE} ,
                        PERMISSION_REQUEST);
            }
        } else if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE , android.Manifest.permission.WRITE_EXTERNAL_STORAGE} ,
                        PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE , android.Manifest.permission.WRITE_EXTERNAL_STORAGE} ,
                        PERMISSION_REQUEST);
            }
        } else{
        }

        databaseReference.child("imageInfo").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ImageInfo i = dataSnapshot.getValue(ImageInfo.class);
                alImageInfo.add(i);
                recyclerAdapter.notifyDataSetChanged();
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
    AlertDialog alert;
    private void ShowDetail(int pos){
        ImageInfo i = alImageInfo.get(pos);
        final AlertDialog.Builder builder = new AlertDialog.Builder(UpLoadActivity.this);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout llDialog = (LinearLayout)inflater.inflate(R.layout.dialog_layout, null);

        ImageView ivDialog = (ImageView)llDialog.findViewById(R.id.ivImage);

        ((TextView)llDialog.findViewById(R.id.tvText)).setText("TEST");

        ((Button)llDialog.findViewById(R.id.btnClick)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        Glide.with(UpLoadActivity.this)
                .using(new FirebaseImageLoader())
                .load(mStorageRef.child(folder+"/" + i.fileName))
                .into(ivDialog);

        builder.setView(llDialog);
        alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.show();
    }
    public  class ListItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivImage;
        public ImageView ivIcon;
        public TextView tvText;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }
    public class RecyclerViewDemoAdapter extends RecyclerView.Adapter {
        Context context;
        ArrayList<ImageInfo> mItems;
        public RecyclerViewDemoAdapter(ArrayList items, Context mContext) {
            mItems = items;
            context = mContext;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // 새로운 뷰를 만든다
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler,parent,false);
            RecyclerView.ViewHolder holder = new ListItemViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ImageInfo i = mItems.get(position);

            ((ListItemViewHolder)holder).tvText.setText("TEST Image");
            ((ListItemViewHolder)holder).ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowDetail(position);
                }
            });
            Glide.with(UpLoadActivity.this)
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef.child(thumb+"/" + i.fileName))
                    .into(((ListItemViewHolder)holder).ivImage);

        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }


    ListAdapter listAdapter;
    private class ListAdapter extends ArrayAdapter<ImageInfo> {
        LayoutInflater inflater;
        Context context;
        int res;

        public ListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ImageInfo> objects) {
            super(context, resource, objects);
            this.context = context;
            this.res = resource;
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(this.res, null);
            }

            ImageInfo i = getItem(position);

            //LoadFile(thumb + "/" + i.fileName , i.fileName, (ImageView)convertView.findViewById(R.id.lvProfile));

            ((TextView)convertView.findViewById(R.id.tvText0)).setText("USER:" + i.user);
            ((TextView)convertView.findViewById(R.id.tvText1)).setText("LAT:" + i.latitude);
            ((TextView)convertView.findViewById(R.id.tvText2)).setText("LON:" + i.longitude);
            ((TextView)convertView.findViewById(R.id.tvText3)).setText(":");

            Glide.with(UpLoadActivity.this)
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef.child(thumb+"/" + i.fileName))
                    .into((ImageView)convertView.findViewById(R.id.lvProfile));

            return convertView;
        }
    }
    private void LoadFile(String storagePath , String filename,  final ImageView ivImage){
        StorageReference imageRef = mStorageRef.child(storagePath);
        try {
            final File imageFile = File.createTempFile(filename,"");
            Log.d(TAG, "Download Image : " + filename);
            imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
                    ivImage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpLoadActivity.this, "Fail to Download", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                } else {
                    Toast.makeText(this, "권한사용을 동의해 주세요", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Permission Not Granted");
                    finish();
                }
                break;
        }
        return;
    }

    boolean isSuccess =true;
    UploadTask uploadTask;
    UploadTask thumbTask;
    String thumb = "app_thumb";

    private boolean UploadFIle(final Uri uri){
        final StorageReference imageRef = mStorageRef.child(folder+ "/" + uri.getLastPathSegment());
        final StorageReference thumbRef = mStorageRef.child(thumb + "/" + uri.getLastPathSegment());
        progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDialog.setTitle("Uploading...");
        progDialog.show();

        Bitmap bitmap = getPreview(uri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();
        thumbTask = thumbRef.putBytes(data);

        uploadTask = imageRef.putFile(uri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                Uri uri = taskSnapshot.getDownloadUrl();
                Log.d(TAG, "UpLoad Complete:" + uri.getPath());
                progDialog.dismiss();
                databaseReference.child("imageInfo").push().setValue(i);
                isSuccess = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpLoadActivity.this, "Photo File Upload Fail!!!", Toast.LENGTH_SHORT).show();
                progDialog.dismiss();
                isSuccess = false;
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Progress :" + progress);
                progDialog.setProgress((int)progress);
            }
        });
        return isSuccess;
    }


    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    Bitmap getPreview(Uri uri) {
        File image = new File(getPath(uri));

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / 512;
        return BitmapFactory.decodeFile(image.getPath(), opts);
    }

    ImageInfo i;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_TAKE_GALLERY){
            Uri uri = data.getData();

            //ivImage.setImageURI(uri);
            String [] fotoProyeccion ={MediaStore.Images.ImageColumns.LATITUDE,MediaStore.Images.ImageColumns.LONGITUDE};

            Cursor fotoDatos = MediaStore.Images.Media.query(getBaseContext().getContentResolver(), uri, fotoProyeccion);

            int latt = fotoDatos.getColumnIndex (MediaStore.Images.ImageColumns.LATITUDE);
            int longi= fotoDatos.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE);

            // Fetch first row
            fotoDatos.moveToFirst();

            // Get the actual values returned
            double latval = fotoDatos.getDouble(latt);
            double lonval = fotoDatos.getDouble(longi);

            String resultado = String.valueOf(latval)+"---"+String.valueOf(lonval);
            Log.d(TAG, "Select Photo :" + getPath(uri));
            Log.d(TAG, "Select Photo :" + resultado);
            i = new ImageInfo(uri.getLastPathSegment());
            i.latitude = latval;
            i.longitude = lonval;
            i.user = user;

            UploadFIle(uri);
        }
    }

}
