package shichengxu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class MyClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        String path0 = MyClassLoader.class.getResource("").getPath().substring(1)
                .replace("/", "\\\\");
        System.out.println(path0);
        String path = path0 + "Hello.xlass"; // xlass

        File file = new File(path);
        try {
            byte[] bytes = getClassBytes(file);
            bytes = subByte255(bytes);
            return defineClass(name, bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] subByte255(byte[] bytes) {
        int length = bytes.length;
        byte[] result = new byte[length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = ((byte) (255 - bytes[i]));
        }
        return result;
    }

    private byte[] getClassBytes(File file) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel = fileInputStream.getChannel();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        WritableByteChannel writableByteChannel = Channels.newChannel(byteArrayOutputStream);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (true) {
            int i = fileChannel.read(byteBuffer);
            if (i == 0 || i == -1)
                break;
            byteBuffer.flip();
            writableByteChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        fileInputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        Class<?> helloClass = new MyClassLoader().findClass("Hello");
        System.out.println(helloClass.getClassLoader()); //com.asdf.test1.MyClassLoader@15fbaa4
        Method method = helloClass.getMethod("hello");
        method.invoke(helloClass.newInstance()); //Hello, classLoader!
    }
}
