package com.iuriioapps.rxandroid;

import android.os.SystemClock;
import android.util.Log;

import com.iuriioapps.rxandroid.DAL.IncidentManager.IResponse;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subscriptions.Subscriptions;

public class DAL {
    public static final IncidentManager INCIDENT_MANAGER = new IncidentManager();

    public static class Incident {
        public final long id;
        public final double latitude;
        public final double longtide;
        public final String description;

        Incident(long id, double latitude, double longtide, String description) {
            this.id = id;
            this.latitude = latitude;
            this.longtide = longtide;
            this.description = description;
        }
    }

    public static class Error extends Exception {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }

    public static class Cancelable {
        private Thread t;

        Cancelable(Thread t) {
            this.t = t;
        }

        public void cancel() {
            Log.d("DAL", "Canceled");

            if (this.t != null && this.t.isAlive()) {
                this.t.interrupt();
                this.t = null;
            }
        }
    }

    public static class IncidentManager {
        public interface IResponse {
            void onData(List<Incident> data);

            void onError(Error error);
        }

        public Cancelable getIncidentsAsync(final IResponse response) {
            Log.d("DAL", "New request");

            final Thread t = new Thread(() -> {
                SystemClock.sleep(2000);

                if (Thread.interrupted()) {
                    response.onError(new Error("Interrupted."));
                    return;
                }

                final List<Incident> incidents = new ArrayList<>(3);
                incidents.add(new Incident(1, 47.1, -122.1, "Incident 1"));
                incidents.add(new Incident(2, 47.2, -122.2, "Incident 2"));
                incidents.add(new Incident(3, 47.3, -122.3, "Incident 3"));

                Log.d("DAL", "On data");
                response.onData(incidents);
            });

            final Cancelable c = new Cancelable(t);
            t.start();
            return c;
        }
    }

    public static Observable<List<Incident>> getIncidents() {
        return Observable.create(subscriber -> {
            Cancelable c = INCIDENT_MANAGER.getIncidentsAsync(new IResponse() {
                @Override
                public void onData(List<Incident> data) {
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                }

                @Override
                public void onError(Error error) {
                    subscriber.onError(error);
                }
            });

            subscriber.add(Subscriptions.create(c::cancel));
        });
    }
}
