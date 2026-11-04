package dev.java10x.MagicFridgeAI.exception;

public class ValidacaoIndisponivelException extends RuntimeException {
    public ValidacaoIndisponivelException() {
        super("Não foi possível validar o alimento agora. Tente de novo em instantes.");
    }
}