package com.iuriio.apps.asyncandroid.app;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class IntentSvcFragment extends Fragment implements View.OnClickListener {
    private TextView text;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (text != null) {
                text.setText("Notice received.");
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(this.receiver, new IntentFilter(NoticeService.BROADCAST));
    }

    @Override
    public void onDetach() {
        super.onDetach();

        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(this.receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View content = inflater.inflate(R.layout.fragment_intent_svc, container, false);
        this.text = (TextView) content.findViewById(R.id.notice_svc_text);

        content.findViewById(R.id.notice_svc_start).setOnClickListener(this);

        final Animation anim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.rotate_around_center);
        content.findViewById(R.id.notice_svc_img).setAnimation(anim);

        return content;
    }


    @Override
    public void onClick(View v) {
        this.getActivity().startService(new Intent(this.getActivity(), NoticeService.class));
    }
}
