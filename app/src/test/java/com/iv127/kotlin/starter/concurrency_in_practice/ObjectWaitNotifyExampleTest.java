package com.iv127.kotlin.starter.concurrency_in_practice;

import org.junit.jupiter.api.Test;

import java.util.List;

public class ObjectWaitNotifyExampleTest {

    @Test
    public void testWaitAndNotifyThroughSenderReceiver() throws Exception {
        Data data = new Data();
        Thread sender = new Thread(new Sender(data));
        Thread receiver = new Thread(new Receiver(data));

        sender.start();
        receiver.start();
    }

    private static class Sender implements Runnable {
        private final Data data;

        public Sender(Data data) {
            this.data = data;
        }

        public void run() {
            List<String> packets = List.of(
                    "First packet",
                    "Second packet",
                    "Third packet",
                    "Fourth packet",
                    "End");

            packets.forEach(data::send);
        }
    }

    private static class Receiver implements Runnable {
        private final Data load;

        public Receiver(Data load) {
            this.load = load;
        }

        public void run() {
            // Keep receiving messages until "End" is received
            while (true) {
                String receivedMessage = load.receive();

                if ("End".equals(receivedMessage)) {
                    break;
                }

                System.out.println(receivedMessage);
            }
        }
    }

    private static class Data {
        private String packet;

        // True: receiver waits
        // False: sender waits
        private boolean transfer = true;

        public synchronized String receive() {
            while (transfer) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Thread Interrupted");
                }
            }

            transfer = true;
            notifyAll();
            return packet;
        }

        public synchronized void send(String packet) {
            while (!transfer) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Thread Interrupted");
                }
            }

            this.packet = packet;
            transfer = false;
            notifyAll();
        }
    }
}
