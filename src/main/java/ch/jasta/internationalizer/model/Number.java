package ch.jasta.internationalizer.model;

public class Number {

  private long id;
  private String number;
  private String type;
  private String internationalNumber;
  
  public Number(long id, String number, String type, String internationalNumber) {
    this.id = id;
    this.number = number;
    this.type = type;
    this.internationalNumber = internationalNumber;
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
  
  public String getInternationalNumber() {
    return internationalNumber;
  }

  public void setInternationalNumber(String internationalNumber) {
    this.internationalNumber = internationalNumber;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(number);
    if (!internationalNumber.equals(number)) {
      builder.append(" -> ");
      builder.append(internationalNumber);
    } else {
      builder.append("   unchanged");
    }
    return builder.toString();
  }
  
  
}
