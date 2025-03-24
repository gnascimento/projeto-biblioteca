import { Routes } from '@angular/router';
import {AssuntoComponent} from './biblioteca/component/assunto/assunto.component';
import {AutorComponent} from './biblioteca/component/autor/autor.component';
import {FormLivroComponent} from './biblioteca/component/form-livro/form-livro.component';
import {LivroComponent} from './biblioteca/component/livro/livro.component';
import {MainComponent} from './biblioteca/component/main/main.component';

export const routes: Routes = [
  { path: '', component: MainComponent },
  { path: 'assuntos', component: AssuntoComponent },
  { path: 'autores', component: AutorComponent },
  { path: 'livros', component: LivroComponent },
  { path: 'livros/novo', component: FormLivroComponent },
  { path: 'livros/editar/:codl', component: FormLivroComponent }
];
