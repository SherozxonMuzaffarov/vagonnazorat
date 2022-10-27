package com.sms.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sms.dto.PlanBiznesDto;
import com.sms.model.PlanBiznes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sms.model.VagonTayyor;
import com.sms.service.VagonTayyorBiznesService;

@Controller
public class VagonTayyorBiznesController {

	@Autowired
	private VagonTayyorBiznesService vagonTayyorService;


	LocalDate today = LocalDate.now();
	int month = today.getMonthValue();

	@PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ' , 'SAMMETROLOGIYA','HAVMETROLOGIYA','ANDJMETROLOGIYA')")
	@GetMapping("")
	public String main(Model model) {
		return "main";
	}

	@PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/rejalar")
	public String rejalar(Model model) {
		return "rejalar";
	}

	@PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/tables")
	public String tables(Model model) {

		String oy = null;
		switch (month) {
			case 1:
				oy = "Yanvar";
				break;
			case 2:
				oy = "Fevral";
				break;
			case 3:
				oy = "Mart";
				break;
			case 4:
				oy = "Aprel";
				break;
			case 5:
				oy = "May";
				break;
			case 6:
				oy = "Iyun";
				break;
			case 7:
				oy = "Iyul";
				break;
			case 8:
				oy = "Avgust";
				break;
			case 9:
				oy = "Sentabr";
				break;
			case 10:
				oy = "Oktabr";
				break;
			case 11:
				oy = "Noyabr";
				break;
			case 12:
				oy = "Dekabr";
				break;

		}

		model.addAttribute("month", oy);
		model.addAttribute("year", month + " oylik");
		return "tables";
	}

	//Yuklab olish uchun Malumot yigib beradi
	List<VagonTayyor> vagonsToDownload  = new ArrayList<>();

	@PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/createExcelBiznes")
	public void pdfFile(Model model,HttpServletResponse response) throws IOException {
		vagonTayyorService.createPdf(vagonsToDownload,response);
		model.addAttribute("vagons",vagonsToDownload);
	 }

	@PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/createExcelBiznesAllMonth")
	public void createPdfFile(Model model,HttpServletResponse response) throws IOException {
		vagonTayyorService.createPdf(vagonsToDownload,response);
		model.addAttribute("vagons",vagonsToDownload);
	 }

    //Tayyor yangi vagon qoshish uchun oyna
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/newTayyor")
	public String createVagonForm(Model model) {
		VagonTayyor vagonTayyor = new VagonTayyor();
		model.addAttribute("vagon", vagonTayyor);
		return "create_tayyorvagon";
	}

    //TAyyor vagon qoshish
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@PostMapping("/vagons/saveTayyor")
	public String saveVagon(@ModelAttribute("vagon") VagonTayyor vagon, HttpServletRequest request ) {
    	if (request.isUserInRole("DIRECTOR")) {
    		vagonTayyorService.saveVagon(vagon);
        }else if (request.isUserInRole("SAM")) {
        	vagonTayyorService.saveVagonSam(vagon);
        }else if (request.isUserInRole("HAV")) {
        	vagonTayyorService.saveVagonHav(vagon);
        }else if (request.isUserInRole("ANDJ")) {
        	vagonTayyorService.saveVagonAndj(vagon);
        }
		return "redirect:/vagons/AllPlanTable";
	}

