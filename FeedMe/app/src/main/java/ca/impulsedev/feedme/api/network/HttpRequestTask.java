package ca.impulsedev.feedme.api.network;

import ca.impulsedev.feedme.api.AsyncTask;
import ca.impulsedev.feedme.api.AsyncTaskResult;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HttpRequestTask extends AsyncTask<HttpResponse> {
    private static final ExecutorService sExecutor
            = Executors.newCachedThreadPool(Executors.defaultThreadFactory());

    private HttpRequest mRequest;
    private Future<HttpResponse> mFutureResponse;

    public HttpRequestTask(HttpRequest request) {
        mRequest = request;
    }

    @Override
    protected AsyncTaskResult<HttpResponse> process() {
        // End if task was cancelled
        if (isCancelled()) {
            return null;
        }

        try {
            // Process request
            mFutureResponse = sExecutor.submit(new Callable<HttpResponse>() {
                @Override
                public HttpResponse call() throws Exception {
                    return mRequest.getResponse();
                }
            });

            HttpResponse response = mFutureResponse.get();

            return new AsyncTaskResult(response);
        } catch (Exception ex) {
            return new AsyncTaskResult(ex);
        }
    }

    @Override
    public void cancel() {
        if (mFutureResponse != null) {
            mFutureResponse.cancel(true);
        }
        mRequest.close();
        super.cancel();
    }
}
