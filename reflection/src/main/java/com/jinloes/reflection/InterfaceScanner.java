package com.jinloes.reflection;

import com.google.common.collect.ImmutableMap;
import com.jinloes.reflection.util.PolicyTypeMapping;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;

public class InterfaceScanner {
  public static void main(String[] args) {
    InterfaceScanner interfaceScanner = new InterfaceScanner();
    interfaceScanner.doScan();
  }

  public Map<String, Class<? extends Foo>> doScan() {
    ImmutableMap.Builder<String, Class<? extends Foo>> policyTypeMaps = ImmutableMap.builder();
    Reflections reflections = new Reflections("com.jinloes.reflection.impl");
    Set<Class<? extends Foo>> subTypes = reflections.getSubTypesOf(Foo.class);
    for (Class<? extends Foo> clazz : subTypes) {
      try {
        System.out.println(clazz + " " + clazz.getAnnotation(PolicyTypeMapping.class).policyType());
        policyTypeMaps.put(clazz.getAnnotation(PolicyTypeMapping.class).policyType(), clazz);
      } catch (NullPointerException e) {
        System.out.println(clazz + " does not have PolicyTypeMapping annotation");
      }
    }
    return policyTypeMaps.build();
  }
}