    //tahrirlash uchun oyna
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/editTayyor/{id}")
	public String editVagonForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("vagon",vagonTayyorService.getVagonById(id));
		return "edit_tayyorvagon";
	}

    //tahrirni saqlash
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@PostMapping("/vagons/updateTayyor/{id}")
	public String updateVagon(@ModelAttribute("vagon") VagonTayyor vagon, @PathVariable Long id,Model model, HttpServletRequest request) throws NotFoundException {
    	if (request.isUserInRole("DIRECTOR")) {
    		vagonTayyorService.updateVagon(vagon, id);
        }else if (request.isUserInRole("SAM")) {
        	vagonTayyorService.updateVagonSam(vagon, id);
        }else if (request.isUserInRole("HAV")) {
        	vagonTayyorService.updateVagonHav(vagon, id);
        }else if (request.isUserInRole("ANDJ")) {
        	vagonTayyorService.updateVagonAndj(vagon, id);
        }
    	return "redirect:/vagons/AllPlanTable";
	}

    //tahrirlash jami oylar uchun
  //tahrirlash uchun oyna
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/editTayyorMonths/{id}")
	public String editVagonFormMonths(@PathVariable("id") Long id, Model model) {
		model.addAttribute("vagon",vagonTayyorService.getVagonById(id));
		return "edit_tayyorvagonMonths";
	}

    //tahrirni saqlash
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@PostMapping("/vagons/updateTayyorMonths/{id}")
	public String updateVagonMonths(@ModelAttribute("vagon") VagonTayyor vagon, @PathVariable Long id,Model model, HttpServletRequest request) throws NotFoundException {
    	if (request.isUserInRole("DIRECTOR")) {
    		vagonTayyorService.updateVagonMonths(vagon, id);
        }else if (request.isUserInRole("SAM")) {
        	vagonTayyorService.updateVagonSamMonths(vagon, id);
        }else if (request.isUserInRole("HAV")) {
        	vagonTayyorService.updateVagonHavMonths(vagon, id);
        }else if (request.isUserInRole("ANDJ")) {
        	vagonTayyorService.updateVagonAndjMonths(vagon, id);
        }
    	return "redirect:/vagons/planTableForMonths";
	}

    //bazadan o'chirish
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/deleteTayyor/{id}")
	public String deleteVagonForm(@PathVariable("id") Long id, HttpServletRequest request ) throws NotFoundException {
    	if (request.isUserInRole("DIRECTOR")) {
    		vagonTayyorService.deleteVagonById(id);
        }else if (request.isUserInRole("SAM")) {
        		vagonTayyorService.deleteVagonSam(id);
        }else if (request.isUserInRole("HAV")) {
    		vagonTayyorService.deleteVagonHav(id);
        }else if (request.isUserInRole("ANDJ")) {
    		vagonTayyorService.deleteVagonAndj(id);
        }
		return "redirect:/vagons/AllPlanTable";
	}

    //All planlar uchun
    @PreAuthorize(value = "hasRole('DIRECTOR')")
   	@GetMapping("/vagons/newPlan")
   	public String addPlan(Model model) {
   		PlanBiznesDto planDto = new PlanBiznesDto();
   		model.addAttribute("planDto", planDto);
   		return "add_plan";
   	}

    //Plan qoshish
    @PreAuthorize(value = "hasAnyRole('DIRECTOR')")
	@PostMapping("/vagons/savePlan")
	public String savePlan(@ModelAttribute("planDto") PlanBiznesDto planDto) {
    	vagonTayyorService.savePlan(planDto);
    	return "redirect:/vagons/AllPlanTable";
	}

    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/AllPlanTable")
	public String getAllPlan(Model model) {
		String oy=null;
		switch (month) {
			case 1:
				oy = "-01";
				break;
			case 2:
				oy = "-02";
				break;
			case 3:
				oy = "-03";
				break;
			case 4:
				oy = "-04";
				break;
			case 5:
				oy = "-05";
				break;
			case 6:
				oy = "-06";
				break;
			case 7:
				oy = "-07";
				break;
			case 8:
				oy = "-08";
				break;
			case 9:
				oy = "-09";
				break;
			case 10:
				oy = "-10";
				break;
			case 11:
				oy = "-11";
				break;
			case 12:
				oy = "-12";
				break;
		}

    	//listni toldirish uchun
    	vagonsToDownload = vagonTayyorService.findAll(oy);
    	model.addAttribute("vagons", vagonTayyorService.findAll(oy));

    	//vaqtni olib turadi
		model.addAttribute("samDate",vagonTayyorService.getSamDate());
		model.addAttribute("havDate", vagonTayyorService.getHavDate());
		model.addAttribute("andjDate",vagonTayyorService.getAndjDate());

    	PlanBiznes planDto = vagonTayyorService.getPlanBiznes();
		//planlar kiritish
		//samarqand depo tamir
		int SamDtHammaPlan=planDto.getSamDtKritiPlanBiznes() + planDto.getSamDtPlatformaPlanBiznes() + planDto.getSamDtPoluvagonPlanBiznes() + planDto.getSamDtSisternaPlanBiznes() + planDto.getSamDtBoshqaPlanBiznes();
		model.addAttribute("SamDtHammaPlan",SamDtHammaPlan);
		model.addAttribute("SamDtKritiPlan", planDto.getSamDtKritiPlanBiznes());
		model.addAttribute("SamDtPlatformaPlan", planDto.getSamDtPlatformaPlanBiznes());
		model.addAttribute("SamDtPoluvagonPlan", planDto.getSamDtPoluvagonPlanBiznes());
		model.addAttribute("SamDtSisternaPlan", planDto.getSamDtSisternaPlanBiznes());
		model.addAttribute("SamDtBoshqaPlan", planDto.getSamDtBoshqaPlanBiznes());

		//havos hamma plan
		int HavDtHammaPlan = planDto.getHavDtKritiPlanBiznes() + planDto.getHavDtPlatformaPlanBiznes() + planDto.getHavDtPoluvagonPlanBiznes() + planDto.getHavDtSisternaPlanBiznes() + planDto.getHavDtBoshqaPlanBiznes();
		model.addAttribute("HavDtHammaPlan", HavDtHammaPlan);
		model.addAttribute("HavDtKritiPlan", planDto.getHavDtKritiPlanBiznes());
		model.addAttribute("HavDtPlatformaPlan", planDto.getHavDtPlatformaPlanBiznes());
		model.addAttribute("HavDtPoluvagonPlan", planDto.getHavDtPoluvagonPlanBiznes());
		model.addAttribute("HavDtSisternaPlan", planDto.getHavDtSisternaPlanBiznes());
		model.addAttribute("HavDtBoshqaPlan", planDto.getHavDtBoshqaPlanBiznes());

		//andijon hamma plan depo tamir
		int AndjDtHammaPlan = planDto.getAndjDtKritiPlanBiznes() + planDto.getAndjDtPlatformaPlanBiznes() + planDto.getAndjDtPoluvagonPlanBiznes() + planDto.getAndjDtSisternaPlanBiznes() + planDto.getAndjDtBoshqaPlanBiznes();
		model.addAttribute("AndjDtHammaPlan", AndjDtHammaPlan);
		model.addAttribute("AndjDtKritiPlan", planDto.getAndjDtKritiPlanBiznes());
		model.addAttribute("AndjDtPlatformaPlan", planDto.getAndjDtPlatformaPlanBiznes());
		model.addAttribute("AndjDtPoluvagonPlan", planDto.getAndjDtPoluvagonPlanBiznes());
		model.addAttribute("AndjDtSisternaPlan", planDto.getAndjDtSisternaPlanBiznes());
		model.addAttribute("AndjDtBoshqaPlan", planDto.getAndjDtBoshqaPlanBiznes());

		// Itogo planlar depo tamir
		model.addAttribute("DtHammaPlan", AndjDtHammaPlan + HavDtHammaPlan + SamDtHammaPlan);
		model.addAttribute("DtKritiPlan", planDto.getAndjDtKritiPlanBiznes() + planDto.getHavDtKritiPlanBiznes() + planDto.getSamDtKritiPlanBiznes());
		model.addAttribute("DtPlatformaPlan", planDto.getAndjDtPlatformaPlanBiznes() + planDto.getHavDtPlatformaPlanBiznes() + planDto.getSamDtPlatformaPlanBiznes());
		model.addAttribute("DtPoluvagonPlan",planDto.getAndjDtPoluvagonPlanBiznes() + planDto.getHavDtPoluvagonPlanBiznes() + planDto.getSamDtPoluvagonPlanBiznes());
		model.addAttribute("DtSisternaPlan", planDto.getAndjDtSisternaPlanBiznes() + planDto.getHavDtSisternaPlanBiznes() + planDto.getSamDtSisternaPlanBiznes());
		model.addAttribute("DtBoshqaPlan", planDto.getAndjDtBoshqaPlanBiznes() + planDto.getHavDtBoshqaPlanBiznes() + planDto.getSamDtBoshqaPlanBiznes());

		//yolovchi vagonlar plan
		model.addAttribute("AndjToYolovchiPlan", planDto.getAndjTYolovchiPlanBiznes());
		model.addAttribute("AndjDtYolovchiPlan", planDto.getAndjDtYolovchiPlanBiznes());

		//VCHD-6 kapital tamir uchun plan
		int SamKtHammaPlan =  planDto.getSamKtKritiPlanBiznes() + planDto.getSamKtPlatformaPlanBiznes() + planDto.getSamKtPoluvagonPlanBiznes() + planDto.getSamKtSisternaPlanBiznes() + planDto.getSamKtBoshqaPlanBiznes();
		model.addAttribute("SamKtHammaPlan",SamKtHammaPlan);
		model.addAttribute("SamKtKritiPlan", planDto.getSamKtKritiPlanBiznes());
		model.addAttribute("SamKtPlatformaPlan", planDto.getSamKtPlatformaPlanBiznes());
		model.addAttribute("SamKtPoluvagonPlan", planDto.getSamKtPoluvagonPlanBiznes());
		model.addAttribute("SamKtSisternaPlan", planDto.getSamKtSisternaPlanBiznes());
		model.addAttribute("SamKtBoshqaPlan", planDto.getSamKtBoshqaPlanBiznes());

		//havos kapital tamir uchun plan
		int HavKtHammaPlan = planDto.getHavKtKritiPlanBiznes() + planDto.getHavKtPlatformaPlanBiznes() + planDto.getHavKtPoluvagonPlanBiznes() + planDto.getHavKtSisternaPlanBiznes() + planDto.getHavKtBoshqaPlanBiznes();
		model.addAttribute("HavKtHammaPlan", HavKtHammaPlan);
		model.addAttribute("HavKtKritiPlan", planDto.getHavKtKritiPlanBiznes());
		model.addAttribute("HavKtPlatformaPlan", planDto.getHavKtPlatformaPlanBiznes());
		model.addAttribute("HavKtPoluvagonPlan", planDto.getHavKtPoluvagonPlanBiznes());
		model.addAttribute("HavKtSisternaPlan", planDto.getHavKtSisternaPlanBiznes());
		model.addAttribute("HavKtBoshqaPlan", planDto.getHavKtBoshqaPlanBiznes());

		//VCHD-5 kapital tamir uchun plan
		int AndjKtHammaPlan = planDto.getAndjKtKritiPlanBiznes() + planDto.getAndjKtPlatformaPlanBiznes() + planDto.getAndjKtPoluvagonPlanBiznes() + planDto.getAndjKtSisternaPlanBiznes() + planDto.getAndjKtBoshqaPlanBiznes();
		model.addAttribute("AndjKtHammaPlan", AndjKtHammaPlan);
		model.addAttribute("AndjKtKritiPlan", planDto.getAndjKtKritiPlanBiznes());
		model.addAttribute("AndjKtPlatformaPlan", planDto.getAndjKtPlatformaPlanBiznes());
		model.addAttribute("AndjKtPoluvagonPlan", planDto.getAndjKtPoluvagonPlanBiznes());
		model.addAttribute("AndjKtSisternaPlan", planDto.getAndjKtSisternaPlanBiznes());
		model.addAttribute("AndjKtBoshqaPlan", planDto.getAndjKtBoshqaPlanBiznes());

		//kapital itogo
		model.addAttribute("KtHammaPlan", AndjKtHammaPlan + HavKtHammaPlan + SamKtHammaPlan);
		model.addAttribute("KtKritiPlan", planDto.getAndjKtKritiPlanBiznes() + planDto.getHavKtKritiPlanBiznes() + planDto.getSamKtKritiPlanBiznes());
		model.addAttribute("KtPlatformaPlan", planDto.getAndjKtPlatformaPlanBiznes() + planDto.getHavKtPlatformaPlanBiznes() + planDto.getSamKtPlatformaPlanBiznes());
		model.addAttribute("KtPoluvagonPlan",planDto.getAndjKtPoluvagonPlanBiznes() + planDto.getHavKtPoluvagonPlanBiznes() + planDto.getSamKtPoluvagonPlanBiznes());
		model.addAttribute("KtSisternaPlan", planDto.getAndjKtSisternaPlanBiznes() + planDto.getHavKtSisternaPlanBiznes() + planDto.getSamKtSisternaPlanBiznes());
		model.addAttribute("KtBoshqaPlan", planDto.getAndjKtBoshqaPlanBiznes() + planDto.getHavKtBoshqaPlanBiznes() + planDto.getSamKtBoshqaPlanBiznes());

		//samarqand KRP plan
		int SamKrpHammaPlan = planDto.getSamKrpKritiPlanBiznes() + planDto.getSamKrpPlatformaPlanBiznes() + planDto.getSamKrpPoluvagonPlanBiznes() + planDto.getSamKrpSisternaPlanBiznes() + planDto.getSamKrpBoshqaPlanBiznes();
		model.addAttribute("SamKrpHammaPlan", SamKrpHammaPlan);
		model.addAttribute("SamKrpKritiPlan", planDto.getSamKrpKritiPlanBiznes());
		model.addAttribute("SamKrpPlatformaPlan", planDto.getSamKrpPlatformaPlanBiznes());
		model.addAttribute("SamKrpPoluvagonPlan", planDto.getSamKrpPoluvagonPlanBiznes());
		model.addAttribute("SamKrpSisternaPlan", planDto.getSamKrpSisternaPlanBiznes());
		model.addAttribute("SamKrpBoshqaPlan", planDto.getSamKrpBoshqaPlanBiznes());

		//VCHD-3 KRP plan
		int HavKrpHammaPlan =  planDto.getHavKrpKritiPlanBiznes() + planDto.getHavKrpPlatformaPlanBiznes() + planDto.getHavKrpPoluvagonPlanBiznes() + planDto.getHavKrpSisternaPlanBiznes() + planDto.getHavKrpBoshqaPlanBiznes();
		model.addAttribute("HavKrpHammaPlan",HavKrpHammaPlan);
		model.addAttribute("HavKrpKritiPlan", planDto.getHavKrpKritiPlanBiznes());
		model.addAttribute("HavKrpPlatformaPlan", planDto.getHavKrpPlatformaPlanBiznes());
		model.addAttribute("HavKrpPoluvagonPlan", planDto.getHavKrpPoluvagonPlanBiznes());
		model.addAttribute("HavKrpSisternaPlan", planDto.getHavKrpSisternaPlanBiznes());
		model.addAttribute("HavKrpBoshqaPlan", planDto.getHavKrpBoshqaPlanBiznes());

		//VCHD-5 Krp plan
		int AndjKrpHammaPlan =  planDto.getAndjKrpKritiPlanBiznes() + planDto.getAndjKrpPlatformaPlanBiznes() + planDto.getAndjKrpPoluvagonPlanBiznes() + planDto.getAndjKrpSisternaPlanBiznes() + planDto.getAndjKrpBoshqaPlanBiznes();
		model.addAttribute("AndjKrpHammaPlan",AndjKrpHammaPlan);
		model.addAttribute("AndjKrpKritiPlan", planDto.getAndjKrpKritiPlanBiznes());
		model.addAttribute("AndjKrpPlatformaPlan", planDto.getAndjKrpPlatformaPlanBiznes());
		model.addAttribute("AndjKrpPoluvagonPlan", planDto.getAndjKrpPoluvagonPlanBiznes());
		model.addAttribute("AndjKrpSisternaPlan", planDto.getAndjKrpSisternaPlanBiznes());
		model.addAttribute("AndjKrpBoshqaPlan", planDto.getAndjKrpBoshqaPlanBiznes());

		//Krp itogo plan
		model.addAttribute("KrpHammaPlan", AndjKrpHammaPlan + HavKrpHammaPlan + SamKrpHammaPlan);
		model.addAttribute("KrpKritiPlan", planDto.getAndjKrpKritiPlanBiznes() + planDto.getHavKrpKritiPlanBiznes() + planDto.getSamKrpKritiPlanBiznes());
		model.addAttribute("KrpPlatformaPlan", planDto.getAndjKrpPlatformaPlanBiznes() + planDto.getHavKrpPlatformaPlanBiznes() + planDto.getSamKrpPlatformaPlanBiznes());
		model.addAttribute("KrpPoluvagonPlan",planDto.getAndjKrpPoluvagonPlanBiznes() + planDto.getHavKrpPoluvagonPlanBiznes() + planDto.getSamKrpPoluvagonPlanBiznes());
		model.addAttribute("KrpSisternaPlan", planDto.getAndjKrpSisternaPlanBiznes() + planDto.getHavKrpSisternaPlanBiznes() + planDto.getSamKrpSisternaPlanBiznes());
		model.addAttribute("KrpBoshqaPlan", planDto.getAndjKrpBoshqaPlanBiznes() + planDto.getHavKrpBoshqaPlanBiznes() + planDto.getSamKrpBoshqaPlanBiznes());


		// factlar 
		//samarqand uchun depli tamir
		int sdHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy);
		model.addAttribute("sdHamma",sdHamma);
		model.addAttribute("sdKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("sdPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("sdPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("sdSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("sdBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy));

		//VCHD-3 uchun depli tamir
		int hdHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy);
		model.addAttribute("hdHamma",hdHamma);
		model.addAttribute("hdKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("hdPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("hdPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("hdSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("hdBoshqaPlan", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy));

		//VCHD-5 uchun depli tamir
		int adHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Depoli ta’mir(ДР)", oy);
		model.addAttribute("adHamma",adHamma);
		model.addAttribute("adKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("adPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("adPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("adSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("adBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy));

		// itogo Fact uchun depli tamir
		int factdhamma = sdHamma + hdHamma + adHamma;
		model.addAttribute("factdhamma",factdhamma);
		int boshqaPlan = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy);
		model.addAttribute("boshqaPlan",boshqaPlan);

		//Yolovchi vagon Fact
		model.addAttribute("atYolovchi", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","TO-3", oy));
		model.addAttribute("adYolovchi", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","Depoli ta’mir(ДР)", oy));


		//samarqand uchun Kapital tamir
		int skHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy);
		model.addAttribute("skHamma",skHamma);
		model.addAttribute("skKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Kapital ta’mir(КР)", oy));
		model.addAttribute("skPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Kapital ta’mir(КР)", oy));
		model.addAttribute("skPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)", oy));
		model.addAttribute("skSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Kapital ta’mir(КР)", oy));
		model.addAttribute("skBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy));

		//VCHD-3 uchun kapital tamir
		int hkHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy);
		model.addAttribute("hkHamma",hkHamma);
		model.addAttribute("hkKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Kapital ta’mir(КР)", oy));
		model.addAttribute("hkPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Kapital ta’mir(КР)", oy));
		model.addAttribute("hkPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)", oy));
		model.addAttribute("hkSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Kapital ta’mir(КР)", oy));
		model.addAttribute("hkBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy));

		//VCHD-5 uchun kapital tamir
		int akHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy);
		model.addAttribute("akHamma",akHamma);
		model.addAttribute("akKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Kapital ta’mir(КР)", oy));
		model.addAttribute("akPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Kapital ta’mir(КР)", oy));
		model.addAttribute("akPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)", oy));
		model.addAttribute("akSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Kapital ta’mir(КР)", oy));
		model.addAttribute("akBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy));

		// itogo Fact uchun kapital tamir
		int factkhamma = skHamma + hkHamma + akHamma;
		model.addAttribute("factkhamma",factkhamma);
		int boshqaKPlan = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy);
		model.addAttribute("boshqaKPlan",boshqaKPlan);


		//samarqand uchun KRP tamir
		int skrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","KRP(КРП)", oy);
		model.addAttribute("skrHamma",skrHamma);
		model.addAttribute("skrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","KRP(КРП)", oy));
		model.addAttribute("skrPlatforma",vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","KRP(КРП)", oy));
		model.addAttribute("skrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","KRP(КРП)", oy));
		model.addAttribute("skrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","KRP(КРП)", oy));
		model.addAttribute("skrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","KRP(КРП)", oy));

		//VCHD-3 uchun KRP
		int hkrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","KRP(КРП)", oy);
		model.addAttribute("hkrHamma",hkrHamma);
		model.addAttribute("hkrKriti",  vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","KRP(КРП)", oy));
		model.addAttribute("hkrPlatforma",  vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","KRP(КРП)", oy));
		model.addAttribute("hkrPoluvagon",  vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","KRP(КРП)", oy));
		model.addAttribute("hkrSisterna",  vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","KRP(КРП)", oy));
		model.addAttribute("hkrBoshqa",  vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","KRP(КРП)", oy));

		//VCHD-5 uchun KRP
		int akrHamma =  vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","KRP(КРП)", oy);
		model.addAttribute("akrHamma",akrHamma);
		model.addAttribute("akrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","KRP(КРП)", oy));
		model.addAttribute("akrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","KRP(КРП)", oy));
		model.addAttribute("akrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","KRP(КРП)", oy));
		model.addAttribute("akrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","KRP(КРП)", oy));
		model.addAttribute("akrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","KRP(КРП)", oy));

		// itogo Fact uchun KRP
		int factkrhamma = skrHamma + hkrHamma + akrHamma;
		model.addAttribute("factkrhamma",factkrhamma);
		int boshqaKr = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","KRP(КРП)", oy);
		model.addAttribute("boshqaKr",boshqaKr);

    	 return "AllPlanTable";
	}



    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
   	@GetMapping("/vagons/planTableForMonths")
   	public String getPlanForAllMonths(Model model) {

		PlanBiznes planDto = vagonTayyorService.getPlanBiznes();
		//planlar kiritish

		//samarqand depo tamir plan
		int sdKriti = planDto.getSamDtKritiPlanBiznesMonths();
		int sdPlatforma=planDto.getSamDtPlatformaPlanBiznesMonths();
		int sdPoluvagon=planDto.getSamDtPoluvagonPlanBiznesMonths();
		int sdSisterna=planDto.getSamDtSisternaPlanBiznesMonths();
		int sdBoshqa=planDto.getSamDtBoshqaPlanBiznesMonths();
		int SamDtHammaPlan=sdKriti+sdPlatforma+sdPoluvagon+sdSisterna+sdBoshqa;

		model.addAttribute("SamDtHammaPlan",SamDtHammaPlan);
		model.addAttribute("SamDtKritiPlan", sdKriti);
		model.addAttribute("SamDtPlatformaPlan", sdPlatforma);
		model.addAttribute("SamDtPoluvagonPlan", sdPoluvagon);
		model.addAttribute("SamDtSisternaPlan", sdSisterna);
		model.addAttribute("SamDtBoshqaPlan", sdBoshqa);

		//havos depo tamir hamma plan
		int hdKriti = planDto.getHavDtKritiPlanBiznesMonths();
		int hdPlatforma=planDto.getHavDtPlatformaPlanBiznesMonths();
		int hdPoluvagon=planDto.getHavDtPoluvagonPlanBiznesMonths();
		int hdSisterna=planDto.getHavDtSisternaPlanBiznesMonths();
		int hdBoshqa=planDto.getHavDtBoshqaPlanBiznesMonths();
		int HavDtHammaPlan = hdKriti + hdPlatforma + hdPoluvagon + hdSisterna + hdBoshqa;

		model.addAttribute("HavDtHammaPlan", HavDtHammaPlan);
		model.addAttribute("HavDtKritiPlan", hdKriti);
		model.addAttribute("HavDtPlatformaPlan", hdPlatforma);
		model.addAttribute("HavDtPoluvagonPlan", hdPoluvagon);
		model.addAttribute("HavDtSisternaPlan", hdSisterna);
		model.addAttribute("HavDtBoshqaPlan", hdBoshqa);

		//VCHD-5 depo tamir plan
		int adKriti = planDto.getAndjDtKritiPlanBiznesMonths();
		int adPlatforma=planDto.getAndjDtPlatformaPlanBiznesMonths();
		int adPoluvagon=planDto.getAndjDtPoluvagonPlanBiznesMonths();
		int adSisterna=planDto.getAndjDtSisternaPlanBiznesMonths();
		int adBoshqa=planDto.getAndjDtBoshqaPlanBiznesMonths();
		int AndjDtHammaPlan = adKriti + adPlatforma + adPoluvagon + adSisterna + adBoshqa;

		model.addAttribute("AndjDtHammaPlan", AndjDtHammaPlan);
		model.addAttribute("AndjDtKritiPlan", adKriti);
		model.addAttribute("AndjDtPlatformaPlan",adPlatforma);
		model.addAttribute("AndjDtPoluvagonPlan", adPoluvagon);
		model.addAttribute("AndjDtSisternaPlan", adSisterna);
		model.addAttribute("AndjDtBoshqaPlan", adBoshqa);

		// Itogo planlar depo tamir
		model.addAttribute("DtHammaPlan", AndjDtHammaPlan + HavDtHammaPlan + SamDtHammaPlan);
		model.addAttribute("DtKritiPlan", sdKriti + hdKriti + adKriti);
		model.addAttribute("DtPlatformaPlan", sdPlatforma + hdPlatforma + adPlatforma);
		model.addAttribute("DtPoluvagonPlan",sdPoluvagon + hdPoluvagon + adPoluvagon);
		model.addAttribute("DtSisternaPlan", sdSisterna + hdSisterna + adSisterna);
		model.addAttribute("DtBoshqaPlan", sdBoshqa + hdBoshqa + adBoshqa);

		//Yolovchi vagon Plan
		int atYolovchi = planDto.getAndjTYolovchiPlanBiznesMonths();
		int adYolovchi = planDto.getAndjDtYolovchiPlanBiznesMonths();

		model.addAttribute("AndjToYolovchiPlan", atYolovchi);
		model.addAttribute("AndjDtYolovchiPlan", adYolovchi);

		//Samrqand kapital plan
		int skKriti = planDto.getSamKtKritiPlanBiznesMonths();
		int skPlatforma=planDto.getSamKtPlatformaPlanBiznesMonths();
		int skPoluvagon=planDto.getSamKtPoluvagonPlanBiznesMonths();
		int skSisterna=planDto.getSamKtSisternaPlanBiznesMonths();
		int skBoshqa=planDto.getSamKtBoshqaPlanBiznesMonths();
		int SamKtHammaPlan=skKriti+skPlatforma+skPoluvagon+skSisterna+skBoshqa;

		model.addAttribute("SamKtHammaPlan",SamKtHammaPlan);
		model.addAttribute("SamKtKritiPlan", skKriti);
		model.addAttribute("SamKtPlatformaPlan", skPlatforma);
		model.addAttribute("SamKtPoluvagonPlan", skPoluvagon);
		model.addAttribute("SamKtSisternaPlan", skSisterna);
		model.addAttribute("SamKtBoshqaPlan", skBoshqa);

		//hovos kapital plan
		int hkKriti = planDto.getHavKtKritiPlanBiznesMonths();
		int hkPlatforma=planDto.getHavKtPlatformaPlanBiznesMonths();
		int hkPoluvagon=planDto.getHavKtPoluvagonPlanBiznesMonths();
		int hkSisterna=planDto.getHavKtSisternaPlanBiznesMonths();
		int hkBoshqa=planDto.getHavKtBoshqaPlanBiznesMonths();
		int HavKtHammaPlan = hkKriti + hkPlatforma + hkPoluvagon + hkSisterna + hkBoshqa;

		model.addAttribute("HavKtHammaPlan", HavKtHammaPlan);
		model.addAttribute("HavKtKritiPlan", hkKriti);
		model.addAttribute("HavKtPlatformaPlan", hkPlatforma);
		model.addAttribute("HavKtPoluvagonPlan", hkPoluvagon);
		model.addAttribute("HavKtSisternaPlan", hkSisterna);
		model.addAttribute("HavKtBoshqaPlan", hkBoshqa);

		//ANDIJON kapital plan
		int akKriti = planDto.getAndjKtKritiPlanBiznesMonths();
		int akPlatforma=planDto.getAndjKtPlatformaPlanBiznesMonths();
		int akPoluvagon=planDto.getAndjKtPoluvagonPlanBiznesMonths();
		int akSisterna=planDto.getAndjKtSisternaPlanBiznesMonths();
		int akBoshqa=planDto.getAndjKtBoshqaPlanBiznesMonths();
		int AndjKtHammaPlan = akKriti + akPlatforma + akPoluvagon + akSisterna + akBoshqa;


		model.addAttribute("AndjKtHammaPlan", AndjKtHammaPlan);
		model.addAttribute("AndjKtKritiPlan", akKriti);
		model.addAttribute("AndjKtPlatformaPlan", akPlatforma);
		model.addAttribute("AndjKtPoluvagonPlan", akPoluvagon);
		model.addAttribute("AndjKtSisternaPlan", akSisterna);
		model.addAttribute("AndjKtBoshqaPlan", akBoshqa);

		//Itogo kapital plan
		model.addAttribute("KtHammaPlan", AndjKtHammaPlan + HavKtHammaPlan + SamKtHammaPlan);
		model.addAttribute("KtKritiPlan", skKriti + hkKriti + akKriti);
		model.addAttribute("KtPlatformaPlan", skPlatforma + hkPlatforma + akPlatforma);
		model.addAttribute("KtPoluvagonPlan",skPoluvagon + hkPoluvagon + akPoluvagon);
		model.addAttribute("KtSisternaPlan", skSisterna + hkSisterna + akSisterna);
		model.addAttribute("KtBoshqaPlan", skBoshqa + hkBoshqa + akBoshqa);

		//Samarqankr Krp plan
		int skrKriti = planDto.getSamKrpKritiPlanBiznesMonths();
		int skrPlatforma=planDto.getSamKrpPlatformaPlanBiznesMonths();
		int skrPoluvagon=planDto.getSamKrpPoluvagonPlanBiznesMonths();
		int skrSisterna=planDto.getSamKrpSisternaPlanBiznesMonths();
		int skrBoshqa=planDto.getSamKrpBoshqaPlanBiznesMonths();
		int SamKrpHammaPlan=skrKriti+skrPlatforma+skrPoluvagon+skrSisterna+skrBoshqa;

		model.addAttribute("SamKrpHammaPlan", SamKrpHammaPlan);
		model.addAttribute("SamKrpKritiPlan", skrKriti);
		model.addAttribute("SamKrpPlatformaPlan", skrPlatforma);
		model.addAttribute("SamKrpPoluvagonPlan", skrPoluvagon);
		model.addAttribute("SamKrpSisternaPlan", skrSisterna);
		model.addAttribute("SamKrpBoshqaPlan", skrBoshqa);

		//Hovos krp plan
		int hkrKriti = planDto.getHavKrpKritiPlanBiznesMonths();
		int hkrPlatforma=planDto.getHavKrpPlatformaPlanBiznesMonths();
		int hkrPoluvagon=planDto.getHavKrpPoluvagonPlanBiznesMonths();
		int hkrSisterna=planDto.getHavKrpSisternaPlanBiznesMonths();
		int hkrBoshqa=planDto.getHavKrpBoshqaPlanBiznesMonths();
		int HavKrpHammaPlan = hkrKriti + hkrPlatforma + hkrPoluvagon + hkrSisterna + hkrBoshqa;

		model.addAttribute("HavKrpHammaPlan",HavKrpHammaPlan);
		model.addAttribute("HavKrpKritiPlan", hkrKriti);
		model.addAttribute("HavKrpPlatformaPlan", hkrPlatforma);
		model.addAttribute("HavKrpPoluvagonPlan", hkrPoluvagon);
		model.addAttribute("HavKrpSisternaPlan", hkrSisterna);
		model.addAttribute("HavKrpBoshqaPlan", hkrBoshqa);

		//andijon krp plan
		int akrKriti = planDto.getAndjKrpKritiPlanBiznesMonths();
		int akrPlatforma=planDto.getAndjKrpPlatformaPlanBiznesMonths();
		int akrPoluvagon=planDto.getAndjKrpPoluvagonPlanBiznesMonths();
		int akrSisterna=planDto.getAndjKrpSisternaPlanBiznesMonths();
		int akrBoshqa=planDto.getAndjKrpBoshqaPlanBiznesMonths();
		int AndjKrpHammaPlan = akrKriti + akrPlatforma + akrPoluvagon + akrSisterna + akrBoshqa;

		model.addAttribute("AndjKrpHammaPlan",AndjKrpHammaPlan);
		model.addAttribute("AndjKrpKritiPlan", akrKriti);
		model.addAttribute("AndjKrpPlatformaPlan", akrPlatforma);
		model.addAttribute("AndjKrpPoluvagonPlan", akrPoluvagon);
		model.addAttribute("AndjKrpSisternaPlan", akrSisterna);
		model.addAttribute("AndjKrpBoshqaPlan", akrBoshqa);

		//itogo krp
		model.addAttribute("KrpHammaPlan", AndjKrpHammaPlan + HavKrpHammaPlan + SamKrpHammaPlan);
		model.addAttribute("KrpKritiPlan", skrKriti + hkrKriti + akrKriti);
		model.addAttribute("KrpPlatformaPlan", skrPlatforma + hkrPlatforma + akrPlatforma);
		model.addAttribute("KrpPoluvagonPlan",akrPoluvagon + hkrPoluvagon + skrPoluvagon);
		model.addAttribute("KrpSisternaPlan", skrSisterna + hkrSisterna + akrSisterna);
		model.addAttribute("KrpBoshqaPlan", skrBoshqa + hkrBoshqa + akrBoshqa);

		//**//
		// samarqand depo tamir hamma false vagonlar soni
		int sdKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Depoli ta’mir(ДР)");
		int sdPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Depoli ta’mir(ДР)");
		int sdPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)");
		int sdSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Depoli ta’mir(ДР)");
		int sdBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Depoli ta’mir(ДР)");
		int sdHammaFalse = sdKritiFalse + sdPlatformaFalse+ sdPoluvagonFalse+ sdSisternaFalse + sdBoshqaFalse;

		model.addAttribute("sdHammaFalse",sdHammaFalse+393);
		model.addAttribute("sdKritiFalse",sdKritiFalse+135);
		model.addAttribute("sdPlatformaFalse",sdPlatformaFalse+8);
		model.addAttribute("sdPoluvagonFalse",sdPoluvagonFalse+67);
		model.addAttribute("sdSisternaFalse",sdSisternaFalse+23);
		model.addAttribute("sdBoshqaFalse",sdBoshqaFalse+160);

		// VCHD-3 depo tamir hamma false vagonlar soni
		int hdKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Depoli ta’mir(ДР)");
		int hdPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Depoli ta’mir(ДР)");
		int hdPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)");
		int hdSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Depoli ta’mir(ДР)");
		int hdBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Depoli ta’mir(ДР)");
		int hdHammaFalse = hdKritiFalse + hdPlatformaFalse+ hdPoluvagonFalse+ hdSisternaFalse + hdBoshqaFalse;

		model.addAttribute("hdHammaFalse",hdHammaFalse+651);
		model.addAttribute("hdKritiFalse",hdKritiFalse+35);
		model.addAttribute("hdPlatformaFalse",hdPlatformaFalse+45);
		model.addAttribute("hdPoluvagonFalse",hdPoluvagonFalse+109);
		model.addAttribute("hdSisternaFalse",hdSisternaFalse+35);
		model.addAttribute("hdBoshqaFalse",hdBoshqaFalse+427);

		// VCHD-5 depo tamir hamma false vagonlar soni
		int adKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Depoli ta’mir(ДР)");
		int adPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Depoli ta’mir(ДР)");
		int adPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)");
		int adSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Depoli ta’mir(ДР)");
		int adBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Depoli ta’mir(ДР)");
		int adHammaFalse = adKritiFalse + adPlatformaFalse+ adPoluvagonFalse+ adSisternaFalse + adBoshqaFalse;

		model.addAttribute("adHammaFalse",adHammaFalse+443);
		model.addAttribute("adKritiFalse",adKritiFalse+224);
		model.addAttribute("adPlatformaFalse",adPlatformaFalse+3);
		model.addAttribute("adPoluvagonFalse",adPoluvagonFalse+103);
		model.addAttribute("adSisternaFalse",adSisternaFalse);
		model.addAttribute("adBoshqaFalse",adBoshqaFalse+113);

		// depoli tamir itogo uchun
		int dHammaFalse =  adHammaFalse + hdHammaFalse+sdHammaFalse;
		int dKritiFalse = sdKritiFalse + hdKritiFalse + adKritiFalse;
		int dPlatforma = adPlatformaFalse + sdPlatformaFalse + hdPlatformaFalse;
		int dPoluvagon  = adPoluvagonFalse + sdPoluvagonFalse + hdPoluvagonFalse;
		int dSisterna = adSisternaFalse + hdSisternaFalse + sdSisternaFalse;
		int dBoshqa = adBoshqaFalse + hdBoshqaFalse + sdBoshqaFalse;

		model.addAttribute("dHammaFalse",dHammaFalse+1487);
		model.addAttribute("dKritiFalse",dKritiFalse+394);
		model.addAttribute("dPlatforma",dPlatforma+56);
		model.addAttribute("dPoluvagon",dPoluvagon+279);
		model.addAttribute("dSisterna",dSisterna+58);
		model.addAttribute("dBoshqa",dBoshqa+700);

		//Yolovchi Andijon fact
		int atYolovchiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","TO-3");
		int adYolovchiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","Depoli ta’mir(ДР)");

		model.addAttribute("atYolovchi",atYolovchiFalse + 37);
		model.addAttribute("adYolovchi",adYolovchiFalse + 24);

		// samarqand KApital tamir hamma false vagonlar soni
		int skKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Kapital ta’mir(КР)");
		int skPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Kapital ta’mir(КР)");
		int skPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)");
		int skSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Kapital ta’mir(КР)");
		int skBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Kapital ta’mir(КР)");
		int skHammaFalse = skKritiFalse + skPlatformaFalse+ skPoluvagonFalse+ skSisternaFalse + skBoshqaFalse;

		model.addAttribute("skHammaFalse",skHammaFalse+284);
		model.addAttribute("skKritiFalse",skKritiFalse+160);
		model.addAttribute("skPlatformaFalse",skPlatformaFalse+44);
		model.addAttribute("skPoluvagonFalse",skPoluvagonFalse+52);
		model.addAttribute("skSisternaFalse",skSisternaFalse+9);
		model.addAttribute("skBoshqaFalse",skBoshqaFalse+19);

		// VCHD-3 kapital tamir hamma false vagonlar soni
		int hkKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Kapital ta’mir(КР)");
		int hkPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Kapital ta’mir(КР)");
		int hkPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)");
		int hkSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Kapital ta’mir(КР)");
		int hkBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Kapital ta’mir(КР)");
		int hkHammaFalse = hkKritiFalse + hkPlatformaFalse+ hkPoluvagonFalse+ hkSisternaFalse + hkBoshqaFalse;

		model.addAttribute("hkHammaFalse",hkHammaFalse+227);
		model.addAttribute("hkKritiFalse",hkKritiFalse+41);
		model.addAttribute("hkPlatformaFalse",hkPlatformaFalse+32);
		model.addAttribute("hkPoluvagonFalse",hkPoluvagonFalse+4);
		model.addAttribute("hkSisternaFalse",hkSisternaFalse+51);
		model.addAttribute("hkBoshqaFalse",hkBoshqaFalse+99);

		// VCHD-5 kapital tamir hamma false vagonlar soni
		int akKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Kapital ta’mir(КР)");
		int akPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Kapital ta’mir(КР)");
		int akPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)");
		int akSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Kapital ta’mir(КР)");
		int akBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Kapital ta’mir(КР)");
		int akHammaFalse = akKritiFalse + akPlatformaFalse+ akPoluvagonFalse+ akSisternaFalse + akBoshqaFalse;

		model.addAttribute("akHammaFalse",akHammaFalse+28);
		model.addAttribute("akKritiFalse",akKritiFalse+26);
		model.addAttribute("akPlatformaFalse",akPlatformaFalse);
		model.addAttribute("akPoluvagonFalse",akPoluvagonFalse+2);
		model.addAttribute("akSisternaFalse",akSisternaFalse);
		model.addAttribute("akBoshqaFalse",akBoshqaFalse);

		// Kapital tamir itogo uchun
		int kHammaFalse =  akHammaFalse + hkHammaFalse+skHammaFalse;
		int kKritiFalse = skKritiFalse + hkKritiFalse + akKritiFalse;
		int kPlatforma = akPlatformaFalse + skPlatformaFalse + hkPlatformaFalse;
		int kPoluvagon  = akPoluvagonFalse + skPoluvagonFalse + hkPoluvagonFalse;
		int kSisterna = akSisternaFalse + hkSisternaFalse + skSisternaFalse;
		int kBoshqa = akBoshqaFalse + hkBoshqaFalse + skBoshqaFalse;

		model.addAttribute("kHammaFalse",kHammaFalse+539);
		model.addAttribute("kKritiFalse",kKritiFalse+227);
		model.addAttribute("kPlatforma",kPlatforma+76);
		model.addAttribute("kPoluvagon",kPoluvagon+58);
		model.addAttribute("kSisterna",kSisterna+60);
		model.addAttribute("kBoshqa",kBoshqa+118);

		//**
		// samarqand KRP tamir hamma false vagonlar soni
		int skrKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","KRP(КРП)");
		int skrPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","KRP(КРП)");
		int skrPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","KRP(КРП)");
		int skrSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","KRP(КРП)");
		int skrBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","KRP(КРП)");
		int skrHammaFalse = skrKritiFalse + skrPlatformaFalse+ skrPoluvagonFalse+ skrSisternaFalse + skrBoshqaFalse;

		model.addAttribute("skrHammaFalse",skrHammaFalse+89);
		model.addAttribute("skrKritiFalse",skrKritiFalse);
		model.addAttribute("skrPlatformaFalse",skrPlatformaFalse);
		model.addAttribute("skrPoluvagonFalse",skrPoluvagonFalse+88);
		model.addAttribute("skrSisternaFalse",skrSisternaFalse+1);
		model.addAttribute("skrBoshqaFalse",skrBoshqaFalse);

		// VCHD-3 KRP tamir hamma false vagonlar soni
		int hkrKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","KRP(КРП)");
		int hkrPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","KRP(КРП)");
		int hkrPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","KRP(КРП)");
		int hkrSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","KRP(КРП)");
		int hkrBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","KRP(КРП)");
		int hkrHammaFalse = hkrKritiFalse + hkrPlatformaFalse+ hkrPoluvagonFalse+ hkrSisternaFalse + hkrBoshqaFalse;

		model.addAttribute("hkrHammaFalse",hkrHammaFalse+83);
		model.addAttribute("hkrKritiFalse",hkrKritiFalse+83);
		model.addAttribute("hkrPlatformaFalse",hkrPlatformaFalse);
		model.addAttribute("hkrPoluvagonFalse",hkrPoluvagonFalse);
		model.addAttribute("hkrSisternaFalse",hkrSisternaFalse);
		model.addAttribute("hkrBoshqaFalse",hkrBoshqaFalse);

		// VCHD-5 KRP tamir hamma false vagonlar soni
		int akrKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","KRP(КРП)");
		int akrPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","KRP(КРП)");
		int akrPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","KRP(КРП)");
		int akrSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","KRP(КРП)");
		int akrBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","KRP(КРП)");
		int akrHammaFalse = akrKritiFalse + akrPlatformaFalse+ akrPoluvagonFalse+ akrSisternaFalse + akrBoshqaFalse;

		model.addAttribute("akrHammaFalse",akrHammaFalse+61);
		model.addAttribute("akrKritiFalse",akrKritiFalse);
		model.addAttribute("akrPlatformaFalse",akrPlatformaFalse);
		model.addAttribute("akrPoluvagonFalse",akrPoluvagonFalse+61);
		model.addAttribute("akrSisternaFalse",akrSisternaFalse);
		model.addAttribute("akBoshqaFalse",akBoshqaFalse);

		// Krp itogo uchun
		int krHammaFalse =  akrHammaFalse + hkrHammaFalse+skrHammaFalse;
		int krKritiFalse = skrKritiFalse + hkrKritiFalse + akrKritiFalse;
		int krPlatforma = akrPlatformaFalse + skrPlatformaFalse + hkrPlatformaFalse;
		int krPoluvagon  = akrPoluvagonFalse + skrPoluvagonFalse + hkrPoluvagonFalse;
		int krSisterna = akrSisternaFalse + hkrSisternaFalse + skrSisternaFalse;
		int krBoshqa = akrBoshqaFalse + hkrBoshqaFalse + skrBoshqaFalse;

		model.addAttribute("krHammaFalse",krHammaFalse+233);
		model.addAttribute("krKritiFalse",krKritiFalse);
		model.addAttribute("krPlatforma",krPlatforma);
		model.addAttribute("krPoluvagon",krPoluvagon+232);
		model.addAttribute("krSisterna",krSisterna+1);
		model.addAttribute("krBoshqa",krBoshqa);

		// hamma false vagonlarni list qilib chiqarish
		vagonsToDownload = vagonTayyorService.findAll();
		model.addAttribute("vagons", vagonTayyorService.findAll());

    	return "planTableForMonths";
    }

    // wagon nomer orqali qidirish 1 oylida
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/searchTayyor")
	public String searchByNomer(Model model,  @RequestParam(value = "participant", required = false) Integer participant) {

		String oy=null;
		switch (month) {
			case 1:
				oy = "-01";
				break;
			case 2:
				oy = "-02";
				break;
			case 3:
				oy = "-03";
				break;
			case 4:
				oy = "-04";
				break;
			case 5:
				oy = "-05";
				break;
			case 6:
				oy = "-06";
				break;
			case 7:
				oy = "-07";
				break;
			case 8:
				oy = "-08";
				break;
			case 9:
				oy = "-09";
				break;
			case 10:
				oy = "-10";
				break;
			case 11:
				oy = "-11";
				break;
			case 12:
				oy = "-12";
				break;
		}

		if(participant==null  ) {
			model.addAttribute("vagons", vagonTayyorService.findAll(oy));
			vagonsToDownload = vagonTayyorService.findAll(oy);
		}else {
			model.addAttribute("vagons", vagonTayyorService.searchByNomer(participant, oy));
			List<VagonTayyor> emptyList = new ArrayList<>();
			vagonsToDownload = emptyList;
			vagonsToDownload.add( vagonTayyorService.searchByNomer(participant, oy));
		}
		//vaqtni olib turadi
		model.addAttribute("samDate",vagonTayyorService.getSamDate());
		model.addAttribute("havDate", vagonTayyorService.getHavDate());
		model.addAttribute("andjDate",vagonTayyorService.getAndjDate());
		PlanBiznes planDto = vagonTayyorService.getPlanBiznes();

		//planlar kiritish
		//samarqand depo tamir
		int SamDtHammaPlan=planDto.getSamDtKritiPlanBiznes() + planDto.getSamDtPlatformaPlanBiznes() + planDto.getSamDtPoluvagonPlanBiznes() + planDto.getSamDtSisternaPlanBiznes() + planDto.getSamDtBoshqaPlanBiznes();
		model.addAttribute("SamDtHammaPlan",SamDtHammaPlan);
		model.addAttribute("SamDtKritiPlan", planDto.getSamDtKritiPlanBiznes());
		model.addAttribute("SamDtPlatformaPlan", planDto.getSamDtPlatformaPlanBiznes());
		model.addAttribute("SamDtPoluvagonPlan", planDto.getSamDtPoluvagonPlanBiznes());
		model.addAttribute("SamDtSisternaPlan", planDto.getSamDtSisternaPlanBiznes());
		model.addAttribute("SamDtBoshqaPlan", planDto.getSamDtBoshqaPlanBiznes());

		//havos hamma plan
		int HavDtHammaPlan = planDto.getHavDtKritiPlanBiznes() + planDto.getHavDtPlatformaPlanBiznes() + planDto.getHavDtPoluvagonPlanBiznes() + planDto.getHavDtSisternaPlanBiznes() + planDto.getHavDtBoshqaPlanBiznes();
		model.addAttribute("HavDtHammaPlan", HavDtHammaPlan);
		model.addAttribute("HavDtKritiPlan", planDto.getHavDtKritiPlanBiznes());
		model.addAttribute("HavDtPlatformaPlan", planDto.getHavDtPlatformaPlanBiznes());
		model.addAttribute("HavDtPoluvagonPlan", planDto.getHavDtPoluvagonPlanBiznes());
		model.addAttribute("HavDtSisternaPlan", planDto.getHavDtSisternaPlanBiznes());
		model.addAttribute("HavDtBoshqaPlan", planDto.getHavDtBoshqaPlanBiznes());

		//andijon hamma plan depo tamir
		int AndjDtHammaPlan = planDto.getAndjDtKritiPlanBiznes() + planDto.getAndjDtPlatformaPlanBiznes() + planDto.getAndjDtPoluvagonPlanBiznes() + planDto.getAndjDtSisternaPlanBiznes() + planDto.getAndjDtBoshqaPlanBiznes();
		model.addAttribute("AndjDtHammaPlan", AndjDtHammaPlan);
		model.addAttribute("AndjDtKritiPlan", planDto.getAndjDtKritiPlanBiznes());
		model.addAttribute("AndjDtPlatformaPlan", planDto.getAndjDtPlatformaPlanBiznes());
		model.addAttribute("AndjDtPoluvagonPlan", planDto.getAndjDtPoluvagonPlanBiznes());
		model.addAttribute("AndjDtSisternaPlan", planDto.getAndjDtSisternaPlanBiznes());
		model.addAttribute("AndjDtBoshqaPlan", planDto.getAndjDtBoshqaPlanBiznes());

		// Itogo planlar depo tamir
		model.addAttribute("DtHammaPlan", AndjDtHammaPlan + HavDtHammaPlan + SamDtHammaPlan);
		model.addAttribute("DtKritiPlan", planDto.getAndjDtKritiPlanBiznes() + planDto.getHavDtKritiPlanBiznes() + planDto.getSamDtKritiPlanBiznes());
		model.addAttribute("DtPlatformaPlan", planDto.getAndjDtPlatformaPlanBiznes() + planDto.getHavDtPlatformaPlanBiznes() + planDto.getSamDtPlatformaPlanBiznes());
		model.addAttribute("DtPoluvagonPlan",planDto.getAndjDtPoluvagonPlanBiznes() + planDto.getHavDtPoluvagonPlanBiznes() + planDto.getSamDtPoluvagonPlanBiznes());
		model.addAttribute("DtSisternaPlan", planDto.getAndjDtSisternaPlanBiznes() + planDto.getHavDtSisternaPlanBiznes() + planDto.getSamDtSisternaPlanBiznes());
		model.addAttribute("DtBoshqaPlan", planDto.getAndjDtBoshqaPlanBiznes() + planDto.getHavDtBoshqaPlanBiznes() + planDto.getSamDtBoshqaPlanBiznes());

		//yolovchi vagonlar plan
		model.addAttribute("AndjToYolovchiPlan", planDto.getAndjTYolovchiPlanBiznes());
		model.addAttribute("AndjDtYolovchiPlan", planDto.getAndjDtYolovchiPlanBiznes());

		//VCHD-6 kapital tamir uchun plan
		int SamKtHammaPlan =  planDto.getSamKtKritiPlanBiznes() + planDto.getSamKtPlatformaPlanBiznes() + planDto.getSamKtPoluvagonPlanBiznes() + planDto.getSamKtSisternaPlanBiznes() + planDto.getSamKtBoshqaPlanBiznes();
		model.addAttribute("SamKtHammaPlan",SamKtHammaPlan);
		model.addAttribute("SamKtKritiPlan", planDto.getSamKtKritiPlanBiznes());
		model.addAttribute("SamKtPlatformaPlan", planDto.getSamKtPlatformaPlanBiznes());
		model.addAttribute("SamKtPoluvagonPlan", planDto.getSamKtPoluvagonPlanBiznes());
		model.addAttribute("SamKtSisternaPlan", planDto.getSamKtSisternaPlanBiznes());
		model.addAttribute("SamKtBoshqaPlan", planDto.getSamKtBoshqaPlanBiznes());

		//havos kapital tamir uchun plan
		int HavKtHammaPlan = planDto.getHavKtKritiPlanBiznes() + planDto.getHavKtPlatformaPlanBiznes() + planDto.getHavKtPoluvagonPlanBiznes() + planDto.getHavKtSisternaPlanBiznes() + planDto.getHavKtBoshqaPlanBiznes();
		model.addAttribute("HavKtHammaPlan", HavKtHammaPlan);
		model.addAttribute("HavKtKritiPlan", planDto.getHavKtKritiPlanBiznes());
		model.addAttribute("HavKtPlatformaPlan", planDto.getHavKtPlatformaPlanBiznes());
		model.addAttribute("HavKtPoluvagonPlan", planDto.getHavKtPoluvagonPlanBiznes());
		model.addAttribute("HavKtSisternaPlan", planDto.getHavKtSisternaPlanBiznes());
		model.addAttribute("HavKtBoshqaPlan", planDto.getHavKtBoshqaPlanBiznes());

		//VCHD-5 kapital tamir uchun plan
		int AndjKtHammaPlan = planDto.getAndjKtKritiPlanBiznes() + planDto.getAndjKtPlatformaPlanBiznes() + planDto.getAndjKtPoluvagonPlanBiznes() + planDto.getAndjKtSisternaPlanBiznes() + planDto.getAndjKtBoshqaPlanBiznes();
		model.addAttribute("AndjKtHammaPlan", AndjKtHammaPlan);
		model.addAttribute("AndjKtKritiPlan", planDto.getAndjKtKritiPlanBiznes());
		model.addAttribute("AndjKtPlatformaPlan", planDto.getAndjKtPlatformaPlanBiznes());
		model.addAttribute("AndjKtPoluvagonPlan", planDto.getAndjKtPoluvagonPlanBiznes());
		model.addAttribute("AndjKtSisternaPlan", planDto.getAndjKtSisternaPlanBiznes());
		model.addAttribute("AndjKtBoshqaPlan", planDto.getAndjKtBoshqaPlanBiznes());

		//kapital itogo
		model.addAttribute("KtHammaPlan", AndjKtHammaPlan + HavKtHammaPlan + SamKtHammaPlan);
		model.addAttribute("KtKritiPlan", planDto.getAndjKtKritiPlanBiznes() + planDto.getHavKtKritiPlanBiznes() + planDto.getSamKtKritiPlanBiznes());
		model.addAttribute("KtPlatformaPlan", planDto.getAndjKtPlatformaPlanBiznes() + planDto.getHavKtPlatformaPlanBiznes() + planDto.getSamKtPlatformaPlanBiznes());
		model.addAttribute("KtPoluvagonPlan",planDto.getAndjKtPoluvagonPlanBiznes() + planDto.getHavKtPoluvagonPlanBiznes() + planDto.getSamKtPoluvagonPlanBiznes());
		model.addAttribute("KtSisternaPlan", planDto.getAndjKtSisternaPlanBiznes() + planDto.getHavKtSisternaPlanBiznes() + planDto.getSamKtSisternaPlanBiznes());
		model.addAttribute("KtBoshqaPlan", planDto.getAndjKtBoshqaPlanBiznes() + planDto.getHavKtBoshqaPlanBiznes() + planDto.getSamKtBoshqaPlanBiznes());

		//samarqand KRP plan
		int SamKrpHammaPlan = planDto.getSamKrpKritiPlanBiznes() + planDto.getSamKrpPlatformaPlanBiznes() + planDto.getSamKrpPoluvagonPlanBiznes() + planDto.getSamKrpSisternaPlanBiznes() + planDto.getSamKrpBoshqaPlanBiznes();
		model.addAttribute("SamKrpHammaPlan", SamKrpHammaPlan);
		model.addAttribute("SamKrpKritiPlan", planDto.getSamKrpKritiPlanBiznes());
		model.addAttribute("SamKrpPlatformaPlan", planDto.getSamKrpPlatformaPlanBiznes());
		model.addAttribute("SamKrpPoluvagonPlan", planDto.getSamKrpPoluvagonPlanBiznes());
		model.addAttribute("SamKrpSisternaPlan", planDto.getSamKrpSisternaPlanBiznes());
		model.addAttribute("SamKrpBoshqaPlan", planDto.getSamKrpBoshqaPlanBiznes());

		//VCHD-3 KRP plan
		int HavKrpHammaPlan =  planDto.getHavKrpKritiPlanBiznes() + planDto.getHavKrpPlatformaPlanBiznes() + planDto.getHavKrpPoluvagonPlanBiznes() + planDto.getHavKrpSisternaPlanBiznes() + planDto.getHavKrpBoshqaPlanBiznes();
		model.addAttribute("HavKrpHammaPlan",HavKrpHammaPlan);
		model.addAttribute("HavKrpKritiPlan", planDto.getHavKrpKritiPlanBiznes());
		model.addAttribute("HavKrpPlatformaPlan", planDto.getHavKrpPlatformaPlanBiznes());
		model.addAttribute("HavKrpPoluvagonPlan", planDto.getHavKrpPoluvagonPlanBiznes());
		model.addAttribute("HavKrpSisternaPlan", planDto.getHavKrpSisternaPlanBiznes());
		model.addAttribute("HavKrpBoshqaPlan", planDto.getHavKrpBoshqaPlanBiznes());

		//VCHD-5 Krp plan
		int AndjKrpHammaPlan =  planDto.getAndjKrpKritiPlanBiznes() + planDto.getAndjKrpPlatformaPlanBiznes() + planDto.getAndjKrpPoluvagonPlanBiznes() + planDto.getAndjKrpSisternaPlanBiznes() + planDto.getAndjKrpBoshqaPlanBiznes();
		model.addAttribute("AndjKrpHammaPlan",AndjKrpHammaPlan);
		model.addAttribute("AndjKrpKritiPlan", planDto.getAndjKrpKritiPlanBiznes());
		model.addAttribute("AndjKrpPlatformaPlan", planDto.getAndjKrpPlatformaPlanBiznes());
		model.addAttribute("AndjKrpPoluvagonPlan", planDto.getAndjKrpPoluvagonPlanBiznes());
		model.addAttribute("AndjKrpSisternaPlan", planDto.getAndjKrpSisternaPlanBiznes());
		model.addAttribute("AndjKrpBoshqaPlan", planDto.getAndjKrpBoshqaPlanBiznes());

		//Krp itogo plan
		model.addAttribute("KrpHammaPlan", AndjKrpHammaPlan + HavKrpHammaPlan + SamKrpHammaPlan);
		model.addAttribute("KrpKritiPlan", planDto.getAndjKrpKritiPlanBiznes() + planDto.getHavKrpKritiPlanBiznes() + planDto.getSamKrpKritiPlanBiznes());
		model.addAttribute("KrpPlatformaPlan", planDto.getAndjKrpPlatformaPlanBiznes() + planDto.getHavKrpPlatformaPlanBiznes() + planDto.getSamKrpPlatformaPlanBiznes());
		model.addAttribute("KrpPoluvagonPlan",planDto.getAndjKrpPoluvagonPlanBiznes() + planDto.getHavKrpPoluvagonPlanBiznes() + planDto.getSamKrpPoluvagonPlanBiznes());
		model.addAttribute("KrpSisternaPlan", planDto.getAndjKrpSisternaPlanBiznes() + planDto.getHavKrpSisternaPlanBiznes() + planDto.getSamKrpSisternaPlanBiznes());
		model.addAttribute("KrpBoshqaPlan", planDto.getAndjKrpBoshqaPlanBiznes() + planDto.getHavKrpBoshqaPlanBiznes() + planDto.getSamKrpBoshqaPlanBiznes());


		// factlar
		//samarqand uchun depli tamir
		int sdHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy);
		model.addAttribute("sdHamma",sdHamma);
		model.addAttribute("sdKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("sdPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("sdPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("sdSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("sdBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy));

		//VCHD-3 uchun depli tamir
		int hdHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy);
		model.addAttribute("hdHamma",hdHamma);
		model.addAttribute("hdKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("hdPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("hdPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("hdSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("hdBoshqaPlan", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy));

		//VCHD-5 uchun depli tamir
		int adHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Depoli ta’mir(ДР)", oy);
		model.addAttribute("adHamma",adHamma);
		model.addAttribute("adKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("adPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("adPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("adSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Depoli ta’mir(ДР)", oy));
		model.addAttribute("adBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy));

		// itogo Fact uchun depli tamir
		int factdhamma = sdHamma + hdHamma + adHamma;
		model.addAttribute("factdhamma",factdhamma);
		int boshqaPlan = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Depoli ta’mir(ДР)", oy);
		model.addAttribute("boshqaPlan",boshqaPlan);

		//Yolovchi vagon Fact
		model.addAttribute("atYolovchi", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","TO-3", oy));
		model.addAttribute("adYolovchi", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","Depoli ta’mir(ДР)", oy));

		//samarqand uchun Kapital tamir
		int skHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy);
		model.addAttribute("skHamma",skHamma);
		model.addAttribute("skKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Kapital ta’mir(КР)", oy));
		model.addAttribute("skPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Kapital ta’mir(КР)", oy));
		model.addAttribute("skPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)", oy));
		model.addAttribute("skSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Kapital ta’mir(КР)", oy));
		model.addAttribute("skBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy));

		//VCHD-3 uchun kapital tamir
		int hkHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy);
		model.addAttribute("hkHamma",hkHamma);
		model.addAttribute("hkKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Kapital ta’mir(КР)", oy));
		model.addAttribute("hkPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Kapital ta’mir(КР)", oy));
		model.addAttribute("hkPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)", oy));
		model.addAttribute("hkSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Kapital ta’mir(КР)", oy));
		model.addAttribute("hkBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy));

		//VCHD-5 uchun kapital tamir
		int akHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy);
		model.addAttribute("akHamma",akHamma);
		model.addAttribute("akKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Kapital ta’mir(КР)", oy));
		model.addAttribute("akPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Kapital ta’mir(КР)", oy));
		model.addAttribute("akPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)", oy));
		model.addAttribute("akSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Kapital ta’mir(КР)", oy));
		model.addAttribute("akBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy));

		// itogo Fact uchun kapital tamir
		int factkhamma = skHamma + hkHamma + akHamma;
		model.addAttribute("factkhamma",factkhamma);
		int boshqaKPlan = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Kapital ta’mir(КР)", oy);
		model.addAttribute("boshqaKPlan",boshqaKPlan);


		//samarqand uchun KRP tamir
		int skrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","KRP(КРП)", oy);
		model.addAttribute("skrHamma",skrHamma);
		model.addAttribute("skrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","KRP(КРП)", oy));
		model.addAttribute("skrPlatforma",vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","KRP(КРП)", oy));
		model.addAttribute("skrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","KRP(КРП)", oy));
		model.addAttribute("skrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","KRP(КРП)", oy));
		model.addAttribute("skrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","KRP(КРП)", oy));

		//VCHD-3 uchun KRP
		int hkrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","KRP(КРП)", oy);
		model.addAttribute("hkrHamma",hkrHamma);
		model.addAttribute("hkrKriti",  vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","KRP(КРП)", oy));
		model.addAttribute("hkrPlatforma",  vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","KRP(КРП)", oy));
		model.addAttribute("hkrPoluvagon",  vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","KRP(КРП)", oy));
		model.addAttribute("hkrSisterna",  vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","KRP(КРП)", oy));
		model.addAttribute("hkrBoshqa",  vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","KRP(КРП)", oy));

		//VCHD-5 uchun KRP
		int akrHamma =  vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","KRP(КРП)", oy);
		model.addAttribute("akrHamma",akrHamma);
		model.addAttribute("akrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","KRP(КРП)", oy));
		model.addAttribute("akrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","KRP(КРП)", oy));
		model.addAttribute("akrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","KRP(КРП)", oy));
		model.addAttribute("akrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","KRP(КРП)", oy));
		model.addAttribute("akrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","KRP(КРП)", oy));

		// itogo Fact uchun KRP
		int factkrhamma = skrHamma + hkrHamma + akrHamma;
		model.addAttribute("factkrhamma",factkrhamma);
		int boshqaKr = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","KRP(КРП)", oy) +
				vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","KRP(КРП)", oy);
		model.addAttribute("boshqaKr",boshqaKr);

		return "AllPlanTable";
    }

    // wagon nomer orqali qidirish shu oygacha hammasidan
    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
	@GetMapping("/vagons/searchTayyorAllMonths")
	public String search(Model model,  @RequestParam(value = "participant", required = false) Integer participant) {
		if(participant==null  ) {
			model.addAttribute("vagons", vagonTayyorService.findAll());
			vagonsToDownload = vagonTayyorService.findAll();
		}else {
			model.addAttribute("vagons", vagonTayyorService.findByNomer(participant));
			List<VagonTayyor> emptyList = new ArrayList<>();
			vagonsToDownload = emptyList;
			vagonsToDownload.add( vagonTayyorService.findByNomer(participant));
		}
		PlanBiznes planDto = vagonTayyorService.getPlanBiznes();
		//planlar kiritish

		//samarqand depo tamir plan
		int sdKriti = planDto.getSamDtKritiPlanBiznesMonths();
		int sdPlatforma=planDto.getSamDtPlatformaPlanBiznesMonths();
		int sdPoluvagon=planDto.getSamDtPoluvagonPlanBiznesMonths();
		int sdSisterna=planDto.getSamDtSisternaPlanBiznesMonths();
		int sdBoshqa=planDto.getSamDtBoshqaPlanBiznesMonths();
		int SamDtHammaPlan=sdKriti+sdPlatforma+sdPoluvagon+sdSisterna+sdBoshqa;

		model.addAttribute("SamDtHammaPlan",SamDtHammaPlan);
		model.addAttribute("SamDtKritiPlan", sdKriti);
		model.addAttribute("SamDtPlatformaPlan", sdPlatforma);
		model.addAttribute("SamDtPoluvagonPlan", sdPoluvagon);
		model.addAttribute("SamDtSisternaPlan", sdSisterna);
		model.addAttribute("SamDtBoshqaPlan", sdBoshqa);

		//havos depo tamir hamma plan
		int hdKriti = planDto.getHavDtKritiPlanBiznesMonths();
		int hdPlatforma=planDto.getHavDtPlatformaPlanBiznesMonths();
		int hdPoluvagon=planDto.getHavDtPoluvagonPlanBiznesMonths();
		int hdSisterna=planDto.getHavDtSisternaPlanBiznesMonths();
		int hdBoshqa=planDto.getHavDtBoshqaPlanBiznesMonths();
		int HavDtHammaPlan = hdKriti + hdPlatforma + hdPoluvagon + hdSisterna + hdBoshqa;

		model.addAttribute("HavDtHammaPlan", HavDtHammaPlan);
		model.addAttribute("HavDtKritiPlan", hdKriti);
		model.addAttribute("HavDtPlatformaPlan", hdPlatforma);
		model.addAttribute("HavDtPoluvagonPlan", hdPoluvagon);
		model.addAttribute("HavDtSisternaPlan", hdSisterna);
		model.addAttribute("HavDtBoshqaPlan", hdBoshqa);

		//VCHD-5 depo tamir plan
		int adKriti = planDto.getAndjDtKritiPlanBiznesMonths();
		int adPlatforma=planDto.getAndjDtPlatformaPlanBiznesMonths();
		int adPoluvagon=planDto.getAndjDtPoluvagonPlanBiznesMonths();
		int adSisterna=planDto.getAndjDtSisternaPlanBiznesMonths();
		int adBoshqa=planDto.getAndjDtBoshqaPlanBiznesMonths();
		int AndjDtHammaPlan = adKriti + adPlatforma + adPoluvagon + adSisterna + adBoshqa;

		model.addAttribute("AndjDtHammaPlan", AndjDtHammaPlan);
		model.addAttribute("AndjDtKritiPlan", adKriti);
		model.addAttribute("AndjDtPlatformaPlan",adPlatforma);
		model.addAttribute("AndjDtPoluvagonPlan", adPoluvagon);
		model.addAttribute("AndjDtSisternaPlan", adSisterna);
		model.addAttribute("AndjDtBoshqaPlan", adBoshqa);

		// Itogo planlar depo tamir
		model.addAttribute("DtHammaPlan", AndjDtHammaPlan + HavDtHammaPlan + SamDtHammaPlan);
		model.addAttribute("DtKritiPlan", sdKriti + hdKriti + adKriti);
		model.addAttribute("DtPlatformaPlan", sdPlatforma + hdPlatforma + adPlatforma);
		model.addAttribute("DtPoluvagonPlan",sdPoluvagon + hdPoluvagon + adPoluvagon);
		model.addAttribute("DtSisternaPlan", sdSisterna + hdSisterna + adSisterna);
		model.addAttribute("DtBoshqaPlan", sdBoshqa + hdBoshqa + adBoshqa);

		//Yolovchi vagon Plan
		int atYolovchi = planDto.getAndjTYolovchiPlanBiznesMonths();
		int adYolovchi = planDto.getAndjDtYolovchiPlanBiznesMonths();

		model.addAttribute("AndjToYolovchiPlan", atYolovchi);
		model.addAttribute("AndjDtYolovchiPlan", adYolovchi);

		//Samrqand kapital plan
		int skKriti = planDto.getSamKtKritiPlanBiznesMonths();
		int skPlatforma=planDto.getSamKtPlatformaPlanBiznesMonths();
		int skPoluvagon=planDto.getSamKtPoluvagonPlanBiznesMonths();
		int skSisterna=planDto.getSamKtSisternaPlanBiznesMonths();
		int skBoshqa=planDto.getSamKtBoshqaPlanBiznesMonths();
		int SamKtHammaPlan=skKriti+skPlatforma+skPoluvagon+skSisterna+skBoshqa;

		model.addAttribute("SamKtHammaPlan",SamKtHammaPlan);
		model.addAttribute("SamKtKritiPlan", skKriti);
		model.addAttribute("SamKtPlatformaPlan", skPlatforma);
		model.addAttribute("SamKtPoluvagonPlan", skPoluvagon);
		model.addAttribute("SamKtSisternaPlan", skSisterna);
		model.addAttribute("SamKtBoshqaPlan", skBoshqa);

		//hovos kapital plan
		int hkKriti = planDto.getHavKtKritiPlanBiznesMonths();
		int hkPlatforma=planDto.getHavKtPlatformaPlanBiznesMonths();
		int hkPoluvagon=planDto.getHavKtPoluvagonPlanBiznesMonths();
		int hkSisterna=planDto.getHavKtSisternaPlanBiznesMonths();
		int hkBoshqa=planDto.getHavKtBoshqaPlanBiznesMonths();
		int HavKtHammaPlan = hkKriti + hkPlatforma + hkPoluvagon + hkSisterna + hkBoshqa;

		model.addAttribute("HavKtHammaPlan", HavKtHammaPlan);
		model.addAttribute("HavKtKritiPlan", hkKriti);
		model.addAttribute("HavKtPlatformaPlan", hkPlatforma);
		model.addAttribute("HavKtPoluvagonPlan", hkPoluvagon);
		model.addAttribute("HavKtSisternaPlan", hkSisterna);
		model.addAttribute("HavKtBoshqaPlan", hkBoshqa);

		//ANDIJON kapital plan
		int akKriti = planDto.getAndjKtKritiPlanBiznesMonths();
		int akPlatforma=planDto.getAndjKtPlatformaPlanBiznesMonths();
		int akPoluvagon=planDto.getAndjKtPoluvagonPlanBiznesMonths();
		int akSisterna=planDto.getAndjKtSisternaPlanBiznesMonths();
		int akBoshqa=planDto.getAndjKtBoshqaPlanBiznesMonths();
		int AndjKtHammaPlan = akKriti + akPlatforma + akPoluvagon + akSisterna + akBoshqa;


		model.addAttribute("AndjKtHammaPlan", AndjKtHammaPlan);
		model.addAttribute("AndjKtKritiPlan", akKriti);
		model.addAttribute("AndjKtPlatformaPlan", akPlatforma);
		model.addAttribute("AndjKtPoluvagonPlan", akPoluvagon);
		model.addAttribute("AndjKtSisternaPlan", akSisterna);
		model.addAttribute("AndjKtBoshqaPlan", akBoshqa);

		//Itogo kapital plan
		model.addAttribute("KtHammaPlan", AndjKtHammaPlan + HavKtHammaPlan + SamKtHammaPlan);
		model.addAttribute("KtKritiPlan", skKriti + hkKriti + akKriti);
		model.addAttribute("KtPlatformaPlan", skPlatforma + hkPlatforma + akPlatforma);
		model.addAttribute("KtPoluvagonPlan",skPoluvagon + hkPoluvagon + akPoluvagon);
		model.addAttribute("KtSisternaPlan", skSisterna + hkSisterna + akSisterna);
		model.addAttribute("KtBoshqaPlan", skBoshqa + hkBoshqa + akBoshqa);

		//Samarqankr Krp plan
		int skrKriti = planDto.getSamKrpKritiPlanBiznesMonths();
		int skrPlatforma=planDto.getSamKrpPlatformaPlanBiznesMonths();
		int skrPoluvagon=planDto.getSamKrpPoluvagonPlanBiznesMonths();
		int skrSisterna=planDto.getSamKrpSisternaPlanBiznesMonths();
		int skrBoshqa=planDto.getSamKrpBoshqaPlanBiznesMonths();
		int SamKrpHammaPlan=skrKriti+skrPlatforma+skrPoluvagon+skrSisterna+skrBoshqa;

		model.addAttribute("SamKrpHammaPlan", SamKrpHammaPlan);
		model.addAttribute("SamKrpKritiPlan", skrKriti);
		model.addAttribute("SamKrpPlatformaPlan", skrPlatforma);
		model.addAttribute("SamKrpPoluvagonPlan", skrPoluvagon);
		model.addAttribute("SamKrpSisternaPlan", skrSisterna);
		model.addAttribute("SamKrpBoshqaPlan", skrBoshqa);

		//Hovos krp plan
		int hkrKriti = planDto.getHavKrpKritiPlanBiznesMonths();
		int hkrPlatforma=planDto.getHavKrpPlatformaPlanBiznesMonths();
		int hkrPoluvagon=planDto.getHavKrpPoluvagonPlanBiznesMonths();
		int hkrSisterna=planDto.getHavKrpSisternaPlanBiznesMonths();
		int hkrBoshqa=planDto.getHavKrpBoshqaPlanBiznesMonths();
		int HavKrpHammaPlan = hkrKriti + hkrPlatforma + hkrPoluvagon + hkrSisterna + hkrBoshqa;

		model.addAttribute("HavKrpHammaPlan",HavKrpHammaPlan);
		model.addAttribute("HavKrpKritiPlan", hkrKriti);
		model.addAttribute("HavKrpPlatformaPlan", hkrPlatforma);
		model.addAttribute("HavKrpPoluvagonPlan", hkrPoluvagon);
		model.addAttribute("HavKrpSisternaPlan", hkrSisterna);
		model.addAttribute("HavKrpBoshqaPlan", hkrBoshqa);

		//andijon krp plan
		int akrKriti = planDto.getAndjKrpKritiPlanBiznesMonths();
		int akrPlatforma=planDto.getAndjKrpPlatformaPlanBiznesMonths();
		int akrPoluvagon=planDto.getAndjKrpPoluvagonPlanBiznesMonths();
		int akrSisterna=planDto.getAndjKrpSisternaPlanBiznesMonths();
		int akrBoshqa=planDto.getAndjKrpBoshqaPlanBiznesMonths();
		int AndjKrpHammaPlan = akrKriti + akrPlatforma + akrPoluvagon + akrSisterna + akrBoshqa;

		model.addAttribute("AndjKrpHammaPlan",AndjKrpHammaPlan);
		model.addAttribute("AndjKrpKritiPlan", akrKriti);
		model.addAttribute("AndjKrpPlatformaPlan", akrPlatforma);
		model.addAttribute("AndjKrpPoluvagonPlan", akrPoluvagon);
		model.addAttribute("AndjKrpSisternaPlan", akrSisterna);
		model.addAttribute("AndjKrpBoshqaPlan", akrBoshqa);

		//itogo krp
		model.addAttribute("KrpHammaPlan", AndjKrpHammaPlan + HavKrpHammaPlan + SamKrpHammaPlan);
		model.addAttribute("KrpKritiPlan", skrKriti + hkrKriti + akrKriti);
		model.addAttribute("KrpPlatformaPlan", skrPlatforma + hkrPlatforma + akrPlatforma);
		model.addAttribute("KrpPoluvagonPlan",akrPoluvagon + hkrPoluvagon + skrPoluvagon);
		model.addAttribute("KrpSisternaPlan", skrSisterna + hkrSisterna + akrSisterna);
		model.addAttribute("KrpBoshqaPlan", skrBoshqa + hkrBoshqa + akrBoshqa);

		//**//
		// samarqand depo tamir hamma false vagonlar soni
		int sdKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Depoli ta’mir(ДР)");
		int sdPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Depoli ta’mir(ДР)");
		int sdPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)");
		int sdSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Depoli ta’mir(ДР)");
		int sdBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Depoli ta’mir(ДР)");
		int sdHammaFalse = sdKritiFalse + sdPlatformaFalse+ sdPoluvagonFalse+ sdSisternaFalse + sdBoshqaFalse;

		model.addAttribute("sdHammaFalse",sdHammaFalse+393);
		model.addAttribute("sdKritiFalse",sdKritiFalse+135);
		model.addAttribute("sdPlatformaFalse",sdPlatformaFalse+8);
		model.addAttribute("sdPoluvagonFalse",sdPoluvagonFalse+67);
		model.addAttribute("sdSisternaFalse",sdSisternaFalse+23);
		model.addAttribute("sdBoshqaFalse",sdBoshqaFalse+160);

		// VCHD-3 depo tamir hamma false vagonlar soni
		int hdKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Depoli ta’mir(ДР)");
		int hdPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Depoli ta’mir(ДР)");
		int hdPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)");
		int hdSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Depoli ta’mir(ДР)");
		int hdBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Depoli ta’mir(ДР)");
		int hdHammaFalse = hdKritiFalse + hdPlatformaFalse+ hdPoluvagonFalse+ hdSisternaFalse + hdBoshqaFalse;

		model.addAttribute("hdHammaFalse",hdHammaFalse+651);
		model.addAttribute("hdKritiFalse",hdKritiFalse+35);
		model.addAttribute("hdPlatformaFalse",hdPlatformaFalse+45);
		model.addAttribute("hdPoluvagonFalse",hdPoluvagonFalse+109);
		model.addAttribute("hdSisternaFalse",hdSisternaFalse+35);
		model.addAttribute("hdBoshqaFalse",hdBoshqaFalse+427);

		// VCHD-5 depo tamir hamma false vagonlar soni
		int adKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Depoli ta’mir(ДР)");
		int adPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Depoli ta’mir(ДР)");
		int adPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)");
		int adSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Depoli ta’mir(ДР)");
		int adBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Depoli ta’mir(ДР)");
		int adHammaFalse = adKritiFalse + adPlatformaFalse+ adPoluvagonFalse+ adSisternaFalse + adBoshqaFalse;

		model.addAttribute("adHammaFalse",adHammaFalse+443);
		model.addAttribute("adKritiFalse",adKritiFalse+224);
		model.addAttribute("adPlatformaFalse",adPlatformaFalse+3);
		model.addAttribute("adPoluvagonFalse",adPoluvagonFalse+103);
		model.addAttribute("adSisternaFalse",adSisternaFalse);
		model.addAttribute("adBoshqaFalse",adBoshqaFalse+113);

		// depoli tamir itogo uchun
		int dHammaFalse =  adHammaFalse + hdHammaFalse+sdHammaFalse;
		int dKritiFalse = sdKritiFalse + hdKritiFalse + adKritiFalse;
		int dPlatforma = adPlatformaFalse + sdPlatformaFalse + hdPlatformaFalse;
		int dPoluvagon  = adPoluvagonFalse + sdPoluvagonFalse + hdPoluvagonFalse;
		int dSisterna = adSisternaFalse + hdSisternaFalse + sdSisternaFalse;
		int dBoshqa = adBoshqaFalse + hdBoshqaFalse + sdBoshqaFalse;

		model.addAttribute("dHammaFalse",dHammaFalse+1487);
		model.addAttribute("dKritiFalse",dKritiFalse+394);
		model.addAttribute("dPlatforma",dPlatforma+56);
		model.addAttribute("dPoluvagon",dPoluvagon+279);
		model.addAttribute("dSisterna",dSisterna+58);
		model.addAttribute("dBoshqa",dBoshqa+700);

		//Yolovchi Andijon fact
		int atYolovchiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","TO-3");
		int adYolovchiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","Depoli ta’mir(ДР)");

		model.addAttribute("atYolovchi",atYolovchiFalse+37);
		model.addAttribute("adYolovchi",adYolovchiFalse+24);

		// samarqand KApital tamir hamma false vagonlar soni
		int skKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Kapital ta’mir(КР)");
		int skPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Kapital ta’mir(КР)");
		int skPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)");
		int skSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Kapital ta’mir(КР)");
		int skBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Kapital ta’mir(КР)");
		int skHammaFalse = skKritiFalse + skPlatformaFalse+ skPoluvagonFalse+ skSisternaFalse + skBoshqaFalse;

		model.addAttribute("skHammaFalse",skHammaFalse+284);
		model.addAttribute("skKritiFalse",skKritiFalse+160);
		model.addAttribute("skPlatformaFalse",skPlatformaFalse+44);
		model.addAttribute("skPoluvagonFalse",skPoluvagonFalse+52);
		model.addAttribute("skSisternaFalse",skSisternaFalse+9);
		model.addAttribute("skBoshqaFalse",skBoshqaFalse+19);

		// VCHD-3 kapital tamir hamma false vagonlar soni
		int hkKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Kapital ta’mir(КР)");
		int hkPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Kapital ta’mir(КР)");
		int hkPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)");
		int hkSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Kapital ta’mir(КР)");
		int hkBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Kapital ta’mir(КР)");
		int hkHammaFalse = hkKritiFalse + hkPlatformaFalse+ hkPoluvagonFalse+ hkSisternaFalse + hkBoshqaFalse;

		model.addAttribute("hkHammaFalse",hkHammaFalse+227);
		model.addAttribute("hkKritiFalse",hkKritiFalse+41);
		model.addAttribute("hkPlatformaFalse",hkPlatformaFalse+32);
		model.addAttribute("hkPoluvagonFalse",hkPoluvagonFalse+4);
		model.addAttribute("hkSisternaFalse",hkSisternaFalse+51);
		model.addAttribute("hkBoshqaFalse",hkBoshqaFalse+99);

		// VCHD-5 kapital tamir hamma false vagonlar soni
		int akKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Kapital ta’mir(КР)");
		int akPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Kapital ta’mir(КР)");
		int akPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)");
		int akSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Kapital ta’mir(КР)");
		int akBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Kapital ta’mir(КР)");
		int akHammaFalse = akKritiFalse + akPlatformaFalse+ akPoluvagonFalse+ akSisternaFalse + akBoshqaFalse;

		model.addAttribute("akHammaFalse",akHammaFalse+28);
		model.addAttribute("akKritiFalse",akKritiFalse+26);
		model.addAttribute("akPlatformaFalse",akPlatformaFalse);
		model.addAttribute("akPoluvagonFalse",akPoluvagonFalse+2);
		model.addAttribute("akSisternaFalse",akSisternaFalse);
		model.addAttribute("akBoshqaFalse",akBoshqaFalse);

		// Kapital tamir itogo uchun
		int kHammaFalse =  akHammaFalse + hkHammaFalse+skHammaFalse;
		int kKritiFalse = skKritiFalse + hkKritiFalse + akKritiFalse;
		int kPlatforma = akPlatformaFalse + skPlatformaFalse + hkPlatformaFalse;
		int kPoluvagon  = akPoluvagonFalse + skPoluvagonFalse + hkPoluvagonFalse;
		int kSisterna = akSisternaFalse + hkSisternaFalse + skSisternaFalse;
		int kBoshqa = akBoshqaFalse + hkBoshqaFalse + skBoshqaFalse;

		model.addAttribute("kHammaFalse",kHammaFalse+539);
		model.addAttribute("kKritiFalse",kKritiFalse+227);
		model.addAttribute("kPlatforma",kPlatforma+76);
		model.addAttribute("kPoluvagon",kPoluvagon+58);
		model.addAttribute("kSisterna",kSisterna+60);
		model.addAttribute("kBoshqa",kBoshqa+118);

		//**
		// samarqand KRP tamir hamma false vagonlar soni
		int skrKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","KRP(КРП)");
		int skrPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","KRP(КРП)");
		int skrPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","KRP(КРП)");
		int skrSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","KRP(КРП)");
		int skrBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","KRP(КРП)");
		int skrHammaFalse = skrKritiFalse + skrPlatformaFalse+ skrPoluvagonFalse+ skrSisternaFalse + skrBoshqaFalse;

		model.addAttribute("skrHammaFalse",skrHammaFalse+89);
		model.addAttribute("skrKritiFalse",skrKritiFalse);
		model.addAttribute("skrPlatformaFalse",skrPlatformaFalse);
		model.addAttribute("skrPoluvagonFalse",skrPoluvagonFalse+88);
		model.addAttribute("skrSisternaFalse",skrSisternaFalse+1);
		model.addAttribute("skrBoshqaFalse",skrBoshqaFalse);

		// VCHD-3 KRP tamir hamma false vagonlar soni
		int hkrKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","KRP(КРП)");
		int hkrPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","KRP(КРП)");
		int hkrPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","KRP(КРП)");
		int hkrSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","KRP(КРП)");
		int hkrBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","KRP(КРП)");
		int hkrHammaFalse = hkrKritiFalse + hkrPlatformaFalse+ hkrPoluvagonFalse+ hkrSisternaFalse + hkrBoshqaFalse;

		model.addAttribute("hkrHammaFalse",hkrHammaFalse+83);
		model.addAttribute("hkrKritiFalse",hkrKritiFalse+83);
		model.addAttribute("hkrPlatformaFalse",hkrPlatformaFalse);
		model.addAttribute("hkrPoluvagonFalse",hkrPoluvagonFalse);
		model.addAttribute("hkrSisternaFalse",hkrSisternaFalse);
		model.addAttribute("hkrBoshqaFalse",hkrBoshqaFalse);

		// VCHD-5 KRP tamir hamma false vagonlar soni
		int akrKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","KRP(КРП)");
		int akrPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","KRP(КРП)");
		int akrPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","KRP(КРП)");
		int akrSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","KRP(КРП)");
		int akrBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","KRP(КРП)");
		int akrHammaFalse = akrKritiFalse + akrPlatformaFalse+ akrPoluvagonFalse+ akrSisternaFalse + akrBoshqaFalse;

		model.addAttribute("akrHammaFalse",akrHammaFalse+61);
		model.addAttribute("akrKritiFalse",akrKritiFalse);
		model.addAttribute("akrPlatformaFalse",akrPlatformaFalse);
		model.addAttribute("akrPoluvagonFalse",akrPoluvagonFalse+61);
		model.addAttribute("akrSisternaFalse",akrSisternaFalse);
		model.addAttribute("akBoshqaFalse",akBoshqaFalse);

		// Krp itogo uchun
		int krHammaFalse =  akrHammaFalse + hkrHammaFalse+skrHammaFalse;
		int krKritiFalse = skrKritiFalse + hkrKritiFalse + akrKritiFalse;
		int krPlatforma = akrPlatformaFalse + skrPlatformaFalse + hkrPlatformaFalse;
		int krPoluvagon  = akrPoluvagonFalse + skrPoluvagonFalse + hkrPoluvagonFalse;
		int krSisterna = akrSisternaFalse + hkrSisternaFalse + skrSisternaFalse;
		int krBoshqa = akrBoshqaFalse + hkrBoshqaFalse + skrBoshqaFalse;

		model.addAttribute("krHammaFalse",krHammaFalse+233);
		model.addAttribute("krKritiFalse",krKritiFalse);
		model.addAttribute("krPlatforma",krPlatforma);
		model.addAttribute("krPoluvagon",krPoluvagon+232);
		model.addAttribute("krSisterna",krSisterna+1);
		model.addAttribute("krBoshqa",krBoshqa);

    	return "planTableForMonths";
    }

    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
   	@GetMapping("/vagons/filterOneMonth")
   	public String filterByDepoNomi(Model model,  @RequestParam(value = "depoNomi", required = false) String depoNomi,
   												@RequestParam(value = "vagonTuri", required = false) String vagonTuri,
   												@RequestParam(value = "country", required = false) String country) {
		String oy=null;
		switch (month) {
			case 1:
				oy = "-01";
				break;
			case 2:
				oy = "-02";
				break;
			case 3:
				oy = "-03";
				break;
			case 4:
				oy = "-04";
				break;
			case 5:
				oy = "-05";
				break;
			case 6:
				oy = "-06";
				break;
			case 7:
				oy = "-07";
				break;
			case 8:
				oy = "-08";
				break;
			case 9:
				oy = "-09";
				break;
			case 10:
				oy = "-10";
				break;
			case 11:
				oy = "-11";
				break;
			case 12:
				oy = "-12";
				break;
		}

   		if(!depoNomi.equalsIgnoreCase("Hammasi") && !vagonTuri.equalsIgnoreCase("Hammasi") && !country.equalsIgnoreCase("Hammasi") ) {
   			vagonsToDownload = vagonTayyorService.findAllByDepoNomiVagonTuriAndCountry(depoNomi, vagonTuri, country, oy);
   			model.addAttribute("vagons", vagonTayyorService.findAllByDepoNomiVagonTuriAndCountry(depoNomi, vagonTuri, country, oy));
   		}else if(depoNomi.equalsIgnoreCase("Hammasi") && !vagonTuri.equalsIgnoreCase("Hammasi") && !country.equalsIgnoreCase("Hammasi")){
   			vagonsToDownload = vagonTayyorService.findAllByVagonTuriAndCountry(vagonTuri , country, oy);
   			model.addAttribute("vagons", vagonTayyorService.findAllByVagonTuriAndCountry(vagonTuri , country, oy));
   		}else if(depoNomi.equalsIgnoreCase("Hammasi") && vagonTuri.equalsIgnoreCase("Hammasi")&& !country.equalsIgnoreCase("Hammasi")){
   			vagonsToDownload = vagonTayyorService.findAllBycountry(country, oy );
   			model.addAttribute("vagons", vagonTayyorService.findAllBycountry(country, oy ));
   		}else if(!depoNomi.equalsIgnoreCase("Hammasi") && !vagonTuri.equalsIgnoreCase("Hammasi") && country.equalsIgnoreCase("Hammasi")){
   			vagonsToDownload = vagonTayyorService.findAllByDepoNomiAndVagonTuri(depoNomi, vagonTuri, oy);
   			model.addAttribute("vagons", vagonTayyorService.findAllByDepoNomiAndVagonTuri(depoNomi, vagonTuri, oy));
   		}else if(!depoNomi.equalsIgnoreCase("Hammasi") && vagonTuri.equalsIgnoreCase("Hammasi") && !country.equalsIgnoreCase("Hammasi")){
   			vagonsToDownload = vagonTayyorService.findAllByDepoNomiAndCountry(depoNomi, country, oy);
   			model.addAttribute("vagons", vagonTayyorService.findAllByDepoNomiAndCountry(depoNomi, country, oy));
   		}else if(depoNomi.equalsIgnoreCase("Hammasi") && !vagonTuri.equalsIgnoreCase("Hammasi") && country.equalsIgnoreCase("Hammasi")){
   			vagonsToDownload = vagonTayyorService.findAllByVagonTuri(vagonTuri, oy);
   			model.addAttribute("vagons", vagonTayyorService.findAllByVagonTuri(vagonTuri, oy));
   		}else if(!depoNomi.equalsIgnoreCase("Hammasi") && vagonTuri.equalsIgnoreCase("Hammasi") && country.equalsIgnoreCase("Hammasi")){
   			model.addAttribute("vagons", vagonTayyorService.findAllByDepoNomi(depoNomi, oy));
   			vagonsToDownload = vagonTayyorService.findAllByDepoNomi(depoNomi, oy );
   		}else {
   			model.addAttribute("vagons", vagonTayyorService.findAll(oy));
   			vagonsToDownload = vagonTayyorService.findAll(oy);
   		}

		//vaqtni olib turadi
		model.addAttribute("samDate",vagonTayyorService.getSamDate());
		model.addAttribute("havDate", vagonTayyorService.getHavDate());
		model.addAttribute("andjDate",vagonTayyorService.getAndjDate());

		PlanBiznes planDto = vagonTayyorService.getPlanBiznes();
		//planlar kiritish
		//samarqand depo tamir
		int SamDtHammaPlan=planDto.getSamDtKritiPlanBiznes() + planDto.getSamDtPlatformaPlanBiznes() + planDto.getSamDtPoluvagonPlanBiznes() + planDto.getSamDtSisternaPlanBiznes() + planDto.getSamDtBoshqaPlanBiznes();
		model.addAttribute("SamDtHammaPlan",SamDtHammaPlan);
		model.addAttribute("SamDtKritiPlan", planDto.getSamDtKritiPlanBiznes());
		model.addAttribute("SamDtPlatformaPlan", planDto.getSamDtPlatformaPlanBiznes());
		model.addAttribute("SamDtPoluvagonPlan", planDto.getSamDtPoluvagonPlanBiznes());
		model.addAttribute("SamDtSisternaPlan", planDto.getSamDtSisternaPlanBiznes());
		model.addAttribute("SamDtBoshqaPlan", planDto.getSamDtBoshqaPlanBiznes());

		//havos hamma plan
		int HavDtHammaPlan = planDto.getHavDtKritiPlanBiznes() + planDto.getHavDtPlatformaPlanBiznes() + planDto.getHavDtPoluvagonPlanBiznes() + planDto.getHavDtSisternaPlanBiznes() + planDto.getHavDtBoshqaPlanBiznes();
		model.addAttribute("HavDtHammaPlan", HavDtHammaPlan);
		model.addAttribute("HavDtKritiPlan", planDto.getHavDtKritiPlanBiznes());
		model.addAttribute("HavDtPlatformaPlan", planDto.getHavDtPlatformaPlanBiznes());
		model.addAttribute("HavDtPoluvagonPlan", planDto.getHavDtPoluvagonPlanBiznes());
		model.addAttribute("HavDtSisternaPlan", planDto.getHavDtSisternaPlanBiznes());
		model.addAttribute("HavDtBoshqaPlan", planDto.getHavDtBoshqaPlanBiznes());

		//andijon hamma plan depo tamir
		int AndjDtHammaPlan = planDto.getAndjDtKritiPlanBiznes() + planDto.getAndjDtPlatformaPlanBiznes() + planDto.getAndjDtPoluvagonPlanBiznes() + planDto.getAndjDtSisternaPlanBiznes() + planDto.getAndjDtBoshqaPlanBiznes();
		model.addAttribute("AndjDtHammaPlan", AndjDtHammaPlan);
		model.addAttribute("AndjDtKritiPlan", planDto.getAndjDtKritiPlanBiznes());
		model.addAttribute("AndjDtPlatformaPlan", planDto.getAndjDtPlatformaPlanBiznes());
		model.addAttribute("AndjDtPoluvagonPlan", planDto.getAndjDtPoluvagonPlanBiznes());
		model.addAttribute("AndjDtSisternaPlan", planDto.getAndjDtSisternaPlanBiznes());
		model.addAttribute("AndjDtBoshqaPlan", planDto.getAndjDtBoshqaPlanBiznes());

		// Itogo planlar depo tamir
		model.addAttribute("DtHammaPlan", AndjDtHammaPlan + HavDtHammaPlan + SamDtHammaPlan);
		model.addAttribute("DtKritiPlan", planDto.getAndjDtKritiPlanBiznes() + planDto.getHavDtKritiPlanBiznes() + planDto.getSamDtKritiPlanBiznes());
		model.addAttribute("DtPlatformaPlan", planDto.getAndjDtPlatformaPlanBiznes() + planDto.getHavDtPlatformaPlanBiznes() + planDto.getSamDtPlatformaPlanBiznes());
		model.addAttribute("DtPoluvagonPlan",planDto.getAndjDtPoluvagonPlanBiznes() + planDto.getHavDtPoluvagonPlanBiznes() + planDto.getSamDtPoluvagonPlanBiznes());
		model.addAttribute("DtSisternaPlan", planDto.getAndjDtSisternaPlanBiznes() + planDto.getHavDtSisternaPlanBiznes() + planDto.getSamDtSisternaPlanBiznes());
		model.addAttribute("DtBoshqaPlan", planDto.getAndjDtBoshqaPlanBiznes() + planDto.getHavDtBoshqaPlanBiznes() + planDto.getSamDtBoshqaPlanBiznes());

		//yolovchi vagonlar plan
		model.addAttribute("AndjToYolovchiPlan", planDto.getAndjTYolovchiPlanBiznes());
		model.addAttribute("AndjDtYolovchiPlan", planDto.getAndjDtYolovchiPlanBiznes());

		//VCHD-6 kapital tamir uchun plan
		int SamKtHammaPlan =  planDto.getSamKtKritiPlanBiznes() + planDto.getSamKtPlatformaPlanBiznes() + planDto.getSamKtPoluvagonPlanBiznes() + planDto.getSamKtSisternaPlanBiznes() + planDto.getSamKtBoshqaPlanBiznes();
		model.addAttribute("SamKtHammaPlan",SamKtHammaPlan);
		model.addAttribute("SamKtKritiPlan", planDto.getSamKtKritiPlanBiznes());
		model.addAttribute("SamKtPlatformaPlan", planDto.getSamKtPlatformaPlanBiznes());
		model.addAttribute("SamKtPoluvagonPlan", planDto.getSamKtPoluvagonPlanBiznes());
		model.addAttribute("SamKtSisternaPlan", planDto.getSamKtSisternaPlanBiznes());
		model.addAttribute("SamKtBoshqaPlan", planDto.getSamKtBoshqaPlanBiznes());

		//havos kapital tamir uchun plan
		int HavKtHammaPlan = planDto.getHavKtKritiPlanBiznes() + planDto.getHavKtPlatformaPlanBiznes() + planDto.getHavKtPoluvagonPlanBiznes() + planDto.getHavKtSisternaPlanBiznes() + planDto.getHavKtBoshqaPlanBiznes();
		model.addAttribute("HavKtHammaPlan", HavKtHammaPlan);
		model.addAttribute("HavKtKritiPlan", planDto.getHavKtKritiPlanBiznes());
		model.addAttribute("HavKtPlatformaPlan", planDto.getHavKtPlatformaPlanBiznes());
		model.addAttribute("HavKtPoluvagonPlan", planDto.getHavKtPoluvagonPlanBiznes());
		model.addAttribute("HavKtSisternaPlan", planDto.getHavKtSisternaPlanBiznes());
		model.addAttribute("HavKtBoshqaPlan", planDto.getHavKtBoshqaPlanBiznes());

		//VCHD-5 kapital tamir uchun plan
		int AndjKtHammaPlan = planDto.getAndjKtKritiPlanBiznes() + planDto.getAndjKtPlatformaPlanBiznes() + planDto.getAndjKtPoluvagonPlanBiznes() + planDto.getAndjKtSisternaPlanBiznes() + planDto.getAndjKtBoshqaPlanBiznes();
		model.addAttribute("AndjKtHammaPlan", AndjKtHammaPlan);
		model.addAttribute("AndjKtKritiPlan", planDto.getAndjKtKritiPlanBiznes());
		model.addAttribute("AndjKtPlatformaPlan", planDto.getAndjKtPlatformaPlanBiznes());
		model.addAttribute("AndjKtPoluvagonPlan", planDto.getAndjKtPoluvagonPlanBiznes());
		model.addAttribute("AndjKtSisternaPlan", planDto.getAndjKtSisternaPlanBiznes());
		model.addAttribute("AndjKtBoshqaPlan", planDto.getAndjKtBoshqaPlanBiznes());

		//kapital itogo
		model.addAttribute("KtHammaPlan", AndjKtHammaPlan + HavKtHammaPlan + SamKtHammaPlan);
		model.addAttribute("KtKritiPlan", planDto.getAndjKtKritiPlanBiznes() + planDto.getHavKtKritiPlanBiznes() + planDto.getSamKtKritiPlanBiznes());
		model.addAttribute("KtPlatformaPlan", planDto.getAndjKtPlatformaPlanBiznes() + planDto.getHavKtPlatformaPlanBiznes() + planDto.getSamKtPlatformaPlanBiznes());
		model.addAttribute("KtPoluvagonPlan",planDto.getAndjKtPoluvagonPlanBiznes() + planDto.getHavKtPoluvagonPlanBiznes() + planDto.getSamKtPoluvagonPlanBiznes());
		model.addAttribute("KtSisternaPlan", planDto.getAndjKtSisternaPlanBiznes() + planDto.getHavKtSisternaPlanBiznes() + planDto.getSamKtSisternaPlanBiznes());
		model.addAttribute("KtBoshqaPlan", planDto.getAndjKtBoshqaPlanBiznes() + planDto.getHavKtBoshqaPlanBiznes() + planDto.getSamKtBoshqaPlanBiznes());

		//samarqand KRP plan
		int SamKrpHammaPlan = planDto.getSamKrpKritiPlanBiznes() + planDto.getSamKrpPlatformaPlanBiznes() + planDto.getSamKrpPoluvagonPlanBiznes() + planDto.getSamKrpSisternaPlanBiznes() + planDto.getSamKrpBoshqaPlanBiznes();
		model.addAttribute("SamKrpHammaPlan", SamKrpHammaPlan);
		model.addAttribute("SamKrpKritiPlan", planDto.getSamKrpKritiPlanBiznes());
		model.addAttribute("SamKrpPlatformaPlan", planDto.getSamKrpPlatformaPlanBiznes());
		model.addAttribute("SamKrpPoluvagonPlan", planDto.getSamKrpPoluvagonPlanBiznes());
		model.addAttribute("SamKrpSisternaPlan", planDto.getSamKrpSisternaPlanBiznes());
		model.addAttribute("SamKrpBoshqaPlan", planDto.getSamKrpBoshqaPlanBiznes());

		//VCHD-3 KRP plan
		int HavKrpHammaPlan =  planDto.getHavKrpKritiPlanBiznes() + planDto.getHavKrpPlatformaPlanBiznes() + planDto.getHavKrpPoluvagonPlanBiznes() + planDto.getHavKrpSisternaPlanBiznes() + planDto.getHavKrpBoshqaPlanBiznes();
		model.addAttribute("HavKrpHammaPlan",HavKrpHammaPlan);
		model.addAttribute("HavKrpKritiPlan", planDto.getHavKrpKritiPlanBiznes());
		model.addAttribute("HavKrpPlatformaPlan", planDto.getHavKrpPlatformaPlanBiznes());
		model.addAttribute("HavKrpPoluvagonPlan", planDto.getHavKrpPoluvagonPlanBiznes());
		model.addAttribute("HavKrpSisternaPlan", planDto.getHavKrpSisternaPlanBiznes());
		model.addAttribute("HavKrpBoshqaPlan", planDto.getHavKrpBoshqaPlanBiznes());

		//VCHD-5 Krp plan
		int AndjKrpHammaPlan =  planDto.getAndjKrpKritiPlanBiznes() + planDto.getAndjKrpPlatformaPlanBiznes() + planDto.getAndjKrpPoluvagonPlanBiznes() + planDto.getAndjKrpSisternaPlanBiznes() + planDto.getAndjKrpBoshqaPlanBiznes();
		model.addAttribute("AndjKrpHammaPlan",AndjKrpHammaPlan);
		model.addAttribute("AndjKrpKritiPlan", planDto.getAndjKrpKritiPlanBiznes());
		model.addAttribute("AndjKrpPlatformaPlan", planDto.getAndjKrpPlatformaPlanBiznes());
		model.addAttribute("AndjKrpPoluvagonPlan", planDto.getAndjKrpPoluvagonPlanBiznes());
		model.addAttribute("AndjKrpSisternaPlan", planDto.getAndjKrpSisternaPlanBiznes());
		model.addAttribute("AndjKrpBoshqaPlan", planDto.getAndjKrpBoshqaPlanBiznes());

		//Krp itogo plan
		model.addAttribute("KrpHammaPlan", AndjKrpHammaPlan + HavKrpHammaPlan + SamKrpHammaPlan);
		model.addAttribute("KrpKritiPlan", planDto.getAndjKrpKritiPlanBiznes() + planDto.getHavKrpKritiPlanBiznes() + planDto.getSamKrpKritiPlanBiznes());
		model.addAttribute("KrpPlatformaPlan", planDto.getAndjKrpPlatformaPlanBiznes() + planDto.getHavKrpPlatformaPlanBiznes() + planDto.getSamKrpPlatformaPlanBiznes());
		model.addAttribute("KrpPoluvagonPlan",planDto.getAndjKrpPoluvagonPlanBiznes() + planDto.getHavKrpPoluvagonPlanBiznes() + planDto.getSamKrpPoluvagonPlanBiznes());
		model.addAttribute("KrpSisternaPlan", planDto.getAndjKrpSisternaPlanBiznes() + planDto.getHavKrpSisternaPlanBiznes() + planDto.getSamKrpSisternaPlanBiznes());
		model.addAttribute("KrpBoshqaPlan", planDto.getAndjKrpBoshqaPlanBiznes() + planDto.getHavKrpBoshqaPlanBiznes() + planDto.getSamKrpBoshqaPlanBiznes());


		// factlar
		if (country.equalsIgnoreCase("Hammasi")) {

			//samarqand uchun depli tamir
			int sdHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy);
			model.addAttribute("sdHamma", sdHamma);
			model.addAttribute("sdKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy));
			model.addAttribute("sdPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Depoli ta’mir(ДР)", oy));
			model.addAttribute("sdPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy));
			model.addAttribute("sdSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy));
			model.addAttribute("sdBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy));

			//VCHD-3 uchun depli tamir
			int hdHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy);
			model.addAttribute("hdHamma", hdHamma);
			model.addAttribute("hdKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy));
			model.addAttribute("hdPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Depoli ta’mir(ДР)", oy));
			model.addAttribute("hdPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy));
			model.addAttribute("hdSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy));
			model.addAttribute("hdBoshqaPlan", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy));

			//VCHD-5 uchun depli tamir
			int adHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy);
			model.addAttribute("adHamma", adHamma);
			model.addAttribute("adKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy));
			model.addAttribute("adPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Depoli ta’mir(ДР)", oy));
			model.addAttribute("adPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy));
			model.addAttribute("adSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy));
			model.addAttribute("adBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy));

			// itogo Fact uchun depli tamir
			int factdhamma = sdHamma + hdHamma + adHamma;
			model.addAttribute("factdhamma", factdhamma);
			int boshqaPlan = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy);
			model.addAttribute("boshqaPlan", boshqaPlan);

			//Yolovchi vagon Fact
			model.addAttribute("atYolovchi", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","TO-3", oy));
			model.addAttribute("adYolovchi", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","Depoli ta’mir(ДР)", oy));


			//samarqand uchun Kapital tamir
			int skHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy);
			model.addAttribute("skHamma", skHamma);
			model.addAttribute("skKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy));
			model.addAttribute("skPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Kapital ta’mir(КР)", oy));
			model.addAttribute("skPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy));
			model.addAttribute("skSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Kapital ta’mir(КР)", oy));
			model.addAttribute("skBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy));

			//VCHD-3 uchun kapital tamir
			int hkHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy);
			model.addAttribute("hkHamma", hkHamma);
			model.addAttribute("hkKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy));
			model.addAttribute("hkPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Kapital ta’mir(КР)", oy));
			model.addAttribute("hkPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy));
			model.addAttribute("hkSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Kapital ta’mir(КР)", oy));
			model.addAttribute("hkBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy));

			//VCHD-5 uchun kapital tamir
			int akHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy);
			model.addAttribute("akHamma", akHamma);
			model.addAttribute("akKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy));
			model.addAttribute("akPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Kapital ta’mir(КР)", oy));
			model.addAttribute("akPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy));
			model.addAttribute("akSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Kapital ta’mir(КР)", oy));
			model.addAttribute("akBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy));

			// itogo Fact uchun kapital tamir
			int factkhamma = skHamma + hkHamma + akHamma;
			model.addAttribute("factkhamma", factkhamma);
			int boshqaKPlan = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy);
			model.addAttribute("boshqaKPlan", boshqaKPlan);


			//samarqand uchun KRP tamir
			int skrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)", oy);
			model.addAttribute("skrHamma", skrHamma);
			model.addAttribute("skrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "KRP(КРП)", oy));
			model.addAttribute("skrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "KRP(КРП)", oy));
			model.addAttribute("skrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy));
			model.addAttribute("skrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "KRP(КРП)", oy));
			model.addAttribute("skrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)", oy));

			//VCHD-3 uchun KRP
			int hkrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)", oy);
			model.addAttribute("hkrHamma", hkrHamma);
			model.addAttribute("hkrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "KRP(КРП)", oy));
			model.addAttribute("hkrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "KRP(КРП)", oy));
			model.addAttribute("hkrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy));
			model.addAttribute("hkrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "KRP(КРП)", oy));
			model.addAttribute("hkrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)", oy));

			//VCHD-5 uchun KRP
			int akrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)", oy);
			model.addAttribute("akrHamma", akrHamma);
			model.addAttribute("akrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "KRP(КРП)", oy));
			model.addAttribute("akrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "KRP(КРП)", oy));
			model.addAttribute("akrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy));
			model.addAttribute("akrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "KRP(КРП)", oy));
			model.addAttribute("akrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)", oy));

			// itogo Fact uchun KRP
			int factkrhamma = skrHamma + hkrHamma + akrHamma;
			model.addAttribute("factkrhamma", factkrhamma);
			int boshqaKr = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)", oy) +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)", oy);
			model.addAttribute("boshqaKr", boshqaKr);

		}else if (country.equalsIgnoreCase("O'TY(ГАЖК)")) {

			//samarqand uchun depli tamir
			int sdHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)");
			model.addAttribute("sdHamma", sdHamma);
			model.addAttribute("sdKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("sdPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("sdPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("sdSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("sdBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));

			//VCHD-3 uchun depli tamir
			int hdHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)");
			model.addAttribute("hdHamma", hdHamma);
			model.addAttribute("hdKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("hdPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("hdPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("hdSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("hdBoshqaPlan", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));

			//VCHD-5 uchun depli tamir
			int adHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)");
			model.addAttribute("adHamma", adHamma);
			model.addAttribute("adKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("adPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("adPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("adSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("adBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)"));

			// itogo Fact uchun depli tamir
			int factdhamma = sdHamma + hdHamma + adHamma;
			model.addAttribute("factdhamma", factdhamma);
			int boshqaPlan = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "O'TY(ГАЖК)");
			model.addAttribute("boshqaPlan", boshqaPlan);

			//Yolovchi vagon Fact
			model.addAttribute("atYolovchi", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","TO-3", oy));
			model.addAttribute("adYolovchi", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","Depoli ta’mir(ДР)", oy));


			//samarqand uchun Kapital tamir
			int skHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)");
			model.addAttribute("skHamma", skHamma);
			model.addAttribute("skKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("skPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("skPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("skSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("skBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));

			//VCHD-3 uchun kapital tamir
			int hkHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)");
			model.addAttribute("hkHamma", hkHamma);
			model.addAttribute("hkKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("hkPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("hkPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("hkSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("hkBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));

			//VCHD-5 uchun kapital tamir
			int akHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)");
			model.addAttribute("akHamma", akHamma);
			model.addAttribute("akKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("akPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("akPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("akSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("akBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)"));

			// itogo Fact uchun kapital tamir
			int factkhamma = skHamma + hkHamma + akHamma;
			model.addAttribute("factkhamma", factkhamma);
			int boshqaKPlan = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "O'TY(ГАЖК)");
			model.addAttribute("boshqaKPlan", boshqaKPlan);


			//samarqand uchun KRP tamir
			int skrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "O'TY(ГАЖК)");
			model.addAttribute("skrHamma", skrHamma);
			model.addAttribute("skrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("skrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("skrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("skrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("skrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));

			//VCHD-3 uchun KRP
			int hkrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "O'TY(ГАЖК)");
			model.addAttribute("hkrHamma", hkrHamma);
			model.addAttribute("hkrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("hkrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("hkrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("hkrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("hkrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));

			//VCHD-5 uchun KRP
			int akrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "O'TY(ГАЖК)");
			model.addAttribute("akrHamma", akrHamma);
			model.addAttribute("akrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("akrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("akrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("akrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));
			model.addAttribute("akrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "O'TY(ГАЖК)"));

			// itogo Fact uchun KRP
			int factkrhamma = skrHamma + hkrHamma + akrHamma;
			model.addAttribute("factkrhamma", factkrhamma);
			int boshqaKr = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "O'TY(ГАЖК)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "O'TY(ГАЖК)");
			model.addAttribute("boshqaKr", boshqaKr);

		} else if (country.equalsIgnoreCase("MDH(СНГ)")) {

			//samarqand uchun depli tamir
			int sdHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy,"MDH(СНГ)");
			model.addAttribute("sdHamma", sdHamma);
			model.addAttribute("sdKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));
			model.addAttribute("sdPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));
			model.addAttribute("sdPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));
			model.addAttribute("sdSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));
			model.addAttribute("sdBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));

			//VCHD-3 uchun depli tamir
			int hdHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy,"MDH(СНГ)");
			model.addAttribute("hdHamma", hdHamma);
			model.addAttribute("hdKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));
			model.addAttribute("hdPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));
			model.addAttribute("hdPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));
			model.addAttribute("hdSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));
			model.addAttribute("hdBoshqaPlan", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));

			//VCHD-5 uchun depli tamir
			int adHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy,"MDH(СНГ)");
			model.addAttribute("adHamma", adHamma);
			model.addAttribute("adKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));
			model.addAttribute("adPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));
			model.addAttribute("adPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));
			model.addAttribute("adSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));
			model.addAttribute("adBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)"));

			// itogo Fact uchun depli tamir
			int factdhamma = sdHamma + hdHamma + adHamma;
			model.addAttribute("factdhamma", factdhamma);
			int boshqaPlan = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "MDH(СНГ)");
			model.addAttribute("boshqaPlan", boshqaPlan);

			//Yolovchi vagon Fact
			model.addAttribute("atYolovchi", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","TO-3", oy));
			model.addAttribute("adYolovchi", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","Depoli ta’mir(ДР)", oy));


			//samarqand uchun Kapital tamir
			int skHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)");
			model.addAttribute("skHamma", skHamma);
			model.addAttribute("skKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));
			model.addAttribute("skPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));
			model.addAttribute("skPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));
			model.addAttribute("skSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));
			model.addAttribute("skBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy,"MDH(СНГ)"));

			//VCHD-3 uchun kapital tamir
			int hkHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy,"MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)");
			model.addAttribute("hkHamma", hkHamma);
			model.addAttribute("hkKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));
			model.addAttribute("hkPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));
			model.addAttribute("hkPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));
			model.addAttribute("hkSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));
			model.addAttribute("hkBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));

			//VCHD-5 uchun kapital tamir
			int akHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)");
			model.addAttribute("akHamma", akHamma);
			model.addAttribute("akKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy,"MDH(СНГ)"));
			model.addAttribute("akPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));
			model.addAttribute("akPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));
			model.addAttribute("akSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));
			model.addAttribute("akBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)"));

			// itogo Fact uchun kapital tamir
			int factkhamma = skHamma + hkHamma + akHamma;
			model.addAttribute("factkhamma", factkhamma);
			int boshqaKPlan = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "MDH(СНГ)");
			model.addAttribute("boshqaKPlan", boshqaKPlan);


			//samarqand uchun KRP tamir
			int skrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "KRP(КРП)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy,"MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "KRP(КРП)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "MDH(СНГ)");
			model.addAttribute("skrHamma", skrHamma);
			model.addAttribute("skrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "MDH(СНГ)"));
			model.addAttribute("skrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "KRP(КРП)", oy,"MDH(СНГ)"));
			model.addAttribute("skrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "MDH(СНГ)"));
			model.addAttribute("skrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "KRP(КРП)", oy, "MDH(СНГ)"));
			model.addAttribute("skrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "MDH(СНГ)"));

			//VCHD-3 uchun KRP
			int hkrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "KRP(КРП)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "KRP(КРП)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)", oy,"MDH(СНГ)");
			model.addAttribute("hkrHamma", hkrHamma);
			model.addAttribute("hkrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "MDH(СНГ)"));
			model.addAttribute("hkrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "KRP(КРП)", oy, "MDH(СНГ)"));
			model.addAttribute("hkrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "MDH(СНГ)"));
			model.addAttribute("hkrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "KRP(КРП)", oy, "MDH(СНГ)"));
			model.addAttribute("hkrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "MDH(СНГ)"));

			//VCHD-5 uchun KRP
			int akrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "KRP(КРП)", oy,"MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "KRP(КРП)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy,"MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "KRP(КРП)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "MDH(СНГ)");
			model.addAttribute("akrHamma", akrHamma);
			model.addAttribute("akrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "MDH(СНГ)"));
			model.addAttribute("akrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "KRP(КРП)", oy, "MDH(СНГ)"));
			model.addAttribute("akrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "MDH(СНГ)"));
			model.addAttribute("akrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "KRP(КРП)", oy,"MDH(СНГ)"));
			model.addAttribute("akrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "MDH(СНГ)"));

			// itogo Fact uchun KRP
			int factkrhamma = skrHamma + hkrHamma + akrHamma;
			model.addAttribute("factkrhamma", factkrhamma);
			int boshqaKr = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)", oy,"MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "MDH(СНГ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "MDH(СНГ)");
			model.addAttribute("boshqaKr", boshqaKr);

		}else if (country.equalsIgnoreCase("Sanoat(ПРОМ)")) {

			//samarqand uchun depli tamir
			int sdHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy,"Sanoat(ПРОМ)");
			model.addAttribute("sdHamma", sdHamma);
			model.addAttribute("sdKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("sdPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("sdPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("sdSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("sdBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));

			//VCHD-3 uchun depli tamir
			int hdHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy,"Sanoat(ПРОМ)");
			model.addAttribute("hdHamma", hdHamma);
			model.addAttribute("hdKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("hdPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("hdPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("hdSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("hdBoshqaPlan", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));

			//VCHD-5 uchun depli tamir
			int adHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy,"Sanoat(ПРОМ)");
			model.addAttribute("adHamma", adHamma);
			model.addAttribute("adKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("adPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("adPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("adSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("adBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)"));

			// itogo Fact uchun depli tamir
			int factdhamma = sdHamma + hdHamma + adHamma;
			model.addAttribute("factdhamma", factdhamma);
			int boshqaPlan = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", oy, "Sanoat(ПРОМ)");
			model.addAttribute("boshqaPlan", boshqaPlan);

			//Yolovchi vagon Fact
			model.addAttribute("atYolovchi", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","TO-3", oy));
			model.addAttribute("adYolovchi", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","Depoli ta’mir(ДР)", oy));


			//samarqand uchun Kapital tamir
			int skHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)");
			model.addAttribute("skHamma", skHamma);
			model.addAttribute("skKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("skPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("skPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("skSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("skBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy,"Sanoat(ПРОМ)"));

			//VCHD-3 uchun kapital tamir
			int hkHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy,"Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)");
			model.addAttribute("hkHamma", hkHamma);
			model.addAttribute("hkKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("hkPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("hkPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("hkSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("hkBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));

			//VCHD-5 uchun kapital tamir
			int akHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)");
			model.addAttribute("akHamma", akHamma);
			model.addAttribute("akKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)", oy,"Sanoat(ПРОМ)"));
			model.addAttribute("akPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("akPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("akSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("akBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)"));

			// itogo Fact uchun kapital tamir
			int factkhamma = skHamma + hkHamma + akHamma;
			model.addAttribute("factkhamma", factkhamma);
			int boshqaKPlan = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)", oy, "Sanoat(ПРОМ)");
			model.addAttribute("boshqaKPlan", boshqaKPlan);


			//samarqand uchun KRP tamir
			int skrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "KRP(КРП)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy,"Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "KRP(КРП)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "Sanoat(ПРОМ)");
			model.addAttribute("skrHamma", skrHamma);
			model.addAttribute("skrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("skrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "KRP(КРП)", oy,"Sanoat(ПРОМ)"));
			model.addAttribute("skrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("skrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("skrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));

			//VCHD-3 uchun KRP
			int hkrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "KRP(КРП)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "KRP(КРП)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)", oy,"Sanoat(ПРОМ)");
			model.addAttribute("hkrHamma", hkrHamma);
			model.addAttribute("hkrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("hkrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("hkrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("hkrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("hkrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));

			//VCHD-5 uchun KRP
			int akrHamma = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "KRP(КРП)", oy,"Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "KRP(КРП)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy,"Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "KRP(КРП)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "Sanoat(ПРОМ)");
			model.addAttribute("akrHamma", akrHamma);
			model.addAttribute("akrKriti", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("akrPlatforma", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("akrPoluvagon", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));
			model.addAttribute("akrSisterna", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "KRP(КРП)", oy,"Sanoat(ПРОМ)"));
			model.addAttribute("akrBoshqa", vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "Sanoat(ПРОМ)"));

			// itogo Fact uchun KRP
			int factkrhamma = skrHamma + hkrHamma + akrHamma;
			model.addAttribute("factkrhamma", factkrhamma);
			int boshqaKr = vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)", oy,"Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "Sanoat(ПРОМ)") +
					vagonTayyorService.countAllActiveByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)", oy, "Sanoat(ПРОМ)");
			model.addAttribute("boshqaKr", boshqaKr);
		}
	   	 return "AllPlanTable";
    }

    @PreAuthorize(value = "hasAnyRole('DIRECTOR', 'SAM','HAV','ANDJ')")
   	@GetMapping("/vagons/filterAllMonth")
   	public String filterByDepoNomiForAllMonths(Model model,  @RequestParam(value = "depoNomi", required = false) String depoNomi,
   												@RequestParam(value = "vagonTuri", required = false) String vagonTuri,
   												@RequestParam(value = "country", required = false) String country) {
   		if(!depoNomi.equalsIgnoreCase("Hammasi") && !vagonTuri.equalsIgnoreCase("Hammasi") && !country.equalsIgnoreCase("Hammasi") ) {
   			vagonsToDownload = vagonTayyorService.findByDepoNomiVagonTuriAndCountry(depoNomi, vagonTuri, country);
   			model.addAttribute("vagons", vagonTayyorService.findByDepoNomiVagonTuriAndCountry(depoNomi, vagonTuri, country));
   		}else if(depoNomi.equalsIgnoreCase("Hammasi") && !vagonTuri.equalsIgnoreCase("Hammasi") && !country.equalsIgnoreCase("Hammasi")){
   			vagonsToDownload = vagonTayyorService.findByVagonTuriAndCountry(vagonTuri , country);
   			model.addAttribute("vagons", vagonTayyorService.findByVagonTuriAndCountry(vagonTuri , country));
   		}else if(depoNomi.equalsIgnoreCase("Hammasi") && vagonTuri.equalsIgnoreCase("Hammasi")&& !country.equalsIgnoreCase("Hammasi")){
   			vagonsToDownload = vagonTayyorService.findBycountry(country );
   			model.addAttribute("vagons", vagonTayyorService.findBycountry(country ));
   		}else if(!depoNomi.equalsIgnoreCase("Hammasi") && !vagonTuri.equalsIgnoreCase("Hammasi") && country.equalsIgnoreCase("Hammasi")){
   			vagonsToDownload = vagonTayyorService.findByDepoNomiAndVagonTuri(depoNomi, vagonTuri);
   			model.addAttribute("vagons", vagonTayyorService.findByDepoNomiAndVagonTuri(depoNomi, vagonTuri));
   		}else if(!depoNomi.equalsIgnoreCase("Hammasi") && vagonTuri.equalsIgnoreCase("Hammasi") && !country.equalsIgnoreCase("Hammasi")){
   			vagonsToDownload = vagonTayyorService.findByDepoNomiAndCountry(depoNomi, country);
   			model.addAttribute("vagons", vagonTayyorService.findByDepoNomiAndCountry(depoNomi, country));
   		}else if(depoNomi.equalsIgnoreCase("Hammasi") && !vagonTuri.equalsIgnoreCase("Hammasi") && country.equalsIgnoreCase("Hammasi")){
   			vagonsToDownload = vagonTayyorService.findByVagonTuri(vagonTuri);
   			model.addAttribute("vagons", vagonTayyorService.findByVagonTuri(vagonTuri));
   		}else if(!depoNomi.equalsIgnoreCase("Hammasi") && vagonTuri.equalsIgnoreCase("Hammasi") && country.equalsIgnoreCase("Hammasi")){
   			vagonsToDownload = vagonTayyorService.findByDepoNomi(depoNomi );
   			model.addAttribute("vagons", vagonTayyorService.findByDepoNomi(depoNomi ));
   		}else {
   			vagonsToDownload = vagonTayyorService.findAll( );
   			model.addAttribute("vagons", vagonTayyorService.findAll());
   		}

		PlanBiznes planDto = vagonTayyorService.getPlanBiznes();
		//planlar kiritish

		//samarqand depo tamir plan
		int sdKriti = planDto.getSamDtKritiPlanBiznesMonths();
		int sdPlatforma=planDto.getSamDtPlatformaPlanBiznesMonths();
		int sdPoluvagon=planDto.getSamDtPoluvagonPlanBiznesMonths();
		int sdSisterna=planDto.getSamDtSisternaPlanBiznesMonths();
		int sdBoshqa=planDto.getSamDtBoshqaPlanBiznesMonths();
		int SamDtHammaPlan=sdKriti+sdPlatforma+sdPoluvagon+sdSisterna+sdBoshqa;

		model.addAttribute("SamDtHammaPlan",SamDtHammaPlan);
		model.addAttribute("SamDtKritiPlan", sdKriti);
		model.addAttribute("SamDtPlatformaPlan", sdPlatforma);
		model.addAttribute("SamDtPoluvagonPlan", sdPoluvagon);
		model.addAttribute("SamDtSisternaPlan", sdSisterna);
		model.addAttribute("SamDtBoshqaPlan", sdBoshqa);

		//havos depo tamir hamma plan
		int hdKriti = planDto.getHavDtKritiPlanBiznesMonths();
		int hdPlatforma=planDto.getHavDtPlatformaPlanBiznesMonths();
		int hdPoluvagon=planDto.getHavDtPoluvagonPlanBiznesMonths();
		int hdSisterna=planDto.getHavDtSisternaPlanBiznesMonths();
		int hdBoshqa=planDto.getHavDtBoshqaPlanBiznesMonths();
		int HavDtHammaPlan = hdKriti + hdPlatforma + hdPoluvagon + hdSisterna + hdBoshqa;

		model.addAttribute("HavDtHammaPlan", HavDtHammaPlan);
		model.addAttribute("HavDtKritiPlan", hdKriti);
		model.addAttribute("HavDtPlatformaPlan", hdPlatforma);
		model.addAttribute("HavDtPoluvagonPlan", hdPoluvagon);
		model.addAttribute("HavDtSisternaPlan", hdSisterna);
		model.addAttribute("HavDtBoshqaPlan", hdBoshqa);

		//VCHD-5 depo tamir plan
		int adKriti = planDto.getAndjDtKritiPlanBiznesMonths();
		int adPlatforma=planDto.getAndjDtPlatformaPlanBiznesMonths();
		int adPoluvagon=planDto.getAndjDtPoluvagonPlanBiznesMonths();
		int adSisterna=planDto.getAndjDtSisternaPlanBiznesMonths();
		int adBoshqa=planDto.getAndjDtBoshqaPlanBiznesMonths();
		int AndjDtHammaPlan = adKriti + adPlatforma + adPoluvagon + adSisterna + adBoshqa;

		model.addAttribute("AndjDtHammaPlan", AndjDtHammaPlan);
		model.addAttribute("AndjDtKritiPlan", adKriti);
		model.addAttribute("AndjDtPlatformaPlan",adPlatforma);
		model.addAttribute("AndjDtPoluvagonPlan", adPoluvagon);
		model.addAttribute("AndjDtSisternaPlan", adSisterna);
		model.addAttribute("AndjDtBoshqaPlan", adBoshqa);

		// Itogo planlar depo tamir
		model.addAttribute("DtHammaPlan", AndjDtHammaPlan + HavDtHammaPlan + SamDtHammaPlan);
		model.addAttribute("DtKritiPlan", sdKriti + hdKriti + adKriti);
		model.addAttribute("DtPlatformaPlan", sdPlatforma + hdPlatforma + adPlatforma);
		model.addAttribute("DtPoluvagonPlan",sdPoluvagon + hdPoluvagon + adPoluvagon);
		model.addAttribute("DtSisternaPlan", sdSisterna + hdSisterna + adSisterna);
		model.addAttribute("DtBoshqaPlan", sdBoshqa + hdBoshqa + adBoshqa);

		//Yolovchi vagon Plan
		int atYolovchi = planDto.getAndjTYolovchiPlanBiznesMonths();
		int adYolovchi = planDto.getAndjDtYolovchiPlanBiznesMonths();

		model.addAttribute("AndjToYolovchiPlan", atYolovchi);
		model.addAttribute("AndjDtYolovchiPlan", adYolovchi);

		//Samrqand kapital plan
		int skKriti = planDto.getSamKtKritiPlanBiznesMonths();
		int skPlatforma=planDto.getSamKtPlatformaPlanBiznesMonths();
		int skPoluvagon=planDto.getSamKtPoluvagonPlanBiznesMonths();
		int skSisterna=planDto.getSamKtSisternaPlanBiznesMonths();
		int skBoshqa=planDto.getSamKtBoshqaPlanBiznesMonths();
		int SamKtHammaPlan=skKriti+skPlatforma+skPoluvagon+skSisterna+skBoshqa;

		model.addAttribute("SamKtHammaPlan",SamKtHammaPlan);
		model.addAttribute("SamKtKritiPlan", skKriti);
		model.addAttribute("SamKtPlatformaPlan", skPlatforma);
		model.addAttribute("SamKtPoluvagonPlan", skPoluvagon);
		model.addAttribute("SamKtSisternaPlan", skSisterna);
		model.addAttribute("SamKtBoshqaPlan", skBoshqa);

		//hovos kapital plan
		int hkKriti = planDto.getHavKtKritiPlanBiznesMonths();
		int hkPlatforma=planDto.getHavKtPlatformaPlanBiznesMonths();
		int hkPoluvagon=planDto.getHavKtPoluvagonPlanBiznesMonths();
		int hkSisterna=planDto.getHavKtSisternaPlanBiznesMonths();
		int hkBoshqa=planDto.getHavKtBoshqaPlanBiznesMonths();
		int HavKtHammaPlan = hkKriti + hkPlatforma + hkPoluvagon + hkSisterna + hkBoshqa;

		model.addAttribute("HavKtHammaPlan", HavKtHammaPlan);
		model.addAttribute("HavKtKritiPlan", hkKriti);
		model.addAttribute("HavKtPlatformaPlan", hkPlatforma);
		model.addAttribute("HavKtPoluvagonPlan", hkPoluvagon);
		model.addAttribute("HavKtSisternaPlan", hkSisterna);
		model.addAttribute("HavKtBoshqaPlan", hkBoshqa);

		//ANDIJON kapital plan
		int akKriti = planDto.getAndjKtKritiPlanBiznesMonths();
		int akPlatforma=planDto.getAndjKtPlatformaPlanBiznesMonths();
		int akPoluvagon=planDto.getAndjKtPoluvagonPlanBiznesMonths();
		int akSisterna=planDto.getAndjKtSisternaPlanBiznesMonths();
		int akBoshqa=planDto.getAndjKtBoshqaPlanBiznesMonths();
		int AndjKtHammaPlan = akKriti + akPlatforma + akPoluvagon + akSisterna + akBoshqa;


		model.addAttribute("AndjKtHammaPlan", AndjKtHammaPlan);
		model.addAttribute("AndjKtKritiPlan", akKriti);
		model.addAttribute("AndjKtPlatformaPlan", akPlatforma);
		model.addAttribute("AndjKtPoluvagonPlan", akPoluvagon);
		model.addAttribute("AndjKtSisternaPlan", akSisterna);
		model.addAttribute("AndjKtBoshqaPlan", akBoshqa);

		//Itogo kapital plan
		model.addAttribute("KtHammaPlan", AndjKtHammaPlan + HavKtHammaPlan + SamKtHammaPlan);
		model.addAttribute("KtKritiPlan", skKriti + hkKriti + akKriti);
		model.addAttribute("KtPlatformaPlan", skPlatforma + hkPlatforma + akPlatforma);
		model.addAttribute("KtPoluvagonPlan",skPoluvagon + hkPoluvagon + akPoluvagon);
		model.addAttribute("KtSisternaPlan", skSisterna + hkSisterna + akSisterna);
		model.addAttribute("KtBoshqaPlan", skBoshqa + hkBoshqa + akBoshqa);

		//Samarqankr Krp plan
		int skrKriti = planDto.getSamKrpKritiPlanBiznesMonths();
		int skrPlatforma=planDto.getSamKrpPlatformaPlanBiznesMonths();
		int skrPoluvagon=planDto.getSamKrpPoluvagonPlanBiznesMonths();
		int skrSisterna=planDto.getSamKrpSisternaPlanBiznesMonths();
		int skrBoshqa=planDto.getSamKrpBoshqaPlanBiznesMonths();
		int SamKrpHammaPlan=skrKriti+skrPlatforma+skrPoluvagon+skrSisterna+skrBoshqa;

		model.addAttribute("SamKrpHammaPlan", SamKrpHammaPlan);
		model.addAttribute("SamKrpKritiPlan", skrKriti);
		model.addAttribute("SamKrpPlatformaPlan", skrPlatforma);
		model.addAttribute("SamKrpPoluvagonPlan", skrPoluvagon);
		model.addAttribute("SamKrpSisternaPlan", skrSisterna);
		model.addAttribute("SamKrpBoshqaPlan", skrBoshqa);

		//Hovos krp plan
		int hkrKriti = planDto.getHavKrpKritiPlanBiznesMonths();
		int hkrPlatforma=planDto.getHavKrpPlatformaPlanBiznesMonths();
		int hkrPoluvagon=planDto.getHavKrpPoluvagonPlanBiznesMonths();
		int hkrSisterna=planDto.getHavKrpSisternaPlanBiznesMonths();
		int hkrBoshqa=planDto.getHavKrpBoshqaPlanBiznesMonths();
		int HavKrpHammaPlan = hkrKriti + hkrPlatforma + hkrPoluvagon + hkrSisterna + hkrBoshqa;

		model.addAttribute("HavKrpHammaPlan",HavKrpHammaPlan);
		model.addAttribute("HavKrpKritiPlan", hkrKriti);
		model.addAttribute("HavKrpPlatformaPlan", hkrPlatforma);
		model.addAttribute("HavKrpPoluvagonPlan", hkrPoluvagon);
		model.addAttribute("HavKrpSisternaPlan", hkrSisterna);
		model.addAttribute("HavKrpBoshqaPlan", hkrBoshqa);

		//andijon krp plan
		int akrKriti = planDto.getAndjKrpKritiPlanBiznesMonths();
		int akrPlatforma=planDto.getAndjKrpPlatformaPlanBiznesMonths();
		int akrPoluvagon=planDto.getAndjKrpPoluvagonPlanBiznesMonths();
		int akrSisterna=planDto.getAndjKrpSisternaPlanBiznesMonths();
		int akrBoshqa=planDto.getAndjKrpBoshqaPlanBiznesMonths();
		int AndjKrpHammaPlan = akrKriti + akrPlatforma + akrPoluvagon + akrSisterna + akrBoshqa;

		model.addAttribute("AndjKrpHammaPlan",AndjKrpHammaPlan);
		model.addAttribute("AndjKrpKritiPlan", akrKriti);
		model.addAttribute("AndjKrpPlatformaPlan", akrPlatforma);
		model.addAttribute("AndjKrpPoluvagonPlan", akrPoluvagon);
		model.addAttribute("AndjKrpSisternaPlan", akrSisterna);
		model.addAttribute("AndjKrpBoshqaPlan", akrBoshqa);

		//itogo krp
		model.addAttribute("KrpHammaPlan", AndjKrpHammaPlan + HavKrpHammaPlan + SamKrpHammaPlan);
		model.addAttribute("KrpKritiPlan", skrKriti + hkrKriti + akrKriti);
		model.addAttribute("KrpPlatformaPlan", skrPlatforma + hkrPlatforma + akrPlatforma);
		model.addAttribute("KrpPoluvagonPlan",akrPoluvagon + hkrPoluvagon + skrPoluvagon);
		model.addAttribute("KrpSisternaPlan", skrSisterna + hkrSisterna + akrSisterna);
		model.addAttribute("KrpBoshqaPlan", skrBoshqa + hkrBoshqa + akrBoshqa);


		//**//Factlar
//		if(country.equalsIgnoreCase("Hammasi")) {
			// samarqand depo tamir hamma false vagonlar soni
			int sdKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Depoli ta’mir(ДР)");
			int sdPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Depoli ta’mir(ДР)");
			int sdPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)");
			int sdSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Depoli ta’mir(ДР)");
			int sdBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Depoli ta’mir(ДР)");
			int sdHammaFalse = sdKritiFalse + sdPlatformaFalse+ sdPoluvagonFalse+ sdSisternaFalse + sdBoshqaFalse;

			model.addAttribute("sdHammaFalse",sdHammaFalse+393);
			model.addAttribute("sdKritiFalse",sdKritiFalse+135);
			model.addAttribute("sdPlatformaFalse",sdPlatformaFalse+8);
			model.addAttribute("sdPoluvagonFalse",sdPoluvagonFalse+67);
			model.addAttribute("sdSisternaFalse",sdSisternaFalse+23);
			model.addAttribute("sdBoshqaFalse",sdBoshqaFalse+160);

			// VCHD-3 depo tamir hamma false vagonlar soni
			int hdKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Depoli ta’mir(ДР)");
			int hdPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Depoli ta’mir(ДР)");
			int hdPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)");
			int hdSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Depoli ta’mir(ДР)");
			int hdBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Depoli ta’mir(ДР)");
			int hdHammaFalse = hdKritiFalse + hdPlatformaFalse+ hdPoluvagonFalse+ hdSisternaFalse + hdBoshqaFalse;

			model.addAttribute("hdHammaFalse",hdHammaFalse+651);
			model.addAttribute("hdKritiFalse",hdKritiFalse+35);
			model.addAttribute("hdPlatformaFalse",hdPlatformaFalse+45);
			model.addAttribute("hdPoluvagonFalse",hdPoluvagonFalse+109);
			model.addAttribute("hdSisternaFalse",hdSisternaFalse+35);
			model.addAttribute("hdBoshqaFalse",hdBoshqaFalse+427);

			// VCHD-5 depo tamir hamma false vagonlar soni
			int adKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Depoli ta’mir(ДР)");
			int adPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Depoli ta’mir(ДР)");
			int adPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Depoli ta’mir(ДР)");
			int adSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Depoli ta’mir(ДР)");
			int adBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Depoli ta’mir(ДР)");
			int adHammaFalse = adKritiFalse + adPlatformaFalse+ adPoluvagonFalse+ adSisternaFalse + adBoshqaFalse;

			model.addAttribute("adHammaFalse",adHammaFalse+443);
			model.addAttribute("adKritiFalse",adKritiFalse+224);
			model.addAttribute("adPlatformaFalse",adPlatformaFalse+3);
			model.addAttribute("adPoluvagonFalse",adPoluvagonFalse+103);
			model.addAttribute("adSisternaFalse",adSisternaFalse);
			model.addAttribute("adBoshqaFalse",adBoshqaFalse+113);

			// depoli tamir itogo uchun
			int dHammaFalse =  adHammaFalse + hdHammaFalse+sdHammaFalse;
			int dKritiFalse = sdKritiFalse + hdKritiFalse + adKritiFalse;
			int dPlatforma = adPlatformaFalse + sdPlatformaFalse + hdPlatformaFalse;
			int dPoluvagon  = adPoluvagonFalse + sdPoluvagonFalse + hdPoluvagonFalse;
			int dSisterna = adSisternaFalse + hdSisternaFalse + sdSisternaFalse;
			int dBoshqa = adBoshqaFalse + hdBoshqaFalse + sdBoshqaFalse;

			model.addAttribute("dHammaFalse",dHammaFalse+1487);
			model.addAttribute("dKritiFalse",dKritiFalse+394);
			model.addAttribute("dPlatforma",dPlatforma+56);
			model.addAttribute("dPoluvagon",dPoluvagon+279);
			model.addAttribute("dSisterna",dSisterna+58);
			model.addAttribute("dBoshqa",dBoshqa+700);


		//Yolovchi Andijon fact
		int atYolovchiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","TO-3");
		int adYolovchiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yo'lovchi vagon(пассжир)","Depoli ta’mir(ДР)");

		model.addAttribute("atYolovchi",atYolovchiFalse+37);
		model.addAttribute("adYolovchi",adYolovchiFalse+24);


		// samarqand KApital tamir hamma false vagonlar soni
			int skKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","Kapital ta’mir(КР)");
			int skPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","Kapital ta’mir(КР)");
			int skPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)");
			int skSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","Kapital ta’mir(КР)");
			int skBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","Kapital ta’mir(КР)");
			int skHammaFalse = skKritiFalse + skPlatformaFalse+ skPoluvagonFalse+ skSisternaFalse + skBoshqaFalse;

			model.addAttribute("skHammaFalse",skHammaFalse+284);
			model.addAttribute("skKritiFalse",skKritiFalse+160);
			model.addAttribute("skPlatformaFalse",skPlatformaFalse+44);
			model.addAttribute("skPoluvagonFalse",skPoluvagonFalse+52);
			model.addAttribute("skSisternaFalse",skSisternaFalse+9);
			model.addAttribute("skBoshqaFalse",skBoshqaFalse+19);

			// VCHD-3 kapital tamir hamma false vagonlar soni
			int hkKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","Kapital ta’mir(КР)");
			int hkPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","Kapital ta’mir(КР)");
			int hkPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)");
			int hkSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","Kapital ta’mir(КР)");
			int hkBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","Kapital ta’mir(КР)");
			int hkHammaFalse = hkKritiFalse + hkPlatformaFalse+ hkPoluvagonFalse+ hkSisternaFalse + hkBoshqaFalse;

			model.addAttribute("hkHammaFalse",hkHammaFalse+227);
			model.addAttribute("hkKritiFalse",hkKritiFalse+41);
			model.addAttribute("hkPlatformaFalse",hkPlatformaFalse+32);
			model.addAttribute("hkPoluvagonFalse",hkPoluvagonFalse+4);
			model.addAttribute("hkSisternaFalse",hkSisternaFalse+51);
			model.addAttribute("hkBoshqaFalse",hkBoshqaFalse+99);

			// VCHD-5 kapital tamir hamma false vagonlar soni
			int akKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","Kapital ta’mir(КР)");
			int akPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","Kapital ta’mir(КР)");
			int akPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","Kapital ta’mir(КР)");
			int akSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","Kapital ta’mir(КР)");
			int akBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","Kapital ta’mir(КР)");
			int akHammaFalse = akKritiFalse + akPlatformaFalse+ akPoluvagonFalse+ akSisternaFalse + akBoshqaFalse;

			model.addAttribute("akHammaFalse",akHammaFalse+28);
			model.addAttribute("akKritiFalse",akKritiFalse+26);
			model.addAttribute("akPlatformaFalse",akPlatformaFalse);
			model.addAttribute("akPoluvagonFalse",akPoluvagonFalse+2);
			model.addAttribute("akSisternaFalse",akSisternaFalse);
			model.addAttribute("akBoshqaFalse",akBoshqaFalse);

			// Kapital tamir itogo uchun
			int kHammaFalse =  akHammaFalse + hkHammaFalse+skHammaFalse;
			int kKritiFalse = skKritiFalse + hkKritiFalse + akKritiFalse;
			int kPlatforma = akPlatformaFalse + skPlatformaFalse + hkPlatformaFalse;
			int kPoluvagon  = akPoluvagonFalse + skPoluvagonFalse + hkPoluvagonFalse;
			int kSisterna = akSisternaFalse + hkSisternaFalse + skSisternaFalse;
			int kBoshqa = akBoshqaFalse + hkBoshqaFalse + skBoshqaFalse;

			model.addAttribute("kHammaFalse",kHammaFalse+539);
			model.addAttribute("kKritiFalse",kKritiFalse+227);
			model.addAttribute("kPlatforma",kPlatforma+76);
			model.addAttribute("kPoluvagon",kPoluvagon+58);
			model.addAttribute("kSisterna",kSisterna+60);
			model.addAttribute("kBoshqa",kBoshqa+118);

			//**
			// samarqand KRP tamir hamma false vagonlar soni
			int skrKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yopiq vagon (крыт)","KRP(КРП)");
			int skrPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Platforma(пф)","KRP(КРП)");
			int skrPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Yarim ochiq vagon(пв)","KRP(КРП)");
			int skrSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Sisterna(цс)","KRP(КРП)");
			int skrBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6","Boshqa turdagi(проч)","KRP(КРП)");
			int skrHammaFalse = skrKritiFalse + skrPlatformaFalse+ skrPoluvagonFalse+ skrSisternaFalse + skrBoshqaFalse;

			model.addAttribute("skrHammaFalse",skrHammaFalse+89);
			model.addAttribute("skrKritiFalse",skrKritiFalse);
			model.addAttribute("skrPlatformaFalse",skrPlatformaFalse);
			model.addAttribute("skrPoluvagonFalse",skrPoluvagonFalse+88);
			model.addAttribute("skrSisternaFalse",skrSisternaFalse+1);
			model.addAttribute("skrBoshqaFalse",skrBoshqaFalse);

			// VCHD-3 KRP tamir hamma false vagonlar soni
			int hkrKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yopiq vagon (крыт)","KRP(КРП)");
			int hkrPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Platforma(пф)","KRP(КРП)");
			int hkrPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Yarim ochiq vagon(пв)","KRP(КРП)");
			int hkrSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Sisterna(цс)","KRP(КРП)");
			int hkrBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3","Boshqa turdagi(проч)","KRP(КРП)");
			int hkrHammaFalse = hkrKritiFalse + hkrPlatformaFalse+ hkrPoluvagonFalse+ hkrSisternaFalse + hkrBoshqaFalse;

			model.addAttribute("hkrHammaFalse",hkrHammaFalse+83);
			model.addAttribute("hkrKritiFalse",hkrKritiFalse+83);
			model.addAttribute("hkrPlatformaFalse",hkrPlatformaFalse);
			model.addAttribute("hkrPoluvagonFalse",hkrPoluvagonFalse);
			model.addAttribute("hkrSisternaFalse",hkrSisternaFalse);
			model.addAttribute("hkrBoshqaFalse",hkrBoshqaFalse);

			// VCHD-5 KRP tamir hamma false vagonlar soni
			int akrKritiFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yopiq vagon (крыт)","KRP(КРП)");
			int akrPlatformaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Platforma(пф)","KRP(КРП)");
			int akrPoluvagonFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Yarim ochiq vagon(пв)","KRP(КРП)");
			int akrSisternaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Sisterna(цс)","KRP(КРП)");
			int akrBoshqaFalse=vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5","Boshqa turdagi(проч)","KRP(КРП)");
			int akrHammaFalse = akrKritiFalse + akrPlatformaFalse+ akrPoluvagonFalse+ akrSisternaFalse + akrBoshqaFalse;

			model.addAttribute("akrHammaFalse",akrHammaFalse+61);
			model.addAttribute("akrKritiFalse",akrKritiFalse);
			model.addAttribute("akrPlatformaFalse",akrPlatformaFalse);
			model.addAttribute("akrPoluvagonFalse",akrPoluvagonFalse+61);
			model.addAttribute("akrSisternaFalse",akrSisternaFalse);
			model.addAttribute("akBoshqaFalse",akBoshqaFalse);

			// Krp itogo uchun
			int krHammaFalse =  akrHammaFalse + hkrHammaFalse+skrHammaFalse;
			int krKritiFalse = skrKritiFalse + hkrKritiFalse + akrKritiFalse;
			int krPlatforma = akrPlatformaFalse + skrPlatformaFalse + hkrPlatformaFalse;
			int krPoluvagon  = akrPoluvagonFalse + skrPoluvagonFalse + hkrPoluvagonFalse;
			int krSisterna = akrSisternaFalse + hkrSisternaFalse + skrSisternaFalse;
			int krBoshqa = akrBoshqaFalse + hkrBoshqaFalse + skrBoshqaFalse;

			model.addAttribute("krHammaFalse",krHammaFalse+233);
			model.addAttribute("krKritiFalse",krKritiFalse);
			model.addAttribute("krPlatforma",krPlatforma);
			model.addAttribute("krPoluvagon",krPoluvagon+232);
			model.addAttribute("krSisterna",krSisterna+1);
			model.addAttribute("krBoshqa",krBoshqa);

//		}else if(country.equalsIgnoreCase("O'TY(ГАЖК)")) {
//
//			// samarqand depo tamir hamma false vagonlar soni
//			int sdKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int sdPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int sdPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int sdSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int sdBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int sdHammaFalse = sdKritiFalse + sdPlatformaFalse + sdPoluvagonFalse + sdSisternaFalse + sdBoshqaFalse;
//
//			model.addAttribute("sdHammaFalse", sdHammaFalse);
//			model.addAttribute("sdKritiFalse", sdKritiFalse);
//			model.addAttribute("sdPlatformaFalse", sdPlatformaFalse);
//			model.addAttribute("sdPoluvagonFalse", sdPoluvagonFalse);
//			model.addAttribute("sdSisternaFalse", sdSisternaFalse);
//			model.addAttribute("sdBoshqaFalse", sdBoshqaFalse);
//
//			// VCHD-3 depo tamir hamma false vagonlar soni
//			int hdKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int hdPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int hdPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int hdSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int hdBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int hdHammaFalse = hdKritiFalse + hdPlatformaFalse + hdPoluvagonFalse + hdSisternaFalse + hdBoshqaFalse;
//
//			model.addAttribute("hdHammaFalse", hdHammaFalse);
//			model.addAttribute("hdKritiFalse", hdKritiFalse);
//			model.addAttribute("hdPlatformaFalse", hdPlatformaFalse);
//			model.addAttribute("hdPoluvagonFalse", hdPoluvagonFalse);
//			model.addAttribute("hdSisternaFalse", hdSisternaFalse);
//			model.addAttribute("hdBoshqaFalse", hdBoshqaFalse);
//
//			// VCHD-5 depo tamir hamma false vagonlar soni
//			int adKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int adPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int adPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int adSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int adBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", "O'TY(ГАЖК)");
//			int adHammaFalse = adKritiFalse + adPlatformaFalse + adPoluvagonFalse + adSisternaFalse + adBoshqaFalse;
//
//			model.addAttribute("adHammaFalse", adHammaFalse);
//			model.addAttribute("adKritiFalse", adKritiFalse);
//			model.addAttribute("adPlatformaFalse", adPlatformaFalse);
//			model.addAttribute("adPoluvagonFalse", adPoluvagonFalse);
//			model.addAttribute("adSisternaFalse", adSisternaFalse);
//			model.addAttribute("adBoshqaFalse", adBoshqaFalse);
//
//			// depoli tamir itogo uchun
//			int dHammaFalse = adHammaFalse + hdHammaFalse + sdHammaFalse;
//			int dKritiFalse = sdKritiFalse + hdKritiFalse + adKritiFalse;
//			int dPlatforma = adPlatformaFalse + sdPlatformaFalse + hdPlatformaFalse;
//			int dPoluvagon = adPoluvagonFalse + sdPoluvagonFalse + hdPoluvagonFalse;
//			int dSisterna = adSisternaFalse + hdSisternaFalse + sdSisternaFalse;
//			int dBoshqa = adBoshqaFalse + hdBoshqaFalse + sdBoshqaFalse;
//
//			model.addAttribute("dHammaFalse", dHammaFalse);
//			model.addAttribute("dKritiFalse", dKritiFalse);
//			model.addAttribute("dPlatforma", dPlatforma);
//			model.addAttribute("dPoluvagon", dPoluvagon);
//			model.addAttribute("dSisterna", dSisterna);
//			model.addAttribute("dBoshqa", dBoshqa);
//
//
//			// samarqand KApital tamir hamma false vagonlar soni
//			int skKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int skPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int skPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int skSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int skBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int skHammaFalse = skKritiFalse + skPlatformaFalse + skPoluvagonFalse + skSisternaFalse + skBoshqaFalse;
//
//			model.addAttribute("skHammaFalse", skHammaFalse);
//			model.addAttribute("skKritiFalse", skKritiFalse);
//			model.addAttribute("skPlatformaFalse", skPlatformaFalse);
//			model.addAttribute("skPoluvagonFalse", skPoluvagonFalse);
//			model.addAttribute("skSisternaFalse", skSisternaFalse);
//			model.addAttribute("skBoshqaFalse", skBoshqaFalse);
//
//			// VCHD-3 kapital tamir hamma false vagonlar soni
//			int hkKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int hkPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int hkPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int hkSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int hkBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int hkHammaFalse = hkKritiFalse + hkPlatformaFalse + hkPoluvagonFalse + hkSisternaFalse + hkBoshqaFalse;
//
//			model.addAttribute("hkHammaFalse", hkHammaFalse);
//			model.addAttribute("hkKritiFalse", hkKritiFalse);
//			model.addAttribute("hkPlatformaFalse", hkPlatformaFalse);
//			model.addAttribute("hkPoluvagonFalse", hkPoluvagonFalse);
//			model.addAttribute("hkSisternaFalse", hkSisternaFalse);
//			model.addAttribute("hkBoshqaFalse", hkBoshqaFalse);
//
//			// VCHD-5 kapital tamir hamma false vagonlar soni
//			int akKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int akPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int akPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int akSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int akBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)" , "O'TY(ГАЖК)");
//			int akHammaFalse = akKritiFalse + akPlatformaFalse + akPoluvagonFalse + akSisternaFalse + akBoshqaFalse;
//
//			model.addAttribute("akHammaFalse", akHammaFalse);
//			model.addAttribute("akKritiFalse", akKritiFalse);
//			model.addAttribute("akPlatformaFalse", akPlatformaFalse);
//			model.addAttribute("akPoluvagonFalse", akPoluvagonFalse);
//			model.addAttribute("akSisternaFalse", akSisternaFalse);
//			model.addAttribute("akBoshqaFalse", akBoshqaFalse);
//
//			// Kapital tamir itogo uchun
//			int kHammaFalse = akHammaFalse + hkHammaFalse + skHammaFalse;
//			int kKritiFalse = skKritiFalse + hkKritiFalse + akKritiFalse;
//			int kPlatforma = akPlatformaFalse + skPlatformaFalse + hkPlatformaFalse;
//			int kPoluvagon = akPoluvagonFalse + skPoluvagonFalse + hkPoluvagonFalse;
//			int kSisterna = akSisternaFalse + hkSisternaFalse + skSisternaFalse;
//			int kBoshqa = akBoshqaFalse + hkBoshqaFalse + skBoshqaFalse;
//
//			model.addAttribute("kHammaFalse", kHammaFalse);
//			model.addAttribute("kKritiFalse", kKritiFalse);
//			model.addAttribute("kPlatforma", kPlatforma);
//			model.addAttribute("kPoluvagon", kPoluvagon);
//			model.addAttribute("kSisterna", kSisterna);
//			model.addAttribute("kBoshqa", kBoshqa);
//
//			//**
//			// samarqand KRP tamir hamma false vagonlar soni
//			int skrKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int skrPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int skrPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int skrSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int skrBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int skrHammaFalse = skrKritiFalse + skrPlatformaFalse + skrPoluvagonFalse + skrSisternaFalse + skrBoshqaFalse;
//
//			model.addAttribute("skrHammaFalse", skrHammaFalse);
//			model.addAttribute("skrKritiFalse", skrKritiFalse);
//			model.addAttribute("skrPlatformaFalse", skrPlatformaFalse);
//			model.addAttribute("skrPoluvagonFalse", skrPoluvagonFalse);
//			model.addAttribute("skrSisternaFalse", skrSisternaFalse);
//			model.addAttribute("skrBoshqaFalse", skrBoshqaFalse);
//
//			// VCHD-3 KRP tamir hamma false vagonlar soni
//			int hkrKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int hkrPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int hkrPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int hkrSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int hkrBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int hkrHammaFalse = hkrKritiFalse + hkrPlatformaFalse + hkrPoluvagonFalse + hkrSisternaFalse + hkrBoshqaFalse;
//
//			model.addAttribute("hkrHammaFalse", hkrHammaFalse);
//			model.addAttribute("hkrKritiFalse", hkrKritiFalse);
//			model.addAttribute("hkrPlatformaFalse", hkrPlatformaFalse);
//			model.addAttribute("hkrPoluvagonFalse", hkrPoluvagonFalse);
//			model.addAttribute("hkrSisternaFalse", hkrSisternaFalse);
//			model.addAttribute("hkrBoshqaFalse", hkrBoshqaFalse);
//
//			// VCHD-5 KRP tamir hamma false vagonlar soni
//			int akrKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int akrPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int akrPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int akrSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int akrBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)" , "O'TY(ГАЖК)");
//			int akrHammaFalse = akrKritiFalse + akrPlatformaFalse + akrPoluvagonFalse + akrSisternaFalse + akrBoshqaFalse;
//
//			model.addAttribute("akrHammaFalse", akrHammaFalse);
//			model.addAttribute("akrKritiFalse", akrKritiFalse);
//			model.addAttribute("akrPlatformaFalse", akrPlatformaFalse);
//			model.addAttribute("akrPoluvagonFalse", akrPoluvagonFalse);
//			model.addAttribute("akrSisternaFalse", akrSisternaFalse);
//			model.addAttribute("akBoshqaFalse", akBoshqaFalse);
//
//			// Krp itogo uchun
//			int krHammaFalse = akrHammaFalse + hkrHammaFalse + skrHammaFalse;
//			int krKritiFalse = skrKritiFalse + hkrKritiFalse + akrKritiFalse;
//			int krPlatforma = akrPlatformaFalse + skrPlatformaFalse + hkrPlatformaFalse;
//			int krPoluvagon = akrPoluvagonFalse + skrPoluvagonFalse + hkrPoluvagonFalse;
//			int krSisterna = akrSisternaFalse + hkrSisternaFalse + skrSisternaFalse;
//			int krBoshqa = akrBoshqaFalse + hkrBoshqaFalse + skrBoshqaFalse;
//
//			model.addAttribute("krHammaFalse", krHammaFalse);
//			model.addAttribute("krKritiFalse", krKritiFalse);
//			model.addAttribute("krPlatforma", krPlatforma);
//			model.addAttribute("krPoluvagon", krPoluvagon);
//			model.addAttribute("krSisterna", krSisterna);
//			model.addAttribute("krBoshqa", krBoshqa);
//
//		}else if(country.equalsIgnoreCase("MDH(СНГ)")) {
//
//			// samarqand depo tamir hamma false vagonlar soni
//			int sdKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int sdPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int sdPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int sdSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int sdBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int sdHammaFalse = sdKritiFalse + sdPlatformaFalse + sdPoluvagonFalse + sdSisternaFalse + sdBoshqaFalse;
//
//			model.addAttribute("sdHammaFalse", sdHammaFalse);
//			model.addAttribute("sdKritiFalse", sdKritiFalse);
//			model.addAttribute("sdPlatformaFalse", sdPlatformaFalse);
//			model.addAttribute("sdPoluvagonFalse", sdPoluvagonFalse);
//			model.addAttribute("sdSisternaFalse", sdSisternaFalse);
//			model.addAttribute("sdBoshqaFalse", sdBoshqaFalse);
//
//			// VCHD-3 depo tamir hamma false vagonlar soni
//			int hdKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int hdPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int hdPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int hdSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int hdBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int hdHammaFalse = hdKritiFalse + hdPlatformaFalse + hdPoluvagonFalse + hdSisternaFalse + hdBoshqaFalse;
//
//			model.addAttribute("hdHammaFalse", hdHammaFalse);
//			model.addAttribute("hdKritiFalse", hdKritiFalse);
//			model.addAttribute("hdPlatformaFalse", hdPlatformaFalse);
//			model.addAttribute("hdPoluvagonFalse", hdPoluvagonFalse);
//			model.addAttribute("hdSisternaFalse", hdSisternaFalse);
//			model.addAttribute("hdBoshqaFalse", hdBoshqaFalse);
//
//			// VCHD-5 depo tamir hamma false vagonlar soni
//			int adKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int adPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int adPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int adSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int adBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", "MDH(СНГ)");
//			int adHammaFalse = adKritiFalse + adPlatformaFalse + adPoluvagonFalse + adSisternaFalse + adBoshqaFalse;
//
//			model.addAttribute("adHammaFalse", adHammaFalse);
//			model.addAttribute("adKritiFalse", adKritiFalse);
//			model.addAttribute("adPlatformaFalse", adPlatformaFalse);
//			model.addAttribute("adPoluvagonFalse", adPoluvagonFalse);
//			model.addAttribute("adSisternaFalse", adSisternaFalse);
//			model.addAttribute("adBoshqaFalse", adBoshqaFalse);
//
//			// depoli tamir itogo uchun
//			int dHammaFalse = adHammaFalse + hdHammaFalse + sdHammaFalse;
//			int dKritiFalse = sdKritiFalse + hdKritiFalse + adKritiFalse;
//			int dPlatforma = adPlatformaFalse + sdPlatformaFalse + hdPlatformaFalse;
//			int dPoluvagon = adPoluvagonFalse + sdPoluvagonFalse + hdPoluvagonFalse;
//			int dSisterna = adSisternaFalse + hdSisternaFalse + sdSisternaFalse;
//			int dBoshqa = adBoshqaFalse + hdBoshqaFalse + sdBoshqaFalse;
//
//			model.addAttribute("dHammaFalse", dHammaFalse);
//			model.addAttribute("dKritiFalse", dKritiFalse);
//			model.addAttribute("dPlatforma", dPlatforma);
//			model.addAttribute("dPoluvagon", dPoluvagon);
//			model.addAttribute("dSisterna", dSisterna);
//			model.addAttribute("dBoshqa", dBoshqa);
//
//
//			// samarqand KApital tamir hamma false vagonlar soni
//			int skKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int skPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int skPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int skSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int skBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int skHammaFalse = skKritiFalse + skPlatformaFalse + skPoluvagonFalse + skSisternaFalse + skBoshqaFalse;
//
//			model.addAttribute("skHammaFalse", skHammaFalse);
//			model.addAttribute("skKritiFalse", skKritiFalse);
//			model.addAttribute("skPlatformaFalse", skPlatformaFalse);
//			model.addAttribute("skPoluvagonFalse", skPoluvagonFalse);
//			model.addAttribute("skSisternaFalse", skSisternaFalse);
//			model.addAttribute("skBoshqaFalse", skBoshqaFalse);
//
//			// VCHD-3 kapital tamir hamma false vagonlar soni
//			int hkKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int hkPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int hkPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int hkSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int hkBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int hkHammaFalse = hkKritiFalse + hkPlatformaFalse + hkPoluvagonFalse + hkSisternaFalse + hkBoshqaFalse;
//
//			model.addAttribute("hkHammaFalse", hkHammaFalse);
//			model.addAttribute("hkKritiFalse", hkKritiFalse);
//			model.addAttribute("hkPlatformaFalse", hkPlatformaFalse);
//			model.addAttribute("hkPoluvagonFalse", hkPoluvagonFalse);
//			model.addAttribute("hkSisternaFalse", hkSisternaFalse);
//			model.addAttribute("hkBoshqaFalse", hkBoshqaFalse);
//
//			// VCHD-5 kapital tamir hamma false vagonlar soni
//			int akKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int akPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int akPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int akSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int akBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)" , "MDH(СНГ)");
//			int akHammaFalse = akKritiFalse + akPlatformaFalse + akPoluvagonFalse + akSisternaFalse + akBoshqaFalse;
//
//			model.addAttribute("akHammaFalse", akHammaFalse);
//			model.addAttribute("akKritiFalse", akKritiFalse);
//			model.addAttribute("akPlatformaFalse", akPlatformaFalse);
//			model.addAttribute("akPoluvagonFalse", akPoluvagonFalse);
//			model.addAttribute("akSisternaFalse", akSisternaFalse);
//			model.addAttribute("akBoshqaFalse", akBoshqaFalse);
//
//			// Kapital tamir itogo uchun
//			int kHammaFalse = akHammaFalse + hkHammaFalse + skHammaFalse;
//			int kKritiFalse = skKritiFalse + hkKritiFalse + akKritiFalse;
//			int kPlatforma = akPlatformaFalse + skPlatformaFalse + hkPlatformaFalse;
//			int kPoluvagon = akPoluvagonFalse + skPoluvagonFalse + hkPoluvagonFalse;
//			int kSisterna = akSisternaFalse + hkSisternaFalse + skSisternaFalse;
//			int kBoshqa = akBoshqaFalse + hkBoshqaFalse + skBoshqaFalse;
//
//			model.addAttribute("kHammaFalse", kHammaFalse);
//			model.addAttribute("kKritiFalse", kKritiFalse);
//			model.addAttribute("kPlatforma", kPlatforma);
//			model.addAttribute("kPoluvagon", kPoluvagon);
//			model.addAttribute("kSisterna", kSisterna);
//			model.addAttribute("kBoshqa", kBoshqa);
//
//			//**
//			// samarqand KRP tamir hamma false vagonlar soni
//			int skrKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "KRP(КРП)" , "MDH(СНГ)");
//			int skrPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "KRP(КРП)" , "MDH(СНГ)");
//			int skrPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "KRP(КРП)" , "MDH(СНГ)");
//			int skrSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "KRP(КРП)" , "MDH(СНГ)");
//			int skrBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)" , "MDH(СНГ)");
//			int skrHammaFalse = skrKritiFalse + skrPlatformaFalse + skrPoluvagonFalse + skrSisternaFalse + skrBoshqaFalse;
//
//			model.addAttribute("skrHammaFalse", skrHammaFalse);
//			model.addAttribute("skrKritiFalse", skrKritiFalse);
//			model.addAttribute("skrPlatformaFalse", skrPlatformaFalse);
//			model.addAttribute("skrPoluvagonFalse", skrPoluvagonFalse);
//			model.addAttribute("skrSisternaFalse", skrSisternaFalse);
//			model.addAttribute("skrBoshqaFalse", skrBoshqaFalse);
//
//			// VCHD-3 KRP tamir hamma false vagonlar soni
//			int hkrKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "KRP(КРП)" , "MDH(СНГ)");
//			int hkrPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "KRP(КРП)" , "MDH(СНГ)");
//			int hkrPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "KRP(КРП)" , "MDH(СНГ)");
//			int hkrSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "KRP(КРП)" , "MDH(СНГ)");
//			int hkrBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)" , "MDH(СНГ)");
//			int hkrHammaFalse = hkrKritiFalse + hkrPlatformaFalse + hkrPoluvagonFalse + hkrSisternaFalse + hkrBoshqaFalse;
//
//			model.addAttribute("hkrHammaFalse", hkrHammaFalse);
//			model.addAttribute("hkrKritiFalse", hkrKritiFalse);
//			model.addAttribute("hkrPlatformaFalse", hkrPlatformaFalse);
//			model.addAttribute("hkrPoluvagonFalse", hkrPoluvagonFalse);
//			model.addAttribute("hkrSisternaFalse", hkrSisternaFalse);
//			model.addAttribute("hkrBoshqaFalse", hkrBoshqaFalse);
//
//			// VCHD-5 KRP tamir hamma false vagonlar soni
//			int akrKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "KRP(КРП)" , "MDH(СНГ)");
//			int akrPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "KRP(КРП)" , "MDH(СНГ)");
//			int akrPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "KRP(КРП)" , "MDH(СНГ)");
//			int akrSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "KRP(КРП)" , "MDH(СНГ)");
//			int akrBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)" , "MDH(СНГ)");
//			int akrHammaFalse = akrKritiFalse + akrPlatformaFalse + akrPoluvagonFalse + akrSisternaFalse + akrBoshqaFalse;
//
//			model.addAttribute("akrHammaFalse", akrHammaFalse);
//			model.addAttribute("akrKritiFalse", akrKritiFalse);
//			model.addAttribute("akrPlatformaFalse", akrPlatformaFalse);
//			model.addAttribute("akrPoluvagonFalse", akrPoluvagonFalse);
//			model.addAttribute("akrSisternaFalse", akrSisternaFalse);
//			model.addAttribute("akBoshqaFalse", akBoshqaFalse);
//
//			// Krp itogo uchun
//			int krHammaFalse = akrHammaFalse + hkrHammaFalse + skrHammaFalse;
//			int krKritiFalse = skrKritiFalse + hkrKritiFalse + akrKritiFalse;
//			int krPlatforma = akrPlatformaFalse + skrPlatformaFalse + hkrPlatformaFalse;
//			int krPoluvagon = akrPoluvagonFalse + skrPoluvagonFalse + hkrPoluvagonFalse;
//			int krSisterna = akrSisternaFalse + hkrSisternaFalse + skrSisternaFalse;
//			int krBoshqa = akrBoshqaFalse + hkrBoshqaFalse + skrBoshqaFalse;
//
//			model.addAttribute("krHammaFalse", krHammaFalse);
//			model.addAttribute("krKritiFalse", krKritiFalse);
//			model.addAttribute("krPlatforma", krPlatforma);
//			model.addAttribute("krPoluvagon", krPoluvagon);
//			model.addAttribute("krSisterna", krSisterna);
//			model.addAttribute("krBoshqa", krBoshqa);
//
//		}else if(country.equalsIgnoreCase("Sanoat(ПРОМ)")) {
//
//			// samarqand depo tamir hamma false vagonlar soni
//			int sdKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int sdPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int sdPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int sdSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int sdBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int sdHammaFalse = sdKritiFalse + sdPlatformaFalse + sdPoluvagonFalse + sdSisternaFalse + sdBoshqaFalse;
//
//			model.addAttribute("sdHammaFalse", sdHammaFalse);
//			model.addAttribute("sdKritiFalse", sdKritiFalse);
//			model.addAttribute("sdPlatformaFalse", sdPlatformaFalse);
//			model.addAttribute("sdPoluvagonFalse", sdPoluvagonFalse);
//			model.addAttribute("sdSisternaFalse", sdSisternaFalse);
//			model.addAttribute("sdBoshqaFalse", sdBoshqaFalse);
//
//			// VCHD-3 depo tamir hamma false vagonlar soni
//			int hdKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int hdPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int hdPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int hdSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int hdBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int hdHammaFalse = hdKritiFalse + hdPlatformaFalse + hdPoluvagonFalse + hdSisternaFalse + hdBoshqaFalse;
//
//			model.addAttribute("hdHammaFalse", hdHammaFalse);
//			model.addAttribute("hdKritiFalse", hdKritiFalse);
//			model.addAttribute("hdPlatformaFalse", hdPlatformaFalse);
//			model.addAttribute("hdPoluvagonFalse", hdPoluvagonFalse);
//			model.addAttribute("hdSisternaFalse", hdSisternaFalse);
//			model.addAttribute("hdBoshqaFalse", hdBoshqaFalse);
//
//			// VCHD-5 depo tamir hamma false vagonlar soni
//			int adKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int adPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int adPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int adSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int adBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Depoli ta’mir(ДР)", "Sanoat(ПРОМ)");
//			int adHammaFalse = adKritiFalse + adPlatformaFalse + adPoluvagonFalse + adSisternaFalse + adBoshqaFalse;
//
//			model.addAttribute("adHammaFalse", adHammaFalse);
//			model.addAttribute("adKritiFalse", adKritiFalse);
//			model.addAttribute("adPlatformaFalse", adPlatformaFalse);
//			model.addAttribute("adPoluvagonFalse", adPoluvagonFalse);
//			model.addAttribute("adSisternaFalse", adSisternaFalse);
//			model.addAttribute("adBoshqaFalse", adBoshqaFalse);
//
//			// depoli tamir itogo uchun
//			int dHammaFalse = adHammaFalse + hdHammaFalse + sdHammaFalse;
//			int dKritiFalse = sdKritiFalse + hdKritiFalse + adKritiFalse;
//			int dPlatforma = adPlatformaFalse + sdPlatformaFalse + hdPlatformaFalse;
//			int dPoluvagon = adPoluvagonFalse + sdPoluvagonFalse + hdPoluvagonFalse;
//			int dSisterna = adSisternaFalse + hdSisternaFalse + sdSisternaFalse;
//			int dBoshqa = adBoshqaFalse + hdBoshqaFalse + sdBoshqaFalse;
//
//			model.addAttribute("dHammaFalse", dHammaFalse);
//			model.addAttribute("dKritiFalse", dKritiFalse);
//			model.addAttribute("dPlatforma", dPlatforma);
//			model.addAttribute("dPoluvagon", dPoluvagon);
//			model.addAttribute("dSisterna", dSisterna);
//			model.addAttribute("dBoshqa", dBoshqa);
//
//
//			// samarqand KApital tamir hamma false vagonlar soni
//			int skKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int skPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int skPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int skSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int skBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int skHammaFalse = skKritiFalse + skPlatformaFalse + skPoluvagonFalse + skSisternaFalse + skBoshqaFalse;
//
//			model.addAttribute("skHammaFalse", skHammaFalse);
//			model.addAttribute("skKritiFalse", skKritiFalse);
//			model.addAttribute("skPlatformaFalse", skPlatformaFalse);
//			model.addAttribute("skPoluvagonFalse", skPoluvagonFalse);
//			model.addAttribute("skSisternaFalse", skSisternaFalse);
//			model.addAttribute("skBoshqaFalse", skBoshqaFalse);
//
//			// VCHD-3 kapital tamir hamma false vagonlar soni
//			int hkKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int hkPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int hkPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int hkSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int hkBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int hkHammaFalse = hkKritiFalse + hkPlatformaFalse + hkPoluvagonFalse + hkSisternaFalse + hkBoshqaFalse;
//
//			model.addAttribute("hkHammaFalse", hkHammaFalse);
//			model.addAttribute("hkKritiFalse", hkKritiFalse);
//			model.addAttribute("hkPlatformaFalse", hkPlatformaFalse);
//			model.addAttribute("hkPoluvagonFalse", hkPoluvagonFalse);
//			model.addAttribute("hkSisternaFalse", hkSisternaFalse);
//			model.addAttribute("hkBoshqaFalse", hkBoshqaFalse);
//
//			// VCHD-5 kapital tamir hamma false vagonlar soni
//			int akKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int akPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int akPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int akSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int akBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "Kapital ta’mir(КР)" , "Sanoat(ПРОМ)");
//			int akHammaFalse = akKritiFalse + akPlatformaFalse + akPoluvagonFalse + akSisternaFalse + akBoshqaFalse;
//
//			model.addAttribute("akHammaFalse", akHammaFalse);
//			model.addAttribute("akKritiFalse", akKritiFalse);
//			model.addAttribute("akPlatformaFalse", akPlatformaFalse);
//			model.addAttribute("akPoluvagonFalse", akPoluvagonFalse);
//			model.addAttribute("akSisternaFalse", akSisternaFalse);
//			model.addAttribute("akBoshqaFalse", akBoshqaFalse);
//
//			// Kapital tamir itogo uchun
//			int kHammaFalse = akHammaFalse + hkHammaFalse + skHammaFalse;
//			int kKritiFalse = skKritiFalse + hkKritiFalse + akKritiFalse;
//			int kPlatforma = akPlatformaFalse + skPlatformaFalse + hkPlatformaFalse;
//			int kPoluvagon = akPoluvagonFalse + skPoluvagonFalse + hkPoluvagonFalse;
//			int kSisterna = akSisternaFalse + hkSisternaFalse + skSisternaFalse;
//			int kBoshqa = akBoshqaFalse + hkBoshqaFalse + skBoshqaFalse;
//
//			model.addAttribute("kHammaFalse", kHammaFalse);
//			model.addAttribute("kKritiFalse", kKritiFalse);
//			model.addAttribute("kPlatforma", kPlatforma);
//			model.addAttribute("kPoluvagon", kPoluvagon);
//			model.addAttribute("kSisterna", kSisterna);
//			model.addAttribute("kBoshqa", kBoshqa);
//
//			//**
//			// samarqand KRP tamir hamma false vagonlar soni
//			int skrKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yopiq vagon (крыт)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int skrPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Platforma(пф)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int skrPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Yarim ochiq vagon(пв)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int skrSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Sisterna(цс)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int skrBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-6", "Boshqa turdagi(проч)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int skrHammaFalse = skrKritiFalse + skrPlatformaFalse + skrPoluvagonFalse + skrSisternaFalse + skrBoshqaFalse;
//
//			model.addAttribute("skrHammaFalse", skrHammaFalse);
//			model.addAttribute("skrKritiFalse", skrKritiFalse);
//			model.addAttribute("skrPlatformaFalse", skrPlatformaFalse);
//			model.addAttribute("skrPoluvagonFalse", skrPoluvagonFalse);
//			model.addAttribute("skrSisternaFalse", skrSisternaFalse);
//			model.addAttribute("skrBoshqaFalse", skrBoshqaFalse);
//
//			// VCHD-3 KRP tamir hamma false vagonlar soni
//			int hkrKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yopiq vagon (крыт)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int hkrPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Platforma(пф)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int hkrPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Yarim ochiq vagon(пв)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int hkrSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Sisterna(цс)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int hkrBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-3", "Boshqa turdagi(проч)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int hkrHammaFalse = hkrKritiFalse + hkrPlatformaFalse + hkrPoluvagonFalse + hkrSisternaFalse + hkrBoshqaFalse;
//
//			model.addAttribute("hkrHammaFalse", hkrHammaFalse);
//			model.addAttribute("hkrKritiFalse", hkrKritiFalse);
//			model.addAttribute("hkrPlatformaFalse", hkrPlatformaFalse);
//			model.addAttribute("hkrPoluvagonFalse", hkrPoluvagonFalse);
//			model.addAttribute("hkrSisternaFalse", hkrSisternaFalse);
//			model.addAttribute("hkrBoshqaFalse", hkrBoshqaFalse);
//
//			// VCHD-5 KRP tamir hamma false vagonlar soni
//			int akrKritiFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yopiq vagon (крыт)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int akrPlatformaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Platforma(пф)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int akrPoluvagonFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Yarim ochiq vagon(пв)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int akrSisternaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Sisterna(цс)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int akrBoshqaFalse = vagonTayyorService.countByDepoNomiVagonTuriAndTamirTuri("VCHD-5", "Boshqa turdagi(проч)", "KRP(КРП)" , "Sanoat(ПРОМ)");
//			int akrHammaFalse = akrKritiFalse + akrPlatformaFalse + akrPoluvagonFalse + akrSisternaFalse + akrBoshqaFalse;
//
//			model.addAttribute("akrHammaFalse", akrHammaFalse);
//			model.addAttribute("akrKritiFalse", akrKritiFalse);
//			model.addAttribute("akrPlatformaFalse", akrPlatformaFalse);
//			model.addAttribute("akrPoluvagonFalse", akrPoluvagonFalse);
//			model.addAttribute("akrSisternaFalse", akrSisternaFalse);
//			model.addAttribute("akBoshqaFalse", akBoshqaFalse);
//
//			// Krp itogo uchun
//			int krHammaFalse = akrHammaFalse + hkrHammaFalse + skrHammaFalse;
//			int krKritiFalse = skrKritiFalse + hkrKritiFalse + akrKritiFalse;
//			int krPlatforma = akrPlatformaFalse + skrPlatformaFalse + hkrPlatformaFalse;
//			int krPoluvagon = akrPoluvagonFalse + skrPoluvagonFalse + hkrPoluvagonFalse;
//			int krSisterna = akrSisternaFalse + hkrSisternaFalse + skrSisternaFalse;
//			int krBoshqa = akrBoshqaFalse + hkrBoshqaFalse + skrBoshqaFalse;
//
//			model.addAttribute("krHammaFalse", krHammaFalse);
//			model.addAttribute("krKritiFalse", krKritiFalse);
//			model.addAttribute("krPlatforma", krPlatforma);
//			model.addAttribute("krPoluvagon", krPoluvagon);
//			model.addAttribute("krSisterna", krSisterna);
//			model.addAttribute("krBoshqa", krBoshqa);
//		}
    	return "planTableForMonths";
    }

}
