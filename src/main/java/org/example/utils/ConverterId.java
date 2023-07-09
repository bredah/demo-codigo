
package org.example.utils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.example.dto.ErrorResponse;


@Getter
public class ConverterId {

  private UUID id;
  private ErrorResponse error;

  public ConverterId(String id) {
    converter(id);
  }

  public boolean hasError() {
    return error != null;
  }

  private void converter(String id) {
    try {
      this.id = UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      List<String> errors = Collections.singletonList("UUID inválido");
      error = new ErrorResponse("validation error", errors);
    }
  }
}
