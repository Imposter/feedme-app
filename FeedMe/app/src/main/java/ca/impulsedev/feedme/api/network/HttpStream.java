package ca.impulsedev.feedme.api.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpStream extends InputStream {
    ByteArrayOutputStream byteStream;
    int position = 0;

    public HttpStream(InputStream stream) throws IOException {
        byteStream = new ByteArrayOutputStream();
        int b = -1;
        while ((b = stream.read()) != -1) {
            byteStream.write(b);
        }
    }

    @Override
    public int read() throws IOException {
        byte[] bytes = byteStream.toByteArray();
        if (position >= bytes.length) {
            return -1;
        }
        return bytes[position++];
    }
}
