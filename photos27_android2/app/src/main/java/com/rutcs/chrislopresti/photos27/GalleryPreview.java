package com.rutcs.chrislopresti.photos27;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GalleryPreview extends AppCompatActivity{
    ImageView GalleryPreviewImg;
    String path;
    ArrayList<HashMap<String,String>> album = new ArrayList<>();
    ImageView backArrow;
    ImageView forwardArrow;
    LinearLayout tags_area;
    TextView tag_text;
    int pos;
    boolean gone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.gallery_preview);
        Intent intent = getIntent();


        path = intent.getStringExtra("path");
        album = (ArrayList<HashMap<String,String>>)intent.getSerializableExtra("albumList");
        pos = intent.getIntExtra("pos",0);


        GalleryPreviewImg = findViewById(R.id.GalleryPreviewImg);
        Glide.with(GalleryPreview.this)
                .load(new File(album.get(pos).get(MyFunc.KEY_PATH))) // Url of the picture
                .into(GalleryPreviewImg);

        backArrow = findViewById(R.id.back);
        forwardArrow = findViewById(R.id.forward);
        tags_area = findViewById(R.id.tag_layout);
        tag_text = findViewById(R.id.tags);

        tags_area.setOnClickListener(view-> {
            final Dialog editTagDialog = new Dialog(GalleryPreview.this);
            editTagDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
            editTagDialog.setContentView(R.layout.edit_tag_dialog);

            String[] tag_categories = TagsManager.getTagString(GalleryPreview.this,album.get(pos).get(MyFunc.KEY_PATH)).split("_person_");

            EditText editTag = editTagDialog.findViewById(R.id.tag_edit_text);
            editTag.setText(tag_categories[0]);
            EditText editTag2 = editTagDialog.findViewById(R.id.tag_edit_text_2);
            if(tag_categories.length>1) {
                editTag2.setText(tag_categories[1]);
            }

            Button confirmChange = editTagDialog.findViewById(R.id.tag_confirm_change);
            confirmChange.setOnClickListener(view1 -> {
                TagsManager.updateTag(album.get(pos).get(MyFunc.KEY_PATH),editTag.getText().toString()+"_person_"+editTag2.getText().toString());
                TagsManager.writeTag(GalleryPreview.this,album.get(pos).get(MyFunc.KEY_PATH),editTag.getText().toString()+"_person_"+editTag2.getText().toString());
                tag_text.setText(editTag.getText().toString()+"_person_"+editTag2.getText().toString());
                editTagDialog.dismiss();
            });

            editTagDialog.show();
        });


        forwardArrow.setOnClickListener(view -> {
            nextPicture();
            //Toast.makeText(GalleryPreview.this,"Load image in pos: " + pos,Toast.LENGTH_SHORT).show();
            Glide.with(GalleryPreview.this).clear(GalleryPreviewImg);
            Glide.with(GalleryPreview.this)
                    .load(new File(album.get(pos).get(MyFunc.KEY_PATH))) // Url of the picture
                    .into(GalleryPreviewImg);
            tag_text.setText(TagsManager.getTagString(GalleryPreview.this,album.get(pos).get(MyFunc.KEY_PATH)));
        });

        backArrow.setOnClickListener(view -> {
            prevPicture();
            //Toast.makeText(GalleryPreview.this,"Load image in pos: " + pos,Toast.LENGTH_SHORT).show();
            Glide.with(GalleryPreview.this).clear(GalleryPreviewImg);
            Glide.with(GalleryPreview.this)
                    .load(new File(album.get(pos).get(MyFunc.KEY_PATH))) // Url of the picture
                    .into(GalleryPreviewImg);
            tag_text.setText(TagsManager.getTagString(GalleryPreview.this,album.get(pos).get(MyFunc.KEY_PATH)));
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        tag_text.setText(TagsManager.getTagString(GalleryPreview.this,album.get(pos).get(MyFunc.KEY_PATH)));
    }
    private void nextPicture() {
        pos++;
        if(pos == album.size())
            pos = 0;
    }
    private void prevPicture() {
        pos--;
        if(pos == -1)
            pos = album.size()-1;
    }
}


