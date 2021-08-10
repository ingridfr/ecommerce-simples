package br.execute.ecommerce.service.mapper;

import br.execute.ecommerce.domain.*;
import br.execute.ecommerce.service.dto.ProdutoNoPedidoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProdutoNoPedido} and its DTO {@link ProdutoNoPedidoDTO}.
 */
@Mapper(componentModel = "spring", uses = { ProdutoMapper.class, PedidoMapper.class })
public interface ProdutoNoPedidoMapper extends EntityMapper<ProdutoNoPedidoDTO, ProdutoNoPedido> {
    @Mapping(target = "produto", source = "produto", qualifiedByName = "nome")
    @Mapping(target = "produto", source = "produto", qualifiedByName = "id")
    ProdutoNoPedidoDTO toDto(ProdutoNoPedido s);
}
