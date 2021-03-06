package com.jsf;

import com.jsf.storm.WordCountBolt;
import com.jsf.storm.WordReportBolt;
import com.jsf.storm.WordSplitBolt;
import com.jsf.storm.WordSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * Created with IntelliJ IDEA.
 * Description: 词频统计
 * User: xujunfei
 * Date: 2019-08-05
 * Time: 11:20
 */
public class App {

    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();
        // 组建拓扑，并使用流分组
        builder.setSpout("WordSpout", new WordSpout());
        builder.setBolt("WordSplitBolt", new WordSplitBolt(), 5).shuffleGrouping("WordSpout");
        builder.setBolt("WordCountBolt", new WordCountBolt(), 5).fieldsGrouping("WordSplitBolt", new Fields("word"));
        builder.setBolt("WordReportBolt", new WordReportBolt(), 10).globalGrouping("WordCountBolt");

        // 配置
        Config config = new Config();
        config.setDebug(false);
        // 本地模式运行
        LocalCluster cluster = new LocalCluster();

        // 提交拓扑图，会一直轮询执行
        cluster.submitTopology("wordcount-topo", config, builder.createTopology());
    }

}
