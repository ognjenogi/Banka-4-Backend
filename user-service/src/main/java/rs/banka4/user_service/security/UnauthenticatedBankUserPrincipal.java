package rs.banka4.user_service.security;

public record UnauthenticatedBankUserPrincipal(
    UserType userType,
    String email
) {
}
