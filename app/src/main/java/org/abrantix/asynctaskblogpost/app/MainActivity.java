package org.abrantix.asynctaskblogpost.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.ref.WeakReference;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private StrongRefAsyncTask mStrongRefTask;
    private StrongRefThread mStrongRefThread;
    private WeakRefAsyncTask mWeakRefTask;
    private WeakRefThread mWeakRefThread;

    private boolean RUN_STRONG_REF_TASK = false;
    private boolean RUN_STRONG_REF_THREAD = false;
    private boolean RUN_WEAK_REF_TASK = false;
    private boolean RUN_WEAK_REF_THREAD = false;

    private ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(4, 12, 999,
            TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(99));

    /**
     * Just allocate some extra memory in order to trigger activity/fragment clean up sooner than
     * usual
     */
    private boolean ALLOC_EXTRA_MEM = false;
    private byte[] mTestBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ALLOC_EXTRA_MEM) {
            mTestBytes = MemDebugUtils.allocMegs(75);
            MemDebugUtils.logMemStatus("Mem" + TAG);
        }

        if (savedInstanceState == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frag_container, new MainFragment());
            ft.commit();

            if (RUN_STRONG_REF_TASK) {
                mStrongRefTask = new StrongRefAsyncTask();
                mStrongRefTask.executeOnExecutor(mExecutor);
            }
            if (RUN_STRONG_REF_THREAD) {
                mStrongRefThread = new StrongRefThread();
                mStrongRefThread.start();
            }
            if (RUN_WEAK_REF_TASK) {
                mWeakRefTask = new WeakRefAsyncTask(new WeakReference<Activity>(this));
                mWeakRefTask.executeOnExecutor(mExecutor);
            }
            if (RUN_WEAK_REF_THREAD) {
                mWeakRefThread = new WeakRefThread(new WeakReference<Activity>(this));
                mWeakRefThread.start();
            }
        }
    }

    private class StrongRefAsyncTask extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... params) {
            try {
                while (true) {
                    Log.d(TAG, "[StrongRefAsyncTask] appcontext: " + (getApplicationContext() !=
                            null) + " || " + this.toString());
                    Thread.sleep(1000);
                }
            } catch(InterruptedException e) { e.printStackTrace(); };
            return null;
        }
    }

    private static class WeakRefAsyncTask extends AsyncTask<Object, Object, Object> {

        WeakReference<Activity> mRef;

        public WeakRefAsyncTask(WeakReference<Activity> ref) {
            mRef = ref;
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                while (true) {
                    Log.d(TAG, "[WeakRefAsyncTask] context: " + (mRef.get() !=
                            null) + " || " + this.toString());
                    Thread.sleep(1000);
                }
            } catch(InterruptedException e) { e.printStackTrace(); };
            return null;
        }
    }

    private class StrongRefThread extends Thread {

        @Override
        public void run() {
            try {
                while (true) {
                    Log.d(TAG, "[StrongRefThread] appcontext: " + (getApplicationContext() !=
                            null) + " || " + this.toString());
                    Thread.sleep(1000);
                }
            } catch(InterruptedException e) { e.printStackTrace(); };
        }
    }

    private static class WeakRefThread extends Thread {

        WeakReference<Activity> mRef;

        public WeakRefThread(WeakReference<Activity> ref) {
            mRef = ref;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Log.d(TAG, "[WeakRefThread] context: " + (mRef.get() !=
                            null) + " || " + this.toString());
                    Thread.sleep(1000);
                }
            } catch(InterruptedException e) { e.printStackTrace(); };
        }
    }
}
