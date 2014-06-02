package org.abrantix.asynctaskblogpost.app;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by fabrantes on 02/06/14.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";

    private boolean RUN_STRONG_REF_TASK = true;
    private boolean RUN_STRONG_REF_THREAD = false;
    private boolean RUN_WEAK_REF_TASK = false;
    private boolean RUN_WEAK_REF_THREAD = false;

    private StrongRefAsyncTask mStrongRefTask;
    private StrongRefThread mStrongRefThread;
    private WeakRefAsyncTask mWeakRefTask;
    private WeakRefThread mWeakRefThread;

    private ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(4, 12, 999,
            TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(99));


    /**
     * Just allocate some extra memory in order to trigger activity/fragment clean up sooner than
     * usual
     */
    private boolean ALLOC_EXTRA_MEM = true;
    private byte[] mTestBytes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ALLOC_EXTRA_MEM) {
            mTestBytes = MemDebugUtils.allocMegs(75);
            MemDebugUtils.logMemStatus("Mem" + TAG);
        }

        if (savedInstanceState == null) {
            if (RUN_STRONG_REF_TASK) {
                mStrongRefTask = new StrongRefAsyncTask();
                mStrongRefTask.executeOnExecutor(mExecutor);
            }
            if (RUN_STRONG_REF_THREAD) {
                mStrongRefThread = new StrongRefThread();
                mStrongRefThread.start();
            }
            if (RUN_WEAK_REF_TASK) {
                mWeakRefTask = new WeakRefAsyncTask(new WeakReference<Fragment>(this));
                mWeakRefTask.executeOnExecutor(mExecutor);
            }
            if (RUN_WEAK_REF_THREAD) {
                mWeakRefThread = new WeakRefThread(new WeakReference<Fragment>(this));
                mWeakRefThread.start();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putBoolean("has_lived_at_least_once", true);
    }

    private class StrongRefAsyncTask extends AsyncTask<Object, Object, Object> {

        @Override
        protected Object doInBackground(Object... params) {
            try {
                while (true) {
                    Log.d(TAG, "[StrongRefAsyncTask] fragment: " + (MainFragment.this !=
                            null) + " || " + this.toString());
                    Thread.sleep(1000);
                }
            } catch(InterruptedException e) { e.printStackTrace(); };
            return null;
        }
    }

    private static class WeakRefAsyncTask extends AsyncTask<Object, Object, Object> {

        WeakReference<Fragment> mRef;

        public WeakRefAsyncTask(WeakReference<Fragment> ref) {
            mRef = ref;
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                while (true) {
                    Log.d(TAG, "[WeakRefAsyncTask] fragment: " + (mRef.get() !=
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
                    Log.d(TAG, "[StrongRefThread] fragment: " + (MainFragment.this !=
                            null) + " || " + this.toString());
                    Thread.sleep(1000);
                }
            } catch(InterruptedException e) { e.printStackTrace(); };
        }
    }

    private static class WeakRefThread extends Thread {

        WeakReference<Fragment> mRef;

        public WeakRefThread(WeakReference<Fragment> ref) {
            mRef = ref;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Log.d(TAG, "[WeakRefThread] fragment: " + (mRef.get() !=
                            null) + " || " + this.toString());
                    Thread.sleep(1000);
                }
            } catch(InterruptedException e) { e.printStackTrace(); };
        }
    }
}
