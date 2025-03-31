package rs.banka4.rafeisen.common.security;

public enum Privilege {
    /* *NEVER* reorder elements in this enum - their order is significant. */
    ADMIN;

    public long bit() {
        return 1L << this.ordinal();
    }
}
