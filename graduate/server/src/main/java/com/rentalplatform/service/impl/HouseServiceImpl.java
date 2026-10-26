package com.rentalplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rentalplatform.common.enums.HouseAuditStatus;
import com.rentalplatform.common.enums.HouseRentStatus;
import com.rentalplatform.common.exception.BusinessException;
import com.rentalplatform.common.result.PageResult;
import com.rentalplatform.common.result.ResultCode;
import com.rentalplatform.mapper.DeviceMapper;
import com.rentalplatform.mapper.EnvRecordMapper;
import com.rentalplatform.mapper.HouseMapper;
import com.rentalplatform.mapper.UserMapper;
import com.rentalplatform.model.dto.HouseAuditDTO;
import com.rentalplatform.model.dto.HouseSaveDTO;
import com.rentalplatform.model.entity.Device;
import com.rentalplatform.model.entity.EnvRecord;
import com.rentalplatform.model.entity.House;
import com.rentalplatform.model.entity.SysUser;
import com.rentalplatform.model.query.HouseQuery;
import com.rentalplatform.model.vo.HouseDetailVO;
import com.rentalplatform.model.vo.HouseItemVO;
import com.rentalplatform.security.LoginUser;
import com.rentalplatform.security.SecurityUtils;
import com.rentalplatform.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService {

    private final HouseMapper houseMapper;
    private final DeviceMapper deviceMapper;
    private final EnvRecordMapper envRecordMapper;
    private final UserMapper userMapper;

    // ───────────────────────── 查询 ─────────────────────────

    @Override
    public PageResult<HouseItemVO> page(HouseQuery query) {
        boolean isAdmin = hasRole("ADMIN");
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<House>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(House::getTitle, query.getKeyword())
                    .or().like(House::getAddress, query.getKeyword()));
        }
        wrapper.eq(StringUtils.hasText(query.getDistrict()), House::getDistrict, query.getDistrict())
                .eq(StringUtils.hasText(query.getLayout()), House::getLayout, query.getLayout())
                .ge(query.getRentMin() != null, House::getRent, query.getRentMin())
                .le(query.getRentMax() != null, House::getRent, query.getRentMax())
                .eq(StringUtils.hasText(query.getRentStatus()), House::getRentStatus, query.getRentStatus());

        if (StringUtils.hasText(query.getAuditStatus())) {
            // 非 ADMIN 查询非 APPROVED → 拒绝（未审核房源不对外可见）
            if (!isAdmin && !HouseAuditStatus.APPROVED.name().equals(query.getAuditStatus())) {
                throw new BusinessException(ResultCode.NO_DATA_PERMISSION);
            }
            wrapper.eq(House::getAuditStatus, query.getAuditStatus());
        } else if (!isAdmin) {
            wrapper.eq(House::getAuditStatus, HouseAuditStatus.APPROVED.name());
        }

        wrapper.orderBy(true, "asc".equalsIgnoreCase(query.getSortOrder()),
                mapSort(query.getSortBy()));

        Page<House> page = houseMapper.selectPage(
                new Page<>(query.getPageNum(), Math.min(query.getPageSize(), 100)), wrapper);
        return toPageResult(page);
    }

    @Override
    public PageResult<HouseItemVO> mine(HouseQuery query) {
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<House>()
                .eq(House::getLandlordId, SecurityUtils.getUserId())
                .eq(StringUtils.hasText(query.getAuditStatus()), House::getAuditStatus, query.getAuditStatus())
                .eq(StringUtils.hasText(query.getRentStatus()), House::getRentStatus, query.getRentStatus())
                .orderByDesc(House::getCreatedAt);
        Page<House> page = houseMapper.selectPage(
                new Page<>(query.getPageNum(), Math.min(query.getPageSize(), 100)), wrapper);
        return toPageResult(page);
    }

    @Override
    public PageResult<HouseItemVO> pendingAudit(int pageNum, int pageSize) {
        Page<House> page = houseMapper.selectPage(
                new Page<>(pageNum, Math.min(pageSize, 100)),
                new LambdaQueryWrapper<House>()
                        .eq(House::getAuditStatus, HouseAuditStatus.PENDING.name())
                        .orderByAsc(House::getCreatedAt));
        return toPageResult(page);
    }

    @Override
    public HouseDetailVO detail(long id) {
        House h = requireHouse(id);
        boolean ownerOrAdmin = isOwner(h) || hasRole("ADMIN");
        // 审核状态管「能不能被看见」（§1.1）：未通过仅本人/ADMIN 可见
        if (!HouseAuditStatus.APPROVED.name().equals(h.getAuditStatus()) && !ownerOrAdmin) {
            throw new BusinessException(ResultCode.NO_DATA_PERMISSION);
        }

        Device device = deviceMapper.selectOne(
                new LambdaQueryWrapper<Device>().eq(Device::getHouseId, id).last("LIMIT 1"));
        EnvRecord latest = latestEnv(List.of(id)).get(id);

        HouseDetailVO.HouseDetailVOBuilder builder = HouseDetailVO.builder()
                .id(h.getId()).title(h.getTitle()).district(h.getDistrict()).street(h.getStreet())
                .address(h.getAddress()).lat(h.getLat()).lng(h.getLng()).layout(h.getLayout())
                .area(h.getArea()).floor(h.getFloor()).totalFloor(h.getTotalFloor())
                .orientation(h.getOrientation()).decoration(h.getDecoration())
                .rent(h.getRent()).deposit(h.getDeposit()).coverUrl(h.getCoverUrl())
                .description(h.getDescription())
                .facilities(StringUtils.hasText(h.getFacilities())
                        ? Arrays.asList(h.getFacilities().split(",")) : List.of())
                .auditStatus(h.getAuditStatus()).rentStatus(h.getRentStatus())
                .auditRemark(h.getAuditRemark()).auditTime(h.getAuditTime())
                .createdAt(h.getCreatedAt()).version(h.getVersion());

        if (device != null) {
            builder.deviceStatus(device.getStatus())
                    .device(new HouseDetailVO.DeviceInfo(
                            device.getId(), device.getName(), device.getStatus(), device.getSensorProfile()));
        }
        if (latest != null && latest.getHcho() != null) {
            builder.latestHcho(latest.getHcho())
                    .hchoExceeded(latest.getHcho().compareTo(HouseItemVO.HCHO_THRESHOLD) > 0);
        }
        if (ownerOrAdmin) {
            SysUser landlord = userMapper.selectById(h.getLandlordId());
            if (landlord != null) {
                builder.landlord(new HouseDetailVO.LandlordContact(
                        landlord.getId(), landlord.getNickname(), landlord.getPhone()));
            }
        }
        return builder.build();
    }

    // ───────────────────────── 写操作 ─────────────────────────

    @Override
    public long create(HouseSaveDTO dto) {
        House h = new House();
        copy(dto, h);
        h.setLandlordId(SecurityUtils.getUserId());
        h.setAuditStatus(HouseAuditStatus.PENDING.name());
        h.setRentStatus(HouseRentStatus.AVAILABLE.name());
        h.setVersion(0);
        houseMapper.insert(h);
        return h.getId();
    }

    @Override
    public void update(long id, HouseSaveDTO dto) {
        House h = requireOwnedHouse(id);
        // 乐观锁：version 不匹配视为并发冲突
        if (dto.getVersion() != null && !dto.getVersion().equals(h.getVersion())) {
            throw new BusinessException(ResultCode.HOUSE_STATUS_INVALID, "数据已被他人修改，请刷新后重试");
        }
        copy(dto, h);
        // 编辑已通过审核的房源 → 回到 PENDING 重新审核（openapi 约定）
        if (HouseAuditStatus.APPROVED.name().equals(h.getAuditStatus())) {
            h.setAuditStatus(HouseAuditStatus.PENDING.name());
            h.setAuditRemark(null);
        }
        houseMapper.updateById(h);
    }

    @Override
    public void audit(long id, HouseAuditDTO dto) {
        House h = requireHouse(id);
        if (!HouseAuditStatus.PENDING.name().equals(h.getAuditStatus())) {
            throw new BusinessException(ResultCode.HOUSE_STATUS_INVALID, "仅待审核房源可审核");
        }
        boolean approved;
        if (HouseAuditStatus.APPROVED.name().equals(dto.getAuditStatus())) {
            approved = true;
        } else if (HouseAuditStatus.REJECTED.name().equals(dto.getAuditStatus())) {
            approved = false;
            if (!StringUtils.hasText(dto.getRemark())) {
                throw new BusinessException(ResultCode.PARAM_INVALID, "驳回时必须填写原因");
            }
        } else {
            throw new BusinessException(ResultCode.PARAM_INVALID, "auditStatus 仅允许 APPROVED/REJECTED");
        }
        if (!approved && HouseRentStatus.RENTED.name().equals(h.getRentStatus())) {
            throw new BusinessException(ResultCode.HOUSE_STATUS_INVALID, "在租房源不允许驳回");
        }
        h.setAuditStatus(dto.getAuditStatus());
        h.setAuditRemark(dto.getRemark());
        h.setAuditorId(SecurityUtils.getUserId());
        houseMapper.updateById(h);
    }

    @Override
    public void offline(long id) {
        House h = requireOwnedHouse(id);
        if (HouseRentStatus.RENTED.name().equals(h.getRentStatus())) {
            throw new BusinessException(ResultCode.HOUSE_STATUS_INVALID, "在租房源不允许下架");
        }
        if (HouseRentStatus.OFFLINE.name().equals(h.getRentStatus())) {
            return; // 幂等
        }
        h.setRentStatus(HouseRentStatus.OFFLINE.name());
        houseMapper.updateById(h);
    }

    @Override
    public void online(long id) {
        House h = requireOwnedHouse(id);
        if (!HouseRentStatus.OFFLINE.name().equals(h.getRentStatus())) {
            throw new BusinessException(ResultCode.HOUSE_STATUS_INVALID, "仅已下架房源可重新上架");
        }
        if (!HouseAuditStatus.APPROVED.name().equals(h.getAuditStatus())) {
            throw new BusinessException(ResultCode.HOUSE_NOT_APPROVED);
        }
        h.setRentStatus(HouseRentStatus.AVAILABLE.name());
        houseMapper.updateById(h);
    }

    // ───────────────────────── 内部工具 ─────────────────────────

    private PageResult<HouseItemVO> toPageResult(Page<House> page) {
        List<Long> ids = page.getRecords().stream().map(House::getId).toList();
        Map<Long, Device> devices = ids.isEmpty() ? Map.of()
                : deviceMapper.selectList(
                        new LambdaQueryWrapper<Device>().in(Device::getHouseId, ids)).stream()
                        .collect(Collectors.toMap(Device::getHouseId, Function.identity(),
                                (a, b) -> a));
        Map<Long, EnvRecord> latest = latestEnv(ids);

        List<HouseItemVO> list = page.getRecords().stream().map(h -> {
            HouseItemVO vo = HouseItemVO.baseOf(h);
            Device d = devices.get(h.getId());
            EnvRecord e = latest.get(h.getId());
            vo.fillEnv(d != null ? d.getStatus() : null, e != null ? e.getHcho() : null);
            return vo;
        }).toList();
        return PageResult.of(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    private Map<Long, EnvRecord> latestEnv(List<Long> houseIds) {
        if (houseIds.isEmpty()) {
            return Map.of();
        }
        return envRecordMapper.selectLatestByHouseIds(houseIds).stream()
                .collect(Collectors.toMap(EnvRecord::getHouseId, Function.identity(),
                        (a, b) -> a));
    }

    private void copy(HouseSaveDTO dto, House h) {
        h.setTitle(dto.getTitle());
        h.setDistrict(dto.getDistrict());
        h.setStreet(dto.getStreet());
        h.setAddress(dto.getAddress());
        h.setLat(dto.getLat());
        h.setLng(dto.getLng());
        h.setArea(dto.getArea());
        h.setLayout(dto.getLayout());
        h.setFloor(dto.getFloor());
        h.setTotalFloor(dto.getTotalFloor());
        h.setOrientation(dto.getOrientation());
        h.setDecoration(dto.getDecoration());
        h.setRent(dto.getRent());
        h.setDeposit(dto.getDeposit());
        h.setFacilities(dto.getFacilities() == null ? null : String.join(",", dto.getFacilities()));
        h.setCoverUrl(dto.getCoverUrl());
        h.setDescription(dto.getDescription());
    }

    private House requireHouse(long id) {
        House h = houseMapper.selectById(id);
        if (h == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "房源不存在");
        }
        return h;
    }

    private House requireOwnedHouse(long id) {
        House h = requireHouse(id);
        if (!isOwner(h) && !hasRole("ADMIN")) {
            throw new BusinessException(ResultCode.HOUSE_NOT_IN_SCOPE);
        }
        return h;
    }

    /** 匿名访问公开接口时按「非本人/非管理员」处理，不抛认证异常（未审核房源 → 30002） */
    private LoginUser currentLogin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getPrincipal() instanceof LoginUser lu ? lu : null;
    }

    private boolean isOwner(House h) {
        LoginUser lu = currentLogin();
        return lu != null && h.getLandlordId() != null && h.getLandlordId().equals(lu.getUserId());
    }

    private boolean hasRole(String role) {
        LoginUser lu = currentLogin();
        return lu != null && role.equals(lu.getRole());
    }

    /** 排序字段白名单，防 SQL 注入 */
    private com.baomidou.mybatisplus.core.toolkit.support.SFunction<House, ?> mapSort(String sortBy) {
        return switch (sortBy == null ? "createdAt" : sortBy) {
            case "rent" -> House::getRent;
            case "area" -> House::getArea;
            default -> House::getCreatedAt;
        };
    }
}
