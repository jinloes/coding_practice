package com.jinloes.impl;

import com.jinloes.Foo;
import com.jinloes.util.PolicyTypeMapping;

/**
 * Created by jinloes on 9/17/15.
 */
@PolicyTypeMapping(policyType = DoublePrintFoo.TYPE)
public class DoublePrintFoo implements Foo {
    static final String TYPE = "Double";
    public void doFoo() {
        System.out.println("Foo");
        System.out.println("Foo");
    }
}
