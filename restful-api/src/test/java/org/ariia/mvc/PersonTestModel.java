package org.ariia.mvc;

import java.util.Date;
import java.util.UUID;

public class PersonTestModel {

    String id;
    String firstName;
    String lastName;

    int salary;
    Date modified;

    public PersonTestModel() {
        this.id = UUID.randomUUID().toString();
//		this.firstName = "Ma";
//		this.lastName = "Jo";
//		this.salary = 7778;
//		this.modified = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return "PersonTestModel [id=" + id + ", firstName=" + firstName
                + ", lastName=" + lastName + ", salary="
                + salary + ", modified=" + modified + "]";
    }


}
