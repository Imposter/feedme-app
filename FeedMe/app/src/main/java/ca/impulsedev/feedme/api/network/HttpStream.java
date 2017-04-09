package ca.impulsedev.feedme.api.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Used for HTTP communication, reads an entire input stream when a response is received
 * and copies stream buffer to another buffer for access later, preventing destruction of data by
 * Java garbage collection
 */
public class HttpStream extends InputStream {
    ByteArrayOutputStream byteStream;
    int position = 0;

    /**
     * Reads input stream to internal byte stream
     * @param stream Input stream to read
     * @throws IOException Thrown if an error occurred while reading the input stream
     */
    public HttpStream(InputStream stream) throws IOException {
        byteStream = new ByteArrayOutputStream();
        int b = -1;
        while ((b = stream.read()) != -1) {
            byteStream.write(b);
        }
    }

    /**
     * Basic reading method for InputStream, allowing for reading HTTP response data
     * @return Byte read, -1 if failed
     * @throws IOException Thrown if an error occurred while reading the stream
     */
    @Override
    public int read() throws IOException {
        byte[] bytes = byteStream.toByteArray();
        if (position >= bytes.length) {
            return -1;
        }
        return bytes[position++];
    }
}
