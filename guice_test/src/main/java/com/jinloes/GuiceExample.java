package com.jinloes;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Created by jinloes on 9/28/15.
 */
public class GuiceExample {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new FooModule());
        for (int i = 0; i < 5; i++) {
            Foo foo = injector.getInstance(Foo.class);
            foo.doFoo();
        }
    }
}
