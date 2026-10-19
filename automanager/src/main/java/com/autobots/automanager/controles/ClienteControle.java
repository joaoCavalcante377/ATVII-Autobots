package com.autobots.automanager.controles;

import java.util.Date;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.representacoes.*;
import com.autobots.automanager.servicos.ClienteServico;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/clientes")
public class ClienteControle {
    private final ClienteServico servico;
    private final ClienteRepresentacao representacao;

    public ClienteControle(ClienteServico servico, ClienteRepresentacao representacao) {
        this.servico = servico;
        this.representacao = representacao;
    }

    @lombok.Data
    public static class DadosCliente {
        @NotBlank
        private String nome;
        private String nomeSocial;
        private Date dataNascimento;
    }

    @GetMapping
    public CollectionModel<EntityModel<ClienteResposta>> listar() {
        var proprio = linkTo(methodOn(ClienteControle.class).listar()).withSelfRel().expand()
                .andAffordance(afford(methodOn(ClienteControle.class).criar(null)));
        return CollectionModel.of(servico.listar().stream().map(representacao::toModel).toList(), proprio,
                linkTo(methodOn(RaizControle.class).inicio()).withRel("inicio").expand())
                .withFallbackType(EntityModel.class, ClienteResposta.class);
    }

    @GetMapping("/{id}")
    public EntityModel<ClienteResposta> buscar(@PathVariable long id) {
        return representacao.toModel(servico.buscar(id));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<EntityModel<ClienteResposta>> criar(@Valid @RequestBody Cliente dados) {
        var modelo = representacao.toModel(servico.criar(dados));
        return ResponseEntity.created(modelo.getRequiredLink("self").toUri()).body(modelo);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public EntityModel<ClienteResposta> atualizar(@PathVariable long id, @Valid @RequestBody DadosCliente dados) {
        return representacao.toModel(servico.atualizar(id, dados.getNome(), dados.getNomeSocial(), dados.getDataNascimento()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable long id) {
        servico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
