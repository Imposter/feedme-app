package ca.impulsedev.feedme.api;

public abstract class AsyncTask<TResult> extends android.os.AsyncTask<Void, Void, AsyncTaskResult<TResult>> {
    @Override
    protected AsyncTaskResult<TResult> doInBackground(Void... params) {
        return process();
    }
    @Override
    protected void onPreExecute() {
        onBegin();
    }
    @Override
    protected void onPostExecute(AsyncTaskResult<TResult> result) {
        onEnd(result);
    }
    @Override
    protected void onCancelled(AsyncTaskResult<TResult> result) {
        onCancelled();
    }

    protected void onBegin() {
    }

    protected void onEnd(AsyncTaskResult<TResult> result) {
    }

    @Override
    protected void onCancelled() {
    }

    protected abstract AsyncTaskResult<TResult> process();

    public void cancel() {
        super.cancel(true);
    }
}
