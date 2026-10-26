package com.rentalplatform.controller;

import com.rentalplatform.common.result.PageResult;
import com.rentalplatform.common.result.R;
import com.rentalplatform.model.dto.HouseAuditDTO;
import com.rentalplatform.model.dto.HouseSaveDTO;
import com.rentalplatform.model.query.HouseQuery;
import com.rentalplatform.model.vo.HouseDetailVO;
import com.rentalplatform.model.vo.HouseItemVO;
import com.rentalplatform.service.HouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "房源")
@RestController
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

    @Operation(summary = "房源列表（公开；ADMIN 可查全量）")
    @GetMapping("/houses")
    public R<PageResult<HouseItemVO>> page(HouseQuery query) {
        return R.ok(houseService.page(query));
    }

    @Operation(summary = "地图找房（W5 实现矩形查询）")
    @GetMapping("/houses/map")
    public R<Object> map() {
        return R.fail(com.rentalplatform.common.result.ResultCode.NOT_FOUND, "W5 实现");
    }

    @Operation(summary = "我发布的房源（LANDLORD）")
    @PreAuthorize("hasRole('LANDLORD')")
    @GetMapping("/houses/mine")
    public R<PageResult<HouseItemVO>> mine(HouseQuery query) {
        return R.ok(houseService.mine(query));
    }

    @Operation(summary = "待审核列表（ADMIN）")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/houses/audit/pending")
    public R<PageResult<HouseItemVO>> pending(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(houseService.pendingAudit(pageNum, pageSize));
    }

    @Operation(summary = "房源详情（公开；未审核仅本人/ADMIN）")
    @GetMapping("/houses/{id}")
    public R<HouseDetailVO> detail(@PathVariable long id) {
        return R.ok(houseService.detail(id));
    }

    @Operation(summary = "发布房源（LANDLORD）")
    @PreAuthorize("hasRole('LANDLORD')")
    @PostMapping("/houses")
    public R<Long> create(@RequestBody @Valid HouseSaveDTO dto) {
        return R.ok(houseService.create(dto));
    }

    @Operation(summary = "编辑房源（LANDLORD 本人 / ADMIN）")
    @PreAuthorize("hasAnyRole('LANDLORD','ADMIN')")
    @PutMapping("/houses/{id}")
    public R<Void> update(@PathVariable long id, @RequestBody @Valid HouseSaveDTO dto) {
        houseService.update(id, dto);
        return R.ok();
    }

    @Operation(summary = "审核房源（ADMIN）")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/houses/{id}/audit")
    public R<Void> audit(@PathVariable long id, @RequestBody @Valid HouseAuditDTO dto) {
        houseService.audit(id, dto);
        return R.ok();
    }

    @Operation(summary = "下架（在租不允许）")
    @PreAuthorize("hasAnyRole('LANDLORD','ADMIN')")
    @PostMapping("/houses/{id}/offline")
    public R<Void> offline(@PathVariable long id) {
        houseService.offline(id);
        return R.ok();
    }

    @Operation(summary = "重新上架（需已审核通过）")
    @PreAuthorize("hasAnyRole('LANDLORD','ADMIN')")
    @PostMapping("/houses/{id}/online")
    public R<Void> online(@PathVariable long id) {
        houseService.online(id);
        return R.ok();
    }
}
