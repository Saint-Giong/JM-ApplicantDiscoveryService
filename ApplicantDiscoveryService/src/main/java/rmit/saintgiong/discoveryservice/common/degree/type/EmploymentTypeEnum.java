package rmit.saintgiong.discoveryservice.common.degree.type;

public enum EmploymentTypeEnum {
    FULL_TIME(0),
    PART_TIME(1),
    FRESHER(2),
    INTERNSHIP(3),
    CONTRACT(4);

    private final int bitIndex;

    EmploymentTypeEnum(int bitIndex) { this.bitIndex = bitIndex; }
    public int getBitIndex() { return bitIndex; }

    // Helper to find enum by name (case-insensitive)
    public static int getIndexByName(String name) {
        return valueOf(name.toUpperCase().replace("-", "_")).bitIndex;
    }
}