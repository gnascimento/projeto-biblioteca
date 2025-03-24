import {ChangeDetectorRef, Component, NgZone, ViewChild} from '@angular/core';
import {LivroModel} from '../../model/livro.model';
import {LivroService} from '../../service/livro.service';
import {TableModule} from 'primeng/table';
import {Button, ButtonDirective, ButtonIcon} from 'primeng/button';
import {FormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {Ripple} from 'primeng/ripple';
import {ConfirmationService, MessageService} from 'primeng/api';
import {PencilIcon, TrashIcon} from 'primeng/icons';
import {StyleClassModule} from 'primeng/styleclass';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {Router} from '@angular/router';

@Component({
  selector: 'app-livro',
  imports: [
    TableModule,
    FormsModule,
    InputText,
    ButtonDirective,
    Ripple,
    ButtonIcon,
    PencilIcon,
    StyleClassModule,
    ConfirmDialogModule,
    TrashIcon,
    Button,
  ],
  templateUrl: './livro.component.html',
  styleUrl: './livro.component.scss',
  providers: [ConfirmationService]
})
export class LivroComponent {

  livros: LivroModel[] = [];

  itemAtual = 0;

  numLinhas = 5;

  numeroTotalElementos = 0;

  numeroTotalDePaginas = 0;

  paginaAtual = 0;

  textoPesquisa: string = '';

  constructor(private livroService: LivroService,
              private messageService: MessageService,
              private confirmationService: ConfirmationService,
              private changeDetectorRef: ChangeDetectorRef,
              private router: Router) {
  }

  ngOnInit() {
  }

  paginar(event: any) {
    console.log(event);
    this.paginaAtual = event ? Math.floor(event.first / event.rows) : 0;
    this.itemAtual = this.paginaAtual * event.rows;
    this.livroService.getLivros(this.paginaAtual, event.rows).subscribe(data => {
      this.livros = data._embedded.livros;
      this.numeroTotalElementos = data.page.totalElements;
      this.numeroTotalDePaginas = data.page.totalPages;
      this.paginaAtual = data.page.number;
      this.changeDetectorRef.detectChanges();
    });
  }

  pesquisar() {
    console.log('Pesquisar livros');
    if (this.textoPesquisa) {
      this.livroService.getLivros(0, this.numLinhas, [], this.textoPesquisa).subscribe(data => {
        this.livros = data._embedded.livros;
        this.numeroTotalElementos = data.page.totalElements;
        this.numeroTotalDePaginas = data.page.totalPages;
        this.paginaAtual = data.page.number;
        this.changeDetectorRef.detectChanges();
      });
    } else {
      this.paginar({first: 0, rows: this.numLinhas});
    }
  }

  confirmarDelecao(event: Event, livro: LivroModel) {
    console.log('Confirmar deleção do livro');
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Tem certeza que deseja excluir o livro?',
      icon: 'pi pi-exclamation-triangle',
      header: 'Confirmação',
      closable: true,
      closeOnEscape: true,
      rejectButtonProps: {
        label: 'Cancelar',
        severity: 'secondary',
        outlined: true,
      },
      acceptButtonProps: {
        label: 'Excluir',
      },
      accept: () => {
        if (livro.codl != null) {
          this.livroService.deleteLivro(livro.codl).subscribe({
            next: () => {
              this.messageService.add({severity: 'success', summary: 'Sucesso', detail: 'Livro excluído com sucesso'});
              this.paginar({first: 0, rows: this.numLinhas});
            }, error: (error: any) => {
              console.log('Erro ao excluir livro');
              console.log(error);
              this.messageService.add({severity: 'error', summary: 'Erro', detail: 'Erro ao excluir livro'});
            }
          });
        }
      }
    });
  }

  editar(event: Event, livro: LivroModel) {
    console.log('Editar livro');
    this.router.navigate(['/livros/editar', livro.codl]);
  }

  novo() {
    console.log('Novo livro');
    this.router.navigate(['/livros/novo']);
  }

  baixarExcel() {
    window.open(this.livroService.exportToExcelUrl(), '_blank');
  }
}
