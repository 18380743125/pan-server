package com.tangl.pan.server.modules.share.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.tangl.pan.core.exception.PanBusinessException;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.modules.share.context.SaveShareFilesContext;
import com.tangl.pan.server.modules.share.entity.PanShareFile;
import com.tangl.pan.server.modules.share.service.IShareFileService;
import com.tangl.pan.server.modules.share.mapper.PanShareFileMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 文件分享业务层
 */
@Service
public class ShareFileServiceImpl extends ServiceImpl<PanShareFileMapper, PanShareFile> implements IShareFileService {

    /**
     * 保存文件和分享的关联关系
     *
     * @param context 上下文实体
     */
    @Override
    public void saveShareFiles(SaveShareFilesContext context) {
        Long shareId = context.getShareId();
        Long userId = context.getUserId();
        List<Long> shareFileIdList = context.getShareFileIdList();

        List<PanShareFile> records = Lists.newArrayList();

        for (Long shareFileId : shareFileIdList) {
            PanShareFile record = new PanShareFile();
            record.setId(IdUtil.get());
            record.setShareId(shareId);
            record.setFileId(shareFileId);
            record.setCreateUser(userId);
            record.setCreateTime(new Date());
            records.add(record);
        }

        if (!saveBatch(records)) {
            throw new PanBusinessException("保存文件分享关联关系失败");
        }
    }
}
