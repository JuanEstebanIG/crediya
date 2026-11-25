package co.com.crediya.api;

import co.com.crediya.api.dto.CrearUsuarioRequestDTO;
import co.com.crediya.api.dto.ErrorResponseDTO;
import co.com.crediya.api.dto.UsuarioResponseDTO;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.registrarusuario.RegistrarUsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones para la gestión de usuarios")
public class ApiRest {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Operation(summary = "Registrar un nuevo usuario solicitante", description = "Crea y persiste un nuevo usuario en el sistema validando campos obligatorios, formato y unicidad de correo electrónico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o error de validación de dominio",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "El correo electrónico ya se encuentra registrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@Valid @RequestBody CrearUsuarioRequestDTO request) {
        log.info("Recibida solicitud de registro de usuario para correo: {}", request.getCorreoElectronico());

        User usuarioDominio = toDomain(request);
        User usuarioRegistrado = registrarUsuarioUseCase.registrarUsuario(usuarioDominio);
        UsuarioResponseDTO response = toResponse(usuarioRegistrado);

        log.info("Registro de usuario completado exitosamente con id: {}", response.getIdUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private User toDomain(CrearUsuarioRequestDTO request) {
        return User.builder()
                .nombre(request.getNombres())
                .apellido(request.getApellidos())
                .fechaNacimiento(request.getFechaNacimiento())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .email(request.getCorreoElectronico())
                .documentoIdentidad(request.getDocumentoIdentidad())
                .idRol(request.getIdRol())
                .salarioBase(request.getSalarioBase())
                .build();
    }

    private UsuarioResponseDTO toResponse(User user) {
        return UsuarioResponseDTO.builder()
                .idUsuario(user.getIdUsuario())
                .nombres(user.getNombre())
                .apellidos(user.getApellido())
                .fechaNacimiento(user.getFechaNacimiento())
                .direccion(user.getDireccion())
                .telefono(user.getTelefono())
                .correoElectronico(user.getEmail())
                .documentoIdentidad(user.getDocumentoIdentidad())
                .idRol(user.getIdRol())
                .salarioBase(user.getSalarioBase())
                .build();
    }
}
