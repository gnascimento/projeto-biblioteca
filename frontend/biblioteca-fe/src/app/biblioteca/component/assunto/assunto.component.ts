import {ChangeDetectorRef, Component, NgZone, ViewChild} from '@angular/core';
import {AssuntoModel} from '../../model/assunto.model';
import {AssuntoService} from '../../service/assunto.service';
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
  selector: 'app-assunto',
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
  templateUrl: './assunto.component.html',
  styleUrl: './assunto.component.scss',
  providers: [ConfirmationService]
})
export class AssuntoComponent {

  assuntos: AssuntoModel[] = [];

  itemAtual = 0;

  numLinhas = 5;

  numeroTotalElementos = 0;

  numeroTotalDePaginas = 0;

  paginaAtual = 0;

  dialogoNovoVisivel = false;

  novoAssunto: AssuntoModel = new AssuntoModel();

  assuntosEmEdicao: AssuntoModel[] = [];

  textoPesquisa: string = '';

  constructor(private assuntoService: AssuntoService,
              private messageService: MessageService,
              private confirmationService: ConfirmationService,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit() {
  }

  mostrarNovoDialog() {
    console.log('Mostrar dialog de novo assunto');
    this.dialogoNovoVisivel = true;
  }

  paginar(event: any) {
    console.log(event);
    this.paginaAtual = event ? Math.floor(event.first / event.rows) : 0;
    this.itemAtual = this.paginaAtual * event.rows;
    this.assuntoService.getAssuntos(this.paginaAtual, event.rows).subscribe(data => {
      this.assuntos = data._embedded.assuntos;
      this.numeroTotalElementos = data.page.totalElements;
      this.numeroTotalDePaginas = data.page.totalPages;
      this.paginaAtual = data.page.number;
      this.changeDetectorRef.detectChanges();
    });
  }

  iniciarEdicao(assunto: AssuntoModel) {
    console.log('Iniciada edição do assunto');
    this.assuntosEmEdicao.push({...assunto});
  }

  atualizarAssunto(assunto: AssuntoModel) {
    console.log('Salvar edição do assunto');
    if (!assunto.descricao) {
      this.messageService.add({severity: 'error', summary: 'Erro', detail: 'Descrição do assunto é obrigatória'});
      return;
    }
    if (assunto.codas != null) {
      this.assuntoService.updateAssunto(assunto.codas, assunto).subscribe({
        next: (data: AssuntoModel) => {
          this.messageService.add({severity: 'success', summary: 'Sucesso', detail: 'Assunto atualizado com sucesso'});
          this.paginar({first: 0, rows: this.numLinhas});
        }, error: (error: any) => {
          console.log('Erro ao atualizar assunto');
          console.log(error);
          this.messageService.add({severity: 'error', summary: 'Erro', detail: 'Erro ao atualizar assunto'});
        }
      });
      // remove o assunto da lista de edicao
      this.assuntosEmEdicao = this.assuntosEmEdicao.filter(a => a.codas !== assunto.codas);
    }
  }

  salvarNovoAssunto() {
    console.log('Salvar novo assunto');
    if (!this.novoAssunto.descricao) {
      this.messageService.add({severity: 'error', summary: 'Erro', detail: 'Descrição do assunto é obrigatória'});
      return;
    }
    this.assuntoService.createAssunto(this.novoAssunto).subscribe({
      next: (data: AssuntoModel) => {
        this.messageService.add({severity: 'success', summary: 'Sucesso', detail: 'Assunto criado com sucesso'});
        this.dialogoNovoVisivel = false;
        this.novoAssunto = new AssuntoModel();
        this.paginar({first: 0, rows: this.numLinhas});
      }, error: (error: any) => {
        console.log('Erro ao criar assunto');
        console.log(error);
        this.messageService.add({severity: 'error', summary: 'Erro', detail: 'Erro ao criar assunto'});
      }
    });
  }

  cancelarEdicao(assunto: AssuntoModel) {
    console.log('Cancelada edição do assunto');
    // restaura o assunto para o estado anterior
    const oldAssunto = this.assuntosEmEdicao.find(a => a.codas === assunto.codas);
    if (oldAssunto) {
      assunto.descricao = oldAssunto.descricao;
    }
  }

  confirmarDelecao(event : Event, assunto: AssuntoModel) {
    console.log('Confirmar deleção do assunto');
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Tem certeza que deseja excluir o assunto?',
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
        if (assunto.codas != null) {
          this.assuntoService.deleteAssunto(assunto.codas).subscribe({
            next: () => {
              this.messageService.add({severity: 'success', summary: 'Sucesso', detail: 'Assunto excluído com sucesso'});
              this.paginar({first: 0, rows: this.numLinhas});
            }, error: (error: any) => {
              console.log('Erro ao excluir assunto');
              console.log(error);
              this.messageService.add({severity: 'error', summary: 'Erro', detail: 'Erro ao excluir assunto'});
            }
          });
        }
      }
    });
  }


  pesquisar() {
    console.log('Pesquisar assuntos');
    if (this.textoPesquisa) {
      this.assuntoService.getAssuntos(0, this.numLinhas, [], this.textoPesquisa).subscribe(data => {
        this.assuntos = data._embedded.assuntos;
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
