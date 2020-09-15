package com.jsf.utils.sdk.fdfs.proto.tracker;

import com.jsf.utils.sdk.fdfs.domain.GroupState;
import com.jsf.utils.sdk.fdfs.proto.AbstractFdfsCommand;
import com.jsf.utils.sdk.fdfs.proto.tracker.internal.TrackerListGroupsRequest;
import com.jsf.utils.sdk.fdfs.proto.tracker.internal.TrackerListGroupsResponse;

import java.util.List;

/**
 * 列出组命令
 * 
 * @author tobato
 *
 */
public class TrackerListGroupsCommand extends AbstractFdfsCommand<List<GroupState>> {

    public TrackerListGroupsCommand() {
        super.request = new TrackerListGroupsRequest();
        super.response = new TrackerListGroupsResponse();
    }

}
