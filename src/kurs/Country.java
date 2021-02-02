package kurs;

public class Country {
    private String name;
    private Long ageAdult;

    public Country(String name, Long ageAdult) {
        this.name = name;
        this.ageAdult = ageAdult;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAgeAdult(Long ageAdult) {
        this.ageAdult = ageAdult;
    }

    public String getName() {
        return name;
    }

    public Long getAgeAdult() {
        return ageAdult;
    }

    public Boolean getTeen(Long age) {
        return age < getAgeAdult();
    }
}
