package de.tdrstudios.ingwer.identity;

public enum AccessType {

    ADMIN(10),
    ALL(9),
    TRUST(8),
    TROLLING(2),
    LOGGING(1),
    NOTHING(0),
    UNKNOWN(0);



  AccessType(int level) {
      this.level = level;
      this.name = getbyLevel(level).name();
  }
  private int level;
  public String name;

    public AccessType getbyLevel(int level) {
        for (AccessType type : values()) {
            if (type.level == level) return type;
        }
        return UNKNOWN;
    }

    public int getLevel() {
        return level;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AccessType getUNKNWON() {
        return UNKNOWN;
    }
}
