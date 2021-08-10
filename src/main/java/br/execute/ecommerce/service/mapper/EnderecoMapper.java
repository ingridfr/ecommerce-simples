package br.execute.ecommerce.service.mapper;

import br.execute.ecommerce.domain.*;
import br.execute.ecommerce.service.dto.EnderecoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Endereco} and its DTO {@link EnderecoDTO}.
 */
@Mapper(componentModel = "spring", uses = { UsuarioMapper.class })
public interface EnderecoMapper extends EntityMapper<EnderecoDTO, Endereco> {
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "nome")
    EnderecoDTO toDto(Endereco s);
}
