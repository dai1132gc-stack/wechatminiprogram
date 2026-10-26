package com.rentalplatform.service;

import com.rentalplatform.common.result.PageResult;
import com.rentalplatform.model.dto.HouseAuditDTO;
import com.rentalplatform.model.dto.HouseSaveDTO;
import com.rentalplatform.model.query.HouseQuery;
import com.rentalplatform.model.vo.HouseDetailVO;
import com.rentalplatform.model.vo.HouseItemVO;

/** 房源服务：状态机规则见技术方案 §10，可见性见 §9。 */
public interface HouseService {

    /** 公开列表：默认仅 APPROVED 且非 OFFLINE；ADMIN 可查全量（auditStatus 过滤） */
    PageResult<HouseItemVO> page(HouseQuery query);

    /** 详情：未审核通过仅本人/ADMIN 可见（30002） */
    HouseDetailVO detail(long id);

    /** 我发布的房源（LANDLORD） */
    PageResult<HouseItemVO> mine(HouseQuery query);

    /** 待审核列表（ADMIN，提交时间正序） */
    PageResult<HouseItemVO> pendingAudit(int pageNum, int pageSize);

    long create(HouseSaveDTO dto);

    /** 编辑已通过审核的房源回到 PENDING 重新审核；乐观锁 version 并发校验 */
    void update(long id, HouseSaveDTO dto);

    /** 审核房源（ADMIN）：REJECTED 必填 remark；RENTED 房源不允许驳回 */
    void audit(long id, HouseAuditDTO dto);

    /** 下架：RENTED 不允许（40001） */
    void offline(long id);

    /** 上架：需已审核通过（40006），仅 OFFLINE 可上架（40001） */
    void online(long id);
}
