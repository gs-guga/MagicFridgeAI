package dev.java10x.MagicFridgeAI.model;



import dev.java10x.MagicFridgeAI.enums.Categoria;
import dev.java10x.MagicFridgeAI.enums.Unidade;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;


@Entity
@Table (name = "food_item")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do item.")
    @Size(max = 80, message = "O nome pode ter no máximo 80 caracteres.")
    private String nome;


    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private Categoria categoria;

    @NotNull(message = "Informe a quantidade.")
    @Min(value = 1, message = "A quantidade deve ser pelo menos 1.")
    private Integer quantidade;

    @NotNull(message = "Informe a unidade.")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private Unidade unidade;

    @NotNull(message = "Informe a validade.")
    private LocalDate validade;
}
