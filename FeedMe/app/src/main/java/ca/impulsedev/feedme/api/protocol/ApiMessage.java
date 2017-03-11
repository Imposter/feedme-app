package ca.impulsedev.feedme.api.protocol;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ApiMessage<TData> {
    @SerializedName("code")
    private int mCode;

    @SerializedName("data")
    private Object mDataSerialized;

    // These fields are transient because we don't want the JSON serializer to
    // discover type info for them
    private transient Class<TData> mClassInfo;
    private transient TData mData;

    public ApiMessage(Class<TData> classInfo) {
        mClassInfo = classInfo;
        mDataSerialized = "null";

        mCode = -1;
        mData = null;
    }

    public int getCode() {
        return mCode;
    }

    public TData getData() {
        return mData;
    }

    public void deserialize(String data) {
        Gson gson = new Gson();
        ApiMessage<TData> message = gson.fromJson(data, getClass());

        mCode = message.mCode;
        mDataSerialized = gson.toJson(message.mDataSerialized);
        mData = gson.fromJson(mDataSerialized.toString(), mClassInfo);
    }

    public String serialize() {
        Gson gson = new Gson();
        mDataSerialized = gson.toJson(mData, mClassInfo);
        return gson.toJson(this, getClass());
    }
}