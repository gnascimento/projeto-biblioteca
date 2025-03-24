export class AutorModel {
    codau: number | undefined;
    nome: string | undefined;
    _links: any[] = [];
    constructor(codau?: number, nome?: string) {
        this.codau = codau;
        this.nome = nome;
    }
}
