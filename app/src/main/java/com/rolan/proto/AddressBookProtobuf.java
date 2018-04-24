package com.rolan.proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangyang on 2018/4/20.下午6:26
 */
public class AddressBookProtobuf {
    public static byte[] encodeTest(String[] names) {

        AddressBookProtos.AddressBook.Builder addressBook = AddressBookProtos.AddressBook.newBuilder();

        for(int i = 0; i < names.length; ++ i) {
            addressBook.addPerson(AddPerson.createPerson(names[i]));
        }
        AddressBookProtos.AddressBook book = addressBook.build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            book.writeTo(baos);
        } catch (IOException e) {
        }

        return baos.toByteArray();
    }


    public static byte[] encodeTest(String[] names, int times) {
        for (int i = 0; i < times - 1; ++ i) {
            encodeTest(names);
        }
        return encodeTest(names);
    }

    public static AddressBookProtos.AddressBook decodeTest(InputStream is) {
        AddressBookProtos.AddressBook addressBook = null;
        try {
            addressBook = AddressBookProtos.AddressBook.parseFrom(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressBook;
    }

    public static AddressBookProtos.AddressBook decodeTest(InputStream is, int times) {
        AddressBookProtos.AddressBook addressBook = null;
        for (int i = 0; i < times; ++ i) {
            addressBook = decodeTest(is);
        }
        return addressBook;
    }
}
