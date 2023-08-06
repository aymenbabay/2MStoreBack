package com.example.meta.store.werehouse.Services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.example.meta.store.werehouse.Dtos.InvoiceDto;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.CommandLine;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.CompanyArticle;
import com.example.meta.store.werehouse.Entities.Invoice;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExportInvoicePdf {

	private final CommandLineService commandLineService;
	

    static DecimalFormat df = new DecimalFormat("#.###");
    
	public static ByteArrayInputStream invoicePdf(List<CommandLine> commandLines, Invoice invoice, Company company, List<Article> articles)  {
		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			PdfWriter.getInstance(document, out);
		
		document.open();
		
		// add text to pdf file
		Font font = FontFactory.getFont(FontFactory.COURIER,14, BaseColor.BLACK);
				
		Paragraph ste = new Paragraph("ste: ", font);
		ste.add(company.getName());
		ste.setAlignment(Element.ALIGN_LEFT);
		Paragraph secteur = new Paragraph("secteur: ", font);
		secteur.add(company.getIndestrySector());
		secteur.setAlignment(Element.ALIGN_RIGHT);
		ste.add(secteur);
		document.add(ste);
		
		Paragraph phone = new Paragraph("phone: ", font);
		phone.add(company.getPhone());
		phone.setAlignment(Element.ALIGN_LEFT);
		document.add(phone);
		
		Paragraph email = new Paragraph("email: ", font);
		email.add(company.getEmail());
		email.setAlignment(Element.ALIGN_RIGHT);
		document.add(email);
		
		Paragraph address = new Paragraph("address ", font);
		address.add(company.getAddress());
		address.setAlignment(Element.ALIGN_LEFT);
		document.add(address);

		Paragraph date = new Paragraph("date facture ", font);
		LocalDateTime lastModifiedDate = invoice.getLastModifiedDate();
		Date dateObj = Date.from(lastModifiedDate.atZone(ZoneId.systemDefault()).toInstant());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = formatter.format(dateObj);
		date.add(formattedDate);
		date.setAlignment(Element.ALIGN_RIGHT);
		document.add(date);



		
		Paragraph para = new Paragraph("Facture N°: ", font);
		para.add(invoice.getCode().toString());
		para.setAlignment(Element.ALIGN_CENTER);
		document.add(para);
		
		Paragraph client = new Paragraph("Client: ", font);
		client.setAlignment(Element.ALIGN_LEFT);
		document.add(client);
		System.out.println(invoice.getId() + " invoice export to pdf 95ssssssssssssssss");
		Paragraph name = new Paragraph("name: ", font);
		name.add(invoice.getClient().getCompany().getName());
		name.setAlignment(Element.ALIGN_LEFT);
		document.add(name);

		Paragraph addressclient = new Paragraph("Address: ", font);
		addressclient.add(invoice.getClient().getCompany().getAddress());
		addressclient.setAlignment(Element.ALIGN_LEFT);
		document.add(addressclient);

		Paragraph phoneclient = new Paragraph("phone: ", font);
		phoneclient.add(invoice.getClient().getCompany().getPhone());
		phoneclient.setAlignment(Element.ALIGN_LEFT);
		document.add(phoneclient);
		document.add(Chunk.NEWLINE);
		
		PdfPTable table = new PdfPTable(7);
		
		// make column title
		
		Stream.of("Libelle","Qte","Unit","Tva","Prix Unit","Tot Tva","Prix Tot Article").forEach(headerTitle ->{
			PdfPCell header = new PdfPCell();
			Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			header.setBorderWidth(1);
			header.setPhrase(new Phrase(headerTitle, headFont));
			table.addCell(header);
		});
		
		for(CommandLine i : commandLines) {
			for(Article j : articles) {
			//	if(i.getCompanyArticle() == j.getcom()) {
					
				
				
		PdfPCell libelleCell = new PdfPCell(new Phrase(j.getLibelle()));
		libelleCell.setPaddingLeft(1);
		libelleCell.setVerticalAlignment(Element.ALIGN_CENTER);
		libelleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(libelleCell);


		PdfPCell qteCell = new PdfPCell(new Phrase(i.getQuantity().toString()));
		qteCell.setPaddingLeft(1);
		qteCell.setVerticalAlignment(Element.ALIGN_CENTER);
		qteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(qteCell);
		
		PdfPCell unitCell = new PdfPCell(new Phrase(j.getUnit()));
		unitCell.setPaddingLeft(1);
		unitCell.setVerticalAlignment(Element.ALIGN_CENTER);
		unitCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(unitCell);
		
		PdfPCell tvaCell = new PdfPCell(new Phrase(j.getTva().toString()));
		tvaCell.setPaddingLeft(1);
		tvaCell.setVerticalAlignment(Element.ALIGN_CENTER);
		tvaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(tvaCell);
		String x = df.format( j.getCost()*j.getMargin());
		PdfPCell puCell = new PdfPCell(new Phrase(x));
		puCell.setPaddingLeft(1);
		puCell.setVerticalAlignment(Element.ALIGN_CENTER);
		puCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(puCell);
		
		PdfPCell tottvaCell = new PdfPCell(new Phrase(i.getTotTva().toString()));
		tottvaCell.setPaddingLeft(1);
		tottvaCell.setVerticalAlignment(Element.ALIGN_CENTER);
		tottvaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(tottvaCell);
		
		PdfPCell prixtotCell = new PdfPCell(new Phrase(i.getPrixArticleTot().toString()));
		prixtotCell.setPaddingLeft(1);
		prixtotCell.setVerticalAlignment(Element.ALIGN_CENTER);
		prixtotCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(prixtotCell);
		}
		
		}
		
		PdfPTable totalTable = new PdfPTable(3);
		

		totalTable.setWidthPercentage(30);
		totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalTable.getDefaultCell().setBorderWidth(0);

		PdfPCell emptyCell = new PdfPCell(new Phrase("Total HT:"));
		emptyCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalTable.addCell(emptyCell);

		PdfPCell totalHTCell = new PdfPCell(new Phrase(invoice.getPrix_article_tot().toString()));
		totalHTCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalTable.addCell(totalHTCell);

		PdfPCell e = new PdfPCell(new Phrase(""));
		e.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalTable.addCell(e);

		PdfPCell totalHTValueCell = new PdfPCell(new Phrase("Total TVA:"));
		totalHTValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalTable.addCell(totalHTValueCell);

		PdfPCell totalTVACell = new PdfPCell(new Phrase(invoice.getTot_tva_invoice().toString()));
		totalTVACell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalTable.addCell(totalTVACell);

		PdfPCell a = new PdfPCell(new Phrase(""));
		a.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalTable.addCell(a);
		
		PdfPCell totalTVAValueCell = new PdfPCell(new Phrase("Total TTC:"));
		totalTVAValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalTable.addCell(totalTVAValueCell);

		PdfPCell totalTTCCell = new PdfPCell(new Phrase(invoice.getPrix_invoice_tot().toString()));
		totalTTCCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalTable.addCell(totalTTCCell);
		
		PdfPCell b = new PdfPCell(new Phrase(""));
		b.setHorizontalAlignment(Element.ALIGN_RIGHT);
		totalTable.addCell(b);

		
		document.add(table);
		document.add(totalTable);
		document.close();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  new ByteArrayInputStream(out.toByteArray());
	}
	
	
	
}
