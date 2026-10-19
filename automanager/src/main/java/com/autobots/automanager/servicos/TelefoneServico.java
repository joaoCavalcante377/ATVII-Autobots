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
public class TelefoneServico {
 private final TelefoneRepositorio repositorio;
 private final ClienteRepositorio clientes;
 private final ClienteServico clienteServico;
 public TelefoneServico(TelefoneRepositorio repositorio, ClienteRepositorio clientes, ClienteServico clienteServico) {
  this.repositorio=repositorio; this.clientes=clientes; this.clienteServico=clienteServico;
 }
 public List<Telefone> listar(Long clienteId) {
  if (clienteId == null) return repositorio.findAll();
  Cliente c = clienteServico.buscar(clienteId);
  return c.getTelefones();
 }
 public Optional<Long> clienteId(long id) {
  return clientes.findByTelefonesId(id).map(Cliente::getId);
 }
 public Telefone buscar(long id) { return repositorio.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND,"Telefone nao encontrado")); }
 public Telefone criar(long clienteId, Telefone d) {
  if (d.getId()!=null) throw new ResponseStatusException(BAD_REQUEST,"Nao envie ID no cadastro");
  Cliente c=clienteServico.buscar(clienteId);
  d=repositorio.saveAndFlush(d); c.getTelefones().add(d);
  clientes.saveAndFlush(c); return d;
 }
 public Telefone atualizar(long id, Telefone dados) {
  Telefone existente=buscar(id);
  if (dados.getId()!=null && !dados.getId().equals(id)) throw new ResponseStatusException(BAD_REQUEST,"ID divergente da URL");
  BeanUtils.copyProperties(dados,existente,"id");
  return repositorio.saveAndFlush(existente);
 }
 public void excluir(long id) {
  Telefone existente=buscar(id);
  Optional<Cliente> dono=clientes.findByTelefonesId(id);
  if (dono.isPresent()) { Cliente c=dono.get(); c.getTelefones().removeIf(item -> item.getId().equals(id)); clientes.saveAndFlush(c); }
  else { repositorio.delete(existente); repositorio.flush(); }
 }
}
