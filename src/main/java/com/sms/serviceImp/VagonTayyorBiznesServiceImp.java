package com.sms.serviceImp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.property.TextAlignment;
import com.sms.dto.PlanBiznesDto;
import com.sms.model.PlanBiznes;
import com.sms.model.LastActionTimes;
import com.sms.model.VagonTayyorUty;
import com.sms.repository.PlanBiznesRepository;
import com.sms.repository.TimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import com.sms.model.VagonTayyor;
import com.sms.repository.VagonTayyorBiznesRepository;
import com.sms.service.VagonTayyorBiznesService;

@Service
public class VagonTayyorBiznesServiceImp implements VagonTayyorBiznesService{

	@Autowired
	private VagonTayyorBiznesRepository vagonTayyorRepository;
	@Autowired 
	private PlanBiznesRepository planBiznesRepository;
	@Autowired
	private TimeRepository utyTimeRepository;

	LocalDateTime today = LocalDateTime.now();
	LocalDateTime minusHours = today.plusHours(5);
	DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    String currentDate = minusHours.format(myFormatObj);
	String samDate ;
	String havDate ;
	String andjDate ;

	public void createPdf(List<VagonTayyor> vagons, HttpServletResponse response) throws IOException {

		String home = System.getProperty("user.home");
		  File file = new File(home + "/Downloads" + "/Biznes reja boyicha tamir malumot.pdf");
		  if (!file.getParentFile().exists())
		      file.getParentFile().mkdirs();
		  if (!file.exists())
		      file.createNewFile();

		List<VagonTayyor> allVagons = vagons;
		try {
			response.setHeader("Content-Disposition",
                    "attachment;fileName=\"" + "Biznes reja boyicha tamir malumot.pdf" +"\"");
			response.setContentType("application/pdf");

			PdfWriter writer = new PdfWriter(file.getAbsolutePath());
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document doc = new Document(pdfDoc);

			String FONT_FILENAME = "./src/main/resources/arial.ttf";
			PdfFont font = PdfFontFactory.createFont(FONT_FILENAME, PdfEncodings.IDENTITY_H);
			doc.setFont(font);

			Paragraph paragraph = new Paragraph("Ta'mirdan chiqgan vagonlar(Biznes reja bo'yicha)");
			paragraph.setBackgroundColor(Color.DARK_GRAY);
			paragraph.setFontColor(Color.WHITE);// Setting background color to cell1
			paragraph.setBorder(Border.NO_BORDER);            // Setting border to cell1
			paragraph.setTextAlignment(TextAlignment.CENTER); // Setting text alignment to cell1
			paragraph.setFontSize(16);

			float[] columnWidth = {30f,200f,200f, 200f,200f,200f,200f,200f,200f,200f};
			Table table = new Table(columnWidth);
			table.setTextAlignment(TextAlignment.CENTER); // Setting text alignment to cell1
			table.addCell(new Cell().add(" № "));
			table.addCell(new Cell().add("Nomeri"));
			table.addCell(new Cell().add("Vagon turi"));
			table.addCell(new Cell().add("VCHD"));
			table.addCell(new Cell().add("Ta'mir turi"));
			table.addCell(new Cell().add("Ishlab chiqarilgan yili"));
			table.addCell(new Cell().add("Ta'mirdan chiqgan vaqti"));
			table.addCell(new Cell().add("Saqlangan vaqti"));
			table.addCell(new Cell().add("Egasi"));
			table.addCell(new Cell().add("Izoh"));
			int i=0;
			for(VagonTayyor vagon:allVagons) {
				i++;
				table.addCell(new Cell().add(String.valueOf(i)));
				table.addCell(new Cell().add(String.valueOf(vagon.getNomer())));
				table.addCell(new Cell().add(vagon.getVagonTuri()));
				table.addCell(new Cell().add(vagon.getDepoNomi()));
				table.addCell(new Cell().add(vagon.getRemontTuri()));
				table.addCell(new Cell().add(String.valueOf(vagon.getIshlabChiqarilganYili())));
				table.addCell(new Cell().add(String.valueOf(vagon.getChiqganVaqti())));
				table.addCell(new Cell().add(String.valueOf(vagon.getCreatedDate())));
				table.addCell(new Cell().add(vagon.getCountry()));
				table.addCell(new Cell().add(vagon.getIzoh()));
			}

			doc.add(paragraph);
			doc.add(table);
			doc.close();
			FileInputStream in = new FileInputStream(file.getAbsoluteFile());
			FileCopyUtils.copy(in, response.getOutputStream());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getSamDate() {
		Optional<LastActionTimes> optionalBiznesTime = utyTimeRepository.findById(1);
		if (!optionalBiznesTime.isPresent())
			return currentDate;
		return optionalBiznesTime.get().getSamBiznesDate();
	}

	public String getHavDate() {
		Optional<LastActionTimes> optionalBiznesTime = utyTimeRepository.findById(1);
		if (!optionalBiznesTime.isPresent())
			return currentDate;
		return optionalBiznesTime.get().getHavBiznesDate();
	}

	public String getAndjDate() {
		Optional<LastActionTimes> optionalBiznesTime = utyTimeRepository.findById(1);
		if (!optionalBiznesTime.isPresent())
			return currentDate;
		return optionalBiznesTime.get().getAndjBiznesDate();
	}

	public String getCurrentDate() {
		return currentDate;
	}


	// bosh admin qoshadi
	@Override
	public VagonTayyor saveVagon(VagonTayyor vagon) {
		if(vagon.getNomer() == null)
			return new VagonTayyor();
		Optional<VagonTayyor> exist=vagonTayyorRepository.findByNomer(vagon.getNomer());
		if(exist.isPresent())
			return new VagonTayyor();
		VagonTayyor savedVagon = new VagonTayyor();
		savedVagon.setNomer(vagon.getNomer());
		savedVagon.setDepoNomi(vagon.getDepoNomi());
		savedVagon.setRemontTuri(vagon.getRemontTuri());
		savedVagon.setVagonTuri(vagon.getVagonTuri());
		savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
		savedVagon.setIzoh(vagon.getIzoh());
		savedVagon.setCountry(vagon.getCountry());
		savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
		savedVagon.setActive(true);

		String currentDate = minusHours.format(myFormatObj);
		savedVagon.setCreatedDate(currentDate);

		return vagonTayyorRepository.save(savedVagon);
	}
	@Override
	public VagonTayyor saveVagonSam(VagonTayyor vagon) {
		if(vagon.getNomer() == null)
			return new VagonTayyor();
		Optional<VagonTayyor> exist=	vagonTayyorRepository.findByNomer(vagon.getNomer());
		if(exist.isPresent() || !vagon.getDepoNomi().equalsIgnoreCase("VCHD-6"))
			return new VagonTayyor();
		VagonTayyor savedVagon = new VagonTayyor();
		savedVagon.setNomer(vagon.getNomer());
		savedVagon.setDepoNomi(vagon.getDepoNomi());
		savedVagon.setRemontTuri(vagon.getRemontTuri());
		savedVagon.setVagonTuri(vagon.getVagonTuri());
		savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
		savedVagon.setIzoh(vagon.getIzoh());
		savedVagon.setCountry(vagon.getCountry());
		savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
		savedVagon.setActive(true);

		LocalDateTime today = LocalDateTime.now();
		LocalDateTime minusHours = today.plusHours(5);
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		samDate = minusHours.format(myFormatObj);

		savedVagon.setCreatedDate(samDate);

		Optional<LastActionTimes> optionalBiznesTime = utyTimeRepository.findById(1);
		optionalBiznesTime.get().setSamBiznesDate(samDate);
		utyTimeRepository.save(optionalBiznesTime.get());

		return vagonTayyorRepository.save(savedVagon);
	}

	@Override
	public VagonTayyor saveVagonHav(VagonTayyor vagon) {
		if(vagon.getNomer() == null)
			return new VagonTayyor();
		Optional<VagonTayyor> exist=vagonTayyorRepository.findByNomer(vagon.getNomer());
		if(exist.isPresent() || !vagon.getDepoNomi().equalsIgnoreCase("VCHD-3"))
			return new VagonTayyor();
		VagonTayyor savedVagon = new VagonTayyor();
		savedVagon.setNomer(vagon.getNomer());
		savedVagon.setDepoNomi(vagon.getDepoNomi());
		savedVagon.setRemontTuri(vagon.getRemontTuri());
		savedVagon.setVagonTuri(vagon.getVagonTuri());
		savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
		savedVagon.setIzoh(vagon.getIzoh());
		savedVagon.setCountry(vagon.getCountry());
		savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
		savedVagon.setActive(true);

		LocalDateTime today = LocalDateTime.now();
		LocalDateTime minusHours = today.plusHours(5);
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		havDate = minusHours.format(myFormatObj);

		savedVagon.setCreatedDate(havDate);

		Optional<LastActionTimes> optionalBiznesTime = utyTimeRepository.findById(1);
		optionalBiznesTime.get().setHavBiznesDate(havDate);
		utyTimeRepository.save(optionalBiznesTime.get());

		return vagonTayyorRepository.save(savedVagon);
	}

	@Override
	public VagonTayyor saveVagonAndj(VagonTayyor vagon) {
		if(vagon.getNomer() == null)
			return new VagonTayyor();
		Optional<VagonTayyor> exist= vagonTayyorRepository.findByNomer(vagon.getNomer());
		if(exist.isPresent() || !vagon.getDepoNomi().equalsIgnoreCase("VCHD-5"))
			return new VagonTayyor();
		VagonTayyor savedVagon = new VagonTayyor();
		savedVagon.setNomer(vagon.getNomer());
		savedVagon.setDepoNomi(vagon.getDepoNomi());
		savedVagon.setRemontTuri(vagon.getRemontTuri());
		savedVagon.setVagonTuri(vagon.getVagonTuri());
		savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
		savedVagon.setIzoh(vagon.getIzoh());
		savedVagon.setCountry(vagon.getCountry());
		savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());
		savedVagon.setActive(true);

		LocalDateTime today = LocalDateTime.now();
		LocalDateTime minusHours = today.plusHours(5);
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		andjDate = minusHours.format(myFormatObj);

		savedVagon.setCreatedDate(andjDate);

		Optional<LastActionTimes> optionalBiznesTime = utyTimeRepository.findById(1);
		optionalBiznesTime.get().setAndjBiznesDate(andjDate);
		utyTimeRepository.save(optionalBiznesTime.get());

		return vagonTayyorRepository.save(savedVagon);
}

	@Override
	public VagonTayyor updateVagon(VagonTayyor vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyor();
		 Optional<VagonTayyor> exist = vagonTayyorRepository.findById(id);
		 if(!exist.isPresent())
			 return new VagonTayyor();
		 VagonTayyor savedVagon = exist.get();
		 savedVagon.setId(id);
		 savedVagon.setNomer(vagon.getNomer());
		 savedVagon.setVagonTuri(vagon.getVagonTuri());
		 savedVagon.setDepoNomi(vagon.getDepoNomi());
		 savedVagon.setRemontTuri(vagon.getRemontTuri());
		 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
		 savedVagon.setIzoh(vagon.getIzoh());
		 savedVagon.setCountry(vagon.getCountry());
		 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());


		 return vagonTayyorRepository.save(savedVagon);
	}

