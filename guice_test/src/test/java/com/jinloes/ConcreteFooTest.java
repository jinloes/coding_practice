package com.jinloes;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Created by jinloes on 9/28/15.
 */
public class ConcreteFooTest {
    @Mock private FooConfig config;
    @Mock private FooClient client;
    @InjectMocks private ConcreteFoo foo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDoFoo() {
        Mockito.when(config.getOs()).thenReturn("Mock");
        foo.doFoo();
        Mockito.verify(config).getOs();
    }

}
