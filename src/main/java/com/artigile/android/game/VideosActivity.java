package com.artigile.android.game;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.artigile.android.R;

import java.io.File;
import java.util.*;

/**
 * @author ivanbahdanau
 */
public class VideosActivity extends ListActivity {

    private ArrayAdapter<File> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videos);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getListView().setItemsCanFocus(false);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        List<File> files = new ArrayList<File>(Arrays.asList(FileUtils.getMagicMazeVideosDir().listFiles()));
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return (int) (file2.lastModified() - file1.lastModified());
            }
        });
        listAdapter = new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, files);
        setListAdapter(listAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        playVideo(listAdapter.getItem(position));
    }


    public void playAgain(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }


    private void playVideo(File file) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        myIntent.setDataAndType(Uri.fromFile(file), mimetype);
        startActivity(myIntent);
    }
}
