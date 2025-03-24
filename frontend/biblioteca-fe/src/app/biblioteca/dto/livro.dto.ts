export class LivroDTO {
  codl: number | undefined;
  titulo: string | undefined;
  editora: string | undefined;
  edicao: number | undefined;
  anoPublicacao: string | undefined;
  valor: number | undefined;
  autores: string[] | undefined;
  assuntos: string[] | undefined;
  constructor(
    codl?: number,
    titulo?: string,
    editora?: string,
    edicao?: number,
    anoPublicacao?: string,
    valor?: number,
    autores?: string[],
    assuntos?: string[]
  ) {
    this.codl = codl;
    this.titulo = titulo;
    this.editora = editora;
    this.edicao = edicao;
    this.anoPublicacao = anoPublicacao;
    this.valor = valor;
    this.autores = autores;
    this.assuntos = assuntos;
  }
}
