package com.jsf.utils.sdk.fdfs.proto.storage;

import com.jsf.utils.sdk.fdfs.domain.MetaData;
import com.jsf.utils.sdk.fdfs.proto.AbstractFdfsCommand;
import com.jsf.utils.sdk.fdfs.proto.storage.internal.StorageGetMetadataRequest;
import com.jsf.utils.sdk.fdfs.proto.storage.internal.StorageGetMetadataResponse;

import java.util.Set;

/**
 * 设置文件标签
 * 
 * @author tobato
 *
 */
public class StorageGetMetadataCommand extends AbstractFdfsCommand<Set<MetaData>> {

    /**
     * 设置文件标签(元数据)
     * 
     * @param groupName
     * @param path
     * @param metaDataSet
     * @param type
     */
    public StorageGetMetadataCommand(String groupName, String path) {
        this.request = new StorageGetMetadataRequest(groupName, path);
        // 输出响应
        this.response = new StorageGetMetadataResponse();
    }

}
