package ca.impulsedev.feedme.api;

/**
 * A wrapper class around task results to implement either an exception or a result depending on
 * whether a task failed to complete or not
 * @param <TResult> Result data type
 */
public class AsyncTaskResult<TResult> {
    private TResult mResult;
    private Exception mException;

    /**
     * Initializes instance with result
     * @param result Task result
     */
    public AsyncTaskResult(TResult result) {
        mResult = result;
    }

    /**
     * Initializes instance with exception information
     * @param exception Exception information
     */
    public AsyncTaskResult(Exception exception) {
        mException = exception;
    }

    /**
     * Error during task execution
     * @return Whether there was an error during task execution
     */
    public boolean hasError() {
        return mException != null;
    }

    /**
     * Has task result
     * @return Whether there is a result for the task
     */
    public boolean hasResult() {
        return mResult != null;
    }

    /**
     * Gets result
     * @return Task result
     */
    public TResult getResult() {
        return mResult;
    }

    /**
     * Gets exception information
     * @return Task exception information
     */
    public Exception getException() {
        return mException;
    }
}
