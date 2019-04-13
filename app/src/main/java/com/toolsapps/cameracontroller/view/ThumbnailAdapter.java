package com.toolsapps.cameracontroller.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.toolsapps.cameracontroller.R;

import java.util.ArrayList;
import java.util.List;

public class ThumbnailAdapter extends BaseAdapter {

    private final List<Integer> handles = new ArrayList<Integer>(10);
    private final List<Bitmap> thumbnails = new ArrayList<Bitmap>(10);
    private final List<String> filenames = new ArrayList<String>(10);
    private final LayoutInflater inflater;
    private boolean visibleFilename;
    private int maxNumPictures;

    public ThumbnailAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setShowFilename(boolean visible) {
        this.visibleFilename = visible;
        notifyDataSetChanged();
    }

    public void setMaxNumPictures(int maxNumPictures) {
        this.maxNumPictures = maxNumPictures;
        checkListSizes();
        notifyDataSetChanged();
    }

    private void checkListSizes() {
        while (handles.size() > maxNumPictures) {
            handles.remove(0);
            thumbnails.get(0).recycle();
            thumbnails.remove(0);
            filenames.remove(0);
        }
    }

    public void addFront(int objectHandle, String filename, Bitmap thumbnail) {
        if (maxNumPictures == 0) {
            if (thumbnail != null) {
                thumbnail.recycle();
            }
            return;
        }
        for (Integer i : handles) {
            if (i == objectHandle) {
                return;
            }
        }
        handles.add(objectHandle);
        thumbnails.add(thumbnail);
        filenames.add(filename);
        checkListSizes();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return handles.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return thumbnails.get(thumbnails.size() - 1 - position);
    }

    public int getItemHandle(int position) {
        return handles.get(handles.size() - 1 - position);
    }

    private String getItemFilename(int position) {
        return filenames.get(filenames.size() - 1 - position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.thumbnail_list_item, parent, false);
        }

        ImageView image = (ImageView) view.findViewById(R.id.image1);
        image.setImageBitmap(getItem(position));

        TextView text = (TextView) view.findViewById(android.R.id.text1);
        if (visibleFilename) {
            text.setText(getItemFilename(position));
        }
        text.setVisibility(visibleFilename ? View.VISIBLE : View.GONE);

        return view;
    }

}
