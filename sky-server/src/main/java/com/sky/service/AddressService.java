package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressService {
    void addNewAddress(AddressBook addressBook);


    List<AddressBook> list(AddressBook addressBook);

    AddressBook getById(Long id);

    void deleteById(Long id);

    void UpdateAddressById(AddressBook addressBook);

    void SetDefultAddress(AddressBook addressBook);
}
