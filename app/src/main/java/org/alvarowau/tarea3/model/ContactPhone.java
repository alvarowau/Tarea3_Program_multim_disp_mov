package org.alvarowau.tarea3.model;

public class ContactPhone {

    private String name;
    private String numberPhone;
    private String birthdate;
    private String photo;


    public ContactPhone(String name, String numberPhone, String birthdate) {
        this.name = name;
        this.numberPhone = numberPhone;
        this.birthdate = birthdate;
        this.photo = "";
    }

    public ContactPhone(String name, String numberPhone, String birthdate, String photo) {
        this.name = name;
        this.numberPhone = numberPhone;
        this.birthdate = birthdate;
        this.photo = photo;
    }


    public ContactPhone(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
