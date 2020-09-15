package com.jsf.utils.sdk.fdfs.proto.storage;

import com.jsf.utils.sdk.fdfs.domain.MetaData;
import com.jsf.utils.sdk.fdfs.proto.AbstractFdfsCommand;
import com.jsf.utils.sdk.fdfs.proto.FdfsResponse;
import com.jsf.utils.sdk.fdfs.proto.storage.enums.StorageMetdataSetType;
import com.jsf.utils.sdk.fdfs.proto.storage.internal.StorageSetMetadataRequest;

import java.util.Set;

/**
 * 设置文件标签
 * 
 * @author tobato
 *
 */
public class StorageSetMetadataCommand extends AbstractFdfsCommand<Void> {

    /**
     * 设置文件标签(元数据)
     * 
     * @param groupName
     * @param path
     * @param metaDataSet
     * @param type
     */
    public StorageSetMetadataCommand(String groupName, String path, Set<MetaData> metaDataSet,
            StorageMetdataSetType type) {
        this.request = new StorageSetMetadataRequest(groupName, path, metaDataSet, type);
        // 输出响应
        this.response = new FdfsResponse<Void>() {
            // default response
        };
    }

}
