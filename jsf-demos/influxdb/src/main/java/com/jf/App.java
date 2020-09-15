package com.jsf;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        while (true) {
            InfluxDbUtil.write();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
