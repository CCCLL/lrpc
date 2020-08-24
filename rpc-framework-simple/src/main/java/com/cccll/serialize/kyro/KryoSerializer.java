package com.cccll.serialize.kyro;

import com.cccll.serialize.Serialize;
import com.esotericsoftware.kryo.Kryo;

public class KryoSerializer implements Serialize {

    /**
     * Because Kryo is not thread safe. So, use ThreadLocal to store Kryo objects
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        return kryo;
    });


    /**
     * 序列化
     *
     * @param obj 要序列化的对象
     * @return 字节数组
     */
    @Override
    public byte[] serialize(Object obj) {
        return new byte[0];
    }

    /**
     * 反序列化
     *
     * @param bytes 序列化后的字节数组
     * @param clazz 目标类
     * @return 反序列化的对象
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }
}
