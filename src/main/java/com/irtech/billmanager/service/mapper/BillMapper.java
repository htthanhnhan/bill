package com.irtech.billmanager.service.mapper;

import com.irtech.billmanager.domain.*;
import com.irtech.billmanager.service.dto.BillDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Bill} and its DTO {@link BillDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BillMapper extends EntityMapper<BillDTO, Bill> {}
