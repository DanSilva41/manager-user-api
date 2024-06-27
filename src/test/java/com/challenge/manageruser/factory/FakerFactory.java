package com.challenge.manageruser.factory;

import net.datafaker.Faker;

import java.util.Locale;

public class FakerFactory {

    public static final Faker faker = new Faker(new Locale("pt-BR"));

    private FakerFactory() throws IllegalAccessException {
        throw new IllegalAccessException("Do not instantiate this class, use statically");
    }
}
