package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta con los datos del usuario registrado")
public class UsuarioResponseDTO {

    @JsonProperty("id_usuario")
    @Schema(description = "Identificador único del usuario", example = "1")
    private Long idUsuario;

    @JsonProperty("nombres")
    @Schema(description = "Nombres del usuario", example = "Juan Carlos")
    private String nombres;

    @JsonProperty("apellidos")
    @Schema(description = "Apellidos del usuario", example = "Perez Gomez")
    private String apellidos;

    @JsonProperty("fecha_nacimiento")
    @Schema(description = "Fecha de nacimiento", example = "1995-05-20")
    private LocalDate fechaNacimiento;

    @JsonProperty("direccion")
    @Schema(description = "Dirección de residencia", example = "Calle 10 # 20-30")
    private String direccion;

    @JsonProperty("telefono")
    @Schema(description = "Teléfono de contacto", example = "3001234567")
    private String telefono;

    @JsonProperty("correo_electronico")
    @Schema(description = "Correo electrónico registrado", example = "juan.perez@crediya.com")
    private String correoElectronico;

    @JsonProperty("documento_identidad")
    @Schema(description = "Documento de identidad", example = "1020304050")
    private String documentoIdentidad;

    @JsonProperty("id_rol")
    @Schema(description = "Identificador del rol asignado", example = "3")
    private Long idRol;

    @JsonProperty("salario_base")
    @Schema(description = "Salario base", example = "3500000")
    private BigDecimal salarioBase;
}
