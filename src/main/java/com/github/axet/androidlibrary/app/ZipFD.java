package com.github.axet.androidlibrary.app;

import net.lingala.zip4j.core.NativeFile;
import net.lingala.zip4j.core.NativeStorage;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ZipFD extends NativeStorage {
    FileDescriptor fd;
    FileInputStream fis;
    FileChannel fc;

    public static class ZipNativeFile extends NativeFile {
        FileChannel fc;

        public ZipNativeFile(FileChannel fc) {
            this.fc = fc;
        }

        public long length() throws IOException {
            return fc.size();
        }

        public void seek(long s) throws IOException {
            fc.position(s);
        }

        public void readFully(byte[] buf, int off, int len) throws IOException {
            read(buf, off, len);
        }

        public int read(byte[] buf) throws IOException {
            ByteBuffer bb = ByteBuffer.wrap(buf);
            int l = fc.read(bb);
            bb.flip();
            return l;
        }

        public int read(byte[] buf, int off, int len) throws IOException {
            ByteBuffer bb = ByteBuffer.wrap(buf, off, len);
            fc.read(bb);
            bb.flip();
            return len;
        }

        public long getFilePointer() throws IOException {
            return fc.position();
        }

        public void close() throws IOException {
        }

        public void write(byte[] buf) throws IOException {
            throw new RuntimeException("not supported");
        }

        public void write(byte[] b, int off, int len) throws IOException {
            throw new RuntimeException("not supported");
        }
    }

    public ZipFD(FileDescriptor fd) {
        super((File) null);
        this.fd = fd;
        fis = new FileInputStream(fd);
        fc = fis.getChannel();
    }

    public NativeFile read() throws FileNotFoundException {
        return new ZipNativeFile(fc);
    }

    public NativeFile write() throws FileNotFoundException {
        throw new RuntimeException("not supported");
    }

    public NativeStorage open(String name) {
        throw new RuntimeException("not supported");
    }

    public boolean exists() {
        return true;
    }

    public boolean canRead() {
        return true;
    }

    public boolean canWrite() {
        return false;
    }

    public boolean isHidden() {
        return false;
    }

    public NativeStorage getParent() {
        throw new RuntimeException("not supported");
    }

    public String getName() {
        throw new RuntimeException("not supported");
    }

    public boolean isDirectory() {
        return false;
    }

    public long lastModified() {
        throw new RuntimeException("not supported");
    }

    public long length() {
        try {
            return fc.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean renameTo(NativeStorage f) {
        throw new RuntimeException("not supported");
    }

    public void setLastModified(long l) {
        throw new RuntimeException("not supported");
    }

    public void setReadOnly() {
        throw new RuntimeException("not supported");
    }

    public boolean mkdirs() {
        throw new RuntimeException("not supported");
    }

    public boolean delete() {
        throw new RuntimeException("not supported");
    }

    public NativeStorage[] listFiles() {
        throw new RuntimeException("not supported");
    }

    public String getPath() {
        throw new RuntimeException("not supported");
    }

    public String getRelPath(NativeStorage child) {
        throw new RuntimeException("not supported");
    }
}
