package ch.jasta.internationalizer.model;


public class Preferences {
  
  private String defaultCountry;
  
  public Preferences(String defaultCountry) {
    this.defaultCountry = defaultCountry;
  }

  public String getDefaultCountry() {
    return defaultCountry;
  }
  
  public void setDefaultCountry(String defaultCountry) {
    this.defaultCountry = defaultCountry;
  }
  
}
