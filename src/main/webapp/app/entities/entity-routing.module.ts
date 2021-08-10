import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'usuario',
        data: { pageTitle: 'ecommerceSimplesApp.usuario.home.title' },
        loadChildren: () => import('./usuario/usuario.module').then(m => m.UsuarioModule),
      },
      {
        path: 'endereco',
        data: { pageTitle: 'ecommerceSimplesApp.endereco.home.title' },
        loadChildren: () => import('./endereco/endereco.module').then(m => m.EnderecoModule),
      },
      {
        path: 'produto',
        data: { pageTitle: 'ecommerceSimplesApp.produto.home.title' },
        loadChildren: () => import('./produto/produto.module').then(m => m.ProdutoModule),
      },
      {
        path: 'pedido',
        data: { pageTitle: 'ecommerceSimplesApp.pedido.home.title' },
        loadChildren: () => import('./pedido/pedido.module').then(m => m.PedidoModule),
      },
      {
        path: 'produto-no-pedido',
        data: { pageTitle: 'ecommerceSimplesApp.produtoNoPedido.home.title' },
        loadChildren: () => import('./produto-no-pedido/produto-no-pedido.module').then(m => m.ProdutoNoPedidoModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
