package sample.stream;

import java.io.IOException;

import static sample.client.Client.objectInputStream;
import static sample.client.Client.objectOutputStream;

public class IOToServerStreamer {
    public void writeObject(Object object) {
        Thread sender = new Thread(() -> {
            try {
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        startAndWait(sender);
    }

    public void writeString(String string) {
        Thread sender = new Thread(() -> {
            try {
                objectOutputStream.writeUTF(string);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        startAndWait(sender);
    }

    public Object readObject() {
        try {
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void startAndWait(Thread sender) {
        sender.start();
        try {
            sender.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String readString() {
        try {
            return objectInputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
