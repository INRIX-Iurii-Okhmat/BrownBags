package com.samples.daggersamples;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;
import javax.inject.Scope;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Sample fragment.
 */
public class CustomFragment extends Fragment {
    @Inject
    protected DataProvider provider;

    @InjectView(R.id.text)
    protected TextView text;

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState) {
        final View content = inflater.inflate(R.layout.fragment_custom, container, false);

        ((ScopedGraphProvider) this.getActivity()).getGraph().inject(this);

        ButterKnife.inject(this, content);

        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDetach() {
        super.onDetach();

        ButterKnife.reset(this);
    }

    /**
     * Called when "button1" is clicked.
     */
    @OnClick(R.id.button1)
    public void onButtonClick() {
        this.text.setText(this.provider.getData());
    }
}
