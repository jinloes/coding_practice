package com.jinloes.reflection.impl;

import com.jinloes.reflection.Foo;
import com.jinloes.reflection.util.PolicyTypeMapping;

/**
 * Created by jinloes on 9/17/15.
 */
@PolicyTypeMapping(policyType = PrintFoo.TYPE)
public class PrintFoo implements Foo {
    static final String TYPE = "print";
    public void doFoo() {
        System.out.println("foo");
    }
}