	@Override
	public VagonTayyor updateVagonSam(VagonTayyor vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyor();
		 Optional<VagonTayyor> exist = vagonTayyorRepository.findById(id);
		 if(exist.get().getDepoNomi().equalsIgnoreCase("VCHD-6") && vagon.getDepoNomi().equalsIgnoreCase("VCHD-6")) {
			 VagonTayyor savedVagon = exist.get();
			 savedVagon.setId(id);
			 savedVagon.setNomer(vagon.getNomer());
			 savedVagon.setVagonTuri(vagon.getVagonTuri());
			 savedVagon.setDepoNomi(vagon.getDepoNomi());
			 savedVagon.setRemontTuri(vagon.getRemontTuri());
			 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
			 savedVagon.setIzoh(vagon.getIzoh());
			 savedVagon.setCountry(vagon.getCountry());
			 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());

			 LocalDateTime today = LocalDateTime.now();
			 LocalDateTime minusHours = today.plusHours(5);
			 DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			 samDate = minusHours.format(myFormatObj);

			 Optional<LastActionTimes> optionalBiznesTime = utyTimeRepository.findById(1);
			 optionalBiznesTime.get().setSamBiznesDate(samDate);
			 utyTimeRepository.save(optionalBiznesTime.get());


			 return vagonTayyorRepository.save(savedVagon);
		 }else
			return new VagonTayyor();

	}

	@Override
	public VagonTayyor updateVagonHav(VagonTayyor vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyor();
		 Optional<VagonTayyor> exist = vagonTayyorRepository.findById(id);
		 if(exist.get().getDepoNomi().equalsIgnoreCase("VCHD-3") && vagon.getDepoNomi().equalsIgnoreCase("VCHD-3")) {

			 VagonTayyor savedVagon = exist.get();
			 savedVagon.setId(id);
			 savedVagon.setNomer(vagon.getNomer());
			 savedVagon.setVagonTuri(vagon.getVagonTuri());
			 savedVagon.setDepoNomi(vagon.getDepoNomi());
			 savedVagon.setRemontTuri(vagon.getRemontTuri());
			 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
			 savedVagon.setIzoh(vagon.getIzoh());
			 savedVagon.setCountry(vagon.getCountry());
			 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());

			 LocalDateTime today = LocalDateTime.now();
			 LocalDateTime minusHours = today.plusHours(5);
			 DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			 havDate = minusHours.format(myFormatObj);

			 Optional<LastActionTimes> optionalBiznesTime = utyTimeRepository.findById(1);
			 optionalBiznesTime.get().setHavBiznesDate(havDate);
			 utyTimeRepository.save(optionalBiznesTime.get());

			 return vagonTayyorRepository.save(savedVagon);
		 }else
			 return new VagonTayyor();
	}

	@Override
	public VagonTayyor updateVagonAndj(VagonTayyor vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyor();
		 Optional<VagonTayyor> exist = vagonTayyorRepository.findById(id);
		 if( exist.get().getDepoNomi().equalsIgnoreCase("VCHD-5") && vagon.getDepoNomi().equalsIgnoreCase("VCHD-5")){
			 VagonTayyor savedVagon = exist.get();
			 savedVagon.setId(id);
			 savedVagon.setNomer(vagon.getNomer());
			 savedVagon.setVagonTuri(vagon.getVagonTuri());
			 savedVagon.setDepoNomi(vagon.getDepoNomi());
			 savedVagon.setRemontTuri(vagon.getRemontTuri());
			 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
			 savedVagon.setIzoh(vagon.getIzoh());
			 savedVagon.setCountry(vagon.getCountry());
			 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());

			 LocalDateTime today = LocalDateTime.now();
			 LocalDateTime minusHours = today.plusHours(5);
			 DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			 andjDate = minusHours.format(myFormatObj);

			 Optional<LastActionTimes> optionalBiznesTime = utyTimeRepository.findById(1);
			 optionalBiznesTime.get().setAndjBiznesDate(andjDate);
			 utyTimeRepository.save(optionalBiznesTime.get());

			 return vagonTayyorRepository.save(savedVagon);
		}else {
				 return new VagonTayyor();
		}
	}

	//hamma oy uchun
	@Override
	public VagonTayyor updateVagonMonths(VagonTayyor vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyor();
		 Optional<VagonTayyor> exist = vagonTayyorRepository.findById(id);
		 if(!exist.isPresent())
			 return new VagonTayyor();
		 VagonTayyor savedVagon = exist.get();
		 savedVagon.setId(id);
		 savedVagon.setNomer(vagon.getNomer());
		 savedVagon.setVagonTuri(vagon.getVagonTuri());
		 savedVagon.setDepoNomi(vagon.getDepoNomi());
		 savedVagon.setRemontTuri(vagon.getRemontTuri());
		 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
		 savedVagon.setIzoh(vagon.getIzoh());
		 savedVagon.setCountry(vagon.getCountry());
		 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());


		 return vagonTayyorRepository.save(savedVagon);
	}

	@Override
	public VagonTayyor updateVagonSamMonths(VagonTayyor vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyor();
		 Optional<VagonTayyor> exist = vagonTayyorRepository.findById(id);
		 if(exist.get().getDepoNomi().equalsIgnoreCase("VCHD-6") && vagon.getDepoNomi().equalsIgnoreCase("VCHD-6")) {
			 VagonTayyor savedVagon = exist.get();
			 savedVagon.setId(id);
			 savedVagon.setNomer(vagon.getNomer());
			 savedVagon.setVagonTuri(vagon.getVagonTuri());
			 savedVagon.setDepoNomi(vagon.getDepoNomi());
			 savedVagon.setRemontTuri(vagon.getRemontTuri());
			 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
			 savedVagon.setIzoh(vagon.getIzoh());
			 savedVagon.setCountry(vagon.getCountry());
			 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());

			 return vagonTayyorRepository.save(savedVagon);
		 }else
			return new VagonTayyor();

	}

	@Override
	public VagonTayyor updateVagonHavMonths(VagonTayyor vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyor();
		 Optional<VagonTayyor> exist = vagonTayyorRepository.findById(id);
		 if(exist.get().getDepoNomi().equalsIgnoreCase("VCHD-3") && vagon.getDepoNomi().equalsIgnoreCase("VCHD-3")) {

			 VagonTayyor savedVagon = exist.get();
			 savedVagon.setId(id);
			 savedVagon.setNomer(vagon.getNomer());
			 savedVagon.setVagonTuri(vagon.getVagonTuri());
			 savedVagon.setDepoNomi(vagon.getDepoNomi());
			 savedVagon.setRemontTuri(vagon.getRemontTuri());
			 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
			 savedVagon.setIzoh(vagon.getIzoh());
			 savedVagon.setCountry(vagon.getCountry());
			 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());

			 return vagonTayyorRepository.save(savedVagon);
		 }else
			 return new VagonTayyor();
	}

	@Override
	public VagonTayyor updateVagonAndjMonths(VagonTayyor vagon, long id) {
		if(vagon.getNomer() == null)
			return new VagonTayyor();
		 Optional<VagonTayyor> exist = vagonTayyorRepository.findById(id);
		 if( exist.get().getDepoNomi().equalsIgnoreCase("VCHD-5") && vagon.getDepoNomi().equalsIgnoreCase("VCHD-5")){
			 VagonTayyor savedVagon = exist.get();
			 savedVagon.setId(id);
			 savedVagon.setNomer(vagon.getNomer());
			 savedVagon.setVagonTuri(vagon.getVagonTuri());
			 savedVagon.setDepoNomi(vagon.getDepoNomi());
			 savedVagon.setRemontTuri(vagon.getRemontTuri());
			 savedVagon.setIshlabChiqarilganYili(vagon.getIshlabChiqarilganYili());
			 savedVagon.setIzoh(vagon.getIzoh());
			 savedVagon.setCountry(vagon.getCountry());
			 savedVagon.setChiqganVaqti(vagon.getChiqganVaqti());

			 return vagonTayyorRepository.save(savedVagon);
		}else {
				 return new VagonTayyor();
		}
	}


	@Override
	public VagonTayyor getVagonById(long id) {
	Optional<VagonTayyor> exist=	vagonTayyorRepository.findById(id);
	if(!exist.isPresent())
		return new VagonTayyor();
	return exist.get();
	}

	@Override
	public void deleteVagonById(long id) throws NotFoundException {
		Optional<VagonTayyor> exist=	vagonTayyorRepository.findById(id);
		if(exist.isPresent())
			vagonTayyorRepository.deleteById(id);

	}

	@Override
	public void deleteVagonSam(long id) throws NotFoundException {
		VagonTayyor exist=	vagonTayyorRepository.findById(id).get();
		if(exist.getDepoNomi().equals("VCHD-6") ) {
			vagonTayyorRepository.deleteById(id);
			LocalDateTime today = LocalDateTime.now();
			LocalDateTime minusHours = today.plusHours(5);
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			samDate = minusHours.format(myFormatObj);

			Optional<LastActionTimes> optionalBiznesTime = utyTimeRepository.findById(1);
			optionalBiznesTime.get().setSamBiznesDate(samDate);
			utyTimeRepository.save(optionalBiznesTime.get());
		}
	}

	@Override
	public void deleteVagonHav(long id) throws NotFoundException{
		VagonTayyor exist=	vagonTayyorRepository.findById(id).get();
		if(exist.getDepoNomi().equals("VCHD-3") ) {
			vagonTayyorRepository.deleteById(id);
			LocalDateTime today = LocalDateTime.now();
			LocalDateTime minusHours = today.plusHours(5);
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			havDate = minusHours.format(myFormatObj);

			Optional<LastActionTimes> optionalBiznesTime = utyTimeRepository.findById(1);
			optionalBiznesTime.get().setHavBiznesDate(havDate);
			utyTimeRepository.save(optionalBiznesTime.get());
		}
	}

	@Override
	public void deleteVagonAndj(long id) throws NotFoundException{
		VagonTayyor exist=	vagonTayyorRepository.findById(id).get();
		if(exist.getDepoNomi().equals("VCHD-5") ) {
			vagonTayyorRepository.deleteById(id);
			LocalDateTime today = LocalDateTime.now();
			LocalDateTime minusHours = today.plusHours(5);
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			andjDate = minusHours.format(myFormatObj);

			Optional<LastActionTimes> optionalBiznesTime = utyTimeRepository.findById(1);
			optionalBiznesTime.get().setAndjBiznesDate(andjDate);
			utyTimeRepository.save(optionalBiznesTime.get());
		}
	}

	@Override
	public int countByDepoNomiVagonTuriAndTamirTuri(String depoNomi, String vagonTuri, String tamirTuri) {
		return vagonTayyorRepository.countByDepoNomiVagonTuriAndTamirTuri(depoNomi, vagonTuri, tamirTuri);
	}

	@Override
	public int countByDepoNomiVagonTuriAndTamirTuri(String depoNomi, String vagonTuri, String tamirTuri,String country) {
		return vagonTayyorRepository.countByDepoNomiVagonTuriAndTamirTuri(depoNomi, vagonTuri, tamirTuri,country);
	}

	@Override
	public List<VagonTayyor> findAll() {
		return vagonTayyorRepository.findAll();
	}

	@Override
	public List<VagonTayyor> findAll(String oy) {
		return vagonTayyorRepository.findAll(oy);
	}

	@Override
	public int countAllActiveByDepoNomiVagonTuriAndTamirTuri(String depoNomi, String vagnTuri,
			String tamirTuri, String oy) {
		return vagonTayyorRepository.countAllActiveByDepoNomiVagonTuriAndTamirTuri(depoNomi, vagnTuri, tamirTuri, oy);
	}

	@Override
	public int countAllActiveByDepoNomiVagonTuriAndTamirTuri(String depoNomi, String vagnTuri,
			String tamirTuri, String oy, String country) {
		return vagonTayyorRepository.countAllActiveByDepoNomiVagonTuriAndTamirTuri(depoNomi, vagnTuri, tamirTuri, oy, country);
	}

	@Override
	public VagonTayyor searchByNomer(Integer nomer, String oy) {
		return vagonTayyorRepository.searchByNomer(nomer, oy);

	}
	@Override
	public VagonTayyor findByNomer(Integer nomer) {
		return vagonTayyorRepository.findByNomer(nomer).get();
	}

	// filterniki
	@Override
	public List<VagonTayyor> findAllByDepoNomiVagonTuriAndCountry(String depoNomi, String vagonTuri, String country, String oy) {
		return vagonTayyorRepository.findAllByDepoNomiVagonTuriAndCountry(depoNomi, vagonTuri, country, oy);
	}


	@Override
	public List<VagonTayyor> findAllByDepoNomiAndVagonTuri(String depoNomi, String vagonTuri, String oy) {
		return vagonTayyorRepository.findAllByDepoNomiAndVagonTuri(depoNomi, vagonTuri, oy);
	}

	@Override
	public List<VagonTayyor> findAllByDepoNomiAndCountry(String depoNomi, String country, String oy) {
		return vagonTayyorRepository.findAllByDepoNomiAndCountry(depoNomi, country, oy);
	}

	@Override
	public List<VagonTayyor> findAllByDepoNomi(String depoNomi, String oy) {
		return vagonTayyorRepository.findAllByDepoNomi(depoNomi, oy);
	}

	@Override
	public List<VagonTayyor> findAllByVagonTuriAndCountry(String vagonTuri, String country, String oy) {
		return vagonTayyorRepository.findAllByVagonTuriAndCountry(vagonTuri, country, oy);
	}

	@Override
	public List<VagonTayyor> findAllBycountry(String country, String oy) {
		return vagonTayyorRepository.findAllBycountry(country, oy);
	}

	@Override
	public List<VagonTayyor> findAllByVagonTuri(String vagonTuri, String oy) {
		return vagonTayyorRepository.findAllByVagonTuri(vagonTuri, oy);
	}

	//
	@Override
	public List<VagonTayyor> findByDepoNomiVagonTuriAndCountry(String depoNomi, String vagonTuri, String country) {
		return vagonTayyorRepository.findByDepoNomiVagonTuriAndCountry(depoNomi, vagonTuri, country);
	}


	@Override
	public List<VagonTayyor> findByDepoNomiAndVagonTuri(String depoNomi, String vagonTuri) {
		return vagonTayyorRepository.findByDepoNomiAndVagonTuri(depoNomi, vagonTuri);
	}

	@Override
	public List<VagonTayyor> findByDepoNomiAndCountry(String depoNomi, String country) {
		return vagonTayyorRepository.findByDepoNomiAndCountry(depoNomi, country);
	}

	@Override
	public List<VagonTayyor> findByDepoNomi(String depoNomi) {
		return vagonTayyorRepository.findByDepoNomi(depoNomi);
	}

	@Override
	public List<VagonTayyor> findByVagonTuriAndCountry(String vagonTuri, String country) {
		return vagonTayyorRepository.findByVagonTuriAndCountry(vagonTuri, country);
	}

	@Override
	public List<VagonTayyor> findBycountry(String country) {
		return vagonTayyorRepository.findBycountry(country);
	}

	@Override
	public List<VagonTayyor> findByVagonTuri(String vagonTuri) {
		return vagonTayyorRepository.findByVagonTuri(vagonTuri);
	}

	@Override
	public void savePlan(PlanBiznesDto planDto) {

		Optional<PlanBiznes> existsPlan = planBiznesRepository.findById(1);

		if (!existsPlan.isPresent()) {

			PlanBiznes biznesPlan = new PlanBiznes();
			biznesPlan.setId(1);

			//bir oy uchun
			//Depoli tamir 
			biznesPlan.setSamDtKritiPlanBiznes(planDto.getSamDtKritiPlanBiznes());
			biznesPlan.setSamDtPlatformaPlanBiznes(planDto.getSamDtPlatformaPlanBiznes());
			biznesPlan.setSamDtPoluvagonPlanBiznes(planDto.getSamDtPoluvagonPlanBiznes());
			biznesPlan.setSamDtSisternaPlanBiznes(planDto.getSamDtSisternaPlanBiznes());
			biznesPlan.setSamDtBoshqaPlanBiznes(planDto.getSamDtBoshqaPlanBiznes());

			biznesPlan.setHavDtKritiPlanBiznes(planDto.getHavDtKritiPlanBiznes());
			biznesPlan.setHavDtPlatformaPlanBiznes(planDto.getHavDtPlatformaPlanBiznes());
			biznesPlan.setHavDtPoluvagonPlanBiznes(planDto.getHavDtPoluvagonPlanBiznes());
			biznesPlan.setHavDtSisternaPlanBiznes(planDto.getHavDtSisternaPlanBiznes());
			biznesPlan.setHavDtBoshqaPlanBiznes(planDto.getHavDtBoshqaPlanBiznes());

			biznesPlan.setAndjDtKritiPlanBiznes(planDto.getAndjDtKritiPlanBiznes());
			biznesPlan.setAndjDtPlatformaPlanBiznes(planDto.getAndjDtPlatformaPlanBiznes());
			biznesPlan.setAndjDtPoluvagonPlanBiznes(planDto.getAndjDtPoluvagonPlanBiznes());
			biznesPlan.setAndjDtSisternaPlanBiznes(planDto.getAndjDtSisternaPlanBiznes());
			biznesPlan.setAndjDtBoshqaPlanBiznes(planDto.getAndjDtBoshqaPlanBiznes());

			biznesPlan.setAndjDtYolovchiPlanBiznes(planDto.getAndjDtYolovchiPlanBiznes());
			biznesPlan.setAndjTYolovchiPlanBiznes(planDto.getAndjTYolovchiPlanBiznes());

			//kapital tamir 
			biznesPlan.setSamKtKritiPlanBiznes(planDto.getSamKtKritiPlanBiznes());
			biznesPlan.setSamKtPlatformaPlanBiznes(planDto.getSamKtPlatformaPlanBiznes());
			biznesPlan.setSamKtPoluvagonPlanBiznes(planDto.getSamKtPoluvagonPlanBiznes());
			biznesPlan.setSamKtSisternaPlanBiznes(planDto.getSamKtSisternaPlanBiznes());
			biznesPlan.setSamKtBoshqaPlanBiznes(planDto.getSamKtBoshqaPlanBiznes());

			biznesPlan.setHavKtKritiPlanBiznes(planDto.getHavKtKritiPlanBiznes());
			biznesPlan.setHavKtPlatformaPlanBiznes(planDto.getHavKtPlatformaPlanBiznes());
			biznesPlan.setHavKtPoluvagonPlanBiznes(planDto.getHavKtPoluvagonPlanBiznes());
			biznesPlan.setHavKtSisternaPlanBiznes(planDto.getHavKtSisternaPlanBiznes());
			biznesPlan.setHavKtBoshqaPlanBiznes(planDto.getHavKtBoshqaPlanBiznes());

			biznesPlan.setAndjKtKritiPlanBiznes(planDto.getAndjKtKritiPlanBiznes());
			biznesPlan.setAndjKtPlatformaPlanBiznes(planDto.getAndjKtPlatformaPlanBiznes());
			biznesPlan.setAndjKtPoluvagonPlanBiznes(planDto.getAndjKtPoluvagonPlanBiznes());
			biznesPlan.setAndjKtSisternaPlanBiznes(planDto.getAndjKtSisternaPlanBiznes());
			biznesPlan.setAndjKtBoshqaPlanBiznes(planDto.getAndjKtBoshqaPlanBiznes());

			//KRP tamir 
			biznesPlan.setSamKrpKritiPlanBiznes(planDto.getSamKrpKritiPlanBiznes());
			biznesPlan.setSamKrpPlatformaPlanBiznes(planDto.getSamKrpPlatformaPlanBiznes());
			biznesPlan.setSamKrpPoluvagonPlanBiznes(planDto.getSamKrpPoluvagonPlanBiznes());
			biznesPlan.setSamKrpSisternaPlanBiznes(planDto.getSamKrpSisternaPlanBiznes());
			biznesPlan.setSamKrpBoshqaPlanBiznes(planDto.getSamKrpBoshqaPlanBiznes());

			biznesPlan.setHavKrpKritiPlanBiznes(planDto.getHavKrpKritiPlanBiznes());
			biznesPlan.setHavKrpPlatformaPlanBiznes(planDto.getHavKrpPlatformaPlanBiznes());
			biznesPlan.setHavKrpPoluvagonPlanBiznes(planDto.getHavKrpPoluvagonPlanBiznes());
			biznesPlan.setHavKrpSisternaPlanBiznes(planDto.getHavKrpSisternaPlanBiznes());
			biznesPlan.setHavKrpBoshqaPlanBiznes(planDto.getHavKrpBoshqaPlanBiznes());

			biznesPlan.setAndjKrpKritiPlanBiznes(planDto.getAndjKrpKritiPlanBiznes());
			biznesPlan.setAndjKrpPlatformaPlanBiznes(planDto.getAndjKrpPlatformaPlanBiznes());
			biznesPlan.setAndjKrpPoluvagonPlanBiznes(planDto.getAndjKrpPoluvagonPlanBiznes());
			biznesPlan.setAndjKrpSisternaPlanBiznes(planDto.getAndjKrpSisternaPlanBiznes());
			biznesPlan.setAndjKrpBoshqaPlanBiznes(planDto.getAndjKrpBoshqaPlanBiznes());

			//Jami oy uchun
			//Depoli tamir
			biznesPlan.setSamDtKritiPlanBiznesMonths(planDto.getSamDtKritiPlanBiznes());
			biznesPlan.setSamDtPlatformaPlanBiznesMonths(planDto.getSamDtPlatformaPlanBiznes());
			biznesPlan.setSamDtPoluvagonPlanBiznesMonths(planDto.getSamDtPoluvagonPlanBiznes());
			biznesPlan.setSamDtSisternaPlanBiznesMonths(planDto.getSamDtSisternaPlanBiznes());
			biznesPlan.setSamDtBoshqaPlanBiznesMonths(planDto.getSamDtBoshqaPlanBiznes());

			biznesPlan.setHavDtKritiPlanBiznesMonths(planDto.getHavDtKritiPlanBiznes());
			biznesPlan.setHavDtPlatformaPlanBiznesMonths(planDto.getHavDtPlatformaPlanBiznes());
			biznesPlan.setHavDtPoluvagonPlanBiznesMonths(planDto.getHavDtPoluvagonPlanBiznes());
			biznesPlan.setHavDtSisternaPlanBiznesMonths(planDto.getHavDtSisternaPlanBiznes());
			biznesPlan.setHavDtBoshqaPlanBiznesMonths(planDto.getHavDtBoshqaPlanBiznes());

			biznesPlan.setAndjDtKritiPlanBiznesMonths(planDto.getAndjDtKritiPlanBiznes());
			biznesPlan.setAndjDtPlatformaPlanBiznesMonths(planDto.getAndjDtPlatformaPlanBiznes());
			biznesPlan.setAndjDtPoluvagonPlanBiznesMonths(planDto.getAndjDtPoluvagonPlanBiznes());
			biznesPlan.setAndjDtSisternaPlanBiznesMonths(planDto.getAndjDtSisternaPlanBiznes());
			biznesPlan.setAndjDtBoshqaPlanBiznesMonths(planDto.getAndjDtBoshqaPlanBiznes());

			biznesPlan.setAndjDtYolovchiPlanBiznesMonths(planDto.getAndjDtYolovchiPlanBiznes());
			biznesPlan.setAndjTYolovchiPlanBiznesMonths(planDto.getAndjTYolovchiPlanBiznes());

			//kapital tamir
			biznesPlan.setSamKtKritiPlanBiznesMonths(planDto.getSamKtKritiPlanBiznes());
			biznesPlan.setSamKtPlatformaPlanBiznesMonths(planDto.getSamKtPlatformaPlanBiznes());
			biznesPlan.setSamKtPoluvagonPlanBiznesMonths(planDto.getSamKtPoluvagonPlanBiznes());
			biznesPlan.setSamKtSisternaPlanBiznesMonths(planDto.getSamKtSisternaPlanBiznes());
			biznesPlan.setSamKtBoshqaPlanBiznesMonths(planDto.getSamKtBoshqaPlanBiznes());

			biznesPlan.setHavKtKritiPlanBiznesMonths(planDto.getHavKtKritiPlanBiznes());
			biznesPlan.setHavKtPlatformaPlanBiznesMonths(planDto.getHavKtPlatformaPlanBiznes());
			biznesPlan.setHavKtPoluvagonPlanBiznesMonths(planDto.getHavKtPoluvagonPlanBiznes());
			biznesPlan.setHavKtSisternaPlanBiznesMonths(planDto.getHavKtSisternaPlanBiznes());
			biznesPlan.setHavKtBoshqaPlanBiznesMonths(planDto.getHavKtBoshqaPlanBiznes());

			biznesPlan.setAndjKtKritiPlanBiznesMonths(planDto.getAndjKtKritiPlanBiznes());
			biznesPlan.setAndjKtPlatformaPlanBiznesMonths(planDto.getAndjKtPlatformaPlanBiznes());
			biznesPlan.setAndjKtPoluvagonPlanBiznesMonths(planDto.getAndjKtPoluvagonPlanBiznes());
			biznesPlan.setAndjKtSisternaPlanBiznesMonths(planDto.getAndjKtSisternaPlanBiznes());
			biznesPlan.setAndjKtBoshqaPlanBiznesMonths(planDto.getAndjKtBoshqaPlanBiznes());

			//KRP tamir
			biznesPlan.setSamKrpKritiPlanBiznesMonths(planDto.getSamKrpKritiPlanBiznes());
			biznesPlan.setSamKrpPlatformaPlanBiznesMonths(planDto.getSamKrpPlatformaPlanBiznes());
			biznesPlan.setSamKrpPoluvagonPlanBiznesMonths(planDto.getSamKrpPoluvagonPlanBiznes());
			biznesPlan.setSamKrpSisternaPlanBiznesMonths(planDto.getSamKrpSisternaPlanBiznes());
			biznesPlan.setSamKrpBoshqaPlanBiznesMonths(planDto.getSamKrpBoshqaPlanBiznes());

			biznesPlan.setHavKrpKritiPlanBiznesMonths(planDto.getHavKrpKritiPlanBiznes());
			biznesPlan.setHavKrpPlatformaPlanBiznesMonths(planDto.getHavKrpPlatformaPlanBiznes());
			biznesPlan.setHavKrpPoluvagonPlanBiznesMonths(planDto.getHavKrpPoluvagonPlanBiznes());
			biznesPlan.setHavKrpSisternaPlanBiznesMonths(planDto.getHavKrpSisternaPlanBiznes());
			biznesPlan.setHavKrpBoshqaPlanBiznesMonths(planDto.getHavKrpBoshqaPlanBiznes());

			biznesPlan.setAndjKrpKritiPlanBiznesMonths(planDto.getAndjKrpKritiPlanBiznes());
			biznesPlan.setAndjKrpPlatformaPlanBiznesMonths(planDto.getAndjKrpPlatformaPlanBiznes());
			biznesPlan.setAndjKrpPoluvagonPlanBiznesMonths(planDto.getAndjKrpPoluvagonPlanBiznes());
			biznesPlan.setAndjKrpSisternaPlanBiznesMonths(planDto.getAndjKrpSisternaPlanBiznes());
			biznesPlan.setAndjKrpBoshqaPlanBiznesMonths(planDto.getAndjKrpBoshqaPlanBiznes());

			planBiznesRepository.save(biznesPlan);

		}else {
			PlanBiznes biznesPlan = existsPlan.get();

			//bir oy uchun
			//Depoli tamir
			biznesPlan.setSamDtKritiPlanBiznes(planDto.getSamDtKritiPlanBiznes());
			biznesPlan.setSamDtPlatformaPlanBiznes(planDto.getSamDtPlatformaPlanBiznes());
			biznesPlan.setSamDtPoluvagonPlanBiznes(planDto.getSamDtPoluvagonPlanBiznes());
			biznesPlan.setSamDtSisternaPlanBiznes(planDto.getSamDtSisternaPlanBiznes());
			biznesPlan.setSamDtBoshqaPlanBiznes(planDto.getSamDtBoshqaPlanBiznes());

			biznesPlan.setHavDtKritiPlanBiznes(planDto.getHavDtKritiPlanBiznes());
			biznesPlan.setHavDtPlatformaPlanBiznes(planDto.getHavDtPlatformaPlanBiznes());
			biznesPlan.setHavDtPoluvagonPlanBiznes(planDto.getHavDtPoluvagonPlanBiznes());
			biznesPlan.setHavDtSisternaPlanBiznes(planDto.getHavDtSisternaPlanBiznes());
			biznesPlan.setHavDtBoshqaPlanBiznes(planDto.getHavDtBoshqaPlanBiznes());

			biznesPlan.setAndjDtKritiPlanBiznes(planDto.getAndjDtKritiPlanBiznes());
			biznesPlan.setAndjDtPlatformaPlanBiznes(planDto.getAndjDtPlatformaPlanBiznes());
			biznesPlan.setAndjDtPoluvagonPlanBiznes(planDto.getAndjDtPoluvagonPlanBiznes());
			biznesPlan.setAndjDtSisternaPlanBiznes(planDto.getAndjDtSisternaPlanBiznes());
			biznesPlan.setAndjDtBoshqaPlanBiznes(planDto.getAndjDtBoshqaPlanBiznes());

			biznesPlan.setAndjDtYolovchiPlanBiznes(planDto.getAndjDtYolovchiPlanBiznes());
			biznesPlan.setAndjTYolovchiPlanBiznes(planDto.getAndjTYolovchiPlanBiznes());

			//kapital tamir
			biznesPlan.setSamKtKritiPlanBiznes(planDto.getSamKtKritiPlanBiznes());
			biznesPlan.setSamKtPlatformaPlanBiznes(planDto.getSamKtPlatformaPlanBiznes());
			biznesPlan.setSamKtPoluvagonPlanBiznes(planDto.getSamKtPoluvagonPlanBiznes());
			biznesPlan.setSamKtSisternaPlanBiznes(planDto.getSamKtSisternaPlanBiznes());
			biznesPlan.setSamKtBoshqaPlanBiznes(planDto.getSamKtBoshqaPlanBiznes());

			biznesPlan.setHavKtKritiPlanBiznes(planDto.getHavKtKritiPlanBiznes());
			biznesPlan.setHavKtPlatformaPlanBiznes(planDto.getHavKtPlatformaPlanBiznes());
			biznesPlan.setHavKtPoluvagonPlanBiznes(planDto.getHavKtPoluvagonPlanBiznes());
			biznesPlan.setHavKtSisternaPlanBiznes(planDto.getHavKtSisternaPlanBiznes());
			biznesPlan.setHavKtBoshqaPlanBiznes(planDto.getHavKtBoshqaPlanBiznes());

			biznesPlan.setAndjKtKritiPlanBiznes(planDto.getAndjKtKritiPlanBiznes());
			biznesPlan.setAndjKtPlatformaPlanBiznes(planDto.getAndjKtPlatformaPlanBiznes());
			biznesPlan.setAndjKtPoluvagonPlanBiznes(planDto.getAndjKtPoluvagonPlanBiznes());
			biznesPlan.setAndjKtSisternaPlanBiznes(planDto.getAndjKtSisternaPlanBiznes());
			biznesPlan.setAndjKtBoshqaPlanBiznes(planDto.getAndjKtBoshqaPlanBiznes());

			//KRP tamir
			biznesPlan.setSamKrpKritiPlanBiznes(planDto.getSamKrpKritiPlanBiznes());
			biznesPlan.setSamKrpPlatformaPlanBiznes(planDto.getSamKrpPlatformaPlanBiznes());
			biznesPlan.setSamKrpPoluvagonPlanBiznes(planDto.getSamKrpPoluvagonPlanBiznes());
			biznesPlan.setSamKrpSisternaPlanBiznes(planDto.getSamKrpSisternaPlanBiznes());
			biznesPlan.setSamKrpBoshqaPlanBiznes(planDto.getSamKrpBoshqaPlanBiznes());

			biznesPlan.setHavKrpKritiPlanBiznes(planDto.getHavKrpKritiPlanBiznes());
			biznesPlan.setHavKrpPlatformaPlanBiznes(planDto.getHavKrpPlatformaPlanBiznes());
			biznesPlan.setHavKrpPoluvagonPlanBiznes(planDto.getHavKrpPoluvagonPlanBiznes());
			biznesPlan.setHavKrpSisternaPlanBiznes(planDto.getHavKrpSisternaPlanBiznes());
			biznesPlan.setHavKrpBoshqaPlanBiznes(planDto.getHavKrpBoshqaPlanBiznes());

			biznesPlan.setAndjKrpKritiPlanBiznes(planDto.getAndjKrpKritiPlanBiznes());
			biznesPlan.setAndjKrpPlatformaPlanBiznes(planDto.getAndjKrpPlatformaPlanBiznes());
			biznesPlan.setAndjKrpPoluvagonPlanBiznes(planDto.getAndjKrpPoluvagonPlanBiznes());
			biznesPlan.setAndjKrpSisternaPlanBiznes(planDto.getAndjKrpSisternaPlanBiznes());
			biznesPlan.setAndjKrpBoshqaPlanBiznes(planDto.getAndjKrpBoshqaPlanBiznes());

			//Jami oy uchun
			//Depoli tamir
			biznesPlan.setSamDtKritiPlanBiznesMonths(biznesPlan.getSamDtKritiPlanBiznesMonths() + planDto.getSamDtKritiPlanBiznes());
			biznesPlan.setSamDtPlatformaPlanBiznesMonths(biznesPlan.getSamDtPlatformaPlanBiznesMonths() +planDto.getSamDtPlatformaPlanBiznes());
			biznesPlan.setSamDtPoluvagonPlanBiznesMonths(biznesPlan.getSamDtPoluvagonPlanBiznesMonths() +planDto.getSamDtPoluvagonPlanBiznes());
			biznesPlan.setSamDtSisternaPlanBiznesMonths(biznesPlan.getSamDtSisternaPlanBiznesMonths() +planDto.getSamDtSisternaPlanBiznes());
			biznesPlan.setSamDtBoshqaPlanBiznesMonths(biznesPlan.getSamDtBoshqaPlanBiznesMonths() +planDto.getSamDtBoshqaPlanBiznes());

			biznesPlan.setHavDtKritiPlanBiznesMonths(biznesPlan.getHavDtKritiPlanBiznesMonths() + planDto.getHavDtKritiPlanBiznes());
			biznesPlan.setHavDtPlatformaPlanBiznesMonths(biznesPlan.getHavDtPlatformaPlanBiznesMonths() +planDto.getHavDtPlatformaPlanBiznes());
			biznesPlan.setHavDtPoluvagonPlanBiznesMonths(biznesPlan.getHavDtPoluvagonPlanBiznesMonths() +planDto.getHavDtPoluvagonPlanBiznes());
			biznesPlan.setHavDtSisternaPlanBiznesMonths(biznesPlan.getHavDtSisternaPlanBiznesMonths() +planDto.getHavDtSisternaPlanBiznes());
			biznesPlan.setHavDtBoshqaPlanBiznesMonths(biznesPlan.getHavDtBoshqaPlanBiznesMonths() +planDto.getHavDtBoshqaPlanBiznes());

			biznesPlan.setAndjDtKritiPlanBiznesMonths(biznesPlan.getAndjDtKritiPlanBiznesMonths() + planDto.getAndjDtKritiPlanBiznes());
			biznesPlan.setAndjDtPlatformaPlanBiznesMonths(biznesPlan.getAndjDtPlatformaPlanBiznesMonths() +planDto.getAndjDtPlatformaPlanBiznes());
			biznesPlan.setAndjDtPoluvagonPlanBiznesMonths(biznesPlan.getAndjDtPoluvagonPlanBiznesMonths() +planDto.getAndjDtPoluvagonPlanBiznes());
			biznesPlan.setAndjDtSisternaPlanBiznesMonths(biznesPlan.getAndjDtSisternaPlanBiznesMonths() +planDto.getAndjDtSisternaPlanBiznes());
			biznesPlan.setAndjDtBoshqaPlanBiznesMonths(biznesPlan.getAndjDtBoshqaPlanBiznesMonths() +planDto.getAndjDtBoshqaPlanBiznes());

			biznesPlan.setAndjDtYolovchiPlanBiznesMonths(biznesPlan.getAndjDtYolovchiPlanBiznesMonths() + planDto.getAndjDtYolovchiPlanBiznes());
			biznesPlan.setAndjTYolovchiPlanBiznesMonths(biznesPlan.getAndjTYolovchiPlanBiznesMonths() + planDto.getAndjTYolovchiPlanBiznes());

			//kapital tamir
			biznesPlan.setSamKtKritiPlanBiznesMonths(biznesPlan.getSamKtKritiPlanBiznesMonths() + planDto.getSamKtKritiPlanBiznes());
			biznesPlan.setSamKtPlatformaPlanBiznesMonths(biznesPlan.getSamKtPlatformaPlanBiznesMonths() +planDto.getSamKtPlatformaPlanBiznes());
			biznesPlan.setSamKtPoluvagonPlanBiznesMonths(biznesPlan.getSamKtPoluvagonPlanBiznesMonths() +planDto.getSamKtPoluvagonPlanBiznes());
			biznesPlan.setSamKtSisternaPlanBiznesMonths(biznesPlan.getSamKtSisternaPlanBiznesMonths() +planDto.getSamKtSisternaPlanBiznes());
			biznesPlan.setSamKtBoshqaPlanBiznesMonths(biznesPlan.getSamKtBoshqaPlanBiznesMonths() +planDto.getSamKtBoshqaPlanBiznes());

			biznesPlan.setHavKtKritiPlanBiznesMonths(biznesPlan.getHavKtKritiPlanBiznesMonths() + planDto.getHavKtKritiPlanBiznes());
			biznesPlan.setHavKtPlatformaPlanBiznesMonths(biznesPlan.getHavKtPlatformaPlanBiznesMonths() +planDto.getHavKtPlatformaPlanBiznes());
			biznesPlan.setHavKtPoluvagonPlanBiznesMonths(biznesPlan.getHavKtPoluvagonPlanBiznesMonths() +planDto.getHavKtPoluvagonPlanBiznes());
			biznesPlan.setHavKtSisternaPlanBiznesMonths(biznesPlan.getHavKtSisternaPlanBiznesMonths() +planDto.getHavKtSisternaPlanBiznes());
			biznesPlan.setHavKtBoshqaPlanBiznesMonths(biznesPlan.getHavKtBoshqaPlanBiznesMonths() +planDto.getHavKtBoshqaPlanBiznes());

			biznesPlan.setAndjKtKritiPlanBiznesMonths(biznesPlan.getAndjKtKritiPlanBiznesMonths() + planDto.getAndjKtKritiPlanBiznes());
			biznesPlan.setAndjKtPlatformaPlanBiznesMonths(biznesPlan.getAndjKtPlatformaPlanBiznesMonths() +planDto.getAndjKtPlatformaPlanBiznes());
			biznesPlan.setAndjKtPoluvagonPlanBiznesMonths(biznesPlan.getAndjKtPoluvagonPlanBiznesMonths() +planDto.getAndjKtPoluvagonPlanBiznes());
			biznesPlan.setAndjKtSisternaPlanBiznesMonths(biznesPlan.getAndjKtSisternaPlanBiznesMonths() +planDto.getAndjKtSisternaPlanBiznes());
			biznesPlan.setAndjKtBoshqaPlanBiznesMonths(biznesPlan.getAndjKtBoshqaPlanBiznesMonths() +planDto.getAndjKtBoshqaPlanBiznes());

			//KRP tamir
			biznesPlan.setSamKrpKritiPlanBiznesMonths(biznesPlan.getSamKrpKritiPlanBiznesMonths() + planDto.getSamKrpKritiPlanBiznes());
			biznesPlan.setSamKrpPlatformaPlanBiznesMonths(biznesPlan.getSamKrpPlatformaPlanBiznesMonths() +planDto.getSamKrpPlatformaPlanBiznes());
			biznesPlan.setSamKrpPoluvagonPlanBiznesMonths(biznesPlan.getSamKrpPoluvagonPlanBiznesMonths() +planDto.getSamKrpPoluvagonPlanBiznes());
			biznesPlan.setSamKrpSisternaPlanBiznesMonths(biznesPlan.getSamKrpSisternaPlanBiznesMonths() +planDto.getSamKrpSisternaPlanBiznes());
			biznesPlan.setSamKrpBoshqaPlanBiznesMonths(biznesPlan.getSamKrpBoshqaPlanBiznesMonths() +planDto.getSamKrpBoshqaPlanBiznes());

			biznesPlan.setHavKrpKritiPlanBiznesMonths(biznesPlan.getHavKrpKritiPlanBiznesMonths() + planDto.getHavKrpKritiPlanBiznes());
			biznesPlan.setHavKrpPlatformaPlanBiznesMonths(biznesPlan.getHavKrpPlatformaPlanBiznesMonths() +planDto.getHavKrpPlatformaPlanBiznes());
			biznesPlan.setHavKrpPoluvagonPlanBiznesMonths(biznesPlan.getHavKrpPoluvagonPlanBiznesMonths() +planDto.getHavKrpPoluvagonPlanBiznes());
			biznesPlan.setHavKrpSisternaPlanBiznesMonths(biznesPlan.getHavKrpSisternaPlanBiznesMonths() +planDto.getHavKrpSisternaPlanBiznes());
			biznesPlan.setHavKrpBoshqaPlanBiznesMonths(biznesPlan.getHavKrpBoshqaPlanBiznesMonths() +planDto.getHavKrpBoshqaPlanBiznes());

			biznesPlan.setAndjKrpKritiPlanBiznesMonths(biznesPlan.getAndjKrpKritiPlanBiznesMonths() + planDto.getAndjKrpKritiPlanBiznes());
			biznesPlan.setAndjKrpPlatformaPlanBiznesMonths(biznesPlan.getAndjKrpPlatformaPlanBiznesMonths() +planDto.getAndjKrpPlatformaPlanBiznes());
			biznesPlan.setAndjKrpPoluvagonPlanBiznesMonths(biznesPlan.getAndjKrpPoluvagonPlanBiznesMonths() +planDto.getAndjKrpPoluvagonPlanBiznes());
			biznesPlan.setAndjKrpSisternaPlanBiznesMonths(biznesPlan.getAndjKrpSisternaPlanBiznesMonths() +planDto.getAndjKrpSisternaPlanBiznes());
			biznesPlan.setAndjKrpBoshqaPlanBiznesMonths(biznesPlan.getAndjKrpBoshqaPlanBiznesMonths() +planDto.getAndjKrpBoshqaPlanBiznes());

			planBiznesRepository.save(biznesPlan);
		}
	}

	@Override
	public PlanBiznes getPlanBiznes() {
		Optional<PlanBiznes> optionalPlanBiznes = planBiznesRepository.findById(1);
		if (optionalPlanBiznes.isPresent())
			return optionalPlanBiznes.get();
		return new PlanBiznes();
	}

	@Override
	public VagonTayyor findById(Long id) {
		return vagonTayyorRepository.findById(id).get();
	}


}
