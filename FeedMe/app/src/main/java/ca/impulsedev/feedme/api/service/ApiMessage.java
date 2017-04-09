/**
 * Feed Me! Android App
 *
 * Created by:
 * - Betty Kwong
 * - Eyaz Rehman
 * - Rameet Sekhon
 * - Rishabh Patel
 */
 
package ca.impulsedev.feedme.api.service;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * API messages are serialized into JSON objects of format { code: INTEGER, data: OBJECT }
 * which are specific to each result type, this class is able to serialize and deserialize any data
 * type which is used for communication between the client and backend
 * @param <TData> Result data type
 */
public class ApiMessage<TData> {
    @SerializedName("code")
    private int mCode;

    @SerializedName("data")
    private Object mDataSerialized;

    // These fields are transient because we don't want the JSON serializer to
    // discover type info for them
    private transient Class<TData> mClassInfo;
    private transient TData mData;

    /**
     * Initialize with required class info to allow serialization
     * @param classInfo Data class type information
     */
    public ApiMessage(Class<TData> classInfo) {
        mClassInfo = classInfo;
        mDataSerialized = "null";

        mCode = -1;
        mData = null;
    }

    /**
     * Get API specific message code
     * @return API specific message code
     */
    public int getCode() {
        return mCode;
    }

    /**
     * Get message data
     * @return API message data
     */
    public TData getData() {
        return mData;
    }

    /**
     * Deserialize JSON string into ApiMessage
     * @param data JSON serialized string
     */
    public void deserialize(String data) {
        Gson gson = new Gson();
        ApiMessage<TData> message = gson.fromJson(data, getClass());

        mCode = message.mCode;
        mDataSerialized = gson.toJson(message.mDataSerialized);
        mData = gson.fromJson(mDataSerialized.toString(), mClassInfo);
    }

    /**
     * Serialize class instance into JSON sting
     * @return String serialized in JSON format
     */
    public String serialize() {
        Gson gson = new Gson();
        mDataSerialized = gson.toJson(mData, mClassInfo);
        return gson.toJson(this, getClass());
    }
}