package com.tangl.pan.server.modules.share.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.modules.share.context.SaveShareFilesContext;
import com.tangl.pan.server.modules.share.entity.TPanShareFile;
import com.tangl.pan.server.modules.share.service.IShareFileService;
import com.tangl.pan.server.modules.share.mapper.TPanShareFileMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 25050
 * @description 针对表【t_pan_share_file(用户分享文件表)】的数据库操作Service实现
 * @createDate 2023-07-23 23:42:53
 */
@Service
public class ShareFileServiceImpl extends ServiceImpl<TPanShareFileMapper, TPanShareFile> implements IShareFileService {

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

        List<TPanShareFile> records = Lists.newArrayList();

        for (Long shareFileId : shareFileIdList) {
            TPanShareFile record = new TPanShareFile();
            record.setId(IdUtil.get());
            record.setShareId(shareId);
            record.setFileId(shareFileId);
            record.setCreateUser(userId);
            record.setCreateTime(new Date());
            records.add(record);
        }

        if (!saveBatch(records)) {
            throw new TPanBusinessException("保存文件分享关联关系失败");
        }
    }
}
