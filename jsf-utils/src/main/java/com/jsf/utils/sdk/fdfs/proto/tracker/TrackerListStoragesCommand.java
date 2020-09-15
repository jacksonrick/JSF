package com.jsf.utils.sdk.fdfs.proto.tracker;

import com.jsf.utils.sdk.fdfs.domain.StorageState;
import com.jsf.utils.sdk.fdfs.proto.AbstractFdfsCommand;
import com.jsf.utils.sdk.fdfs.proto.tracker.internal.TrackerListStoragesRequest;
import com.jsf.utils.sdk.fdfs.proto.tracker.internal.TrackerListStoragesResponse;

import java.util.List;

/**
 * 列出组命令
 * 
 * @author tobato
 *
 */
public class TrackerListStoragesCommand extends AbstractFdfsCommand<List<StorageState>> {

    public TrackerListStoragesCommand(String groupName, String storageIpAddr) {
        super.request = new TrackerListStoragesRequest(groupName, storageIpAddr);
        super.response = new TrackerListStoragesResponse();
    }

    public TrackerListStoragesCommand(String groupName) {
        super.request = new TrackerListStoragesRequest(groupName);
        super.response = new TrackerListStoragesResponse();
    }

}
