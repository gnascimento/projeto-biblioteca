export class AssuntoModel {
    codas: number | undefined;
    descricao: string | undefined;
    _links: any[] = [];
    constructor(codas?: number, descricao?: string) {
        this.codas = codas;
        this.descricao = descricao;
    }
}
