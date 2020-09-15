package com.jsf.utils.sdk.fdfs.proto.tracker;

import com.jsf.utils.sdk.fdfs.domain.StorageNode;
import com.jsf.utils.sdk.fdfs.proto.AbstractFdfsCommand;
import com.jsf.utils.sdk.fdfs.proto.FdfsResponse;
import com.jsf.utils.sdk.fdfs.proto.tracker.internal.TrackerGetStoreStorageRequest;
import com.jsf.utils.sdk.fdfs.proto.tracker.internal.TrackerGetStoreStorageWithGroupRequest;

/**
 * 获取存储节点命令
 * 
 * @author tobato
 *
 */
public class TrackerGetStoreStorageCommand extends AbstractFdfsCommand<StorageNode> {

    public TrackerGetStoreStorageCommand(String groupName) {
        super.request = new TrackerGetStoreStorageWithGroupRequest(groupName);
        super.response = new FdfsResponse<StorageNode>() {
            // default response
        };
    }

    public TrackerGetStoreStorageCommand() {
        super.request = new TrackerGetStoreStorageRequest();
        super.response = new FdfsResponse<StorageNode>() {
            // default response
        };
    }

}
