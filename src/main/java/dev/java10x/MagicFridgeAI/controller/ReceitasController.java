package dev.java10x.MagicFridgeAI.controller;

import dev.java10x.MagicFridgeAI.service.ReceitasService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/receitas")
public class ReceitasController {

    private final ReceitasService receitasService;

    public ReceitasController(ReceitasService receitasService) {
        this.receitasService = receitasService;
    }

    @GetMapping
    public Mono<String> gerarReceita() {
        return receitasService.gerarReceita();
    }
}
