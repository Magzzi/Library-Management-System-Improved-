package com.library.management.classes;

class Person {
    private String name;
    private int age;
    private String address;

    //Constructor w/ no parameters
    public Person(){

    }

    //Constructor w/ parameters
    public Person(String name, int age, String address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }

    // Getter
    public String getName() {
        return name;
    }

    public int getAge() {
            return age;
        }

    public String getAddress() {
        return address;
    }

    //Setter
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}