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
public class EnderecoServico {
 private final EnderecoRepositorio repositorio;
 private final ClienteRepositorio clientes;
 private final ClienteServico clienteServico;
 public EnderecoServico(EnderecoRepositorio repositorio, ClienteRepositorio clientes, ClienteServico clienteServico) {
  this.repositorio=repositorio; this.clientes=clientes; this.clienteServico=clienteServico;
 }
 public List<Endereco> listar(Long clienteId) {
  if (clienteId == null) return repositorio.findAll();
  Cliente c = clienteServico.buscar(clienteId);
  return c.getEndereco() == null ? List.of() : List.of(c.getEndereco());
 }
 public Optional<Long> clienteId(long id) {
  return clientes.findByEnderecoId(id).map(Cliente::getId);
 }
 public Endereco buscar(long id) { return repositorio.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND,"Endereco nao encontrado")); }
 public Endereco criar(long clienteId, Endereco d) {
  if (d.getId()!=null) throw new ResponseStatusException(BAD_REQUEST,"Nao envie ID no cadastro");
  Cliente c=clienteServico.buscar(clienteId);
  if (c.getEndereco()!=null) throw new ResponseStatusException(CONFLICT, "Cliente ja possui endereco"); d=repositorio.saveAndFlush(d); c.setEndereco(d);
  clientes.saveAndFlush(c); return d;
 }
 public Endereco atualizar(long id, Endereco dados) {
  Endereco existente=buscar(id);
  if (dados.getId()!=null && !dados.getId().equals(id)) throw new ResponseStatusException(BAD_REQUEST,"ID divergente da URL");
  BeanUtils.copyProperties(dados,existente,"id");
  return repositorio.saveAndFlush(existente);
 }
 public void excluir(long id) {
  Endereco existente=buscar(id);
  Optional<Cliente> dono=clientes.findByEnderecoId(id);
  if (dono.isPresent()) { Cliente c=dono.get(); c.setEndereco(null); clientes.saveAndFlush(c); }
  else { repositorio.delete(existente); repositorio.flush(); }
 }
}
