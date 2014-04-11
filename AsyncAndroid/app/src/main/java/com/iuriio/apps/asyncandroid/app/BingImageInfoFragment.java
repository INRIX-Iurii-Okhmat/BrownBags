package com.iuriio.apps.asyncandroid.app;



import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class BingImageInfoFragment extends Fragment {
    private TextView textCopyright;
    private ImageView imageDailyPic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View content = inflater.inflate(R.layout.fragment_bing_image_info, container, false);

        this.textCopyright = (TextView) content.findViewById(R.id.bing_image_copyright);
        this.imageDailyPic = (ImageView) content.findViewById(R.id.bing_image_pic);

        return content;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.textCopyright.setText(this.getArguments().getString("copyright"));

        Picasso
                .with(getActivity())
                .load(Uri.parse(this.getArguments().getString("url")))
                .placeholder(R.drawable.ic_launcher)
                .into(this.imageDailyPic);
    }
}
