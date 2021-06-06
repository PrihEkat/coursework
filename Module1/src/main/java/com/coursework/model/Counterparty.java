package com.coursework.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**A class describing counterparty.*/
@Entity
@Table(name = "counterparty")
public class Counterparty extends AbstractEntity {
    @Column
    private String name;

    @Column
    private String inn;

    @Column
    private String kpp;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "bik_bank")
    private String bikBank;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBikBank() {
        return bikBank;
    }

    public void setBikBank(String bikBank) {
        this.bikBank = bikBank;
    }

    @Override
    public String toString() {
        return "Counterparty{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                ", inn='" + inn + '\'' +
                ", kpp='" + kpp + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", bikBank='" + bikBank + '\'' +
                '}';
    }
}

