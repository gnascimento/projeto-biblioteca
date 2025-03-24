import {ChangeDetectorRef, Component, NgZone, ViewChild} from '@angular/core';
import {AutorModel} from '../../model/autor.model';
import {AutorService} from '../../service/autor.service';
import {TableModule} from 'primeng/table';
import {Button, ButtonDirective, ButtonIcon} from 'primeng/button';
import {FormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {Ripple} from 'primeng/ripple';
import {NgIf} from '@angular/common';
import {ConfirmationService, MessageService} from 'primeng/api';
import {CheckIcon, PencilIcon, TimesIcon, TrashIcon} from 'primeng/icons';
import {Dialog} from 'primeng/dialog';
import {StyleClassModule} from 'primeng/styleclass';
import {ConfirmDialogModule} from 'primeng/confirmdialog';

@Component({
  selector: 'app-autor',
  imports: [
    TableModule,
    FormsModule,
    InputText,
    ButtonDirective,
    Ripple,
    NgIf,
    TimesIcon,
    ButtonIcon,
    PencilIcon,
    CheckIcon,
    Button,
    Dialog,
    StyleClassModule,
    ConfirmDialogModule,
    TrashIcon,
  ],
  templateUrl: './autor.component.html',
  styleUrl: './autor.component.scss',
  providers: [ConfirmationService]
})
export class AutorComponent {

  autores: AutorModel[] = [];

  itemAtual = 0;

  numLinhas = 5;

  numeroTotalElementos = 0;

  numeroTotalDePaginas = 0;

  paginaAtual = 0;

  dialogoNovoVisivel = false;

  novoAutor: AutorModel = new AutorModel();

  autoresEmEdicao: AutorModel[] = [];

  textoPesquisa: string = '';

  constructor(private autorService: AutorService,
              private messageService: MessageService,
              private confirmationService: ConfirmationService,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit() {
  }

  mostrarNovoDialog() {
    console.log('Mostrar dialog de novo autor');
    this.dialogoNovoVisivel = true;
  }

  paginar(event: any) {
    console.log(event);
    this.paginaAtual = event ? Math.floor(event.first / event.rows) : 0;
    this.itemAtual = this.paginaAtual * event.rows;
    this.autorService.getAutores(this.paginaAtual, event.rows).subscribe(data => {
      this.autores = data._embedded.autores;
      this.numeroTotalElementos = data.page.totalElements;
      this.numeroTotalDePaginas = data.page.totalPages;
      this.paginaAtual = data.page.number;
      this.changeDetectorRef.detectChanges();
    });
  }

  iniciarEdicao(autor: AutorModel) {
    console.log('Iniciada edição do autor');
    this.autoresEmEdicao.push({...autor});
  }

  atualizarAutor(autor: AutorModel) {
    console.log('Salvar edição do autor');
    if (!autor.nome) {
      this.messageService.add({severity: 'error', summary: 'Erro', detail: 'Nome do autor é obrigatório'});
      return;
    }
    if (autor.codau != null) {
      this.autorService.updateAutor(autor.codau, autor).subscribe({
        next: (data: AutorModel) => {
          this.messageService.add({severity: 'success', summary: 'Sucesso', detail: 'Autor atualizado com sucesso'});
          this.paginar({first: 0, rows: this.numLinhas});
        }, error: (error: any) => {
          console.log('Erro ao atualizar autor');
          console.log(error);
          this.messageService.add({severity: 'error', summary: 'Erro', detail: 'Erro ao atualizar autor'});
        }
      });
      // remove o autor da lista de edicao
      this.autoresEmEdicao = this.autoresEmEdicao.filter(a => a.codau !== autor.codau);
    }
  }

  salvarNovoAutor() {
    console.log('Salvar novo autor');
    if (!this.novoAutor.nome) {
      this.messageService.add({severity: 'error', summary: 'Erro', detail: 'Nome do autor é obrigatório'});
      return;
    }
    this.autorService.createAutor(this.novoAutor).subscribe({
      next: (data: AutorModel) => {
        this.messageService.add({severity: 'success', summary: 'Sucesso', detail: 'Autor criado com sucesso'});
        this.dialogoNovoVisivel = false;
        this.novoAutor = new AutorModel();
        this.paginar({first: 0, rows: this.numLinhas});
      }, error: (error: any) => {
        console.log('Erro ao criar autor');
        console.log(error);
        this.messageService.add({severity: 'error', summary: 'Erro', detail: 'Erro ao criar autor'});
      }
    });
  }

  cancelarEdicao(autor: AutorModel) {
    console.log('Cancelada edição do autor');
    // restaura o autor para o estado anterior
    const oldAutor = this.autoresEmEdicao.find(a => a.codau === autor.codau);
    if (oldAutor) {
      autor.nome = oldAutor.nome;
    }
  }

  confirmarDelecao(event : Event, autor: AutorModel) {
    console.log('Confirmar deleção do autor');
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Tem certeza que deseja excluir o autor?',
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
        if (autor.codau != null) {
          this.autorService.deleteAutor(autor.codau).subscribe({
            next: () => {
              this.messageService.add({severity: 'success', summary: 'Sucesso', detail: 'Autor excluído com sucesso'});
              this.paginar({first: 0, rows: this.numLinhas});
            }, error: (error: any) => {
              console.log('Erro ao excluir autor');
              console.log(error);
              this.messageService.add({severity: 'error', summary: 'Erro', detail: 'Erro ao excluir autor'});
            }
          });
        }
      }
    });
  }

  pesquisar() {
    console.log('Pesquisar autores');
    if (this.textoPesquisa) {
      this.autorService.getAutores(0, this.numLinhas, [], this.textoPesquisa).subscribe(data => {
        this.autores = data._embedded.autores;
        this.numeroTotalElementos = data.page.totalElements;
        this.numeroTotalDePaginas = data.page.totalPages;
        this.paginaAtual = data.page.number;
        this.changeDetectorRef.detectChanges();
      });
    } else {
      this.paginar({first: 0, rows: this.numLinhas});
    }
  }

}
