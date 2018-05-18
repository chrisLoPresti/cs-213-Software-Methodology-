package com.rutcs.chrislopresti.photos27;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_PERMISSION = 1;
    AlbumManager albumManager;
    GridView gridView;
    FloatingActionButton fab_main;
    ArrayList<HashMap<String,String>> albums = new ArrayList<>();
    AlbumAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gallery_view);

        Resources r = getApplicationContext().getResources();
        DisplayMetrics dm = r.getDisplayMetrics();
        float dp = dm.widthPixels / (dm.densityDpi/160f);

        if (dp < 360) {
            dp = (dp-17) / 2;
            gridView.setColumnWidth(Math.round(MyFunc.convertDpToPixel(dp,getApplicationContext())));
        }

        String[] PERMISSIONS = {(Manifest.permission.WRITE_EXTERNAL_STORAGE),(Manifest.permission.READ_EXTERNAL_STORAGE)};

        if(!MyFunc.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this,PERMISSIONS,REQUEST_PERMISSION);
        }

        fab_main = findViewById(R.id.main_fab);

    }

    @Override
    protected void onResume(){
        super.onResume();
        gridView.setLongClickable(true);
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!MyFunc.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION);
        }else{
            albumManager = new AlbumManager();
            albumManager.execute();
        }
        TagsManager.readTags(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this,"Our query is the following: " + query, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);

        MenuItem search = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) search.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this,"Our query is the following: " + query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) {
                    adapter.swapItems(albums);
                    gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                        Intent intent = new Intent(MainActivity.this,AlbumActivity.class);
                        intent.putExtra("name",albums.get(+i).get(MyFunc.KEY_ALBUM));
                        startActivity(intent);
                    });
                } else {
                    ArrayList<HashMap<String, String>> toLoad = TagsManager.searchTags(newText);
                    adapter.swapItems(toLoad);
                    gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                        Intent intent = new Intent(MainActivity.this,GalleryPreview.class);
                        intent.putExtra("path",toLoad.get(+i).get(MyFunc.KEY_PATH));
                        intent.putExtra("albumList",toLoad);
                        intent.putExtra("pos",+i);
                        startActivity(intent);
                    });
                }
                return false;
            }
        });

        return true;
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    class AlbumManager extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String path;
            String album;
            String timestamp;

            Uri uriEXT = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriINT = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            String[] projection = { MediaStore.MediaColumns.DATA,MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.MediaColumns.DATE_MODIFIED};
            Cursor cursorEXT = getContentResolver().query(uriEXT,projection,
                    "_data IS NOT NULL) GROUP BY (bucket_display_name",null,null);
            Cursor cursorINT = getContentResolver().query(uriINT, projection,
                    "_data IS NOT NULL) GROUP BY (bucket_display_name", null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorEXT,cursorINT});

            while (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED));
                albums.add(MyFunc.mappingInbox(album,path,timestamp,MyFunc.convertToTime(timestamp)));
            }

            cursor.close();
            Collections.sort(albums,new MapComparator(MyFunc.KEY_TIMESTAMP, "dsc"));
            return "";
        }

        protected void onPreExecute(){
            super.onPreExecute();
            albums.clear();
        }

        protected void onPostExecute(String XML){
            adapter = new AlbumAdapter(MainActivity.this, albums);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent intent = new Intent(MainActivity.this,AlbumActivity.class);
                intent.putExtra("name",albums.get(+i).get(MyFunc.KEY_ALBUM));
                startActivity(intent);
            });
            gridView.setOnItemLongClickListener((adapterView, view, i, l) ->{
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Edit/Delete Album");
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                File file = new File(albums.get(+i).get(MyFunc.KEY_PATH));
                File dir = new File(file.getParent()+"/");
                String dirpath = dir.getParent()+"/";
                input.setText(dir.getName());
                builder.setView(input);

                builder.setNeutralButton("Cancel", (dialogInterface, i1) -> {
                    dialogInterface.cancel();
                });
                builder.setPositiveButton("Confirm Change", ((dialogInterface, i1) -> {
                    String[] originalData = dir.list();
                    for(int pos=0; pos<originalData.length ; pos++) {
                        originalData[pos] = dir.getAbsolutePath()+"/"+originalData[pos];
                    }

                    File newDir = new File(dirpath + input.getText().toString());
                    dir.renameTo(newDir);

                    String[] newData = newDir.list();
                    for(int pos=0; pos<newData.length ; pos++) {
                        newData[pos] = newDir.getAbsolutePath()+"/"+newData[pos];
                    }

                    File toCopy = new File(albums.get(i).get(MyFunc.KEY_PATH));
                    MediaScannerConnection.scanFile(MainActivity.this, originalData, null, (s,uri) -> {System.out.println("SCAN COMPLETED: " + s);});
                    MediaScannerConnection.scanFile(MainActivity.this, newData, null, (s,uri) -> {System.out.println("SCAN COMPLETED: " + s);});

                    albums.get(i).put(MyFunc.KEY_PATH, input.getText().toString()+"/"+toCopy.getName());
                    albums.get(i).put(MyFunc.KEY_ALBUM, input.getText().toString().substring(input.getText().toString().lastIndexOf("/")+1));

                    adapter.swapItems(albums);
                }));

                builder.setNegativeButton("Delete" , (dialogInterface, i1) -> {
                    String[] originalData = dir.list();
                    for(int pos=0; pos<originalData.length ; pos++) {
                        originalData[pos] = dir.getAbsolutePath()+"/"+originalData[pos];
                        new File(originalData[pos]).delete();
                    }
                    MediaScannerConnection.scanFile(MainActivity.this, originalData,null,(s,uri) -> {System.out.println("SCAN COMPLETED: " + s);} );
                    dir.delete();
                    MediaScannerConnection.scanFile(MainActivity.this,new String[] {dir.getPath()},null,(s,uri)->{System.out.println("SCAN COMPLETED: " + s);});
                    albums.remove(i);
                    albums.trimToSize();
                    adapter.swapItems(albums);
                });

                builder.show();
                return true;
            });

            fab_main.setOnClickListener(view -> {
                AlertDialog.Builder newalbumdb = new AlertDialog.Builder(MainActivity.this);
                final EditText input = new EditText(MainActivity.this);
                input.setHint("NAME GOES HERE");
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                newalbumdb.setTitle("Enter New Album Name");
                newalbumdb.setView(input);
                newalbumdb.setPositiveButton("Create", (dialogInterface, i) -> {
                    String mainFolderPath = "/storage/emulated/0/Photos27/";
                    if(input.getText().toString().length()>0) {
                        String albumName = input.getText().toString();
                        File dirToCreate = new File(mainFolderPath+albumName);
                        if(dirToCreate.exists()){
                            new AlertDialog.Builder(MainActivity.this).setMessage("Already Exists, Love.").show();
                        } else {
                            dirToCreate.mkdirs();
                            try {
                                InputStream ins = getResources().openRawResource(+R.drawable.ic_photo_camera_white_24dp);
                                BufferedReader br = new BufferedReader(new InputStreamReader(ins));
                                StringBuffer sb= new StringBuffer();
                                String line;
                                while((line = br.readLine()) != null){
                                    sb.append(line);
                                }
                                File f = new File(sb.toString());
                                File samplefile = MyFunc.createImageFile(dirToCreate,"SAMPLE_");
                                f.renameTo(samplefile);

                                MediaScannerConnection.scanFile(MainActivity.this, new String[] {dirToCreate.getPath(),samplefile.getPath()}, null, (s,uri) -> {System.out.println("SCAN COMPLETED: " + s);});
                                HashMap<String, String> addThis = new HashMap<String, String>();
                                addThis.put(MyFunc.KEY_PATH, samplefile.getPath());
                                addThis.put(MyFunc.KEY_ALBUM, albumName);
                                albums.add(0,addThis);
                                adapter.swapItems(albums);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        new AlertDialog.Builder(MainActivity.this).setMessage("Really? No Name?").show();
                    }
                });
                newalbumdb.show();
            });
        }
    }

}

class AlbumAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String,String>> data;

    public AlbumAdapter(Activity a, ArrayList<HashMap<String,String>> arrayList) {
        activity = a;
        data = arrayList;
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
        AlbumViewHolder holder = null;
        if (view == null) {
            holder = new AlbumViewHolder();
            view = LayoutInflater.from(activity).inflate(R.layout.album_row,viewGroup,false);
            holder.image = view.findViewById(R.id.album_icon);
            holder.title = view.findViewById(R.id.album_title);
            view.setTag(holder);
        } else {
            holder = (AlbumViewHolder) view.getTag();
        }
        holder.image.setId(i);
        holder.title.setId(i);

        HashMap<String,String> albm = new HashMap<>();
        albm = data.get(i);
        try {
            holder.title.setText(albm.get(MyFunc.KEY_ALBUM));

            Glide.with(activity).load(new File(albm.get(MyFunc.KEY_PATH)))
                    .into(holder.image);
        } catch (Exception e) {
            System.out.print("Worse: " + e.getMessage());
        }
        return view;
    }

    public void swapItems(ArrayList<HashMap<String, String>> album) {
        this.data = album;
        notifyDataSetChanged();
    }
}

class AlbumViewHolder {
    ImageView image;
    TextView title;
}
