package izanagi.api.v1;

import java.io.IOException;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(NoSuchElementException.class)
  public void handleNoSuchElementException(
      final NoSuchElementException exception,
      final HttpServletResponse response
  ) throws IOException {
    response.sendError(HttpStatus.NOT_FOUND.value(), exception.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public void handleIllegalArgumentException(
      final IllegalArgumentException exception,
      final HttpServletResponse response
  ) throws IOException {
    response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public void handleIllegalStateException(
      final IllegalStateException exception,
      final HttpServletResponse response
  ) throws IOException {
    response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
  }
}
