package com.jinloes;

import javax.inject.Inject;

/**
 * Created by jinloes on 9/28/15.
 */
public class ConcreteFoo implements Foo {
    private final FooConfig fooConfig;
    private final FooClient fooClient;

    @Inject
    ConcreteFoo(FooConfig fooConfig, FooClient fooClient) {
        this.fooConfig = fooConfig;
        this.fooClient = fooClient;
    }

    @Override
    public void doFoo() {
        System.out.println("Doing concrete foo on OS: " + fooConfig.getOs() + " on instance " + this +
                " with config " + fooConfig + " and client " + fooClient);
    }
}
