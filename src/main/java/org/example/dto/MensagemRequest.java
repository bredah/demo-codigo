
package org.example.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MensagemRequest {
  private String usuario;
  private String conteudo;
}
