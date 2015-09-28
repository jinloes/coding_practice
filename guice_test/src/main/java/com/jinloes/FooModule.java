package com.jinloes;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Created by jinloes on 9/28/15.
 */
public class FooModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Foo.class).to(ConcreteFoo.class);
        bind(FooConfig.class).in(Singleton.class);
    }

    @Provides
    public FooClient fooClientProvider() {
        // This is a simple case but it demonstrates how to provide a an object
        return new FooClient();
    }
}
