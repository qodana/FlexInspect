package org.flexInspect;

public class ClassA {
    String foo;
    String bar;
    ClassA(String foo, String bar) {
        this.foo = foo;
        this.bar = bar;
    }
    String fooBarMethod() {
        return foo+bar;
    }
    void dependOnClassB() {
        ClassB bClass = new ClassB("a", "b", "c");
        ClassB cClass = new ClassB("ac", "ba", "cf");
        System.out.println(bClass);
    }
}
