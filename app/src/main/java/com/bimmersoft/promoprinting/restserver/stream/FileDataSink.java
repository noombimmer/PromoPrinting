package com.bimmersoft.promoprinting.restserver.stream;

import com.bimmersoft.promoprinting.restserver.AsyncServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by koush on 2/2/14.
 */
public class FileDataSink extends OutputStreamDataSink {
    File file;
    public FileDataSink(AsyncServer server, File file) {
        super(server);
        this.file = file;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        OutputStream ret = super.getOutputStream();
        if (ret == null) {
            ret = new FileOutputStream(file);
            setOutputStream(ret);
        }
        return ret;
    }
}
