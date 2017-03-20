package ca.impulsedev.feedme.api;

public abstract class AsyncTask<TResult> extends android.os.AsyncTask<Void, Void, AsyncTaskResult<TResult>> {
    private boolean mRunning;

    @Override
    protected AsyncTaskResult<TResult> doInBackground(Void... params) {
        return process();
    }
    @Override
    protected void onPreExecute() {
        mRunning = true;
        onBegin();
    }
    @Override
    protected void onPostExecute(AsyncTaskResult<TResult> result) {
        onEnd(result);
        mRunning = false;
    }
    @Override
    protected void onCancelled(AsyncTaskResult<TResult> result) {
        onCancelled();
        mRunning = false;
    }

    protected void onBegin() {
    }

    protected void onEnd(AsyncTaskResult<TResult> result) {
    }

    @Override
    protected void onCancelled() {
    }

    public boolean isRunning() {
        return mRunning;
    }

    protected abstract AsyncTaskResult<TResult> process();

    public void cancel() {
        super.cancel(true);
    }
}
