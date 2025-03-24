import { AutorModel } from './autor.model';
import { AssuntoModel } from './assunto.model';

export class LivroModel {
  codl: number;
  titulo: string;
  editora: string | undefined;
  edicao: number | undefined;
  anoPublicacao: string;
  valor: number | undefined;
  autores: AutorModel[];
  assuntos: AssuntoModel[];
  _links: any[] = [];
  constructor(
    codl: number,
    titulo: string,
    editora: string,
    edicao: number,
    anoPublicacao: string,
    valor?: number,
    autores?: AutorModel[],
    assuntos?: AssuntoModel[]
  ) {
    this.codl = codl;
    this.titulo = titulo;
    this.editora = editora;
    this.edicao = edicao;
    this.anoPublicacao = anoPublicacao;
    this.valor = valor;
    this.autores = autores? autores : [];
    this.assuntos = assuntos? assuntos : [];
  }
}
