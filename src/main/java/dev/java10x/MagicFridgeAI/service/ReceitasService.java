package dev.java10x.MagicFridgeAI.service;

import dev.java10x.MagicFridgeAI.model.FoodItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReceitasService {

    private final FoodItemService foodItemService;
    private final WebClient webClient;
    private final String model;

    public ReceitasService(
            FoodItemService foodItemService,
            WebClient geminiWebClient,
            @Value("${spring.ai.google.genai.chat.options.model}") String model) {
        this.foodItemService = foodItemService;
        this.webClient = geminiWebClient;
        this.model = model;
    }

    public Mono<String> gerarReceita() {
        List<FoodItem> itens = foodItemService.listar();

        if (itens.isEmpty()) {
            return Mono.just("Não há itens cadastrados na geladeira.");
        }
        String listaDeItens = itens.stream()
                .map(i -> i.getQuantidade() + " " + i.getUnidade().getTexto()
                        + " de " + i.getNome()
                        + " (validade: " + i.getValidade() + ")")
                .collect(Collectors.joining(", "));

        String prompt = "Tenho estes itens na geladeira: " + listaDeItens
                + ". Sugira uma receita usando principalmente esses itens, "
                + "priorizando os que vencem primeiro. Respeite as quantidades "
                + "disponíveis: nunca use mais do que tenho de cada item. "
                + "Informe a quantidade de cada ingrediente em gramas, ml ou unidades. "
                + "Responda em português, com ingredientes e modo de preparo.";

        Map<String, Object> corpo = Map.of("model", model, "input", prompt);

        return webClient.post()
                .uri("/v1beta/interactions")
                .bodyValue(corpo)
                .retrieve()
                .bodyToMono(Map.class)
                .map(this::extrairTexto)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(e -> e instanceof WebClientResponseException w
                                && w.getStatusCode().value() == 503)
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                .onErrorResume(WebClientResponseException.class, e ->
                        Mono.just("Erro ao chamar o Gemini (" + e.getStatusCode() + "): "
                                + e.getResponseBodyAsString()));
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
                    if (!texto.isEmpty()) {
                        return texto.toString();
                    }
                }
            }
        }
        return "Não consegui ler a resposta do Gemini.";
    }
}