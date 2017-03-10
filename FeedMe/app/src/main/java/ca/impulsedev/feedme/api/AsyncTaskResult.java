package ca.impulsedev.feedme.api;

public class AsyncTaskResult<TResult> {
    private TResult mResult;
    private Exception mException;

    public AsyncTaskResult(TResult result) {
        mResult = result;
    }

    public AsyncTaskResult(Exception exception) {
        mException = exception;
    }

    public boolean hasError() {
        return mException != null;
    }

    public boolean hasResult() {
        return mResult != null;
    }

    public TResult getResult() {
        return mResult;
    }

    public Exception getException() {
        return mException;
    }
}
