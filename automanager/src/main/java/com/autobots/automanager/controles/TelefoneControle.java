package com.autobots.automanager.controles;

import jakarta.validation.Valid;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.representacoes.TelefoneRepresentacao;
import com.autobots.automanager.servicos.TelefoneServico;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/telefones")
public class TelefoneControle {
    private final TelefoneServico servico;
    private final TelefoneRepresentacao representacao;

    public TelefoneControle(TelefoneServico servico, TelefoneRepresentacao representacao) {
        this.servico = servico;
        this.representacao = representacao;
    }

    @GetMapping
    public CollectionModel<EntityModel<Telefone>> listar(@RequestParam(required = false) Long clienteId) {
        var registros = servico.listar(clienteId);
        var proprio = linkTo(methodOn(TelefoneControle.class).listar(clienteId)).withSelfRel().expand();
        if (clienteId != null) {
            proprio = proprio.andAffordance(afford(methodOn(TelefoneControle.class).criarTelefone(clienteId, null)));
        }
        var modelo = CollectionModel.of(registros.stream().map(representacao::toModel).toList(), proprio,
                linkTo(methodOn(RaizControle.class).inicio()).withRel("inicio").expand())
                .withFallbackType(EntityModel.class, Telefone.class);
        if (clienteId != null) {
            modelo.add(linkTo(methodOn(ClienteControle.class).buscar(clienteId)).withRel("cliente").expand());
        } else {
            modelo.add(linkTo(methodOn(ClienteControle.class).listar()).withRel("clientes").expand());
        }
        return modelo;
    }

    @GetMapping("/{id}")
    public EntityModel<Telefone> buscar(@PathVariable long id) {
        return representacao.toModel(servico.buscar(id));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<EntityModel<Telefone>> criarTelefone(@RequestParam long clienteId,
            @Valid @RequestBody Telefone dados) {
        var modelo = representacao.toModel(servico.criar(clienteId, dados));
        return ResponseEntity.created(modelo.getRequiredLink("self").toUri()).body(modelo);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public EntityModel<Telefone> atualizarTelefone(@PathVariable long id, @Valid @RequestBody Telefone dados) {
        return representacao.toModel(servico.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirTelefone(@PathVariable long id) {
        servico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
