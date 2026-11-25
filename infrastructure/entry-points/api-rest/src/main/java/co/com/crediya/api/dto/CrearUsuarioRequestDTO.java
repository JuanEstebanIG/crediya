package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "DTO de entrada para el registro de un nuevo usuario solicitante")
public class CrearUsuarioRequestDTO {

    @NotBlank(message = "El campo nombres es obligatorio")
    @JsonProperty("nombres")
    @JsonAlias({"nombre"})
    @Schema(description = "Nombres del usuario", example = "Juan Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombres;

    @NotBlank(message = "El campo apellidos es obligatorio")
    @JsonProperty("apellidos")
    @JsonAlias({"apellido"})
    @Schema(description = "Apellidos del usuario", example = "Perez Gomez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellidos;

    @JsonProperty("fecha_nacimiento")
    @JsonAlias({"fechaNacimiento"})
    @Schema(description = "Fecha de nacimiento en formato ISO-8601 (YYYY-MM-DD)", example = "1995-05-20")
    private LocalDate fechaNacimiento;

    @JsonProperty("direccion")
    @Schema(description = "Dirección de residencia", example = "Calle 10 # 20-30")
    private String direccion;

    @JsonProperty("telefono")
    @Schema(description = "Número telefónico de contacto", example = "3001234567")
    private String telefono;

    @NotBlank(message = "El campo correo_electronico es obligatorio")
    @Email(message = "El correo_electronico debe tener un formato válido")
    @JsonProperty("correo_electronico")
    @JsonAlias({"correoElectronico", "email"})
    @Schema(description = "Correo electrónico único del usuario", example = "juan.perez@crediya.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String correoElectronico;

    @NotNull(message = "El campo salario_base es obligatorio")
    @DecimalMin(value = "0", inclusive = true, message = "El salario_base debe ser mayor o igual a 0")
    @DecimalMax(value = "15000000", inclusive = true, message = "El salario_base debe ser menor o igual a 15000000")
    @JsonProperty("salario_base")
    @JsonAlias({"salarioBase"})
    @Schema(description = "Salario base del solicitante (entre 0 y 15000000)", example = "3500000", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal salarioBase;

    @JsonProperty("documento_identidad")
    @JsonAlias({"documentoIdentidad"})
    @Schema(description = "Número de documento de identidad", example = "1020304050")
    private String documentoIdentidad;

    @JsonProperty("id_rol")
    @JsonAlias({"idRol"})
    @Schema(description = "Identificador del rol asignado al usuario", example = "3")
    private Long idRol;
}
