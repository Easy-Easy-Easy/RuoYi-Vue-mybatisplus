package com.ruoyi.project.websocket;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName OnlineChat
 * @Description TODO
 * @Author zheng
 * @Date 2020/11/24 10:09
 * @Version 1.0
 **/
@ServerEndpoint(value = "/onlineChat")
@Component
public class OnlineChat {
    private static Log log = LogFactory.getLog(OnlineChat.class);
    /**
     * 在线人数
     */
    public static int onlineNumber = 0;
    private Lock lock = new ReentrantLock();

    /**
     * 会话
     */
    private Session session;
    @OnOpen
    public void onOpen(Session session) {
        boolean errorFlag = false;
        lock.lock();
        try {
            this.session = session;
            String sid = UUID.randomUUID().toString().replace("-", "");
            onlineNumber++;
            lock.unlock();
            log.info("有用户加入新连接！ 当前在线人数" + onlineNumber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() throws InterruptedException {
        lock.lock();
        try {
            onlineNumber--;
            lock.unlock();
            log.info("有连接关闭！ 当前在线人数" + onlineNumber);
        } catch (Exception e) {
            log.error("onclose exception" + e);
        }finally {
          lock.unlock();
        }

    }

    /**
     * 收到客户端的消息
     *
     * @param message 消息
     * @param session 会话
     * @throws InterruptedException
     */

    @OnMessage
    public void onMessage(String message, Session session) {

    }


    @OnError
    public void onError(Throwable throwable) {
        log.info("[WebSocketServer] Connection Exception :throwable = " + throwable.getMessage());
    }

    /**
     * 发送消息
     *
     * @param message 消息
     */
    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("sendStringMessage" + e);
        }
    }

    /**
     * 发送bytemessage
     *
     * @param message
     */
    public void sendByteMessage(byte[] message) {
        try {
            ByteBuffer bf = ByteBuffer.wrap(message);
            session.getBasicRemote().sendBinary(bf);
        } catch (IOException e) {
            log.error("sendByteMessage" + e);
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            this.session.close();
        } catch (Exception e) {
            log.error("------------------------ 连接关闭异常  --- ------------------------" + e);
        }
    }
}
