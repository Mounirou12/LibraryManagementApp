package com.projet;

import java.time.LocalDate;

public class Member {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate membershipDate;
    private MemberStatus status;

    
    public Member(int id, String firstName, String lastName, String email, String phone, LocalDate membershipDate,
            MemberStatus status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.membershipDate = membershipDate;
        this.status = status;
    }


    public int getId() {
        return id;
    }


    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public String getEmail() {
        return email;
    }


    public String getPhone() {
        return phone;
    }


    public LocalDate getMembershipDate() {
        return membershipDate;
    }


    public MemberStatus getStatus() {
        return status;
    }

}
