package ch.jasta.internationalizer.model;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;


public class Number {

  private long id;
  private String number;
  private String type;
  
  public Number(long id, String number, String type) {
    this.id = id;
    this.number = number;
    this.type = type;
  }

  public long getId() {
    return id;
  }
  
  public String getNumber() {
    return number;
  }
  
  public void setNumber(String number) {
    this.number = number;
  }

  public String getType() {
    return type;
  }
  
  public String getInternationalNumber(String twoLetterCountry) {
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    PhoneNumber phoneNumber;
    try {
      phoneNumber = phoneUtil.parse(getNumber(), twoLetterCountry);
    } catch (NumberParseException e) {
      System.err.println("NumberParseException was thrown: " + e.toString());
      return getNumber();
    }
    if (!phoneUtil.isValidNumber(phoneNumber)) {
      System.err.println("Not a valid number: " + phoneNumber);
      return getNumber();
    }
    
    String internationalNumber = phoneUtil.format(phoneNumber, PhoneNumberFormat.INTERNATIONAL);
    return internationalNumber;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(number);
    //FIXME : Must be taken from preferences
    String twoLetterCountry = "CH";
    String internationalNumber = getInternationalNumber(twoLetterCountry);
    if (internationalNumber != null 
        && !internationalNumber.trim().equalsIgnoreCase(number)) {
      builder.append(" -> ");
      builder.append(internationalNumber);
    } else {
      builder.append(" nothing to change.");
    }
    return builder.toString();
  }
  
  
}
