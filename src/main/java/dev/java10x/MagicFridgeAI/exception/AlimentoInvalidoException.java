package dev.java10x.MagicFridgeAI.exception;

public class AlimentoInvalidoException extends RuntimeException {
    public AlimentoInvalidoException(String mensagem) {
        super(mensagem);
    }
}