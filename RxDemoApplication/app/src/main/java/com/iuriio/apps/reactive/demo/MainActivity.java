package com.iuriio.apps.reactive.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.functions.Action1;


public class MainActivity extends Activity {
    private final Action1<String> stringSubscription = new Action1<String>() {
        @Override
        public void call(String s) {
            System.out.println(s);
            //outputText.setText(outputText.getText() + s + "\n");
        }
    };

    private final Action1<Throwable> errorHandler = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            System.out.println(throwable.getMessage());
        }
    };

    @InjectView(R.id.output)
    TextView outputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        // <editor-fold desc="Intro.">
        // HelloRx.sayHelloTo("Jim", "John", "James");
        // </editor-fold>

        // <editor-fold desc="Non-blocking observable.">
//        NonBlockingObservable
//                .create()
//                .subscribe(this.stringSubscription);
        // </editor-fold>

        // <editor-fold desc="Error handling.">
//        NonBlockingObservable
//                .create()
//                .subscribe(this.stringSubscription, this.errorHandler);

        // <editor-fold desc="Error rewriting">
//                .onErrorResumeNext(new Func1<Throwable, Observable<? extends String>>() {
//                    @Override
//                    public Observable<? extends String> call(Throwable throwable) {
//                        final Throwable customException = new Exception("Custom: " + throwable.getMessage());
//                        return Observable.error(customException);
//                    }
//                })
        // </editor-fold>

        // </editor-fold>
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_creating_observables:
                this.startActivity(new Intent(this, CreatingObservablesActivity.class));
                return true;
            case R.id.action_transforming_observables:
                this.startActivity(new Intent(this, TransformingObservablesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
