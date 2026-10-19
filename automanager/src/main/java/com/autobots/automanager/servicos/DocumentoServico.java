package com.autobots.automanager.servicos;
import java.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;
import com.autobots.automanager.entidades.*;
import com.autobots.automanager.repositorios.*;
@Service
@Transactional
public class DocumentoServico {
 private final DocumentoRepositorio repositorio;
 private final ClienteRepositorio clientes;
 private final ClienteServico clienteServico;
 public DocumentoServico(DocumentoRepositorio repositorio, ClienteRepositorio clientes, ClienteServico clienteServico) {
  this.repositorio=repositorio; this.clientes=clientes; this.clienteServico=clienteServico;
 }
 public List<Documento> listar(Long clienteId) {
  if (clienteId == null) return repositorio.findAll();
  Cliente c = clienteServico.buscar(clienteId);
  return c.getDocumentos();
 }
 public Optional<Long> clienteId(long id) {
  return clientes.findByDocumentosId(id).map(Cliente::getId);
 }
 public Documento buscar(long id) { return repositorio.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND,"Documento nao encontrado")); }
 public Documento criar(long clienteId, Documento d) {
  if (d.getId()!=null) throw new ResponseStatusException(BAD_REQUEST,"Nao envie ID no cadastro");
  Cliente c=clienteServico.buscar(clienteId);
  d=repositorio.saveAndFlush(d); c.getDocumentos().add(d);
  clientes.saveAndFlush(c); return d;
 }
 public Documento atualizar(long id, Documento dados) {
  Documento existente=buscar(id);
  if (dados.getId()!=null && !dados.getId().equals(id)) throw new ResponseStatusException(BAD_REQUEST,"ID divergente da URL");
  BeanUtils.copyProperties(dados,existente,"id");
  return repositorio.saveAndFlush(existente);
 }
 public void excluir(long id) {
  Documento existente=buscar(id);
  Optional<Cliente> dono=clientes.findByDocumentosId(id);
  if (dono.isPresent()) { Cliente c=dono.get(); c.getDocumentos().removeIf(item -> item.getId().equals(id)); clientes.saveAndFlush(c); }
  else { repositorio.delete(existente); repositorio.flush(); }
 }
}
