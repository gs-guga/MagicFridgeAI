package dev.java10x.MagicFridgeAI.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String model;

    public GeminiService(
            WebClient geminiWebClient,
            @Value("${spring.ai.google.genai.chat.options.model}") String model) {
        this.webClient = geminiWebClient;
        this.model = model;
    }

    public Mono<String> perguntar(String prompt) {
        Map<String, Object> corpo = Map.of("model", model, "input", prompt);

        return webClient.post()
                .uri("/v1beta/interactions")
                .bodyValue(corpo)
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::extrairTexto)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
                        .filter(e -> e instanceof WebClientResponseException w
                                && w.getStatusCode().value() == 503)
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()));
    }

    private String extrairTexto(Map<?, ?> resposta) {
        if (resposta.get("steps") instanceof List<?> steps) {
            for (Object step : steps) {
                if (step instanceof Map<?, ?> s
                        && "model_output".equals(s.get("type"))
                        && s.get("content") instanceof List<?> conteudos) {
                    StringBuilder texto = new StringBuilder();
                    for (Object c : conteudos) {
                        if (c instanceof Map<?, ?> parte
                                && "text".equals(parte.get("type"))
                                && parte.get("text") != null) {
                            texto.append(parte.get("text"));
                        }
                    }
                    if (!texto.isEmpty()) return texto.toString();
                }
            }
        }
        return "";
    }
}
