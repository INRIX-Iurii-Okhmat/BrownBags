package com.iuriioapps.rxandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class RxCustomOperatorsActivity extends AppCompatActivity {
    @BindView(R.id.txtNumber)
    protected EditText txtNumber;

    @BindView(R.id.txtResponse)
    protected TextView txtResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_rx_custom_operators);

        ButterKnife.bind(this);

        RxTextView.textChanges(this.txtNumber)
                .observeOn(AndroidSchedulers.mainThread())
                .map(String::valueOf)
                .map(this::tryParseInt)
                .lift(new SequenceOperator())
                .map(sqrt -> String.format("SQRT is %d", sqrt))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> txtResponse.setText(result),
                        error -> txtResponse.setText(error.getMessage()));

    }

    private int tryParseInt(final String value) {
        return TextUtils.isEmpty(value) ? 0 : Integer.parseInt(value);
    }

    public static class SequenceOperator implements Observable.Operator<Integer, Integer> {
        @Override
        public Subscriber<? super Integer> call(Subscriber<? super Integer> subscriber) {
            return new Subscriber<Integer>(subscriber) {
                @Override
                public void onCompleted() {
                    subscriber.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    subscriber.onError(e);
                }

                @Override
                public void onNext(Integer integer) {
                    int roundedSqrt = (int) Math.round(Math.sqrt(integer));
                    subscriber.onNext(roundedSqrt);
                }
            };
        }
    }
}
