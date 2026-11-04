package dev.java10x.MagicFridgeAI.service;

import dev.java10x.MagicFridgeAI.model.FoodItem;
import dev.java10x.MagicFridgeAI.repository.FoodItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodItemService {

    private final FoodItemRepository repository;
    private final ValidadorDeAlimento validador;

    public FoodItemService(FoodItemRepository repository, ValidadorDeAlimento validador) {
        this.repository = repository;
        this.validador = validador;
    }

    public FoodItem criar(FoodItem item) {
        validar(item);
        return repository.save(item);
    }

    public List<FoodItem> listar() {
        return repository.findAll();
    }

    public Optional<FoodItem> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Optional<FoodItem> atualizar(Long id, FoodItem novo) {
        validar(novo);
        return repository.findById(id).map(existente -> {
            existente.setNome(novo.getNome());
            existente.setCategoria(novo.getCategoria());
            existente.setQuantidade(novo.getQuantidade());
            existente.setValidade(novo.getValidade());
            existente.setUnidade(novo.getUnidade());
            return repository.save(existente);
        });
    }

    public boolean deletar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    private void validar(FoodItem item) {
        ValidadorDeAlimento.Resultado r = validador.validar(item.getNome());
        item.setNome(r.nome());
        item.setCategoria(r.categoria());
    }
}
