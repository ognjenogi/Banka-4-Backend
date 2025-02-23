package rs.banka4.user_service.exceptions;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final int statusCode;

    public CustomException(String message, int statusCode) {
      super(message);
      this.statusCode = statusCode;
   }
}
