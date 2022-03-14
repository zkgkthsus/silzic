package com.ssafy.deathnotelive.service;

import java.util.Random;

public class Util {

    final static char[] charTableList = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    public static String getRoomCode() {
        Random random = new Random(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(charTableList[random.nextInt(charTableList.length)]);
        }
        return sb.toString();
    }

}
