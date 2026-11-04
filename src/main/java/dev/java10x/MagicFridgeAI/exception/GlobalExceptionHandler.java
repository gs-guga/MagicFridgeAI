package dev.java10x.MagicFridgeAI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> campos.putIfAbsent(e.getField(), e.getDefaultMessage()));

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Alguns campos estão inválidos.");
        problema.setTitle("Dados inválidos");
        problema.setProperty("campos", campos);
        return problema;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail tratarCorpoIlegivel(HttpMessageNotReadableException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Não foi possível ler os dados enviados. Confira o JSON "
                        + "e use a data no formato aaaa-mm-dd.");
        problema.setTitle("Requisição inválida");
        return problema;
    }

    @ExceptionHandler(AlimentoInvalidoException.class)
    public ProblemDetail tratarAlimentoInvalido(AlimentoInvalidoException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        problema.setTitle("Alimento inválido");
        return problema;
    }

    @ExceptionHandler(ValidacaoIndisponivelException.class)
    public ProblemDetail tratarValidacaoIndisponivel(ValidacaoIndisponivelException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problema.setTitle("Validação indisponível");
        return problema;
    }
}