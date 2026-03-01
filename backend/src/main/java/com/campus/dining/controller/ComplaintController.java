package com.campus.dining.controller;

import com.campus.dining.common.PageResult;
import com.campus.dining.common.Result;
import com.campus.dining.dto.ComplaintDTO;
import com.campus.dining.entity.Complaint;
import com.campus.dining.security.LoginUser;
import com.campus.dining.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    /**
     * 创建投诉
     */
    @PostMapping
    public Result<Complaint> createComplaint(@Valid @RequestBody ComplaintDTO dto) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(complaintService.createComplaint(userId, dto));
    }

    /**
     * 查询当前用户投诉列表
     */
    @GetMapping("/mine")
    public Result<PageResult<Complaint>> listMyComplaints(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        return Result.success(complaintService.listComplaintsByUser(userId, page, size));
    }
}
