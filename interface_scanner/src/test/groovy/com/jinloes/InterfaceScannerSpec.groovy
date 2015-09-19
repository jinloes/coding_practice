package com.jinloes

import com.jinloes.impl.DoublePrintFoo
import com.jinloes.impl.PrintFoo
import spock.lang.Specification

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo

class InterfaceScannerSpec extends Specification {
    def InterfaceScanner scanner;

    def setup() {
        scanner = new InterfaceScanner();
    }

    def "An interface scanner should find implementation classes"() {
        when: "scan for interfaces"
            def Map<String, Class<? extends Foo>> result = scanner.doScan();
        then: "com.jinloes.impl should be scanned for implementations of Foo"
            def Map<String, Class<? extends Foo>> map = ["Double": DoublePrintFoo.class, "print": PrintFoo.class]
            assertThat result as LinkedHashMap<String, Class>, equalTo(map)
    }
}