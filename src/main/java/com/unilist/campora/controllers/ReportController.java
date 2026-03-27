package com.unilist.campora.controllers;


import com.unilist.campora.dto.reports.CreateReportDto;
import com.unilist.campora.model.Listing;
import com.unilist.campora.model.Report;
import com.unilist.campora.model.User;
import com.unilist.campora.repository.ListingRepository;
import com.unilist.campora.repository.ReportRepository;
import com.unilist.campora.repository.UserRepository;
import com.unilist.campora.services.UserService;
import com.unilist.campora.utils.enums.ReportStatus;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/reports")
@Controller
public class ReportController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ReportRepository reportRepository;
    private final ListingRepository listingRepository;

    public ReportController(UserRepository userRepository, UserService userService, ReportRepository reportRepository, ListingRepository listingRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.reportRepository = reportRepository;
        this.listingRepository = listingRepository;
    }


    @PostMapping("/create")
    public ResponseEntity<?> createReport(@RequestBody CreateReportDto req){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        listingRepository.findById(req.listingId())
                .orElseThrow(()-> new RuntimeException("Listing not found"));
        boolean isCurrUserListing = currentUser.getListings().stream().anyMatch(listing -> listing.getId().equals(req.listingId()));
        if(isCurrUserListing){
            return ResponseEntity.badRequest().body("You cannot report your listing");
        }
        boolean alreadyReported = currentUser.getReports().stream()
                .anyMatch(report -> report.getListingId().equals(req.listingId()));
        System.out.println("User already reported: "+alreadyReported);

        if(alreadyReported){
            return ResponseEntity.badRequest().body("You have already reported this listing");
        }

        Report report = new Report();
        report.setReporterId(req.reporterId());
        report.setListingId(req.listingId());
        report.setReason(req.reason());
        report.setDescription(req.description());
        report.setStatus(ReportStatus.PENDING);
        report.setUser(currentUser);
        reportRepository.save(report);

        return ResponseEntity.ok().body("Reported created!");

    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllReports(){
        return ResponseEntity.ok().body(reportRepository.findAll());
    }
}
