package com.github.axet.androidlibrary.app;

import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import de.innosystec.unrar.NativeFile;
import de.innosystec.unrar.NativeStorage;

public class RarFD extends NativeStorage {
    FileDescriptor fd;
    FileInputStream fis;
    FileChannel fc;

    public static class RarFile extends NativeFile {
        FileChannel fc;

        public RarFile(FileChannel fc) {
            this.fc = fc;
        }

        public void setPosition(long s) throws IOException {
            fc.position(s);
        }

        public int read() throws IOException {
            ByteBuffer bb = ByteBuffer.allocate(1);
            fc.read(bb);
            bb.flip();
            return bb.getInt();
        }

        public int readFully(byte[] buf, int len) throws IOException {
            ByteBuffer bb = ByteBuffer.allocate(len);
            fc.read(bb);
            bb.flip();
            ByteBuffer.wrap(buf).put(bb);
            return len;
        }

        public int read(byte[] buf, int off, int len) throws IOException {
            ByteBuffer bb = ByteBuffer.allocate(len);
            fc.read(bb);
            bb.flip();
            ByteBuffer.wrap(buf, off, len).put(bb);
            return len;
        }

        public long getPosition() throws IOException {
            return fc.position();
        }

        public void close() throws IOException {
            fc.close();
        }
    }

    public RarFD(FileDescriptor fd) {
        super((File) null);
        this.fd = fd;
        fis = new FileInputStream(fd);
        fc = fis.getChannel();
    }

    @Override
    public NativeFile read() throws FileNotFoundException {
        return new RarFile(fc);
    }

    @Override
    public NativeStorage open(String name) {
        throw new RuntimeException("not supported");
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public NativeStorage getParent() {
        throw new RuntimeException("not supported");
    }

    @Override
    public long length() {
        try {
            return fc.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPath() {
        throw new RuntimeException("not supported");
    }
}
