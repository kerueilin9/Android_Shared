package com.example.android;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.databinding.ActivityMainEditBinding;

import java.io.File;
import java.util.ArrayList;

public class MainActivity_edit extends AppCompatActivity {

    ActivityMainEditBinding binding;
    RoomViewModel roomViewModel;
    RecyclerView recyclerView;
    mRecycleAdapter_edit adapter;
    ImageButton imageButton1, imageButton2, imageButton3;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_edit);
        binding = ActivityMainEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imageButton1 = findViewById(R.id.rename_edit);
        imageButton2 = findViewById(R.id.delete_edit);
        imageButton3 = findViewById(R.id.share_edit);

        roomViewModel = ViewModelProviders.of(this).get(RoomViewModel.class);
        recyclerView = findViewById(R.id.recyclerView_edit);
        adapter = new mRecycleAdapter_edit();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        roomViewModel.getAllItemLive().observe(this, mRoomItems -> {
            adapter.setAllItem(this, mRoomItems);
            this.setTitle(getString(R.string.My_Document) + " (" + (adapter.getItemCount()) + ")");
            adapter.notifyDataSetChanged();
            //recyclerView.scrollToPosition(0);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setImageButtonBackground(){
        if (adapter.getSelectedItem().size() == 0){
            imageButton1.setBackground(getDrawable(R.drawable.edit_hovered));
            imageButton2.setBackground(getDrawable(R.drawable.edit_hovered));
            imageButton3.setBackground(getDrawable(R.drawable.edit_hovered));
        } else if (adapter.getSelectedItem().size() != 1){
            imageButton1.setBackground(getDrawable(R.drawable.edit_hovered));
        } else {
            imageButton1.setBackground(getDrawable(R.drawable.hover));
            imageButton2.setBackground(getDrawable(R.drawable.hover));
            imageButton3.setBackground(getDrawable(R.drawable.hover));
        }
    }

    public void rename(View view) {
        if (adapter.getSelectedItem().size() == 1){
            AlertDialog.Builder alert_rename = new AlertDialog.Builder(this);
            final EditText editText = new EditText(this);
            FrameLayout container = new FrameLayout(this);
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = params.rightMargin = this.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            editText.setSingleLine();
            editText.setLayoutParams(params);
            container.addView(editText);
            alert_rename.setTitle(R.string.rename);
            alert_rename.setView(container);
            alert_rename.setPositiveButton("OK", (dialog, which) -> {
                mRoomItem roomItem1 = new mRoomItem(adapter.getSelectedItem().get(0).getImgResource(), editText.getText().toString());
                roomItem1.setId(adapter.getSelectedItem().get(0).getId());
                roomViewModel.updateR(roomItem1);
                super.onBackPressed();
                //Toast.makeText(context.getApplicationContext(), String.valueOf(roomItem.getId()), Toast.LENGTH_SHORT).show();
            });
            alert_rename.setNegativeButton("CANCEL", (dialogInterface, i) -> {

            });
            alert_rename.show();
        }
    }

    public void share(View view) {
        ArrayList<Uri> imageUriArray = new ArrayList<Uri>();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        for (int i = 0; i< adapter.getSelectedItem().size(); i++){
            Uri imgUri = Uri.parse(adapter.getSelectedItem().get(i).getImgResource());
            File temp1 = new File(imgUri.getPath());
            Uri screenshotUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", temp1);
            imageUriArray.add(screenshotUri);
        }
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Text caption message!!");
        sharingIntent.setType("text/plain");
        sharingIntent.setType("image/jpeg");
        sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriArray);
        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
    }

    public void delete(View view) {
        if (adapter.getSelectedItem().size() == adapter.getItemCount()){
            AlertDialog.Builder alert_delete = new AlertDialog.Builder(this);
            alert_delete.setIcon(R.drawable.ic_baseline_error_24);
            alert_delete.setTitle(R.string.Delete_those_item);
            alert_delete.setMessage(R.string.This_action_will_delete_those_item);
            alert_delete.setPositiveButton("OK", (dialog, which) -> {
                display("all");
                roomViewModel.deleteAllR();
                super.onBackPressed();
            });
            alert_delete.setNegativeButton("CANCEL", (dialogInterface, i) -> {

            });
            alert_delete.show();
        } else if (adapter.getSelectedItem().size() != 0){
            AlertDialog.Builder alert_delete = new AlertDialog.Builder(this);
            alert_delete.setIcon(R.drawable.ic_baseline_error_24);
            alert_delete.setTitle(R.string.Delete_those_item);
            alert_delete.setMessage(R.string.This_action_will_delete_those_item);
            alert_delete.setPositiveButton("OK", (dialog, which) -> {
                for (int i = 0; i< adapter.getSelectedItem().size(); i++){
                    roomViewModel.deleteR(adapter.getSelectedItem().get(i));
                }
                super.onBackPressed();
            });
            alert_delete.setNegativeButton("CANCEL", (dialogInterface, i) -> {

            });
            alert_delete.show();
        }
    }

    public void display(String str){
        Toast.makeText(getApplication(), str, Toast.LENGTH_SHORT).show();
    }
}