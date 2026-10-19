package com.autobots.automanager.controles;

import jakarta.validation.Valid;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.representacoes.DocumentoRepresentacao;
import com.autobots.automanager.servicos.DocumentoServico;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/documentos")
public class DocumentoControle {
    private final DocumentoServico servico;
    private final DocumentoRepresentacao representacao;

    public DocumentoControle(DocumentoServico servico, DocumentoRepresentacao representacao) {
        this.servico = servico;
        this.representacao = representacao;
    }

    @GetMapping
    public CollectionModel<EntityModel<Documento>> listar(@RequestParam(required = false) Long clienteId) {
        var registros = servico.listar(clienteId);
        var proprio = linkTo(methodOn(DocumentoControle.class).listar(clienteId)).withSelfRel().expand();
        if (clienteId != null) {
            proprio = proprio.andAffordance(afford(methodOn(DocumentoControle.class).criarDocumento(clienteId, null)));
        }
        var modelo = CollectionModel.of(registros.stream().map(representacao::toModel).toList(), proprio,
                linkTo(methodOn(RaizControle.class).inicio()).withRel("inicio").expand())
                .withFallbackType(EntityModel.class, Documento.class);
        if (clienteId != null) {
            modelo.add(linkTo(methodOn(ClienteControle.class).buscar(clienteId)).withRel("cliente").expand());
        } else {
            modelo.add(linkTo(methodOn(ClienteControle.class).listar()).withRel("clientes").expand());
        }
        return modelo;
    }

    @GetMapping("/{id}")
    public EntityModel<Documento> buscar(@PathVariable long id) {
        return representacao.toModel(servico.buscar(id));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<EntityModel<Documento>> criarDocumento(@RequestParam long clienteId,
            @Valid @RequestBody Documento dados) {
        var modelo = representacao.toModel(servico.criar(clienteId, dados));
        return ResponseEntity.created(modelo.getRequiredLink("self").toUri()).body(modelo);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public EntityModel<Documento> atualizarDocumento(@PathVariable long id, @Valid @RequestBody Documento dados) {
        return representacao.toModel(servico.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirDocumento(@PathVariable long id) {
        servico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
