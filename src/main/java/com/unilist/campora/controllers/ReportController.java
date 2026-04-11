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
import com.unilist.campora.utils.enums.ReportTargetType;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;
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



        boolean isCurrUserListing = currentUser.getListings().stream().anyMatch(listing -> listing.getId().equals(req.getListingId()));

        if(isCurrUserListing){
            return ResponseEntity.badRequest().body("You cannot report your listing");
        }

        boolean alreadyReportedListing = currentUser.getReports().stream()
                .anyMatch(report -> report.getReporterId().equals(req.getListingId()));
        boolean alreadyReportedUser = currentUser.getReports().stream()
                .anyMatch(report -> report.getTargetId().equals(req.getTargetUserId()));

        if(alreadyReportedListing){
            return ResponseEntity.badRequest().body("You have already reported this listing");
        }
        if(alreadyReportedUser){
            return ResponseEntity.badRequest().body("You have already reported this user");

        }
        UUID targetId;
        if(req.getTargetType() == ReportTargetType.USER){
            targetId = req.getTargetUserId();
        }else if(req.getTargetType() == ReportTargetType.LISTING){
            listingRepository.findById(req.getListingId())
                    .orElseThrow(()-> new RuntimeException("Listing not found"));
            targetId = req.getListingId();
        }else {
            throw new IllegalArgumentException("Invalid targetType or missing targetId");
        }

        Report report = new Report();
        report.setTargetType(req.getTargetType());
        report.setReporterId(req.getReporterId());
        report.setTargetId(targetId);
        report.setReason(req.getReason());
        report.setDescription(req.getDescription());
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
