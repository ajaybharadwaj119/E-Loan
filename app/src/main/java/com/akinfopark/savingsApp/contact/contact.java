package com.akinfopark.savingsApp.contact;

public class contact {

    private String name = "";
    private String number = "";


    @Override
    public String toString() {
        return "{" +
                "\"name\":" + "\"" + name + "\"" +
                ",\"number\":" + "\"" + number + "\"" +
                " }";
    }

    public contact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
