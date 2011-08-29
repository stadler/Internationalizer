package ch.jasta.internationalizer.model;

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
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(number);
    return builder.toString();
  }
  
  
}
