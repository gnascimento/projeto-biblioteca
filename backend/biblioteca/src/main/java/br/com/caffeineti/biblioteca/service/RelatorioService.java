package br.com.caffeineti.biblioteca.service;

import br.com.caffeineti.biblioteca.model.ViewRelatorio;
import br.com.caffeineti.biblioteca.repository.ViewRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final ViewRelatorioRepository viewRelatorioRepository; // Supondo que esse repositório contenha o método customizado

    public XSSFWorkbook gerarRelatorio() {
        // Cria o workbook e a planilha com os dados
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet dataSheet = workbook.createSheet("Dados");

        // Criação do header
        Row headerRow = dataSheet.createRow(0);
        headerRow.createCell(0).setCellValue("Autor");
        headerRow.createCell(1).setCellValue("Assunto");
        headerRow.createCell(2).setCellValue("Codl");
        headerRow.createCell(3).setCellValue("Titulo");
        headerRow.createCell(4).setCellValue("Valor");
        headerRow.createCell(5).setCellValue("Ano Publicacao");
        headerRow.createCell(6).setCellValue("Editora");
        headerRow.createCell(7).setCellValue("Edicao");

        List<ViewRelatorio> dados = viewRelatorioRepository.findBooksComAutorAssunto();

        // Preenche os dados na planilha
        int rowIndex = 1;
        for (ViewRelatorio dto : dados) {
            Row row = dataSheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(dto.getAutor());
            row.createCell(1).setCellValue(dto.getAssunto());
            row.createCell(2).setCellValue(dto.getCodl());
            row.createCell(3).setCellValue(dto.getTitulo());
            if (dto.getValor() != null) {
                row.createCell(4).setCellValue(dto.getValor());
            }
            if (dto.getAnoPublicacao() != null) {
                row.createCell(5).setCellValue(dto.getAnoPublicacao());
            }
            row.createCell(6).setCellValue(dto.getEditora());
            if (dto.getEdicao() != null) {
                row.createCell(7).setCellValue(dto.getEdicao());
            }
        }

        buildPivotAutorLivro(workbook, dataSheet);
        buildPivotAutorAssunto(workbook, dataSheet);

        return workbook;
    }

    private void buildPivotAutorAssunto(XSSFWorkbook workbook, Sheet dataSheet) {
        // Cria a planilha para o Pivot Autor x Assunto
        Sheet pivotSheet = workbook.createSheet("Pivot_Autor_Assunto");

        // Define a área de dados para o pivot table (do header até o último dado)
        int lastRow = dataSheet.getLastRowNum();
        AreaReference source = new AreaReference(new CellReference(0, 0),
                new CellReference(lastRow, 7),
                workbook.getSpreadsheetVersion());

        // Define a célula de início para o pivot table na planilha "Pivot"
        CellReference position = new CellReference(1, 1);

        // Cria o pivot table
        XSSFPivotTable pivotTable = ((XSSFSheet) pivotSheet).createPivotTable(source, position, (XSSFSheet) dataSheet);

        // Configura o pivot: define "Autor" e "Assunto" como rótulos de linhas
        pivotTable.addRowLabel(0); // coluna "Autor"
        pivotTable.addRowLabel(1); // coluna "Assunto"
        // Adiciona uma coluna de dados (ex.: contagem de livros, usando a coluna "Codl")
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, 2, "Total de Livros");
    }

    private void buildPivotAutorLivro(XSSFWorkbook workbook, Sheet dataSheet) {
        // Cria a planilha para o Pivot Autor x Livro
        Sheet pivotSheet = workbook.createSheet("Pivot_Autor_Livro");

        // Define a área de dados para o pivot table (do header até o último dado)
        int lastRow = dataSheet.getLastRowNum();
        AreaReference source = new AreaReference(new CellReference(0, 0),
                new CellReference(lastRow, 7),
                workbook.getSpreadsheetVersion());

        // Define a célula de início para o pivot table na planilha "Pivot"
        CellReference position = new CellReference(1, 1);

        // Cria o pivot table
        XSSFPivotTable pivotTable = ((XSSFSheet) pivotSheet).createPivotTable(source, position, (XSSFSheet) dataSheet);

        // Configura o pivot: define "Autor" e "Titulo" como rótulos de linhas
        pivotTable.addRowLabel(0); // coluna "Autor"
        pivotTable.addRowLabel(3); // coluna "Titulo"
    }
}
