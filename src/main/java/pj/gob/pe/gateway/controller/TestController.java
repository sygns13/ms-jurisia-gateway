package pj.gob.pe.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Test", description = "API para testear el gateway")
@RestController
@RequestMapping("/v1/test")
@RequiredArgsConstructor
public class TestController {

    @Operation(summary = "Consulta Test", description = "Retorna un string")
    @GetMapping("/get-all")
    public ResponseEntity<String> listarAll() throws Exception{

      String resultado = "ok";

        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }
}
