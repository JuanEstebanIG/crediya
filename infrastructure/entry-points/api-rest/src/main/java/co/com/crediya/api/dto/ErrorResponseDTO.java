package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO estándar de respuesta para errores")
public class ErrorResponseDTO {

    @Schema(description = "Fecha y hora del error")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP", example = "400")
    private int status;

    @Schema(description = "Descripción del estado HTTP", example = "Bad Request")
    private String error;

    @Schema(description = "Mensaje descriptivo del error", example = "Error de validación")
    private String mensaje;

    @Schema(description = "Lista de detalles específicos del error")
    private List<String> detalles;
}
