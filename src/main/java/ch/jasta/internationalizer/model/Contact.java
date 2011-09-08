package ch.jasta.internationalizer.model;

import java.util.LinkedList;
import java.util.List;

public class Contact {

  private String id;
  private String displayName;
  private List<Number> numbers;
  
  public Contact(String id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }

  public String getId() {
    return id;
  }

  public String getDisplayName() {
    return displayName;
  }
  
  public List<Number> getNumbers() {
    if (numbers == null) {
      numbers = new LinkedList<Number>();
    }
    return numbers;
  }
  
  public void addNumber(Number number) {
    getNumbers().add(number);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(displayName);
    if (getNumbers().size() > 0) {
      builder.append(":");
      for (Number number : getNumbers()) {
        builder.append("\n").append(number);
      }
    }
    return builder.toString();
  }
  
}
