package org.example;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        WindowManager window = new WindowManager(1024, 640, "Test");

        GameEngineManager gameEngineManager = new GameEngineManager(window);

    }
}