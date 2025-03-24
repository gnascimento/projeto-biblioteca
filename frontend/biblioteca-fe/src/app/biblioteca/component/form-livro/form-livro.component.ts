import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {LivroService} from '../../service/livro.service';
import {AutorService} from '../../service/autor.service';
import {AssuntoService} from '../../service/assunto.service';
import {AutorModel} from '../../model/autor.model';
import {AssuntoModel} from '../../model/assunto.model';
import {LivroModel} from '../../model/livro.model';
import {MessageService} from 'primeng/api';
import {InputText} from 'primeng/inputtext';
import {ReactiveFormsModule} from '@angular/forms';
import {AutoComplete} from 'primeng/autocomplete';
import {ButtonDirective, ButtonLabel} from 'primeng/button';
import {LivroDTO} from '../../dto/livro.dto';
import {InputNumber} from 'primeng/inputnumber';
import {NgForOf, NgIf} from '@angular/common';
import {Select} from 'primeng/select';
import {Message} from 'primeng/message';
import { forkJoin } from 'rxjs';


@Component({
  selector: 'app-form-livro',
  templateUrl: './form-livro.component.html',
  styleUrl: './form-livro.component.scss',
  imports: [
    InputText,
    ReactiveFormsModule,
    AutoComplete,
    ButtonDirective,
    ButtonLabel,
    InputNumber,
    NgForOf,
    Select,
    NgIf,
    Message
  ],
  providers: []
})
export class FormLivroComponent implements OnInit {
  livroForm: FormGroup;
  autores: AutorModel[] = [];
  assuntos: AssuntoModel[] = [];
  filteredAutores: AutorModel[] = [];
  filteredAssuntos: AssuntoModel[] = [];
  anos: string[] = [];
  formErros: string[] = [];
  codl: number | undefined;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private livroService: LivroService,
    private autorService: AutorService,
    private assuntoService: AssuntoService,
    private messageService: MessageService
  ) {
    this.livroForm = this.fb.group({
      titulo: ['', Validators.required],
      editora: ['', Validators.required],
      edicao: [''],
      anoPublicacao: [],
      valor: [],
      autores: [[], Validators.required],
      assuntos: [[], Validators.required]
    });
  }

  ngOnInit() {
    this.gerarAnos();
    this.codl = this.route.snapshot.params['codl'];
    if (this.codl) {
      this.loadLivro(this.codl);
    }
  }

  gerarAnos() {
    const anoAtual = new Date().getFullYear();
    for (let ano = anoAtual; ano >= 0; ano--) {
      // insira o ano como uma string
      this.anos.push(ano.toString());
    }
  }

  loadLivro(codl: number) {
    forkJoin({
      getLivro: this.livroService.getLivroById(codl),
      getAutores: this.livroService.getLivroAutores(codl),
      getAssuntos: this.livroService.getLivroAssuntos(codl)
    }).subscribe({
      next: (data) => {
        this.livroForm.patchValue(data.getLivro);

        this.livroForm.patchValue({
          autores: data.getAutores._embedded.autores,
          assuntos: data.getAssuntos._embedded.assuntos
        });
      }
    })

    this.livroService.getLivroById(codl).subscribe((livro: LivroModel) => {
      this.livroForm.patchValue(livro);
    });
  }

  filterAutores(event: any) {
    const query = event.query.toLowerCase();
    this.autorService.getAutores(0, 20, [], query).subscribe(data => {
      this.filteredAutores = data._embedded.autores;
    });
  }

  filterAssuntos(event: any) {
    const query = event.query.toLowerCase();
    this.assuntoService.getAssuntos(0, 20, [], query).subscribe(data => {
      this.filteredAssuntos = data._embedded.assuntos;
    });
  }

  onSubmit() {
    if (this.livroForm.valid) {
      const livro: LivroModel = this.livroForm.value;
      const autores = livro.autores.flatMap(autor => autor._links)
        .map(link => link.self.href);
      const assuntos = livro.assuntos.flatMap(assunto => assunto._links)
        .map(link => link.self.href);
      let livroParaEnviar: LivroDTO = new LivroDTO(
        this.codl,
        livro.titulo,
        livro.editora,
        livro.edicao,
        livro.anoPublicacao,
        livro.valor,
        autores, assuntos
      );

      if (this.codl) {
        forkJoin({
          updateLivro: this.livroService.updateLivro(this.codl, livroParaEnviar),
          updateAutores: this.livroService.updateLivroAutores(this.codl, livroParaEnviar.autores),
          updateAssuntos: this.livroService.updateLivroAssuntos(this.codl, livroParaEnviar.assuntos)
        }).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: 'Livro atualizado com sucesso'
            });
            this.formErros = [];
            this.router.navigate(['/livros']);
          },
          error: (err) => this.messageService.add({severity: 'error', summary: 'Error', detail: 'Erro ao atualizar livro'})
        });
      } else {
        this.livroService.createLivro(livroParaEnviar).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: 'Livro salvo com sucesso'
            });
            this.livroForm.reset();
            this.formErros = [];
            this.router.navigate(['/livros']);
          },
          error: () => this.messageService.add({severity: 'error', summary: 'Error', detail: 'Erro ao salvar livro'})
        });
      }
    } else {
      this.messageService.add({severity: 'error', summary: 'Error', detail: 'Formulário inválido'});
      this.livroForm.markAllAsTouched();
      this.getFormValidationErrors();
    }
  }

  getFormValidationErrors() {
    this.formErros = [];
    Object.keys(this.livroForm.controls).forEach(key => {
      const controlErrors = this.livroForm.get(key)?.errors;
      if (controlErrors) {
        Object.keys(controlErrors).forEach(keyError => {
          this.formErros.push(this.getErrorMessage(key));
        });
      }
    });
  }

  getErrorMessage(controlName: string): string {
    const control = this.livroForm.get(controlName);
    if (control?.hasError('required')) {
      return `O campo ${controlName} é obrigatório.`;
    }
    if (control?.hasError('invalidYear')) {
      return `O campo ${controlName} deve ser um ano válido.`;
    }
    return '';
  }
}
