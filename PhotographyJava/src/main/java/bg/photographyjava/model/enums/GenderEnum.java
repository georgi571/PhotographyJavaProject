package bg.photographyjava.model.enums;

public enum GenderEnum {
    MALE ("Male"),
    FEMALE("Female");

    private final String genderType;

    GenderEnum(String genderType) {
        this.genderType = genderType;
    }

    public String getGenderType() {
        return genderType;
    }

    public static GenderEnum fromString(String genderType) {
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.getGenderType().equalsIgnoreCase(genderType)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("No enum constant for gender type: " + genderType);
    }
}
