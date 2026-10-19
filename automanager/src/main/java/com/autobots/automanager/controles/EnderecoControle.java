package com.autobots.automanager.controles;

import jakarta.validation.Valid;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.representacoes.EnderecoRepresentacao;
import com.autobots.automanager.servicos.EnderecoServico;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/enderecos")
public class EnderecoControle {
    private final EnderecoServico servico;
    private final EnderecoRepresentacao representacao;

    public EnderecoControle(EnderecoServico servico, EnderecoRepresentacao representacao) {
        this.servico = servico;
        this.representacao = representacao;
    }

    @GetMapping
    public CollectionModel<EntityModel<Endereco>> listar(@RequestParam(required = false) Long clienteId) {
        var registros = servico.listar(clienteId);
        var proprio = linkTo(methodOn(EnderecoControle.class).listar(clienteId)).withSelfRel().expand();
        if (clienteId != null && registros.isEmpty()) {
            proprio = proprio.andAffordance(afford(methodOn(EnderecoControle.class).criarEndereco(clienteId, null)));
        }
        var modelo = CollectionModel.of(registros.stream().map(representacao::toModel).toList(), proprio,
                linkTo(methodOn(RaizControle.class).inicio()).withRel("inicio").expand())
                .withFallbackType(EntityModel.class, Endereco.class);
        if (clienteId != null) {
            modelo.add(linkTo(methodOn(ClienteControle.class).buscar(clienteId)).withRel("cliente").expand());
        } else {
            modelo.add(linkTo(methodOn(ClienteControle.class).listar()).withRel("clientes").expand());
        }
        return modelo;
    }

    @GetMapping("/{id}")
    public EntityModel<Endereco> buscar(@PathVariable long id) {
        return representacao.toModel(servico.buscar(id));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<EntityModel<Endereco>> criarEndereco(@RequestParam long clienteId,
            @Valid @RequestBody Endereco dados) {
        var modelo = representacao.toModel(servico.criar(clienteId, dados));
        return ResponseEntity.created(modelo.getRequiredLink("self").toUri()).body(modelo);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public EntityModel<Endereco> atualizarEndereco(@PathVariable long id, @Valid @RequestBody Endereco dados) {
        return representacao.toModel(servico.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirEndereco(@PathVariable long id) {
        servico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
