package com.rutcs.chrislopresti.photos27;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class AlbumActivity extends AppCompatActivity{
    GridView gridView;
    ArrayList<HashMap<String,String>> album = new ArrayList<>();
    String name_album = "";
    AlbumLoader albumLoader;
    String camFilePath = "";
    SAlbumAdapter albumAdapter;
    Boolean removeFirst = false;

    private boolean submenu_open;
    private FloatingActionButton submenu_fab;
    private LinearLayout layout_addExisting;
    private LinearLayout layout_addFromCam;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);;
        setContentView(R.layout.album_images);

        Intent intent = getIntent();
        name_album = intent.getStringExtra("name");
        setTitle(name_album);

        gridView = findViewById(R.id.inneralbum_grid);

        Resources r = getApplicationContext().getResources();
        DisplayMetrics dm = r.getDisplayMetrics();
        float dp = dm.widthPixels / (dm.densityDpi/160f);

        if (dp < 360) {
            dp = (dp-17) / 2;
            gridView.setColumnWidth(Math.round(MyFunc.convertDpToPixel(dp,getApplicationContext())));
        }

        submenu_fab = this.findViewById(R.id.fabSetting);
        layout_addFromCam = this.findViewById(R.id.layoutFabPhoto);
        layout_addExisting = this.findViewById(R.id.layoutFabPreexisting);

        submenu_fab.setOnClickListener(view -> {
            if(submenu_open == true) {
                closeSubMenu();
            } else {
                openSubMenu();
            }
        });


        closeSubMenu();

    }

    protected void onResume() {
        super.onResume();
        albumLoader = new AlbumLoader();
        albumLoader.execute();
    }

    private void closeSubMenu() {
        if(layout_addExisting!=null && layout_addFromCam!=null && submenu_fab!=null) {
            layout_addExisting.setVisibility(View.GONE);
            layout_addFromCam.setVisibility(View.GONE);
            submenu_fab.setImageResource(R.drawable.ic_settings_white_24dp);
            submenu_open = false;
        }
    }

    private void openSubMenu() {
        if(layout_addExisting!=null && layout_addFromCam!=null && submenu_fab!=null) {
            layout_addExisting.setVisibility(View.VISIBLE);
            layout_addFromCam.setVisibility(View.VISIBLE);
            submenu_fab.setImageResource(R.drawable.ic_close_white_24dp);
            submenu_open = true;
        }
    }

    public void setCamFilePath(String path) {
        this.camFilePath = path;
    }

    class AlbumLoader extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String path;
            String albm;
            String timestamp;

            Uri uriEXT = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriINT = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            String[] projection = { MediaStore.MediaColumns.DATA,MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.MediaColumns.DATE_MODIFIED};
            Cursor cursorEXT = getContentResolver().query(uriEXT,projection,
                    "bucket_display_name = \""+name_album+"\"",null,null);
            Cursor cursorINT = getContentResolver().query(uriINT, projection,
                    "bucket_display_name = \""+name_album+"\"", null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorEXT,cursorINT});

            while (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                albm = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED));
                album.add(MyFunc.mappingInbox(albm,path,timestamp,MyFunc.convertToTime(timestamp)));
            }

            cursor.close();
            Collections.sort(album,new MapComparator(MyFunc.KEY_TIMESTAMP, "dsc"));
            return "";
        }

        protected void onPreExecute() {
            super.onPreExecute();
            album.clear();
        }

        protected void onPostExecute(String xml) {
            if(removeFirst) {
                album.remove(0);
                removeFirst = false;
            }
            albumAdapter = new SAlbumAdapter(AlbumActivity.this,album);
            gridView.setAdapter(albumAdapter);
            gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent intent = new Intent(AlbumActivity.this,GalleryPreview.class);
                intent.putExtra("path",album.get(+i).get(MyFunc.KEY_PATH));
                intent.putExtra("albumList",album);
                intent.putExtra("pos",+i);
                startActivity(intent);
            });

            gridView.setOnItemLongClickListener((adapterView, view, i, l) ->{
                if(i>album.size()){
                    return false;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AlbumActivity.this);
                builder.setTitle("Edit/Delete Image");
                final EditText input = new EditText(AlbumActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                File file = new File(album.get(+i).get(MyFunc.KEY_PATH));
                File dir = new File(file.getParent());
                input.setText(file.getName());
                builder.setView(input);

                builder.setNeutralButton("Move To...", (dialogInterface, i1) -> {
                    DialogProperties properties = new DialogProperties();

                    properties.selection_mode = DialogConfigs.SINGLE_MODE;
                    properties.selection_type = DialogConfigs.DIR_SELECT;
                    properties.root = new File("/storage/emulated/0/Photos27/");
                    properties.error_dir = properties.offset = properties.root;
                    properties.extensions = null;
                    Toast.makeText(AlbumActivity.this,"This is a full move, to make a copy into an album use the add function instead.",Toast.LENGTH_LONG);

                    FilePickerDialog filePickerDialog = new FilePickerDialog(AlbumActivity.this,properties);
                    filePickerDialog.setTitle("Choose an Album:");
                    filePickerDialog.setDialogSelectionListener(dirToMoveTo -> {
                        String name = file.getName();
                        file.renameTo(new File(dirToMoveTo[0]+"/"+name));
                        String[] toScan = new String[] {file.getAbsolutePath(),dirToMoveTo[0]+"/"+name};
                        MediaScannerConnection.scanFile(AlbumActivity.this, toScan, null, (s, uri1) -> System.out.println("SCAN COMPLETED: " + s));
                        if(file.getParentFile().list().length == 0) {
                            file.getParentFile().delete();
                            Intent intent = new Intent(AlbumActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                        album.remove(i);
                        album.trimToSize();
                        albumAdapter.swapItems(album);
                    });
                    filePickerDialog.show();
                });

                builder.setPositiveButton("Confirm Change", ((dialogInterface, i1) -> {
                    String path = file.getAbsolutePath();
                    File newFile = new File(dir.getAbsolutePath()+"/"+input.getText().toString());
                    file.renameTo(newFile);
                    String[] toScan = {path,newFile.getPath()};
                    MediaScannerConnection.scanFile(AlbumActivity.this, toScan, null, (s, uri1) -> System.out.println("SCAN COMPLETED: " + s));

                    album.get(i).put(MyFunc.KEY_PATH,newFile.getPath());
                }));

                builder.setNegativeButton("Delete" , (dialogInterface, i1) -> {
                    File parent = file.getParentFile();
                    file.delete();
                    String[] toScan =  {file.getPath()};
                    MediaScannerConnection.scanFile(AlbumActivity.this, toScan, null, (s, uri1) -> System.out.println("SCAN COMPLETED: " + s));
                    String[] files = parent.list();
                    System.out.print("This one" + files.length);
                    if((parent.list() == null || parent.list().length == 0) && !parent.getAbsolutePath().contains("storage/emulated/0/Download")) {
                        parent.delete();
                        Intent intent = new Intent(AlbumActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                    album.remove(i);
                    album.trimToSize();
                    albumAdapter.swapItems(album);
                });

                builder.show();
                return true;
            });

            layout_addFromCam.setOnClickListener(view -> {
                String[] PERMISSIONS = {(Manifest.permission.CAMERA)};

                if(!MyFunc.hasPermissions(AlbumActivity.this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(AlbumActivity.this,PERMISSIONS,1);
                }
                Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(camIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    File parentdir = new File(album.get(0).get(MyFunc.KEY_PATH)).getParentFile();
                    try {
                        photoFile = MyFunc.createImageFile(parentdir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(AlbumActivity.this,
                                "com.example.android.fileprovider",
                                photoFile);
                        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(camIntent, 1);
                        String pathFinal[] = {photoFile.getAbsolutePath()};
                        camFilePath = pathFinal[0];
                        MediaScannerConnection.scanFile(AlbumActivity.this, pathFinal, null, (s,uri) -> {System.out.println("SCAN COMPLETED: " + s);});
                    }
                }
            });

            layout_addExisting.setOnClickListener(view -> {

                DialogProperties properties = new DialogProperties();

                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File(DialogConfigs.DEFAULT_DIR);
                properties.error_dir = properties.offset = properties.root;
                properties.extensions = null;

                FilePickerDialog filePickerDialog = new FilePickerDialog(AlbumActivity.this,properties);
                filePickerDialog.setTitle("Choose a Photo:");
                filePickerDialog.setDialogSelectionListener(filePaths -> {
                    if(filePaths.length > 0) {
                        File file = new File(filePaths[0]);
                        String newpath = album.get(0).get(MyFunc.KEY_PATH).substring(0,(album.get(0).get(MyFunc.KEY_PATH).lastIndexOf("/")))+"/"+file.getName();
                        File newFile = new File(newpath);
                        if(newFile.exists()) {
                            Toast.makeText(AlbumActivity.this,"Can't add an image twice son",Toast.LENGTH_SHORT);
                            return;
                        }

                        //file.renameTo(newFile);
                        try {
                            MyFunc.copy(file,newFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String[] toScan = {filePaths[0], newFile.getPath()};
                        MediaScannerConnection.scanFile(AlbumActivity.this, toScan, null, (s, uri1) -> System.out.println("SCAN COMPLETED: " + s));
                        HashMap<String, String> addThis = new HashMap<>();
                        addThis.put(MyFunc.KEY_PATH, newFile.getPath());
                        album.add(album.size(), addThis);
                        albumAdapter.swapItems(album);
                    } else {
                        new AlertDialog.Builder(AlbumActivity.this).setTitle("No Choice Made").setMessage("No file was chosen").show();
                    }
                });
                filePickerDialog.show();

            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == RESULT_CANCELED) {
                new File(camFilePath).delete();
                removeFirst = true;
                MediaScannerConnection.scanFile(AlbumActivity.this, new String[] {camFilePath}, null, (s,uri) -> {System.out.println("SCAN COMPLETED: " + s);});
            }
        }
    }
}

class SAlbumAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String,String>> data;


    public SAlbumAdapter(Activity activity, ArrayList<HashMap<String, String>> album) {
        this.activity = activity;
        this.data = album;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SAlbumViewHolder holder;
        if(view == null) {
            holder = new SAlbumViewHolder();
            view = LayoutInflater.from(activity).inflate(R.layout.single_album_row,viewGroup,false);
            holder.image = view.findViewById(R.id.album_icon);
            view.setTag(holder);
        } else {
            holder = (SAlbumViewHolder) view.getTag();
        }
        holder.image.setId(i);

        HashMap < String, String > song = new HashMap < String, String > ();
        song = data.get(i);
        try {

            Glide.with(activity)
                    .load(new File(song.get(MyFunc.KEY_PATH))) // Uri of the picture
                    .into(holder.image);


        } catch (Exception e) {}
        return view;
    }

    public void swapItems(ArrayList<HashMap<String, String>> album) {
        this.data = album;
        notifyDataSetChanged();
    }
}

class SAlbumViewHolder {
    ImageView image;
}