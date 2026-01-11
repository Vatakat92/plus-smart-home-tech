package ru.yandex.practicum.commerce.warehouse.util;

import java.security.SecureRandom;

public class RandomAddressGenerator {
    
    private static final String[] ADDRESSES = 
        new String[] {"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS = 
        ADDRESSES[new SecureRandom().nextInt(0, ADDRESSES.length)];
    
    public static String getCurrentAddress() {
        return CURRENT_ADDRESS;
    }
}